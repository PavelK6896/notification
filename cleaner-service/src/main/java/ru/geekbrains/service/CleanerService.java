package ru.geekbrains.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.repository.AdRepository;

import java.time.OffsetDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CleanerService {

    private final AdRepository adRepository;

    @Transactional
    public void deleteAllAdsOlderThan (int olderThanValue){
        log.info(String.format("Start cleaning process. Remove all ads older than = %s day(s)", olderThanValue));

        OffsetDateTime date = OffsetDateTime.now();
        long numberOfItemsRemoved = adRepository.deleteAdsByUpdatedAtLessThan(date.minusDays(olderThanValue));

        log.info(String.format("End cleaning process. Number of items removed = %s", numberOfItemsRemoved));
    }
}
