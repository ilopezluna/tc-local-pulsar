package com.example.tclocaldev.run;

import com.example.tclocaldev.AbstractIntegrationTest;
import com.example.tclocaldev.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@Import(Application.class)
public class TestApplication {

    public static void main(String[] args) {
        Hooks.onOperatorDebug();
        var application = Application.createSpringApplication();
        application.addInitializers(new AbstractIntegrationTest.Initializer());
        application.run(args);
    }
}
