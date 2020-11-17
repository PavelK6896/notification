package ru.geekbrains.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "apartments", schema = "advertisement")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "type")
    String type;

    @Column(name = "title")
    String title;

    @Column(name = "description")
    String description;

    @Column(name = "price")
    BigDecimal price;

    @Column(name = "rooms")
    String rooms;

    @Column(name = "area")
    Float area;

    @Column(name = "area_kitchen")
    Float areaKitchen;

    @Column(name = "area_living")
    Float areaLiving;

    @Column(name = "floor")
    Integer floor;

    @Column(name = "link")
    String link;

    @Column(name = "is_active")
    Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at")
    OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    OffsetDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "address_id")
    Address address;

    @Column(name = "is_daily", columnDefinition = "boolean default false")
    Boolean isDaily;
}