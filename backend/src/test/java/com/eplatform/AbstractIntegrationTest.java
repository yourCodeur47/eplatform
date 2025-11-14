package com.eplatform;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Classe de base pour les tests d'intégration.
 * Configure automatiquement PostgreSQL et Redis via Testcontainers.
 *
 * Les tests d'intégration doivent étendre cette classe pour bénéficier
 * automatiquement de l'infrastructure de test (base de données, cache, etc.).
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public abstract class AbstractIntegrationTest {

    /**
     * Container PostgreSQL partagé entre tous les tests.
     * Utilise la version 15 pour correspondre à l'environnement de production.
     */
    @Container
    protected static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                    .withDatabaseName("eplatform_test")
                    .withUsername("test_user")
                    .withPassword("test_password")
                    .withReuse(true); // Réutilise le container entre les tests pour améliorer les performances

    /**
     * Container Redis partagé entre tous les tests.
     * Utilise la version 7 pour correspondre à l'environnement de production.
     */
    @Container
    protected static final GenericContainer<?> REDIS_CONTAINER =
            new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                    .withExposedPorts(6379)
                    .withReuse(true);

    /**
     * Configure dynamiquement les propriétés Spring pour utiliser les containers Testcontainers.
     * Ces propriétés remplacent celles définies dans application-test.yml.
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Configuration PostgreSQL
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);

        // Configuration Redis
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    }
}
