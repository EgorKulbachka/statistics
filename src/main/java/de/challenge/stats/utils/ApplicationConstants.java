package de.challenge.stats.utils;

import lombok.NoArgsConstructor;

import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ApplicationConstants {

    public static final long STATISTICS_PERIOD_IN_SECONDS = 60L;

    public static Instant getOldestTimestampForStatistics() {
        return Instant.now().minusSeconds(STATISTICS_PERIOD_IN_SECONDS);
    }
}
