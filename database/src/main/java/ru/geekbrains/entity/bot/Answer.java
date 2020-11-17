package ru.geekbrains.entity.bot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "answers", schema = "bot")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    long id;

    @Column(name = "country")
    String country;

    @Column(name = "city")
    String city;

    @Column(name = "rooms")
    String rooms;

    @Column(name = "min_price")
    long minPrice;

    @Column(name = "max_price")
    long maxPrice;

    @Column(name = "floor")
    String floor;

    @Override
    public String toString() {
        return  "\nСтрана: " + country +
                "\nГород: " + city +
                "\nКоличество комнат: " + rooms +
                "\nЦена: {" + minPrice + " - " + maxPrice + "}" +
                "\nЭтаж: " + floor;
    }
}
