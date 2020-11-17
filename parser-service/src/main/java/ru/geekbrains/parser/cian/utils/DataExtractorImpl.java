package ru.geekbrains.parser.cian.utils;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.geekbrains.parser.cian.CianApartment;
import ru.geekbrains.parser.cian.utils.exception.FloorNotFoundException;
import ru.geekbrains.parser.cian.utils.exception.PeriodNotFoundException;
import ru.geekbrains.parser.cian.utils.exception.PriceNotFoundException;
import ru.geekbrains.parser.cian.utils.exception.SquareInfoNotFoundException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class DataExtractorImpl implements DataExtractor {
    private class CianAddress {
        String country;
        String region;
        String city;
        String district;
        String street;

        public CianAddress(String country, String region, String city, String district, String street) {
            this.country = country;
            this.region = region;
            this.city = city;
            this.district = district;
            this.street = street;
        }

        public String getCountry() {
            return country;
        }

        public String getRegion() {
            return region;
        }

        public String getCity() {
            return city;
        }

        public String getDistrict() {
            return district;
        }

        public String getStreet() {
            return street;
        }

        @Override
        public String toString() {
            return country + ", " +
                    region + ", " +
                    city + ", " +
                    district + ", " +
                    street;
        }
    }

    @Override
    public CianApartment buildApartment(Element adTag) {
        CianApartment cianApartment = new CianApartment();
        CianAddress cianAddress = getAddress(adTag);
        cianApartment.setCountry(cianAddress.getCountry());
        cianApartment.setRegion(cianAddress.getRegion());
        cianApartment.setCity(cianAddress.getCity());
        cianApartment.setDistrict(cianAddress.getDistrict());
        cianApartment.setStreet(cianAddress.getStreet());
        cianApartment.setAddress(cianAddress.toString());
        cianApartment.setRooms(this.getQuantity(adTag));
        cianApartment.setArea(this.getQuadrature(adTag));
        cianApartment.setPrice(this.getPrice(adTag));
        cianApartment.setDescription(this.getDescription(adTag));
        cianApartment.setInfoDescription(this.getTitle(adTag));
        cianApartment.setUrl(this.getLink(adTag));
        cianApartment.setFloor(this.getFloor(adTag));
        cianApartment.setIsDaily(this.getPeriod(adTag));

        return cianApartment;
    }


    private CianAddress getAddress(Element adTag) {
        Elements addressElements = adTag.select("a[data-name$=GeoLabel]");
        //Регион и город обозначаются одинаково: region
        String[] regionAndCity = addressElements.stream().filter(element -> element.attr("href").contains("region")).map(Element::text).toArray(String[]::new);
        String regionFromAd = regionAndCity[0]; //регион
        String cityFromAd = (regionAndCity.length > 1) ? regionAndCity[1] : regionFromAd; // город
        //Если в городе есть более крупное деление, чем на районы (например на округа), то district будет 2 шт.
        String[] districts = addressElements.stream().filter(element -> element.attr("href").contains("district")).map(Element::text).toArray(String[]::new);
        StringJoiner stringJoiner = new StringJoiner(", ");
        Arrays.stream(districts).forEach(stringJoiner::add);
        String districtFromAd = stringJoiner.toString();
        String streetFromAd = addressElements.stream().filter(element -> element.attr("href").contains("street")).map(Element::text).findFirst().orElse("улица не указана");

        return new CianAddress("Россия", regionFromAd, cityFromAd, districtFromAd, streetFromAd);
    }

    private String getQuantity(Element adTag) {

        Element adTitle = adTag.select("span[data-mark$=OfferTitle]").first();
        Element adSubtitle = adTag.select("span[data-mark$=OfferSubtitle]").first();

        Pattern roomsAmountPattern = Pattern.compile("[0-9]-комн.");
        Matcher matcherRoomsInTitle = roomsAmountPattern.matcher(adTitle.text());
        Matcher matcherRoomsInSubtitle = null;
        if (adSubtitle != null) {
            matcherRoomsInSubtitle = roomsAmountPattern.matcher(adSubtitle.text());
        }
        String roomsAmount;
        if (matcherRoomsInTitle.find()) {
            roomsAmount = matcherRoomsInTitle.group().substring(0, matcherRoomsInTitle.group().indexOf("-"));
        } else if (matcherRoomsInSubtitle != null && matcherRoomsInSubtitle.find()) {
            roomsAmount = matcherRoomsInSubtitle.group().substring(0, matcherRoomsInSubtitle.group().indexOf("-"));
        } else {
            roomsAmount = "0";
        }

        return roomsAmount;
    }

    private Float getQuadrature(Element adTag) {

        Pattern squarePattern = Pattern.compile("\\d{1,4},?\\d{1,2}?\\sм²");
        Matcher matcherSquareInTitle = squarePattern.matcher(getTitle(adTag));
        String square;
        if (matcherSquareInTitle.find()) {
            square = matcherSquareInTitle.group();
        } else {
            throw new SquareInfoNotFoundException("Apartment's square hasn't been found on the cian.ru page");
        }
        String squareWithoutMetersAndRounded = String.valueOf(Float.parseFloat(square.replaceAll("\\sм²", "").replaceAll(",", ".")));
        return Float.parseFloat(squareWithoutMetersAndRounded);

    }

    private Boolean getPeriod(Element adTag) {
        Element adPriceContent = adTag.select("span[data-mark$=MainPrice]").first();
        Pattern periodPattern = Pattern.compile("[ЁёА-я]{3,5}", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcherPeriod = periodPattern.matcher(adPriceContent.text());
        boolean period;
        if (matcherPeriod.find()) {
            period = matcherPeriod.group().equals("сутки");
        } else {
            throw new PeriodNotFoundException("Period hasn't been found on the cian.ru page");
        }
        return period;
    }

    private BigDecimal getPrice(Element adTag) {
        Element adPriceContent = adTag.select("span[data-mark$=MainPrice]").first();

        Pattern pricePattern = Pattern.compile("^[\\d\\s]+\\s₽");
        Matcher matcherPrice = pricePattern.matcher(adPriceContent.text());
        BigDecimal price;
        if (matcherPrice.find()) {
            price = new BigDecimal(matcherPrice.group().substring(0, matcherPrice.group().lastIndexOf(" ")).replaceAll("\\s", ""));
        } else {
            throw new PriceNotFoundException("Price hasn't been found on the cian.ru page");
        }
        return price;
    }

    private String getTitle(Element adTag) {

        Element adTitle = adTag.select("span[data-mark$=OfferTitle]").first();
        Element adSubtitle = adTag.select("span[data-mark$=OfferSubtitle]").first();

        return adTitle.text() + " " + (adSubtitle != null ? adSubtitle.text() : "");
    }

    private String getDescription(Element adTag) {
        Element adDescription = adTag.select("div[class~=\\S*description\\S*]").first();
        return adDescription.text();
    }

    private String getLink(Element adTag) {
        Element adLinkTag = adTag.selectFirst("div[data-name$=LinkArea]");
        return adLinkTag.selectFirst("a").attr("href");
    }

    private int getFloor(Element adTag) {
        Pattern floorPattern = Pattern.compile("\\d{1,2}/\\d{1,2}");
        Matcher floorMatcher = floorPattern.matcher(getTitle(adTag));
        int floor = 0;
        if (floorMatcher.find()) {
            floor = Integer.parseInt(floorMatcher.group().substring(0, floorMatcher.group().indexOf("/")));
        } else {
            throw new FloorNotFoundException("Floor hasn't been found on the page");
        }
        return floor;
    }
}
