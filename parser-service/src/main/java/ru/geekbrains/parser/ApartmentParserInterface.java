package ru.geekbrains.parser;

import java.math.BigDecimal;

public interface ApartmentParserInterface {


    public String getTitle();

    public String getDescription();

    public BigDecimal getPrice();

    public String getRooms();

    public Float getArea();

    public Float getAreaKitchen();

    public Float getAreaLiving();

    public Integer getFloor();

    public String getLink();

    public String getCountry();

    public String getRegion();

    public String getCity();

    public String getDistrict();

    public String getStreet();

    public String getHouse();

    public Short getFloorsCount();

    public Boolean getIsDaily();
}
