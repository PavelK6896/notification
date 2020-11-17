package ru.geekbrains.ui.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainSettingDto {

    @JsonProperty("whatsApp")
    Boolean whatsApp;
    @JsonProperty("facebook")
    Boolean facebook;
    @JsonProperty("telegram")
    Boolean telegram;
    @JsonProperty("email")
    Boolean email;
    @JsonProperty("search")
    String search;
    @JsonProperty("priceFrom")
    String priceFrom;
    @JsonProperty("priceTo")
    String priceTo;
    @JsonProperty("one")
    Boolean one;
    @JsonProperty("two")
    Boolean two;
    @JsonProperty("three")
    Boolean three;

}
