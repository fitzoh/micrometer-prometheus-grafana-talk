package com.github.fitzoh.monitoring.logging;

import net.logstash.logback.argument.StructuredArguments;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("json")
@Component
public class AwesomeJsonLoggingExample implements CommandLineRunner {

    private static Logger log = LoggerFactory.getLogger(AwesomeJsonLoggingExample.class);

    /**
     * {"@timestamp":"2019-02-23T10:01:42.277-05:00","@version":"1","message":"doing a thing structured_value=0","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"0","marker_value":0,"structured_value":0}
     * {"@timestamp":"2019-02-23T10:01:42.289-05:00","@version":"1","message":"doing a thing structured_value=1","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"1","marker_value":1,"structured_value":1}
     * {"@timestamp":"2019-02-23T10:01:42.289-05:00","@version":"1","message":"doing a thing structured_value=2","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"2","marker_value":2,"structured_value":2}
     * {"@timestamp":"2019-02-23T10:01:42.290-05:00","@version":"1","message":"doing a thing structured_value=3","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"3","marker_value":3,"structured_value":3}
     * {"@timestamp":"2019-02-23T10:01:42.290-05:00","@version":"1","message":"doing a thing structured_value=4","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"4","marker_value":4,"structured_value":4}
     * {"@timestamp":"2019-02-23T10:01:42.290-05:00","@version":"1","message":"doing a thing structured_value=5","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"5","marker_value":5,"structured_value":5}
     * {"@timestamp":"2019-02-23T10:01:42.290-05:00","@version":"1","message":"doing a thing structured_value=6","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"6","marker_value":6,"structured_value":6}
     * {"@timestamp":"2019-02-23T10:01:42.290-05:00","@version":"1","message":"doing a thing structured_value=7","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"7","marker_value":7,"structured_value":7}
     * {"@timestamp":"2019-02-23T10:01:42.290-05:00","@version":"1","message":"doing a thing structured_value=8","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"8","marker_value":8,"structured_value":8}
     * {"@timestamp":"2019-02-23T10:01:42.292-05:00","@version":"1","message":"doing a thing structured_value=9","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"9","marker_value":9,"structured_value":9}
     *
     * {
     *   "@timestamp": "2019-02-23T10:01:42.277-05:00",
     *   "@version": "1",
     *   "message": "doing a thing structured_value=0",
     *   "logger_name": "com.github.fitzoh.loggingexample.AwesomeJsonLogExample",
     *   "thread_name": "main",
     *   "level": "INFO",
     *   "level_value": 20000,
     *   "mdc_value": "0",
     *   "marker_value": 0,
     *   "structured_value": 0
     * }
     */
    @Override
    public void run(String... args) {
        for (int i = 0; i < 10; i++) {
            MDC.put("mdc_value", String.valueOf(i));
            log.info(Markers.append("marker_value", i), "doing a thing {}", StructuredArguments.keyValue("structured_value", i));
        }
    }
}
