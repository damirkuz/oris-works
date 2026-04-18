package ru.kuzdikenov.service.impl;

import org.junit.jupiter.api.Test;
import ru.kuzdikenov.repository.inmemory.MetricRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MetricServiceTest {

    @Test
    void getStatisticsAggregatesSuccessAndFailure() {
        MetricRepository metricRepository = new MetricRepository();
        metricRepository.incrementSuccess("HelloService.sayHello");
        metricRepository.incrementSuccess("HelloService.sayHello");
        metricRepository.incrementFailure("HelloService.sayHello");
        MetricService metricService = new MetricService(metricRepository);

        MetricService.MetricStats stats = metricService.getStatistics().get("HelloService.sayHello");

        assertEquals(2L, stats.successCount());
        assertEquals(1L, stats.failureCount());
    }
}
