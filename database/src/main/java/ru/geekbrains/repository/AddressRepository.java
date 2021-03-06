package ru.geekbrains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.geekbrains.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
