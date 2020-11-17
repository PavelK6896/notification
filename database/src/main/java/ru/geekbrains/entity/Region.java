package ru.geekbrains.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "regions", schema = "geo")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    String name;

    @ManyToOne
    @JoinColumn(name = "country_id")
    Country country;

    @OneToMany(mappedBy = "region")
    @Cascade(CascadeType.ALL)
    @JsonIgnore
    List<City> cities;
}
