# Plateforme de Gestion de Compétences et Formation

Application web full-stack pour la gestion des compétences et des formations en entreprise.

## 📋 Stack Technique

### Backend
- **Java 21** (LTS)
- **Spring Boot 3.2.1**
- **PostgreSQL 16**
- **Redis 7**
- **Liquibase** (migrations DB)
- **JWT** pour l'authentification
- **MapStruct** + **Lombok**
- **Swagger/OpenAPI** (documentation API)

### Frontend
- **Angular 18**
- **TypeScript 5.4**
- **Angular Material** / **PrimeNG**
- **RxJS 7**

### DevOps
- **Docker** + **Docker Compose**
- **MinIO** (stockage S3-compatible)
- **RabbitMQ** (messages)

## 🚀 Démarrage Rapide

### Prérequis

```bash
# Vérifier les versions
java -version        # 21+
node -v              # 20+
docker --version     # 24+
docker-compose -v
```

### 1. Cloner le projet

```bash
git clone https://github.com/votre-org/elearning-platform.git
cd elearning-platform
```

### 2. Configuration de l'environnement

```bash
# Copier le fichier d'exemple
cp .env.example .env

# Modifier les variables si nécessaire
nano .env
```

### 3. Lancer les services Docker

```bash
# Lancer PostgreSQL, Redis, MinIO, RabbitMQ
docker-compose up -d

# Vérifier que tous les services sont démarrés
docker-compose ps

# Voir les logs
docker-compose logs -f
```

### 4. Lancer le Backend

```bash
cd backend

# Première installation
./mvnw clean install

# Lancer l'application
./mvnw spring-boot:run

# L'API sera accessible sur http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui/index.html
```

### 5. Lancer le Frontend

```bash
cd frontend

# Installer les dépendances
npm install

# Lancer le serveur de développement
ng serve

# L'application sera accessible sur http://localhost:4200
```

## 📁 Structure du Projet

```
elearning/
├── backend/                    # Application Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/elearning/platform/
│   │   │   │   ├── domain/           # Entités et repositories
│   │   │   │   ├── application/      # DTOs et use cases
│   │   │   │   └── infrastructure/   # Configs et adapters
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/changelog/     # Migrations Liquibase
│   │   └── test/
│   └── pom.xml
│
├── frontend/                   # Application Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/          # Services, guards, interceptors
│   │   │   ├── features/      # Modules fonctionnels
│   │   │   └── shared/        # Composants partagés
│   │   ├── assets/
│   │   └── environments/
│   └── package.json
│
├── docker/                     # Configurations Docker
│   └── postgres/
│       └── init.sql
│
├── docs/                       # Documentation
│   ├── SPEC.md                # Spécifications
│   ├── PLANNING.md            # Planning
│   ├── STACK.md               # Stack technique
│   ├── TEC.md                 # Documentation technique
│   └── TEC-LIGHT.md           # Guide de démarrage
│
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

## 🔐 Authentification

### Endpoints principaux

```http
POST /api/auth/register    # Inscription
POST /api/auth/login       # Connexion
POST /api/auth/refresh     # Rafraîchir le token
GET  /api/users/me         # Profil utilisateur
```

### Exemple de login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@company.com",
    "password": "admin123"
  }'
```

## 📊 Base de Données

### Accéder à PostgreSQL

```bash
# Via Docker
docker exec -it elearning-db psql -U admin -d skills_db

# Commandes utiles
\dt                 # Lister les tables
\d+ users          # Décrire la table users
\q                 # Quitter
```

### Migrations Liquibase

```bash
# Appliquer les migrations
cd backend
./mvnw liquibase:update

# Voir le statut
./mvnw liquibase:status

# Rollback (1 changeset)
./mvnw liquibase:rollback -Dliquibase.rollbackCount=1
```

## 🧪 Tests

### Backend

```bash
cd backend

# Tests unitaires
./mvnw test

# Tests avec couverture
./mvnw verify

# Voir le rapport de couverture
open target/site/jacoco/index.html
```

### Frontend

```bash
cd frontend

# Tests unitaires
ng test

# Tests avec couverture
ng test --code-coverage

# Tests E2E
ng e2e
```

## 📦 Build Production

### Backend

```bash
cd backend

# Build JAR
./mvnw clean package -DskipTests

# Le JAR sera dans target/platform-1.0.0-SNAPSHOT.jar

# Build image Docker
docker build -t elearning-backend .
```

### Frontend

```bash
cd frontend

# Build production
ng build --configuration production

# Les fichiers seront dans dist/

# Build image Docker
docker build -t elearning-frontend .
```

## 🔧 Configuration Avancée

### Variables d'Environnement

#### Variables Obligatoires

| Variable | Description | Exemple |
|----------|-------------|---------|
| `DB_HOST` | Hôte PostgreSQL | `localhost` |
| `DB_PORT` | Port PostgreSQL | `5432` |
| `DB_NAME` | Nom de la base | `skills_db` |
| `DB_USERNAME` | Utilisateur DB | `admin` |
| `DB_PASSWORD` | Mot de passe DB | `admin123` |
| `REDIS_HOST` | Hôte Redis | `localhost` |
| `REDIS_PORT` | Port Redis | `6379` |
| `JWT_SECRET` | Secret JWT (256 bits) | `your-super-secret-key` |

#### Variables Optionnelles

| Variable | Description | Défaut |
|----------|-------------|--------|
| `REDIS_PASSWORD` | Mot de passe Redis | (vide) |
| `MINIO_ENDPOINT` | Endpoint MinIO | `http://localhost:9000` |
| `MINIO_ACCESS_KEY` | Access key MinIO | `minioadmin` |
| `MINIO_SECRET_KEY` | Secret key MinIO | `minioadmin` |
| `SMTP_HOST` | Serveur SMTP | `localhost` |
| `SMTP_PORT` | Port SMTP | `1025` |
| `CORS_ALLOWED_ORIGINS` | Origines CORS autorisées | `http://localhost:4200` |
| `LOG_LEVEL` | Niveau de log | `INFO` |

### Créer le Premier Utilisateur Admin

#### Option 1 : Via les Migrations Liquibase (Recommandé)

Les migrations Liquibase créent automatiquement un admin par défaut :

```
Email: admin@company.com
Password: Admin123!
```

**Changer le mot de passe après première connexion !**

#### Option 2 : Via API

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@company.com",
    "password": "Admin123!",
    "firstName": "Admin",
    "lastName": "System"
  }'

# Puis promouvoir en admin via SQL
docker exec -it elearning-db psql -U admin -d skills_db -c \
  "UPDATE users SET role = 'ADMIN' WHERE email = 'admin@company.com';"
```

#### Option 3 : Script SQL Direct

```sql
-- Se connecter à la DB
docker exec -it elearning-db psql -U admin -d skills_db

-- Créer l'admin
INSERT INTO users (email, password, first_name, last_name, is_active, created_at)
VALUES (
  'admin@company.com',
  '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYVFPvPyYrC', -- BCrypt: Admin123!
  'Admin',
  'System',
  true,
  NOW()
);

-- Assigner le rôle ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@company.com'
  AND r.name = 'ADMIN';
```

### Import Initial des Données

#### Import Utilisateurs CSV

```bash
# Format du CSV : email,firstName,lastName,departmentId,jobPositionId
cat users.csv
email,firstName,lastName,departmentId,jobPositionId
john.doe@company.com,John,Doe,1,5
jane.smith@company.com,Jane,Smith,2,8

# Import via API
curl -X POST http://localhost:8080/api/admin/users/import \
  -H "Authorization: Bearer {admin_token}" \
  -F "file=@users.csv"
```

#### Import Compétences

```bash
# Via interface admin ou API
curl -X POST http://localhost:8080/api/admin/skills/import \
  -H "Authorization: Bearer {admin_token}" \
  -F "file=@skills.csv"
```

---

## 🐛 Dépannage

### Port 8080 déjà utilisé

```bash
# Trouver le processus
lsof -i :8080

# Tuer le processus
kill -9 <PID>

# Alternative : Changer le port dans application.yml
server:
  port: 8081
```

### Erreur de connexion PostgreSQL

```bash
# Vérifier que le conteneur tourne
docker ps | grep postgres

# Voir les logs
docker logs elearning-db

# Tester la connexion
docker exec -it elearning-db psql -U admin -d skills_db -c "SELECT 1;"

# Relancer
docker-compose restart postgres

# Vérifier les credentials dans .env
cat .env | grep DB_
```

### Redis Connection Refused

```bash
# Vérifier Redis
docker ps | grep redis

# Tester Redis
docker exec -it elearning-redis redis-cli ping
# Devrait retourner: PONG

# Voir les logs
docker logs elearning-redis

# Relancer
docker-compose restart redis
```

### Liquibase checksum failed

```bash
cd backend

# Nettoyer les checksums
./mvnw liquibase:clearCheckSums

# Voir le statut
./mvnw liquibase:status

# Force update (ATTENTION : peut perdre des données)
./mvnw liquibase:dropAll
./mvnw liquibase:update
```

### MinIO Access Denied

```bash
# Vérifier MinIO
docker ps | grep minio

# Accéder à la console
open http://localhost:9001

# Créer le bucket manuellement
mc alias set local http://localhost:9000 minioadmin minioadmin
mc mb local/elearning-files
mc policy set download local/elearning-files
```

### Frontend ne charge pas

```bash
cd frontend

# Nettoyer node_modules
rm -rf node_modules package-lock.json

# Réinstaller
npm install

# Vérifier la version de Node
node -v  # Devrait être 20+

# Clear cache Angular
ng cache clean

# Rebuild
ng build --configuration development
```

### CORS Errors

Ajouter dans `backend/src/main/resources/application.yml` :

```yaml
cors:
  allowed-origins: http://localhost:4200,http://localhost:3000
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: "*"
```

### Réinitialiser complètement

```bash
# Arrêter et supprimer tous les conteneurs et volumes
docker-compose down -v

# Supprimer les images (optionnel)
docker rmi $(docker images -q elearning*)

# Nettoyer le backend
cd backend
./mvnw clean
rm -rf target

# Nettoyer le frontend
cd ../frontend
rm -rf node_modules dist .angular

# Tout relancer
cd ..
docker-compose up -d
cd backend && ./mvnw clean install && ./mvnw spring-boot:run &
cd ../frontend && npm install && ng serve
```

### Logs de Débogage

```bash
# Logs backend (Spring Boot)
cd backend
./mvnw spring-boot:run -Dlogging.level.com.elearning=DEBUG

# Logs Docker
docker-compose logs -f

# Logs d'un service spécifique
docker-compose logs -f postgres
docker-compose logs -f redis

# Logs Angular
ng serve --verbose
```

---

## 💾 Backup et Restore

### Backup Manuel

```bash
# Backup PostgreSQL
docker exec elearning-db pg_dump -U admin skills_db > backup-$(date +%Y%m%d).sql

# Backup MinIO
mc mirror local/elearning-files ./minio-backup

# Backup complet (DB + Files)
./scripts/backup.sh  # Voir scripts/
```

### Restore

```bash
# Restore PostgreSQL
docker exec -i elearning-db psql -U admin skills_db < backup-20250115.sql

# Restore MinIO
mc mirror ./minio-backup local/elearning-files

# Restore complet
./scripts/restore.sh backup-20250115.tar.gz
```

### Backup Automatique

**Ajouter dans crontab :**

```bash
crontab -e

# Backup quotidien à 2h du matin
0 2 * * * /path/to/elearning/scripts/backup.sh
```

---

## 🔄 Migration de Données

### Migration depuis une autre instance

```bash
# 1. Export depuis l'ancienne instance
ssh old-server "docker exec elearning-db pg_dump -U admin skills_db" > old-db.sql

# 2. Import dans la nouvelle instance
docker exec -i elearning-db psql -U admin skills_db < old-db.sql

# 3. Sync des fichiers
rsync -avz old-server:/path/to/minio-data/ ./minio-data/
```

### Export pour Analyse

```bash
# Export CSV de toutes les évaluations
docker exec elearning-db psql -U admin skills_db -c \
  "COPY (SELECT * FROM evaluations) TO STDOUT CSV HEADER" > evaluations.csv

# Export des utilisateurs
docker exec elearning-db psql -U admin skills_db -c \
  "COPY (SELECT id, email, first_name, last_name, department FROM users)
   TO STDOUT CSV HEADER" > users.csv
```

---

## 🔐 Sécurité en Développement

### Changer les Credentials par Défaut

**IMPORTANT** : Ne jamais utiliser les credentials par défaut en production !

```bash
# Générer des credentials sécurisés
# PostgreSQL
openssl rand -base64 32  # Password DB

# Redis
openssl rand -base64 32  # Password Redis

# JWT Secret (256 bits minimum)
openssl rand -base64 64

# Mettre à jour .env
nano .env
```

### Activer HTTPS en Local (Optionnel)

```bash
# Générer un certificat self-signed
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes

# Configurer Spring Boot
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: changeit
    key-store-type: PKCS12
```

## 📚 Documentation

- **API Documentation**: http://localhost:8080/swagger-ui/index.html
- **Actuator**: http://localhost:8080/actuator
- **MinIO Console**: http://localhost:9001
- **RabbitMQ Management**: http://localhost:15672

## 🤝 Contribution

1. Créer une branche feature
```bash
git checkout -b feature/AUTH-001-jwt-authentication
```

2. Committer avec le format Conventional Commits
```bash
git commit -m "feat(auth): implement JWT authentication"
```

3. Pousser et créer une Pull Request
```bash
git push origin feature/AUTH-001-jwt-authentication
```

## 📝 Licence

Propriétaire - © 2025 E-Learning Platform

## 👥 Équipe

- **Tech Lead**: [À définir]
- **Backend**: [À définir]
- **Frontend**: [À définir]
- **DevOps**: [À définir]

## 📞 Support

- Email: support@elearning.company.com
- Documentation: https://docs.elearning.company.com
- Issues: https://github.com/votre-org/elearning-platform/issues

---

**Version**: 1.0.0
**Dernière mise à jour**: 2025-11-12
