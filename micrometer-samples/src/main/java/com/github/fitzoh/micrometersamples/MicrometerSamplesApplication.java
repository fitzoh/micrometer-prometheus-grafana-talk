package com.github.fitzoh.micrometersamples;

import io.micrometer.core.instrument.*;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class MicrometerSamplesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicrometerSamplesApplication.class, args);
    }


    public void doSomething() {

    }


    public void counterExample() {
        MeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        Counter counter = registry.counter("my.counter", "tagKey", "tagValue");
        counter = Counter.builder("my.counter")
                .baseUnit("things")
                .description("count some things")
                .tags("tagKey", "tagValue")
                .register(registry);

        counter.increment();
        counter.increment(5);

        System.out.println("total count is " + counter.count());
    }


    public void gaugeExample() {
        MeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        //Supplier<Number>
        Gauge.builder("my.gague", () -> 5)
                .description("a constant gauge")
                .baseUnit("things")
                .tag("tagKey", "tagValue")
                .register(registry);

        //auto-gauge for any Number
        AtomicInteger value = registry.gauge("my.gauge", new AtomicInteger(0));

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

        //combination of T and ToDoubleFunction<T>
        registry.gauge("my.gauge", executor, ThreadPoolExecutor::getActiveCount);
    }

    public void timerExample() {
        MeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        Timer timer = registry.timer("my.timer", "tagKey", "tagValue");

        timer = Timer.builder("my.timer")
                .description("time some things")
                .tags("tagKey", "tagValue")
                .register(registry);


        LongTaskTimer whatever = LongTaskTimer.builder("whatever")
                .register(registry);


        //Runnable () -> Void
        timer.record(() -> System.out.println("doing work"));
        //Consumer () -> T
        int result = timer.record(() -> 5);

        //manually record time
        timer.record(1234, TimeUnit.MILLISECONDS);
        timer.record(Duration.ofMillis(1234));


        System.out.println("total time in seconds: " + timer.totalTime(TimeUnit.SECONDS));
    }

    public void longTaskTimerExample() {
        MeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        LongTaskTimer ltTimer = LongTaskTimer.builder("my.long.timer")
                .description("time some long things")
                .tag("tagKey", "tagValue")
                .register(registry);

        LongTaskTimer.Sample sample = ltTimer.start();
        //do some work
        sample.stop();

        //Runnable () -> Void
        ltTimer.record(() -> System.out.println("doing work"));
        //Consumer () -> T
        int result = ltTimer.record(() -> 5);


        System.out.println("total time in seconds: " + ltTimer.duration(TimeUnit.SECONDS));
        System.out.println("number of active tasks: " + ltTimer.activeTasks());
    }


    public void distributionSummaryExample() {
        MeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        DistributionSummary summary = registry.summary("my.summary", "tagKey", "tagValue");
        summary = DistributionSummary.builder("my.summary")
                .description("summarize some things")
                .baseUnit("things")
                .register(registry);

        summary.record(5);
        summary.record(10);

        System.out.println("count: " + summary.count());
        System.out.println("total: " + summary.totalAmount());
        System.out.println("mean: " + summary.mean());
        System.out.println("max: " + summary.max());
    }

}
