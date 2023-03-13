package com.example.tclocalpulsar;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class CommonConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
