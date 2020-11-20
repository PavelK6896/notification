package ru.geekbrains.parser.cian;

import com.gargoylesoftware.htmlunit.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.entity.system.Proxy;
import ru.geekbrains.model.Parser;
import ru.geekbrains.model.Task;
import ru.geekbrains.parser.ApartmentParserInterface;
import ru.geekbrains.parser.cian.utils.ProxyGate;
import ru.geekbrains.parser.cian.utils.ProxyGateImpl;
import ru.geekbrains.parser.cian.utils.exception.AdsNotFoundException;
import ru.geekbrains.parser.cian.utils.exception.CaptchaException;
import ru.geekbrains.parser.cian.utils.CianRegionDefiner;
import ru.geekbrains.parser.cian.utils.DataExtractor;
import ru.geekbrains.service.parserservice.ParserService;
import ru.geekbrains.service.system.ProxyService;

import java.util.*;
import java.util.logging.Level;

import static java.util.stream.Collectors.groupingBy;

/**
 * Has methods to parse html from cian.ru
 * <p>
 * Full GET request URL to get Cian ads is: https://www.cian.ru/cat.php?currency=2&deal_type=rent&engine_version=2&maxprice=20000&minprice=10000&offer_type=flat&p=1&region=4581&room1=1&room2=1&room3=1&type=4
 * Where
 * https://www.cian.ru/cat.php - main URL;
 * Get parameters:
 * currency=2 - designation is undefined yet
 * deal_type=rent - type of the deal rent or sale
 * engine_version=2 - designation is undefined yet
 * maxprice=
 * minprice=
 * offer_type=flat - subject of a search may be flat or suburban for house searching
 * p= - page number
 * region= - region number
 * room0= - room search, value 1 if checkbox is marked
 * room1= - 1room flat, value 1 if checkbox is marked
 * room2= - 2room flat, value 1 if checkbox is marked
 * room3= - 4room flat, value 1 if checkbox is marked
 * type=4 - has a impact on results (change some link which logic related to
 * </p>
 */

@Slf4j
@Component
public class CianParser extends Parser implements Runnable {

    private final DataExtractor dataExtractor;
    private final ProxyGate proxyGate;
    private final CianRegionDefiner cianRegionDefiner;
    private final List<ApartmentParserInterface> cianApartments = new ArrayList<>();
    private boolean isProcessing = false;
    private final String name = "Циан";
    private ParserService parserService;
    private Task task;

    @Autowired
    public CianParser(DataExtractor dataExtractor, CianRegionDefiner cianRegionDefiner, ParserService parserService, ProxyGate proxyGate) {
        this.dataExtractor = dataExtractor;
        this.proxyGate = proxyGate;
        this.cianRegionDefiner = cianRegionDefiner;
        this.parserService = parserService;
        this.parserService.register(this);
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
    }

    /**
     * Starts new Thread, initializes variable task
     *
     * @param country Name of a country where parser should find adTags
     * @param city    Name of a city in which parser should find adTags
     */
    public void start(String country, String city) {
        task = new Task();
        task.setCountry(country);
        task.setCity(city);

        new Thread(this).start();
    }

    /**
     * Fills List<CinaApartements> with ads from cain.ru
     * Search page deep is limited to SEARCH_DEEP value
     */

    @Override
    public void run() {
        setProcessing(true);
        if (!task.getCountry().equals("Россия")) {
            throw new RuntimeException("You are searching adTags in the country " + task.getCountry() + " but we can find ads only in 'Россия'");
        }

        List<String> regionCodes = cianRegionDefiner.getRegions(task.getCity());

        String pageValue = "1";
        final String SEARCH_DEEP = "5";
        final String DEAL_TYPE = "rent";
        final String OFFER_TYPE = "flat";
        boolean hasNextPage = true;

        for (String regionCode : regionCodes) {
            while (hasNextPage) {  // uncomment to search throughout all the target pages

                URIBuilder uri = new URIBuilder();
                uri.setScheme("https")
                        .setHost("www.cian.ru")
                        .setPath("/cat.php")
                        .addParameter("engine_version", "2")
                        .addParameter("deal_type", DEAL_TYPE)
                        .addParameter("offer_type", OFFER_TYPE)
                        .addParameter("region", regionCode)
                        .addParameter("p", pageValue);

                //*********** HtmlUnitDriver connection - 1st variant*****************
                HtmlUnitDriver driver = new HtmlUnitDriver() {
                    @Override
                    protected WebClient modifyWebClient(WebClient client) {
                        client.setCssErrorHandler(new SilentCssErrorHandler()); // shouting up wall of warnings in logs form htmlUnitDriver
                        return client;
                    }

                };

                Proxy proxy = proxyGate.getProxy();
                log.info("proxyGate has returned proxy with host:port - " + proxy.getAddress());
                driver.setProxy(proxy.getHost(), Integer.parseInt(proxy.getPort()));
                driver.get("https://ipinfo.io/ip");
                log.info("Site https://ipinfo.io/ip is reporting that your IP is: " + Jsoup.parse(driver.getPageSource()).getElementsByTag("body").text());
                driver.get(uri.toString());

                Document document = Jsoup.parse(driver.getPageSource());

                if (document.title().contains("Captcha")) {
                   log.warn("Proxy " + proxy.getAddress() + " has been banned from " + this.getName());
                   proxyGate.setProxyBlockedBy(this.getName(), proxy);
                }

                Elements adTags = document.getElementsByTag("article");

                if (adTags.size() == 0)
                    throw new AdsNotFoundException("There are no ads can be parsed from the page");

                log.info("Page #" + pageValue + " is scanned");
                log.info("Amount of ad tags on the page: " + adTags.size());

                for (Element adTag : adTags) {
                    CianApartment cianApartment = dataExtractor.buildApartment(adTag);
                    if (cianApartment.getCity().equals(task.getCity())) cianApartments.add(cianApartment);
                }

                String lastPageValueMark = document.selectFirst("div[data-name~=^Pagination]").getElementsByTag("li").last().children().first().text();
                if (lastPageValueMark.equals(pageValue) || pageValue.equals(SEARCH_DEEP)) {
                    hasNextPage = false;
                }
                pageValue = String.valueOf(Integer.parseInt(pageValue) + 1);
            }
            pageValue = "1";
        }
        setProcessing(false);
    }


    public List<ApartmentParserInterface> getResult() {

        return cianApartments;
    }

    public boolean getProcessingStatus() {
        return isProcessing;
    }

    @Override
    public void setProcessing(boolean processing) {
        isProcessing = processing;
    }

    @Override
    public String getName() {
        return name;
    }


}

