package de.challenge.stats.service;

import de.challenge.stats.utils.ApplicationConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsCleanupTrigger {

    private static final long CLEANUP_PERIOD_MILLISECONDS = ApplicationConstants.STATISTICS_PERIOD_IN_SECONDS * 1000 / 3;

    private final TransactionStatisticsService statisticsService;

    @Scheduled(initialDelay = CLEANUP_PERIOD_MILLISECONDS, fixedDelay = CLEANUP_PERIOD_MILLISECONDS)
    public void launchCleanup() {
        log.debug("Launching statistics cleanup");
        Instant statisticsExpirationTimestamp = ApplicationConstants.getOldestTimestampForStatistics();
        statisticsService.clearStatisticsOlderThan(statisticsExpirationTimestamp);
    }

}

