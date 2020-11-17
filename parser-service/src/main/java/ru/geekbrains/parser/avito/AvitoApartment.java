package ru.geekbrains.parser.avito;

import ru.geekbrains.parser.ApartmentParserInterface;

import java.math.BigDecimal;

public class AvitoApartment implements ApartmentParserInterface {

    private Integer id;

    private String url;

    private String description;

    private BigDecimal price;

    private String city;

    private String address;

    //Краткое описаие пример "1-к квартира, 38.8 м², 2/14 эт."
    private String infoDescription;

    //продам/аренда итд
    private String offerType;

    private Float areaKitchen;

    private Float areaLive;

    private Float area;

    private int floor;

    private int floorsCount;

    //Монолитный
    private String houseType;

    //новостройка
    private String type;

    //Студия/1/2/3/4 итд
    private String rooms;

    private String country;

    private String region;

    private String district;

    private String street;

    private String house;

    private Boolean isDaily;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return infoDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getInfoDescription() {
        return infoDescription;
    }

    public void setInfoDescription(String infoDescription) {
        this.infoDescription = infoDescription;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null) {
            this.address = address;
            return;
        }
        String[] addressParts = address.split(",", 3);
        if (addressParts.length > 1) {
            this.setStreet(addressParts[0]);
            this.setHouse(addressParts[1]);
        }
        this.address = address;
    }


    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public Float getAreaKitchen() {
        return areaKitchen;
    }

    public void setAreaKitchen(Float areaKitchen) {
        this.areaKitchen = areaKitchen;
    }

    public Float getArea() {
        return area;
    }

    public void setArea(Float area) {
        this.area = area;
    }

    public Float getAreaLive() {
        return areaLive;
    }

    public void setAreaLive(Float areaLive) {
        this.areaLive = areaLive;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public Short getFloorsCount() {
        return (short) floorsCount;
    }

    public void setFloorsCount(int floorsCount) {
        this.floorsCount = floorsCount;
    }

    public String getHouseType() {
        return houseType;
    }

    public void setHouseType(String houseType) {
        this.houseType = houseType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public boolean isEmpty() {

        return this.id == null || this.infoDescription.isEmpty() || this.price == null;
    }

    @Override
    public Float getAreaLiving() {
        return areaLive;
    }

    @Override
    public String getLink() {
        return url;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public String getRegion() {
        return region;
    }

    @Override
    public String getDistrict() {
        return district;
    }

    @Override
    public String getStreet() {
        return street;
    }

    @Override
    public String getHouse() {
        return house;
    }

    @Override
    public Boolean getIsDaily() {
        return null;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setDistrict(String district) {

        if (district == null) {
            this.district = district;
        } else {
            district = district.replaceFirst("р-н", "");
            this.district = district.trim();
        }
    }

    public void setStreet(String street) {
        street = street.replaceFirst("ул.", "");
        this.street = street.trim();
    }

    public void setHouse(String house) {
        house = house.replaceFirst("д.", "");
        house = house.replaceFirst("стр.", "");
        this.house = house.trim();
    }

    public void setDaily(Boolean daily) {
        isDaily = daily;
    }
}
