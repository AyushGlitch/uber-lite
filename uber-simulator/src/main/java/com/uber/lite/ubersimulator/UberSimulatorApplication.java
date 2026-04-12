package com.uber.lite.ubersimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UberSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(UberSimulatorApplication.class, args);
    }

}
