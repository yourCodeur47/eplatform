# E-Platform Backend

Backend de la plateforme de gestion de compétences et formation, développé avec Spring Boot 3.2 et Java 21.

## Architecture

Le projet adopte une **architecture hexagonale** (clean architecture) organisée en 3 couches :

```
com.eplatform/
├── domain/              # Couche métier (indépendante)
│   ├── model/          # Entités métier
│   ├── repository/     # Interfaces repositories
│   ├── service/        # Services métier
│   └── exception/      # Exceptions métier
├── application/         # Couche application
│   ├── dto/            # Data Transfer Objects
│   ├── mapper/         # MapStruct mappers
│   └── usecase/        # Use cases / orchestration
└── infrastructure/      # Couche infrastructure
    ├── persistence/    # Implémentations JPA
    ├── web/            # Contrôleurs REST
    ├── config/         # Configurations Spring
    └── security/       # Sécurité JWT
```

## Prérequis

- **Java 21 LTS** (OpenJDK ou Oracle JDK)
- **Maven 3.9+**
- **Docker Desktop 24+** (pour les services d'infrastructure)
- **Git 2.40+**
- **IDE** : IntelliJ IDEA Ultimate (recommandé) ou VS Code

## Installation

### 1. Cloner le projet

```bash
git clone <repository-url>
cd eplatform/backend
```

### 2. Démarrer les services d'infrastructure

```bash
# Démarrer PostgreSQL, Redis, MinIO et MailHog
docker-compose up -d

# Vérifier que tous les services sont démarrés
docker-compose ps
```

Services disponibles :
- **PostgreSQL** : `localhost:5432`
- **Redis** : `localhost:6379`
- **MinIO API** : `http://localhost:9000`
- **MinIO Console** : `http://localhost:9001` (minioadmin/minioadmin)
- **MailHog UI** : `http://localhost:8025`

### 3. Compiler le projet

```bash
# Compiler sans exécuter les tests
mvn clean install -DskipTests

# Compiler avec les tests
mvn clean install
```

### 4. Lancer l'application

```bash
# Avec Maven
mvn spring-boot:run

# Ou avec le JAR compilé
java -jar target/eplatform-backend.jar
```

L'application démarre sur **http://localhost:8080**

## Configuration

### Profils disponibles

- **local** : Développement local (par défaut)
- **staging** : Pré-production
- **prod** : Production

```bash
# Démarrer avec un profil spécifique
mvn spring-boot:run -Dspring-boot.run.profiles=staging

# Ou avec variable d'environnement
export SPRING_PROFILE=staging
mvn spring-boot:run
```

### Variables d'environnement

Principales variables d'environnement (voir `application-local.yml` pour la liste complète) :

| Variable | Description | Valeur par défaut |
|----------|-------------|-------------------|
| `SPRING_PROFILE` | Profil actif | `local` |
| `DB_HOST` | Hôte PostgreSQL | `localhost` |
| `DB_PORT` | Port PostgreSQL | `5432` |
| `DB_NAME` | Nom de la base | `eplatform_db` |
| `DB_USERNAME` | Utilisateur DB | `eplatform_user` |
| `DB_PASSWORD` | Mot de passe DB | `eplatform_password` |
| `REDIS_HOST` | Hôte Redis | `localhost` |
| `REDIS_PORT` | Port Redis | `6379` |
| `JWT_SECRET` | Secret JWT | *(à définir en prod)* |

## Documentation API

Une fois l'application démarrée :

- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **OpenAPI JSON** : http://localhost:8080/api-docs

## Tests

```bash
# Exécuter tous les tests
mvn test

# Tests avec couverture de code
mvn clean test jacoco:report

# Voir le rapport de couverture
open target/site/jacoco/index.html
```

Objectif de couverture : **80%**

## Base de données

### Migrations Liquibase

Les migrations sont automatiquement exécutées au démarrage de l'application.

```bash
# Voir le statut des migrations
mvn liquibase:status

# Générer un diff SQL
mvn liquibase:diff
```

### Accès à la base de données

```bash
# Via psql
psql -h localhost -p 5432 -U eplatform_user -d eplatform_db

# Via pgAdmin (optionnel)
docker-compose --profile tools up -d
# Puis ouvrir http://localhost:5050
```

## Monitoring

### Actuator Endpoints

- **Health** : http://localhost:8080/actuator/health
- **Info** : http://localhost:8080/actuator/info
- **Metrics** : http://localhost:8080/actuator/metrics
- **Prometheus** : http://localhost:8080/actuator/prometheus

## Build Docker

```bash
# Construire l'image Docker
docker build -t eplatform-backend:latest .

# Lancer le conteneur
docker run -p 8080:8080 \
  -e SPRING_PROFILE=local \
  -e DB_HOST=host.docker.internal \
  eplatform-backend:latest
```

## Scripts utiles

```bash
# Nettoyer le projet
mvn clean

# Formater le code (si plugin activé)
mvn formatter:format

# Analyser les dépendances
mvn dependency:tree

# Vérifier les mises à jour
mvn versions:display-dependency-updates

# Arrêter tous les services Docker
docker-compose down

# Réinitialiser complètement (ATTENTION : perte de données)
docker-compose down -v
```

## Structure du projet

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/eplatform/
│   │   │   ├── EplatformApplication.java
│   │   │   ├── domain/
│   │   │   ├── application/
│   │   │   └── infrastructure/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       ├── application-staging.yml
│   │       ├── application-prod.yml
│   │       └── db/changelog/
│   └── test/
│       └── java/com/eplatform/
├── pom.xml
├── docker-compose.yml
├── init-db.sql
└── README.md
```

## Dépendances principales

- **Spring Boot** 3.2.1
- **Spring Data JPA** (Hibernate)
- **Spring Security** + JWT (jjwt 0.12.3)
- **PostgreSQL** 16
- **Redis** 7
- **Liquibase** (migrations)
- **Lombok** 1.18.30
- **MapStruct** 1.5.5
- **SpringDoc OpenAPI** 2.3.0
- **Caffeine Cache** 3.1.8
- **Testcontainers** 1.19.3

## Support

Pour plus d'informations, consulter :
- [Guide de mise en place backend](../GUIDE-MISE-EN-PLACE-BACKEND.md)
- [Documentation technique](../TEC.md)
- [Spécifications](../SPEC.md)
- [Architecture](../ARCHITECTURE.md)

## Licence

Propriétaire - E-Platform Team © 2025
