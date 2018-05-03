package de.challenge.stats.service;

import de.challenge.stats.model.Statistics;
import de.challenge.stats.model.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static java.math.BigDecimal.ZERO;

public class TransactionStatisticsServiceTest {

    Instant baseline = Instant.now();
    TransactionStatisticsService service = new TransactionStatisticsService();

    @Test
    public void transactionShouldBeAddedToAverage() {
        service.addTransaction(new Transaction(BigDecimal.valueOf(32.2), baseline));

        Statistics result = service.getStatisticsSince(baseline.minusSeconds(30L));

        Assert.assertEquals(BigDecimal.valueOf(32.2), result.getAvg());
        Assert.assertEquals(BigDecimal.valueOf(32.2), result.getSum());
        Assert.assertEquals(BigDecimal.valueOf(32.2), result.getMax());
        Assert.assertEquals(BigDecimal.valueOf(32.2), result.getMin());
        Assert.assertEquals(1, result.getCount());
    }

    @Test
    public void whenNoTransactionsReturnEmptyStats() {
        Statistics result = service.getStatisticsSince(Instant.EPOCH);

        Assert.assertNotNull(result);
        Assert.assertEquals(ZERO, result.getAvg());
        Assert.assertEquals(ZERO, result.getSum());
        Assert.assertEquals(ZERO, result.getMax());
        Assert.assertEquals(ZERO, result.getMin());
        Assert.assertEquals(0, result.getCount());
    }

    @Test
    public void statisticsShouldBeAggregatedForMultipleTransactions() {
        service.addTransaction(new Transaction(BigDecimal.valueOf(32.2), baseline));
        service.addTransaction(new Transaction(BigDecimal.valueOf(99.28), baseline));

        Statistics result = service.getStatisticsSince(Instant.EPOCH);

        Assert.assertEquals(BigDecimal.valueOf(65.74), result.getAvg());
        Assert.assertEquals(BigDecimal.valueOf(131.48), result.getSum());
        Assert.assertEquals(BigDecimal.valueOf(99.28), result.getMax());
        Assert.assertEquals(BigDecimal.valueOf(32.2), result.getMin());
        Assert.assertEquals(2, result.getCount());
    }

    @Test
    public void transactionsOlderThanRequestedTimestampShouldBeIgnored() {
        service.addTransaction(new Transaction(BigDecimal.valueOf(32.2), baseline.minusSeconds(60L)));
        service.addTransaction(new Transaction(BigDecimal.valueOf(99.28), baseline.minusSeconds(62L)));
        service.addTransaction(new Transaction(BigDecimal.valueOf(99.28), baseline.minusSeconds(61L)));

        Statistics result = service.getStatisticsSince(baseline.minusSeconds(60L));

        Assert.assertEquals(BigDecimal.valueOf(32.2), result.getAvg());
        Assert.assertEquals(BigDecimal.valueOf(32.2), result.getSum());
        Assert.assertEquals(BigDecimal.valueOf(32.2), result.getMax());
        Assert.assertEquals(BigDecimal.valueOf(32.2), result.getMin());
        Assert.assertEquals(1, result.getCount());
    }

    @Test
    public void statisticsShouldBeClearedByTimestamp() {
        service.addTransaction(new Transaction(BigDecimal.valueOf(32.2), baseline));
        service.addTransaction(new Transaction(BigDecimal.valueOf(99.28), baseline.minusSeconds(25L)));
        service.addTransaction(new Transaction(BigDecimal.valueOf(100), baseline.minusSeconds(59L)));

        service.clearStatisticsOlderThan(baseline.minusSeconds(30L));
        Statistics result = service.getStatisticsSince(Instant.EPOCH);

        Assert.assertEquals(BigDecimal.valueOf(65.74), result.getAvg());
        Assert.assertEquals(BigDecimal.valueOf(131.48), result.getSum());
        Assert.assertEquals(BigDecimal.valueOf(99.28), result.getMax());
        Assert.assertEquals(BigDecimal.valueOf(32.2), result.getMin());
        Assert.assertEquals(2, result.getCount());
    }

}
