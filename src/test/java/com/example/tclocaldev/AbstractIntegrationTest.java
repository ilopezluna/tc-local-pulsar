package com.example.tclocaldev;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import reactor.blockhound.BlockHound;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public class AbstractIntegrationTest {
    static {
        BlockHound
            .builder()
            // fine to have blocking calls during the startup
            .allowBlockingCallsInside("io.r2dbc.postgresql.client.StartupMessageFlow", "exchange")
            .install();
    }

    @Autowired
    public WebTestClient webTestClient;

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        static DockerImageName POSTGRESQL_IMAGE = DockerImageName.parse("postgres:14-alpine");
        static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>(POSTGRESQL_IMAGE).withReuse(true);

        public static Map<String, Object> getProperties() {
            pg.start();

            return Map.of(
                "spring.r2dbc.url",
                "r2dbc:postgresql://%s:%d/%s".formatted(pg.getHost(), pg.getMappedPort(5432), pg.getDatabaseName()),
                "spring.flyway.url",
                "jdbc:postgresql://%s:%d/%s".formatted(pg.getHost(), pg.getMappedPort(5432), pg.getDatabaseName()),
                "spring.r2dbc.username",
                pg.getUsername(),
                "spring.r2dbc.password",
                pg.getPassword()
            );
        }

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            var env = context.getEnvironment();
            env.getPropertySources().addFirst(new MapPropertySource("testcontainers", getProperties()));
        }
    }
}
