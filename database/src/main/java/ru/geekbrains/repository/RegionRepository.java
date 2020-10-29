package ru.geekbrains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.geekbrains.entity.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
}
