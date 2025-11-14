package com.eplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Application principale de la plateforme de gestion de compétences et formation.
 *
 * Cette application Spring Boot implémente une architecture hexagonale avec :
 * - Couche Domain : entités métier, interfaces repositories, services métier
 * - Couche Application : use cases, DTOs, mappers
 * - Couche Infrastructure : contrôleurs REST, implémentations JPA, configurations
 *
 * @author E-Platform Team
 * @version 0.1.0
 * @since 2025-01-15
 */
@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
public class EplatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(EplatformApplication.class, args);
    }

}
