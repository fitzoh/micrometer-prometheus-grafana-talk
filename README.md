# Application monitoring with Micrometer, Prometheus, and Grafana

Companion code for the (hopefully somewhat useful) presentation you just sat through!

Feedback appreciated, @fitzoh on Twitter.

### Links

[Slides](https://docs.google.com/presentation/d/14Z23SLsCwZFDXOOFAcCoDAXf8LCmWbv99b9qXiQtRr0)

[Micrometer](https://micrometer.io/)
* [Micrometer slack inviter](http://slack.micrometer.io/)

[Prometheus](https://prometheus.io/)
* [PromQL cheat sheet](https://timber.io/blog/promql-for-humans/)
* [Prometheus maintainer's blog](https://www.robustperception.io/blog)

[Grafana](https://grafana.com)
* [Grafana plugins](https://grafana.com/plugins)


[Logstash Logback Encoder](https://github.com/logstash/logstash-logback-encoder)

[USE/RED methods](https://www.vividcortex.com/blog/monitoring-and-observability-with-use-and-red)

[3 pillars of observability (take your pick)](https://www.google.com/search?q=3+pillars+of+observability)

[Slide code snippets generated via Carbon](https://carbon.now.sh)


### Demo

Instructions assume a *NIX machine, sorry windows people.  Some steps block, use multiple terminals where necessary. Prometheus and Grafana require Docker.

###### Spring Boot Apps

The demo assumes two instances of the Spring Boot service are running.
Start them like this:
* `./gradlew bootRun`
* `SPRING_PROFILES_ACTIVE=slow ./gradlew bootRun`

They'll run on ports `8080` and `8081`.

###### Prometheus

Start Prometheus via `./gradlew startPrometheus`.  You can view the UI at port `9090`. The config file is `prometheus.yml`.

###### Grafana

Start Grafana via `./gradlew startGrafana`.  You can view the UI at port `3000` (log in with admin:grafana).  State is persisted through a volume in the `grafana` directory.


### Logging

The `src/main/java/com/github/fitzoh/monitoring/logging` directory contains a simple example to show the difference between default [Spring Boot](https://spring.io/projects/spring-boot) logging settings and structured JSON logging using the [Logstash Logback Encoder](https://github.com/logstash/logstash-logback-encoder#standard-fields).

On application startup (`./gradlew bootrun`), the default logger outputs something that looks like this:

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

If you run it with the json logger enabled (`./gradlew bootRun --args='--spring.profiles.active=json'`): 

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

[Promethenus Output](http://localhost:8080/actuator/prometheus)

Create traffice using the application end points:
* (http://localhost:8080/random) or (http://localhost:8081/random)
* (http://localhost:8080/also-random) or (http://localhost:8081/also-random)
* (http://localhost:8080/yet-another/random) or (http://localhost:8081/yet-another/random)
