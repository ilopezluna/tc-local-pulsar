package com.example.tclocalpulsar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static final String TENANT = "tenant";
    public static final String NAMESPACE = TENANT + "/namespace";
    public static final String TOPIC = NAMESPACE + "/notes";

    public static void main(String[] args) {
        createSpringApplication().run(args);
    }

    public static SpringApplication createSpringApplication() {
        return new SpringApplication(Application.class);
    }
}
