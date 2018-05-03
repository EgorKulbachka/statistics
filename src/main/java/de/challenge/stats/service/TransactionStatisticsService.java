package de.challenge.stats.service;

import de.challenge.stats.model.Statistics;
import de.challenge.stats.model.Transaction;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Service
public class TransactionStatisticsService {

    private final ConcurrentSkipListMap<Long, Statistics> timestampStats = new ConcurrentSkipListMap<>();

    public void addTransaction(Transaction transaction) {
        long timestamp = transaction.getTimestamp().toEpochMilli();
        Statistics transactionStats = toStatistics(transaction);
        timestampStats.merge(timestamp, transactionStats, this::addStatistics);
    }

    public Statistics getStatisticsSince(Instant startTimestamp) {
        ConcurrentNavigableMap<Long, Statistics> stats = timestampStats.tailMap(startTimestamp.toEpochMilli());

        return stats.values().stream()
                .reduce(this::addStatistics)
                .orElse(Statistics.EMPTY);
    }

    public void clearStatisticsOlderThan(Instant timestamp) {
        timestampStats.headMap(timestamp.toEpochMilli())
                .clear();
    }

    private Statistics addStatistics(Statistics left, Statistics right) {
        Statistics.StatisticsBuilder newStatistics = Statistics.builder();

        newStatistics.max(left.getMax().max(right.getMax()));
        newStatistics.min(left.getMin().min(right.getMin()));

        newStatistics.count(left.getCount() + right.getCount());
        newStatistics.sum(left.getSum().add(right.getSum()));

        return newStatistics.build();
    }

    private Statistics toStatistics(Transaction transaction) {
        return Statistics.builder()
                .sum(transaction.getAmount())
                .min(transaction.getAmount())
                .max(transaction.getAmount())
                .count(1L)
                .build();
    }



}
