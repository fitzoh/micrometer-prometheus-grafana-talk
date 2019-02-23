package com.github.fitzoh.loggingexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Profile("!json")
@Component
public class SuperBoringPlainLogExample implements CommandLineRunner {

    private static Logger log = LoggerFactory.getLogger(SuperBoringPlainLogExample.class);

    /**
     * 2019-02-23 10:01:03.118  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 0
     * 2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 1
     * 2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 2
     * 2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 3
     * 2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 4
     * 2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 5
     * 2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 6
     * 2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 7
     * 2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 8
     * 2019-02-23 10:01:03.119  INFO 7777 --- [           main] c.g.f.l.SuperBoringPlainLogExample       : doing a thing 9
     */
    @Override
    public void run(String... args) {
        for (int i = 0; i < 10; i++) {
            log.info("doing a thing {}", i);
        }
    }
}
