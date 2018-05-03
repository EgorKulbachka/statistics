package de.challenge.test;

import de.challenge.stats.Launcher;
import de.challenge.stats.service.TransactionStatisticsService;
import lombok.Data;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Launcher.class)
public class StatisticsIntegrationTest {

    @Autowired
    private TestRestTemplate webClient;

    @Autowired
    private TransactionStatisticsService statisticsService;

    @Before
    public void setUp() {
        statisticsService.clearStatisticsOlderThan(Instant.now().plusSeconds(30L));
    }

    @Test
    public void statisticsShouldBeReturned() {
        ResponseEntity<StatisticsResponse> response = webClient.getForEntity("/statistics", StatisticsResponse.class);

        Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
        Assert.assertEquals(BigDecimal.ZERO, response.getBody().getAvg());
        Assert.assertEquals(BigDecimal.ZERO, response.getBody().getSum());
        Assert.assertEquals(BigDecimal.ZERO, response.getBody().getMin());
        Assert.assertEquals(BigDecimal.ZERO, response.getBody().getMax());
        Assert.assertEquals(0L, response.getBody().getCount());
    }

    @Test
    public void outdatedTransactionsShouldBeOmitted() {
        ResponseEntity transactionAddResponse = addTransaction(Instant.now().minusSeconds(61L), 50.33);

        Assert.assertEquals(HttpStatus.NO_CONTENT, transactionAddResponse.getStatusCode());

        ResponseEntity<StatisticsResponse> statsResponse = webClient.getForEntity("/statistics", StatisticsResponse.class);

        Assert.assertTrue(statsResponse.getStatusCode().is2xxSuccessful());
        Assert.assertEquals(BigDecimal.ZERO, statsResponse.getBody().getAvg());
        Assert.assertEquals(BigDecimal.ZERO, statsResponse.getBody().getSum());
        Assert.assertEquals(BigDecimal.ZERO, statsResponse.getBody().getMin());
        Assert.assertEquals(BigDecimal.ZERO, statsResponse.getBody().getMax());
        Assert.assertEquals(0L, statsResponse.getBody().getCount());
    }

    @Test
    public void statisticsShouldBeCalculatedForTransactionsSent() {

        Assert.assertEquals(HttpStatus.CREATED, addTransaction(Instant.now().minusSeconds(32L), 52.12).getStatusCode());
        Assert.assertEquals(HttpStatus.CREATED, addTransaction(Instant.now(), 500).getStatusCode());

        ResponseEntity<StatisticsResponse> statsResponse = webClient.getForEntity("/statistics", StatisticsResponse.class);

        Assert.assertTrue(statsResponse.getStatusCode().is2xxSuccessful());
        Assert.assertEquals(BigDecimal.valueOf(276.06), statsResponse.getBody().getAvg());
        Assert.assertEquals(BigDecimal.valueOf(552.12), statsResponse.getBody().getSum());
        Assert.assertEquals(BigDecimal.valueOf(52.12), statsResponse.getBody().getMin());
        Assert.assertEquals(BigDecimal.valueOf(500.0), statsResponse.getBody().getMax());
        Assert.assertEquals(2L, statsResponse.getBody().getCount());
    }

    private ResponseEntity<?> addTransaction(Instant time, double amount) {
        Map<String, Object> request = new HashMap<>();
        request.put("timestamp", time.toEpochMilli());
        request.put("amount", amount);
        return webClient.postForEntity("/transactions", request, String.class);
    }


    @Data
    private static final class StatisticsResponse {
        BigDecimal avg;
        BigDecimal sum;
        BigDecimal min;
        BigDecimal max;
        long count;
    }
}
