package ru.geekbrains.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.geekbrains.service.CleanerService;

@Configuration
@EnableScheduling
@Slf4j
@Data
@RequiredArgsConstructor
public class SpringConfig {

    private final CleanerService cleanerService;

    @Value("${cleaner.delete.older-than:10}")
    private int olderThanValue;

    @Scheduled(cron = "${cleaner.start}")
//    @Scheduled(fixedDelay = 10000)
    public void scheduleDeleteAdsFromBD () {
        cleanerService.deleteAllAdsOlderThan(olderThanValue);
    }

}
