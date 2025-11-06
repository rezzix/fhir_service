package net.rezzix.fhirservice.controller;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    private final Tracer tracer;
    private final LongCounter testCounter;

    public TestController(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer(TestController.class.getName());
        Meter meter = openTelemetry.getMeter(TestController.class.getName());
        this.testCounter = meter.counterBuilder("test.requests.count").setDescription("Counts the number of test requests").build();
    }

    @GetMapping("/hello")
    public String hello() {
        Span span = tracer.spanBuilder("hello-endpoint").startSpan();
        try {
            testCounter.add(1);
            return "Hello, OpenTelemetry!";
        } finally {
            span.end();
        }
    }
}