package ru.geekbrains.parser.avito;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.java.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.geekbrains.model.Parser;
import ru.geekbrains.model.Task;
import ru.geekbrains.parser.ApartmentParserInterface;
import ru.geekbrains.service.geo.GeoService;
import ru.geekbrains.service.parserservice.ParserService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Log
public class AvitoParser extends Parser implements Runnable {


    private AvitoClient client;

    private ParserService parserService;

    private GeoService geoService;

    private Task task;

    @Value("${parsers.avito.listLinkSelector:.snippet-list .snippet.item_table .snippet-title .snippet-link}")
    private String listLinkSelector;

    @Value("${parsers.avito.apartmentSelectors.infoDescription:.title-info-title-text}")
    private String infoDescriptionSelector;

    @Value("${parsers.avito.apartmentSelectors.description:.item-description-html}")
    private String descriptionSelector;

    @Value("${parsers.avito.apartmentSelectors.priceSelector:.js-item-price}")
    private String priceSelector;

    @Value("${parsers.avito.apartmentSelectors.addressSelector:.item-address__string}")
    private String addressSelector;

    @Value("${parsers.avito.apartmentSelectors.districtSelector:.item-address-georeferences-item__content}")
    private String districtSelector;

    @Value("${parsers.avito.maxPage:1}")
    private int maxPage;

    private HashMap<String, String> cityMap;

    @Autowired
    public AvitoParser(AvitoClient client, ParserService parserService, GeoService geoService) {
        this.client = client;
        this.parserService = parserService;
        this.geoService = geoService;

//        this.parserService.register(this);
        this.cityMap = new HashMap<>();
    }

    public void start(String country, String city) {
        this.setProcessing(true);

        this.task = new Task();
        task.setCountry(country);
        task.setCity(city);

        new Thread(this).start();
    }

    public void run() {

        List<ApartmentParserInterface> avitoApartments = this.parse(this.task);
        if (avitoApartments != null) {
            this.setApartmentList(avitoApartments);
            log.info(String.valueOf(avitoApartments.size()));
        } else {
            this.setApartmentList(new ArrayList<ApartmentParserInterface>());
        }
        this.setProcessing(false);
    }

    public String getName() {

        return "avito";
    }

    public List<ApartmentParserInterface> parse(Task task) {

        try {

            String prefix = cityMap.get(task.getCity() == null);
            if (prefix == null) {
                Integer cityId = this.findCityId(task.getCity());
                prefix = this.findCityPrefix(cityId);
                cityMap.put(task.getCity(), prefix);
            }

            ArrayList<ApartmentParserInterface> avitoApartments = new ArrayList<>();
            ArrayList<String> allLinks = new ArrayList<>();

            for (int page = 1; page <= this.maxPage; page++) {
                String apartmentListPage = client.apartmentList(prefix, AvitoClient.SORT_NEW_FIRST, page);
                List<String> apartmentLink = this.getApartmentLink(apartmentListPage);
                if (apartmentLink.isEmpty()) {
                    break;
                }
                allLinks.addAll(apartmentLink);
            }

            for (String apartmentUrl : allLinks) {
                log.info(apartmentUrl);
                AvitoApartment avitoApartment = this.parseApartment(apartmentUrl);
                if (!avitoApartment.isEmpty()) {
                    avitoApartments.add(avitoApartment);
                    if (avitoApartments.size() >= 10) {
                        break;
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            return avitoApartments;
        } catch (CityNotFound e) {
            log.info(e.getMessage());
            return null;
        }
    }

    /**
     * Поиск ID города в БД Avito
     */
    private Integer findCityId(String city) throws CityNotFound {

        String response = client.findCity(city);
        log.info(response);

        if (response == null) {
            throw new CityNotFound("Empty city list");
        }

        try {
            JsonObject object = new Gson().fromJson(response, JsonObject.class);
            JsonArray locations = object.get("result").getAsJsonObject().get("locations").getAsJsonArray();
            for (JsonElement location : locations) {
                JsonObject locationObject = location.getAsJsonObject();
                Integer cityId = locationObject.get("id").getAsInt();
                String name = locationObject.get("names").getAsJsonObject().get("1").getAsString();
                if (name.equalsIgnoreCase(city)) {
                    return cityId;
                }
            }
        } catch (NullPointerException e) {
            throw new CityNotFound("IP blocked");
        }

        throw new CityNotFound("City not found");
    }

    /**
     * Поиск префикса к урл в зависемости от ID города
     */
    private String findCityPrefix(Integer cityId) throws CityNotFound {

        String response = client.cityInfo(cityId);

        if (response == null) {
            throw new CityNotFound("Empty city location");
        }

        JsonObject object = new Gson().fromJson(response, JsonObject.class);

        return object.get("url").getAsString();
    }

    /**
     * Парсинг списка ссылок на объяаления на основе html страницы
     */
    private List<String> getApartmentLink(String apartmentListPage) {

        ArrayList<String> links = new ArrayList<>();

        Document doc = Jsoup.parse(apartmentListPage, this.client.getBaseUrl());
        Elements apartments = doc.select(this.listLinkSelector);
        for (Element apartment : apartments) {
            String url = apartment.attr("href");
            if (!url.isEmpty()) {
                links.add(url);
            }
        }

        return links;
    }

    private AvitoApartment parseApartment(String url) {

        AvitoApartment avitoApartment = new AvitoApartment();
        avitoApartment.setUrl(url);
        avitoApartment.setCountry(this.task.getCountry());
        avitoApartment.setCity(this.task.getCity());

        String page = this.client.apartmentPage(url);
        if (page == null) {
            return avitoApartment;
        }
        Document doc = Jsoup.parse(page, this.client.getBaseUrl());


        avitoApartment.setInfoDescription(this.parseStringValue(this.infoDescriptionSelector, doc));
        avitoApartment.setDescription(this.parseStringValue(this.descriptionSelector, doc));
        avitoApartment.setAddress(this.parseStringValue(this.addressSelector, doc));
        avitoApartment.setDistrict(this.parseStringValue(this.districtSelector, doc));

        Element price = doc.selectFirst(this.priceSelector);
        if (price != null) {
            avitoApartment.setPrice(new BigDecimal(price.attr("content")));
        }

        //Вся доп информация о квартире находится в JS переменной window.dataLayer
        //что бы не парсить по отдельности все нужные данные берем из этой переменной
        Pattern p = Pattern.compile("(window\\.dataLayer[^\\[]+([^\\;]+))");
        Matcher m = p.matcher(page);
        if (m.find()) {
            try {
                String dataLayer = m.group(2);
                //тк dataLayer это массив JSON структур
                dataLayer = "{data:" + dataLayer + "}";
                JsonObject object = new Gson().fromJson(dataLayer, JsonObject.class);
                for (JsonElement dataArray : object.get("data").getAsJsonArray()) {
                    JsonObject data = dataArray.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> keyValue : data.entrySet()) {
                        switch (keyValue.getKey()) {
                            case "itemID":
                                avitoApartment.setId(keyValue.getValue().getAsInt());
                                break;
                            case "dynx_price":
                                avitoApartment.setPrice(new BigDecimal(keyValue.getValue().getAsLong()));
                                break;
                            case "offer_type":
                                avitoApartment.setOfferType(keyValue.getValue().getAsString());
                                break;
                            case "area_kitchen":
                                avitoApartment.setAreaKitchen(this.prepareFloat(keyValue.getValue().getAsString()));
                                break;
                            case "area_live":
                                avitoApartment.setAreaLive(this.prepareFloat(keyValue.getValue().getAsString()));
                                break;
                            case "area":
                                avitoApartment.setArea(this.prepareFloat(keyValue.getValue().getAsString()));
                                break;
                            case "floor":
                                avitoApartment.setFloor(keyValue.getValue().getAsInt());
                                break;
                            case "floors_count":
                                avitoApartment.setFloorsCount(keyValue.getValue().getAsInt());
                                break;
                            case "house_type":
                                avitoApartment.setHouseType(keyValue.getValue().getAsString());
                                break;
                            case "type":
                                avitoApartment.setType(keyValue.getValue().getAsString());
                                break;
                            case "rooms":
                                avitoApartment.setRooms(keyValue.getValue().getAsString());
                                break;
                        }
                        /**
                         * {
                         *    "data":[
                         *       {
                         *          "pageType":"Item",
                         *          "itemID":1955625478,
                         *          "vertical":"RE",
                         *          "categoryId":24,
                         *          "categorySlug":"kvartiry",
                         *          "microCategoryId":4919,
                         *          "locationId":637640,
                         *          "isShop":1,
                         *          "isClientType1":1,
                         *          "itemPrice":7648080,
                         *          "withDelivery":1,
                         *          "otdelka":"Без отделки",
                         *          "status":"Квартира",
                         *          "nazvanie_novostroyki":1506384,
                         *          "korpus":1506529,
                         *          "nazvanie_obekta_nedvizhimosti":"Жилой комплекс «Красноказарменная 15»",
                         *          "korpus_stroenie":"Корпус 1.1",
                         *          "tip_uchastiya":1,
                         *          "ofitsialnyy_zastroyshchik":"АО «СЗ Красноказарменная 15»",
                         *          "development_property_engine_id":"WKHPFEDMN5",
                         *          "offer_type":"Продам",
                         *          "commission":"Застройщик",
                         *          "area_kitchen":"6.4 м²",
                         *          "area_live":"12.6 м²",
                         *          "floor":"18",
                         *          "floors_count":"26",
                         *          "house_type":"Монолитный",
                         *          "type":"Новостройка",
                         *          "rooms":"Студия",
                         *          "area":"26.4 м²"
                         *       }
                         *    ]
                         * }
                         */
                    }
                }
            } catch (IllegalStateException e) {
                log.info(e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                log.info(e.getMessage());
            }
        }


        return avitoApartment;
    }

    private String parseStringValue(String selector, Document doc) {

        Element element = doc.selectFirst(selector);
        if (element != null) {
            return element.text();
        }

        return null;
    }

    private Float prepareFloat(String number) {
        number = number.replaceAll("[^0-9\\.]", "");
        return Float.parseFloat(number);
    }
}
