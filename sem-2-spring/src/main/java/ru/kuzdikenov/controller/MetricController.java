package ru.kuzdikenov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kuzdikenov.service.impl.BenchmarkService;
import ru.kuzdikenov.service.impl.MetricService;

import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MetricController {

    private final MetricService metricService;
    private final BenchmarkService benchmarkService;

    @GetMapping("/metrics")
    public Map<String, MetricService.MetricStats> getMetricStatistics() {
        return metricService.getStatistics();
    }

    @GetMapping("/benchmarks")
    public Map<String, BenchmarkService.BenchmarkStats> getBenchmarkStatistics() {
        return benchmarkService.getStatistics();
    }

    @GetMapping("/benchmarks/percentile")
    public BenchmarkService.PercentileStats getPercentile(
            @RequestParam String methodName,
            @RequestParam double percentile
    ) {
        return benchmarkService.getPercentile(methodName, percentile);
    }
}
