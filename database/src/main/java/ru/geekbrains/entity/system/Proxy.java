package ru.geekbrains.entity.system;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "proxies", schema = "system")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Proxy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "address")
    String address;

    @Column(name = "login")
    String login;

    @Column(name = "password")
    String password;

    @Column(name = "active")
    Boolean active;

    @Column(name = "last_used", columnDefinition = "timestamp default current_timestamp")
    Timestamp lastUsed;

    @Column(name = "banned_by_avito", columnDefinition = "boolean default false")
    Boolean bannedByAvito;

    @Column(name = "banned_by_cian", columnDefinition = "boolean default false")
    Boolean bannedByCian;


    public String getHost() {
        return this.address.split(":")[0];
    }

    public String getPort() {
        return this.address.split(":")[1];
    }
}
