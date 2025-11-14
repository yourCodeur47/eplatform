package com.eplatform;

import org.junit.jupiter.api.Test;

/**
 * Tests de base pour vérifier que le contexte Spring Boot se charge correctement.
 * Utilise Testcontainers pour PostgreSQL et Redis.
 */
class EplatformApplicationTests extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
        // Ce test vérifie que le contexte Spring Boot se charge sans erreur
        // C'est un test de base important qui valide la configuration générale
        // PostgreSQL et Redis sont automatiquement démarrés via Testcontainers
    }

    @Test
    void applicationStarts() {
        // Vérifie que l'application peut démarrer avec toutes ses dépendances
        // Testcontainers fournit PostgreSQL et Redis automatiquement
    }
}
