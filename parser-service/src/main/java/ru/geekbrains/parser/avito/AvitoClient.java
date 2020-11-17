package ru.geekbrains.parser.avito;

import lombok.extern.java.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.geekbrains.entity.system.Proxy;
import ru.geekbrains.parser.cian.service.UserAgentService;
import ru.geekbrains.repository.ProxyRepository;
import ru.geekbrains.service.system.ProxyService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log
@Service
public class AvitoClient {


    public static final Integer SORT_NEW_FIRST = 104;

    private CloseableHttpClient httpClient;

    private ProxyService proxyService;

    private UserAgentService userAgentService;

    private Proxy proxy;

    @Value("${parsers.avito.baseUrl:https://www.avito.ru}")
    private String baseUrl;

    @Value("${parsers.avito.cityFindUrl:https://www.avito.ru/web/1/slocations}")
    private String cityFindUrl;

    @Value("${parsers.avito.cityInfoUrl:https://www.avito.ru/js/catalog}")
    private String cityInfoUrl;

    @Value("${parsers.avito.apartmentCategoryUrlPath:/kvartiry/prodam-ASgBAgICAUSSA8YQ}")
    private String apartmentCategoryUrlPath;

    @Autowired
    public AvitoClient(ProxyService proxyService, UserAgentService userAgentService) {
        this.userAgentService = userAgentService;
        this.proxyService = proxyService;
        Optional<Proxy> optionalProxy = proxyService.findByActive();
        if(!optionalProxy.isPresent()){
            throw new RuntimeException("No valid proxy");
        }
        this.proxy = optionalProxy.get();


        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(this.proxy.getHost(), Integer.parseInt(this.proxy.getPort())),
                new UsernamePasswordCredentials(this.proxy.getLogin(), this.proxy.getPassword()));
        this.httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider).build();
    }

    public String findCity(String city) {

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("limit", "10"));
        params.add(new BasicNameValuePair("q", city));

        return sendGet(this.cityFindUrl, params);
    }

    public String cityInfo(Integer cityId) {

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("locationId", cityId.toString()));

        return sendGet(this.cityInfoUrl, params);
    }

    public String apartmentList(String cityPrefix, Integer sort, Integer page) {

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("s", sort.toString()));
        params.add(new BasicNameValuePair("p", page.toString()));

        return sendGet(baseUrl + cityPrefix + apartmentCategoryUrlPath, params);
    }

    public String apartmentPage(String url) {

        return sendGet(baseUrl + url, new ArrayList<>());
    }

    private String sendGet(String url, List<NameValuePair> getParameters) {

        try {


            URIBuilder builder = new URIBuilder(url);
            for (NameValuePair param : getParameters) {
                builder.setParameter(param.getName(), param.getValue());
            }

            HttpGet request = new HttpGet(builder.build());
            this.prepareHeaders(request);
            this.setProxy(request);

            return this.request(request);
        } catch (Exception e) {
            log.severe(e.getMessage());
            return null;
        }
    }

    private String sendPost(String url, List<NameValuePair> postParameters) {
        try {
            HttpPost request = new HttpPost(url);
            request.setEntity(new UrlEncodedFormEntity(postParameters));

            return this.request(request);
        } catch (Exception e) {
            log.severe(e.getMessage());
            return null;
        }
    }

    private void prepareHeaders(HttpUriRequest request) {

        request.addHeader(HttpHeaders.USER_AGENT, userAgentService.getRandomUserAgent());
    }

    private void setProxy(HttpRequestBase request) {


        HttpHost proxy = new HttpHost(this.proxy.getHost(), Integer.parseInt(this.proxy.getPort()), "http");
        RequestConfig config = RequestConfig.custom()
                .setProxy(proxy)
                .build();
        request.setConfig(config);

    }

    private String request(HttpUriRequest request) {

        try (CloseableHttpResponse response = httpClient.execute(request)) {

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        }

        return null;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
