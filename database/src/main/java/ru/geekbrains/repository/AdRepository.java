package ru.geekbrains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.geekbrains.entity.Ad;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long>, JpaSpecificationExecutor<Ad> {
    Optional<Ad> findByLink(String link);
    long deleteAdsByUpdatedAtLessThan(OffsetDateTime updatedAt);
}
