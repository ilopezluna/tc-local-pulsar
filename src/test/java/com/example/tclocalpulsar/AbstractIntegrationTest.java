package com.example.tclocalpulsar;

import lombok.SneakyThrows;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.common.policies.data.TenantInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.PulsarContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import reactor.blockhound.BlockHound;

import java.util.Map;
import java.util.Set;

import static com.example.tclocalpulsar.Application.NAMESPACE;
import static com.example.tclocalpulsar.Application.TENANT;

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

        static PostgreSQLContainer<?> postgresSQL = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:14-alpine")
        )
            .withReuse(true);

        static PulsarContainer pulsar = new PulsarContainer(DockerImageName.parse("apachepulsar/pulsar"))
            .withReuse(true);

        public static Map<String, Object> getProperties() {
            Startables.deepStart(postgresSQL, pulsar).join();
            setupTopic(pulsar.getHttpServiceUrl(), Application.TOPIC);

            return Map.of(
                "spring.r2dbc.url",
                "r2dbc:postgresql://%s:%d/%s".formatted(
                        postgresSQL.getHost(),
                        postgresSQL.getMappedPort(5432),
                        postgresSQL.getDatabaseName()
                    ),
                "spring.flyway.url",
                "jdbc:postgresql://%s:%d/%s".formatted(
                        postgresSQL.getHost(),
                        postgresSQL.getMappedPort(5432),
                        postgresSQL.getDatabaseName()
                    ),
                "spring.r2dbc.username",
                postgresSQL.getUsername(),
                "spring.r2dbc.password",
                postgresSQL.getPassword(),
                "spring.pulsar.client.service-url",
                pulsar.getPulsarBrokerUrl()
            );
        }

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            var env = context.getEnvironment();
            env.getPropertySources().addFirst(new MapPropertySource("testcontainers", getProperties()));
        }

        @SneakyThrows
        public static void setupTopic(String httpServiceUrl, String topic) {
            try (var pulsarAdmin = PulsarAdmin.builder().serviceHttpUrl(httpServiceUrl).build()) {
                if (pulsarAdmin.tenants().getTenants().contains(TENANT)) {
                    // for reusable containers case
                    return;
                }
                pulsarAdmin
                    .tenants()
                    .createTenant(
                        TENANT,
                        TenantInfo.builder().allowedClusters(Set.copyOf(pulsarAdmin.clusters().getClusters())).build()
                    );
                pulsarAdmin.namespaces().createNamespace(NAMESPACE);
                pulsarAdmin.topics().createPartitionedTopic(topic, 2);
            }
        }
    }
}
