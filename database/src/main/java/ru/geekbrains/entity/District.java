package ru.geekbrains.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "districts", schema = "geo")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class District {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    String name;

    @ManyToOne
    @JoinColumn(name = "city_id")
    City city;

    @OneToMany(mappedBy = "district")
    @Cascade(CascadeType.ALL)
    @JsonIgnore
    List<Street> streets;
}
