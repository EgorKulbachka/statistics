package de.challenge.stats.controller;

import de.challenge.stats.model.Statistics;
import de.challenge.stats.model.Transaction;
import de.challenge.stats.service.TransactionStatisticsService;
import de.challenge.stats.utils.ApplicationConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequiredArgsConstructor
public class StatisticsController {

    private final TransactionStatisticsService statisticsService;

    @RequestMapping(path = "/statistics", method = GET)
    public Statistics getStatistics() {
        Instant statisticsTimestamp = ApplicationConstants.getOldestTimestampForStatistics();

        return statisticsService.getStatisticsSince(statisticsTimestamp);
    }

    @RequestMapping(path = "/transactions", method = POST)
    public ResponseEntity<?> addTransaction(@RequestBody @Valid Transaction transaction) {
        Instant statisticsExpirationTimestamp = ApplicationConstants.getOldestTimestampForStatistics();

        if (transaction.getTimestamp().isBefore(statisticsExpirationTimestamp)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            statisticsService.addTransaction(transaction);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

}
