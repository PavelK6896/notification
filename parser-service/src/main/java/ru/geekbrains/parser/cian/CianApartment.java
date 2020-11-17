package ru.geekbrains.parser.cian;

import ru.geekbrains.parser.ApartmentParserInterface;

import java.math.BigDecimal;

public class CianApartment implements ApartmentParserInterface {

    private Integer id;

    private String url;

    private String description;

    private BigDecimal price;

    private String city;

    private String address;

    private String infoDescription;

    private String offerType;

    private Float areaKitchen;

    private Float areaLive;

    private Float area;

    private int floor;

    private int floorsCount;

    private String houseType;

    private String type;

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
    public Boolean getIsDaily() {return isDaily;}

    public void setCountry(String country) {
        this.country = country;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public void setIsDaily(Boolean isDaily) {
        this.isDaily = isDaily;
    }
}
