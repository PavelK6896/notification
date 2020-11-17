package ru.geekbrains.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "countries", schema = "geo")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    String name;

    @OneToMany(mappedBy = "country")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JsonIgnore
    List<Region> regions;
}
