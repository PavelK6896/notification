package ru.geekbrains.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Data
@Entity
@Table(name = "addresses", schema = "geo")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "house")
    String house;

    @Column(name = "floors_count")
    Short floorsCount;

    @ManyToOne
    @JoinColumn(name = "street_id")
    Street street;

    public String getAddressToString() {
        return String.format("%s, %s, %s, %s",street.getCity().getCountry().getName(), street.getCity().getName(), street.getDistrict().getName(), street.getName());
    }
}