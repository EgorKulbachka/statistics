package de.challenge.stats.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.ZERO;

@Value
@Builder
public class Statistics {

    public static final Statistics EMPTY = Statistics.builder().min(ZERO).max(ZERO).sum(ZERO).count(0).build();

    BigDecimal sum;
    BigDecimal max;
    BigDecimal min;
    long count;

    public BigDecimal getAvg() {
        return count == 0L
                ? BigDecimal.ZERO
                : sum.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP);
    }

}
