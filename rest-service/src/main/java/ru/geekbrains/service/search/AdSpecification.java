package ru.geekbrains.service.search;

import org.springframework.data.jpa.domain.Specification;
import ru.geekbrains.entity.Ad;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;

public class AdSpecification {
    public static Specification<Ad> trueLiteral(){
        return (root, query, builder) -> builder.isTrue(builder.literal(true));
    }

    public static Specification<Ad> containsCountry(String name){
        return (root, query, builder) -> builder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Ad> priceGreaterThanEqual(Long price){
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("price"), price);
    }
    public static Specification<Ad> priceLessThanEqual(Long price){
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("price"), price);
    }

    public static Specification<Ad> roomIs(String room){
        return (root, query, builder) -> builder.equal(root.get("rooms"), room);
    }

    public static Specification<Ad> cityIs(String city){
        return (root, query, builder) -> builder.equal(root.get("address").get("street").get("city").get("name"), city);
    }

    public static Specification<Ad> countryIs(String country){
        return (root, query, builder) -> builder.equal(root.get("address").get("street").get("city").get("country").get("name"), country);
    }

    public static Specification<Ad> floorIs(String floor){
        return (root, query, builder) -> builder.equal(root.get("floor"), floor);
    }

}
