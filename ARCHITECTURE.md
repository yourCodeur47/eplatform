# Architecture de la Plateforme - Décisions et Rationale

## Vue d'Ensemble

Ce document documente les décisions architecturales importantes prises pour la plateforme de gestion de compétences.

---

## ADR (Architecture Decision Records)

### ADR-001 : Architecture Hexagonale (Clean Architecture)

**Date** : 2025-01-10
**Statut** : ✅ Accepté
**Décideurs** : Tech Lead, Architecte

**Contexte**

Choix de l'architecture backend pour assurer maintenabilité et testabilité.

**Décision**

Adoption de l'architecture hexagonale avec 3 couches :
- **Domain** : Entités métier, interfaces repositories, logique métier pure
- **Application** : Use cases, DTOs, orchestration
- **Infrastructure** : Implémentations techniques (JPA, controllers, configs)

**Conséquences**

✅ **Avantages**
- Testabilité élevée (domain isolé)
- Indépendance vis-à-vis des frameworks
- Changement de DB facile
- Logique métier centralisée

❌ **Inconvénients**
- Courbe d'apprentissage
- Plus de fichiers/classes
- Mapping Entity ↔ DTO

**Alternatives Considérées**
- Architecture en couches traditionnelle (rejetée : couplage fort)
- DDD complet (rejetée : trop complexe pour la taille du projet)

---

### ADR-002 : Monolithe Modulaire (pas de Microservices)

**Date** : 2025-01-10
**Statut** : ✅ Accepté
**Décideurs** : Tech Lead, CTO

**Contexte**

Choix entre monolithe et microservices pour le démarrage du projet.

**Décision**

Démarrer avec un **monolithe modulaire** bien structuré, avec possibilité de migration vers microservices plus tard.

**Rationale**

1. **Complexité** : Équipe de 2 développeurs, pas besoin de la complexité des microservices
2. **Déploiement** : Plus simple (1 seul déploiement)
3. **Transactions** : Transactions ACID simples
4. **Performance** : Pas de latence réseau inter-services
5. **Débogage** : Plus facile à débugger
6. **Évolutivité** : Structure modulaire permet migration future

**Conséquences**

✅ **Avantages**
- Simplicité opérationnelle
- Déploiements rapides
- Transactions fiables
- Moins de DevOps

❌ **Inconvénients**
- Scaling horizontal plus complexe
- Couplage technique possible
- Redéploiement complet à chaque changement

**Critères de Migration vers Microservices**

Reconsidérer si :
- Équipe > 10 développeurs
- > 100 000 utilisateurs actifs
- Besoins de scaling différenciés par module
- Besoin de déploiements indépendants

---

### ADR-003 : PostgreSQL comme Base de Données Principale

**Date** : 2025-01-10
**Statut** : ✅ Accepté
**Décideurs** : Tech Lead, DBA

**Contexte**

Choix de la base de données relationnelle.

**Décision**

Utilisation de **PostgreSQL 16** comme base de données principale.

**Rationale**

1. **Open Source** : Gratuit, communauté active
2. **Fonctionnalités** : JSON/JSONB, full-text search, partitionnement
3. **Performance** : Excellente pour OLTP et analytics simples
4. **ACID** : Transactions fiables
5. **Réplication** : Master-slave pour HA
6. **Popularité** : Compétences disponibles sur le marché

**Alternatives Considérées**
- **MySQL** : Moins de fonctionnalités avancées
- **Oracle** : Coût prohibitif
- **MongoDB** : Pas adapté pour nos relations complexes

---

### ADR-004 : Redis pour le Cache Distribué

**Date** : 2025-01-10
**Statut** : ✅ Accepté
**Décideurs** : Tech Lead

**Contexte**

Besoin de cache pour améliorer les performances.

**Décision**

Utilisation de **Redis** comme cache L2 (distribué) + **Caffeine** comme cache L1 (local).

**Rationale**

1. **Performance** : Redis très rapide (< 1ms latence)
2. **TTL** : Gestion fine de l'expiration
3. **Distribué** : Partagé entre instances
4. **Pub/Sub** : Utile pour notifications temps réel
5. **Session storage** : Peut stocker les sessions

**Stratégie de Cache**
- **L1 (Caffeine)** : Entités récentes (5 min, 1000 max)
- **L2 (Redis)** : Données statiques (compétences, référentiels)
- **Invalidation** : Event-driven via Spring Events

---

### ADR-005 : Angular 18 avec Signals pour le Frontend

**Date** : 2025-01-12
**Statut** : ✅ Accepté
**Décideurs** : Tech Lead, Frontend Dev

**Contexte**

Choix du framework frontend.

**Décision**

Utilisation d'**Angular 18** avec **Signals** pour le state management.

**Rationale**

1. **Enterprise-ready** : Framework complet, pas de choix à faire
2. **TypeScript natif** : Typage fort out-of-the-box
3. **Signals** : Alternative légère à NgRx pour notre taille
4. **Tooling** : CLI puissant, DevTools, Testing intégré
5. **Long-term support** : Google derrière

**Alternatives Considérées**
- **React + Redux** : Plus de choix à faire, écosystème fragmenté
- **Vue.js** : Moins adapté enterprise

**State Management**
- **Signals** pour la majorité des cas
- **NgRx** seulement si complexité augmente (> 50 stores)

---

### ADR-006 : JWT pour l'Authentification

**Date** : 2025-01-12
**Statut** : ✅ Accepté
**Décideurs** : Tech Lead, Security Officer

**Contexte**

Choix du mécanisme d'authentification.

**Décision**

Utilisation de **JWT** (JSON Web Tokens) avec refresh tokens.

**Rationale**

1. **Stateless** : Pas de session serveur
2. **Scalable** : Fonctionne avec load balancer
3. **Mobile-friendly** : Facile à intégrer dans apps mobiles
4. **Standard** : RFC 7519
5. **Claims** : Peut contenir rôles et permissions

**Configuration**
- **Access token** : 1h de validité
- **Refresh token** : 7 jours de validité
- **Signature** : HMAC-SHA256
- **Rotation** : Refresh tokens renouvelés à chaque usage

**Alternatives Considérées**
- **Sessions serveur** : Non scalable
- **OAuth2** : Trop complexe pour notre cas

---

### ADR-007 : MinIO pour le Stockage de Fichiers

**Date** : 2025-01-15
**Statut** : ✅ Accepté
**Décideurs** : Tech Lead, DevOps

**Contexte**

Besoin de stocker des fichiers (justificatifs, attestations, documents).

**Décision**

Utilisation de **MinIO** comme stockage S3-compatible.

**Rationale**

1. **S3-compatible** : API standard, migration vers AWS S3 facile
2. **Self-hosted** : Contrôle des données
3. **Open Source** : Gratuit
4. **Performance** : Très rapide
5. **High Availability** : Mode distribué disponible

**Organisation**
```
minio/
├── evaluation-evidences/
├── training-materials/
├── certificates/
└── user-avatars/
```

**Alternatives Considérées**
- **AWS S3** : Coût récurrent, dépendance cloud
- **Système de fichiers local** : Pas scalable

---

### ADR-008 : Liquibase pour les Migrations de Base de Données

**Date** : 2025-01-15
**Statut** : ✅ Accepté
**Décideurs** : Tech Lead, DBA

**Contexte**

Gestion du versioning du schéma de base de données.

**Décision**

Utilisation de **Liquibase** pour les migrations.

**Rationale**

1. **Versioning** : Historique complet des changements
2. **Rollback** : Possibilité de revenir en arrière
3. **Multi-DB** : Supporte PostgreSQL, MySQL, etc.
4. **CI/CD friendly** : Intégration facile
5. **Validation** : Détection de drifts

**Organisation**
```
src/main/resources/db/changelog/
├── db.changelog-master.xml
├── v1.0/
│   ├── 01-create-users-tables.xml
│   ├── 02-create-skills-tables.xml
│   └── 03-create-evaluations-tables.xml
└── v1.1/
    └── 01-add-budget-tables.xml
```

**Alternatives Considérées**
- **Flyway** : Moins de fonctionnalités
- **JPA ddl-auto** : Pas assez de contrôle

---

### ADR-009 : Docker + Kubernetes pour le Déploiement

**Date** : 2025-01-18
**Statut** : ✅ Accepté
**Décideurs** : Tech Lead, DevOps, CTO

**Contexte**

Stratégie de conteneurisation et orchestration.

**Décision**

Utilisation de **Docker** pour la conteneurisation et **Kubernetes** pour l'orchestration en production.

**Rationale**

1. **Portabilité** : Runs anywhere
2. **Isolation** : Dépendances encapsulées
3. **Scalabilité** : Kubernetes auto-scaling
4. **Self-healing** : Kubernetes redémarre les pods défaillants
5. **Standard** : Compétences disponibles

**Stratégie**
- **Dev** : Docker Compose
- **Staging/Prod** : Kubernetes (EKS/AKS/GKE)
- **CI/CD** : GitLab CI → Build Docker → Push Registry → Deploy K8s

**Alternatives Considérées**
- **VMs traditionnelles** : Moins efficient
- **Serverless** : Pas adapté pour notre architecture

---

### ADR-010 : MapStruct pour le Mapping Entity-DTO

**Date** : 2025-01-20
**Statut** : ✅ Accepté
**Décideurs** : Tech Lead

**Contexte**

Besoin de mapper les entités JPA vers des DTOs.

**Décision**

Utilisation de **MapStruct** pour le mapping automatique.

**Rationale**

1. **Performance** : Génération de code à la compilation
2. **Type-safe** : Erreurs détectées à la compilation
3. **Maintenabilité** : Pas de mapping manuel fastidieux
4. **Lisibilité** : Interface simple

**Exemple**
```java
@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillDTO toDTO(Skill entity);
    Skill toEntity(SkillDTO dto);
    List<SkillDTO> toDTOList(List<Skill> entities);
}
```

**Alternatives Considérées**
- **ModelMapper** : Runtime reflection (plus lent)
- **Mapping manuel** : Trop verbeux et error-prone

---

## Diagrammes d'Architecture

### Architecture Globale (C4 - Level 1)

```
┌─────────────────────────────────────────────────────┐
│                    Users                             │
│  (Employés, Managers, RH, Admins, Formateurs)       │
└──────────────────┬──────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────┐
│          eplatform Web Application                   │
│  [Angular 18 SPA + Spring Boot API]                 │
│                                                      │
│  Gestion de compétences, formations, évaluations    │
└──────────┬──────────────────┬────────────────┬──────┘
           │                  │                │
           ▼                  ▼                ▼
    ┌──────────┐      ┌──────────┐     ┌──────────┐
    │PostgreSQL│      │  Redis   │     │  MinIO   │
    │ Database │      │  Cache   │     │  Storage │
    └──────────┘      └──────────┘     └──────────┘
```

### Architecture Backend (C4 - Level 2)

```
┌────────────────────────────────────────────────────┐
│               Spring Boot Backend                   │
├────────────────────────────────────────────────────┤
│                                                     │
│  ┌─────────────────────────────────────────────┐  │
│  │        Infrastructure Layer                  │  │
│  │  (Controllers, Security, Config, JPA Repos) │  │
│  └────────────────┬────────────────────────────┘  │
│                   │                                │
│  ┌────────────────▼────────────────────────────┐  │
│  │         Application Layer                    │  │
│  │       (Use Cases, DTOs, Mappers)            │  │
│  └────────────────┬────────────────────────────┘  │
│                   │                                │
│  ┌────────────────▼────────────────────────────┐  │
│  │           Domain Layer                       │  │
│  │  (Entities, Domain Services, Repositories)  │  │
│  └─────────────────────────────────────────────┘  │
│                                                     │
└────────────────────────────────────────────────────┘
```

### Flux de Requête API

```
Client (Browser)
    │
    │ 1. HTTP Request
    │
    ▼
┌──────────────────┐
│  AuthInterceptor │  → Ajoute JWT token
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│   RateLimiter    │  → Vérifie limite requêtes
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│   Controller     │  → Valide DTO, mappe vers use case
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│    Service       │  → Logique métier
└────────┬─────────┘
         │
         ├──────────► Cache (Redis)
         │                │
         │                ▼
         └──────────► Repository (JPA) → PostgreSQL
```

---

## Métriques de Qualité Cibles

| Métrique | Cible | Actuel |
|----------|-------|--------|
| **Code Coverage** | ≥ 80% | - |
| **Bugs (SonarQube)** | 0 | - |
| **Code Smells** | < 100 | - |
| **Technical Debt** | < 5% | - |
| **Vulnerabilities** | 0 | - |
| **Duplications** | < 3% | - |
| **Cyclomatic Complexity** | < 10 par méthode | - |
| **API Response Time** | p95 < 500ms | - |
| **Database Queries** | < 100ms | - |
| **Cache Hit Rate** | > 80% | - |

---

## Prochaines Décisions

### En Discussion

1. **Monitoring** : Prometheus + Grafana vs ELK Stack
2. **Tracing distribué** : Jaeger vs Zipkin
3. **Message Broker** : RabbitMQ vs Apache Kafka
4. **CDN** : Cloudflare vs AWS CloudFront

---

## Révisions

| Date | Version | Auteur | Changements |
|------|---------|--------|-------------|
| 2025-01-10 | 1.0 | Tech Lead | Création initiale |
| 2025-01-20 | 1.1 | Tech Lead | Ajout ADR-010 (MapStruct) |

---

**Maintenu par** : Équipe Architecture
**Contact** : architecture@company.com
