package com.techiethoughts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Rajesh Bandarupalli
 *
 */

@SpringBootApplication
@ComponentScan(basePackages = {"com.techiethoughts.config"})
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}

