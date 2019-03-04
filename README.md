# Logging

A simple example to show the difference between default [Spring Boot](https://spring.io/projects/spring-boot) logging settings and structured JSON logging using the [Logstash Logback Encoder](https://github.com/logstash/logstash-logback-encoder#standard-fields).


To run with the default logger: `./gradlew bootrun`

output:
```
2019-02-23 10:01:03.118  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 0
2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 1
2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 2
2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 3
2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 4
2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 5
2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 6
2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 7
2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 8
2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 9
```

To run with the json logger: `./gradlew bootRun --args='--spring.profiles.active=json'`

output:
```
{"@timestamp":"2019-02-23T10:01:42.277-05:00","@version":"1","message":"doing a thing structured_value=0","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"0","marker_value":0,"structured_value":0}
{"@timestamp":"2019-02-23T10:01:42.289-05:00","@version":"1","message":"doing a thing structured_value=1","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"1","marker_value":1,"structured_value":1}
{"@timestamp":"2019-02-23T10:01:42.289-05:00","@version":"1","message":"doing a thing structured_value=2","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"2","marker_value":2,"structured_value":2}
{"@timestamp":"2019-02-23T10:01:42.290-05:00","@version":"1","message":"doing a thing structured_value=3","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"3","marker_value":3,"structured_value":3}
{"@timestamp":"2019-02-23T10:01:42.290-05:00","@version":"1","message":"doing a thing structured_value=4","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"4","marker_value":4,"structured_value":4}
{"@timestamp":"2019-02-23T10:01:42.290-05:00","@version":"1","message":"doing a thing structured_value=5","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"5","marker_value":5,"structured_value":5}
{"@timestamp":"2019-02-23T10:01:42.290-05:00","@version":"1","message":"doing a thing structured_value=6","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"6","marker_value":6,"structured_value":6}
{"@timestamp":"2019-02-23T10:01:42.290-05:00","@version":"1","message":"doing a thing structured_value=7","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"7","marker_value":7,"structured_value":7}
{"@timestamp":"2019-02-23T10:01:42.290-05:00","@version":"1","message":"doing a thing structured_value=8","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"8","marker_value":8,"structured_value":8}
{"@timestamp":"2019-02-23T10:01:42.292-05:00","@version":"1","message":"doing a thing structured_value=9","logger_name":"com.github.fitzoh.loggingexample.AwesomeJsonLogExample","thread_name":"main","level":"INFO","level_value":20000,"mdc_value":"9","marker_value":9,"structured_value":9}
```

cleaned up with [jq](https://stedolan.github.io/jq/):
```
{
  "@timestamp": "2019-02-23T10:01:42.277-05:00",
  "@version": "1",
  "message": "doing a thing structured_value=0",
  "logger_name": "com.github.fitzoh.loggingexample.AwesomeJsonLogExample",
  "thread_name": "main",
  "level": "INFO",
  "level_value": 20000,
  "mdc_value": "0",
  "marker_value": 0,
  "structured_value": 0
}
```

Note that you won't generally log the same values using MDC, Markers, and structured arguments.
All three are included here for the sake of demonstrating available options.
