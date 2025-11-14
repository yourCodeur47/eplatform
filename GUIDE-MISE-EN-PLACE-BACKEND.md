# Guide de Mise en Place — Backend
## Plateforme de Gestion de Compétences et Formation

---

## 1. Résumé Exécutif

Ce guide décrit la mise en place complète du backend de la plateforme de gestion de compétences, basé sur Spring Boot 3.2 et Java 21. Le système adopte une architecture hexagonale pour garantir la maintenabilité et la testabilité. L'infrastructure repose sur PostgreSQL pour la persistance, Redis pour le cache distribué, MinIO pour le stockage objet, et intègre une sécurité JWT robuste. Le processus couvre l'installation des outils, la configuration des services, le déploiement des modules métier, et la mise en production sur Kubernetes avec monitoring intégré.

---

## 2. Contexte et Périmètre

### Contexte Projet
Selon **SPEC.md** (Section 1), la plateforme vise à centraliser la gestion des compétences, évaluations et formations pour les entreprises. Le backend expose une API REST sécurisée consommée par une application Angular.

### Périmètre Technique
D'après **STACK.md** (Section Backend) et **ARCHITECTURE.md** (ADR-001, ADR-002), le backend couvre :
- API REST avec authentification JWT et RBAC
- Gestion de 12 modules fonctionnels (utilisateurs, compétences, évaluations, formations, etc.)
- Persistance relationnelle avec migrations versionnées
- Cache distribué multi-niveaux
- Stockage de fichiers sécurisé
- Observabilité complète (logs, métriques, traces)

### Exclusions
Le frontend Angular, l'infrastructure réseau externe, et les intégrations SIRH tierces sont hors périmètre.

---

## 3. Prérequis et Dépendances

### Outils de Développement
Référence : **README.md** (Section Prérequis)
1. Machine de développement avec minimum 8 GB RAM, 50 GB disque disponible
2. Java Development Kit version 21 LTS installé et configuré dans le PATH
3. Maven version 3.9 ou supérieure pour la gestion des dépendances
4. Docker Desktop version 24 ou supérieure avec support Docker Compose
5. Git version 2.40 ou supérieure pour le contrôle de version
6. IDE supportant Java 21 et Spring Boot (IntelliJ IDEA Ultimate recommandé)

### Services Externes
Référence : **TEC.md** (Section Infrastructure)
1. Instance PostgreSQL 16 (locale via Docker ou managée en production)
2. Cluster Redis 7 avec support haute disponibilité
3. Service de stockage objet compatible S3 (MinIO ou AWS S3)
4. Serveur SMTP pour l'envoi d'emails transactionnels
5. Registry Docker privé pour les images de production

### Accès et Permissions
1. Compte avec privilèges administrateur sur la machine de développement
2. Accès au repository Git du projet avec droits de lecture/écriture
3. Credentials pour les environnements de staging et production
4. Accès aux secrets management systems (Vault, AWS Secrets Manager, ou Kubernetes Secrets)

---

## 4. Architecture et Composants

### Patron Architectural
Selon **ARCHITECTURE.md** (ADR-001), le système adopte une architecture hexagonale en trois couches :
1. **Couche Domain** : entités métier, interfaces repositories, services métier purs, indépendants de toute technologie
2. **Couche Application** : use cases, DTOs, mappers, orchestration des flux métier
3. **Couche Infrastructure** : contrôleurs REST, implémentations JPA, configurations Spring, adaptateurs externes

### Composants Principaux
Référence : **TEC.md** (Section Modèle de Données)
1. **Module Authentification** : génération et validation JWT, refresh tokens, gestion des sessions
2. **Module Utilisateurs** : CRUD utilisateurs, rôles RBAC, permissions granulaires, hiérarchie organisationnelle
3. **Module Compétences** : référentiel de compétences hiérarchisé, catégorisation, niveaux de maîtrise
4. **Module Évaluations** : auto-évaluations, validation manager, évaluations 360°, campagnes planifiées
5. **Module Formations** : catalogue, sessions, inscriptions avec workflow d'approbation, émargement électronique
6. **Module Parcours** : création et suivi de parcours personnalisés, gamification
7. **Module Reporting** : dashboards temps réel, génération de rapports, analytics prédictifs
8. **Module Administration** : configuration système, gestion organisation, import/export massif, audit

### Interactions
Le frontend communique via API REST. Les services internes utilisent des événements Spring pour le découplage. Le cache Redis est consulté avant la base de données. Les fichiers sont stockés sur MinIO avec URLs signées temporaires.

---

## 5. Préparation de l'Environnement

### Configuration Machine Locale
Référence : **CONTRIBUTING.md** (Section Setup)
1. Cloner le repository Git dans un répertoire dédié
2. Créer un fichier de variables d'environnement local en copiant le template fourni
3. Initialiser les sous-modules Git si présents
4. Configurer les hooks Git pour les vérifications pré-commit (formatage, tests unitaires)

### Provisionnement Infrastructure
Référence : **DEPLOYMENT.md** (Section Docker Compose)
1. Démarrer PostgreSQL via Docker Compose avec persistance volumique
2. Initialiser la base de données avec utilisateur applicatif et schéma vide
3. Démarrer Redis en mode standalone pour développement, cluster pour staging/production
4. Démarrer MinIO et créer le bucket applicatif avec politique d'accès appropriée
5. Démarrer MailHog pour capturer les emails en développement

### Comptes et Rôles
1. Créer un utilisateur administrateur initial via script de seed
2. Définir les rôles par défaut (ADMIN, HR_ADMIN, MANAGER, EMPLOYEE, TRAINER)
3. Configurer les permissions granulaires par rôle selon la matrice RBAC définie dans **SPEC.md** (Section Sécurité)

---

## 6. Configuration

### Fichiers de Configuration
Référence : **TEC.md** (Section Configuration Spring Boot)
1. **application.yml** : configuration commune à tous les environnements (structure, logging par défaut)
2. **application-local.yml** : surcharges pour développement local (logs DEBUG, show-sql activé)
3. **application-staging.yml** : configuration pré-production (logs INFO, connexions managées)
4. **application-prod.yml** : configuration production (logs WARN, optimisations activées, références aux secrets externes)

### Paramètres Critiques
Référence : **README.md** (Section Variables d'Environnement)
1. **Base de données** : URL JDBC, utilisateur, mot de passe, taille du pool de connexions (HikariCP)
2. **Redis** : hôte, port, mot de passe, configuration du pool Lettuce
3. **JWT** : secret de signature (minimum 256 bits), durée de vie access token (1h), durée refresh token (7 jours)
4. **CORS** : origines autorisées (domaines frontend), méthodes HTTP, headers exposés
5. **Stockage** : endpoint MinIO/S3, access key, secret key, nom du bucket
6. **Email** : serveur SMTP, port, authentification, adresse expéditeur par défaut

### Secrets Management
Référence : **SECURITY.md** (Section Sécurité Technique)
1. Ne jamais committer de secrets dans le code source
2. Utiliser des variables d'environnement ou un gestionnaire de secrets dédié
3. Générer des secrets aléatoires robustes pour JWT et mots de passe services
4. Chiffrer les secrets au repos dans les fichiers de configuration
5. Implémenter la rotation automatique des secrets selon politique de sécurité

---

## 7. Mise en Place Pas à Pas

### Justification de l'Organisation en 8 Phases

L'organisation suivante résout les dépendances critiques entre composants :

| Phase | Composants | Justification | Changement vs v1.0 |
|-------|------------|---------------|---------------------|
| 1 | Initialisation Projet | Structure de base | ✅ Inchangé |
| 2 | Architecture de Base | Fondations techniques sans sécurité | ⚠️ Swagger déplacé vers Phase 4 |
| 3 | **Persistance de Base** | **User, Role, Permission avant sécurité** | 🆕 **Nouvelle phase** |
| 4 | Sécurité + Swagger | Spring Security utilise User créé en Phase 3, Swagger documente JWT | ⚠️ Modifié + ajout Swagger |
| 5 | **Persistance Métier** | **Entités métier après authentification** | 🆕 **Scindée depuis Phase 4 v1.0** |
| 6 | Modules Métier | Services et API sur entités existantes | ⚠️ Décalé (ex-Phase 5) |
| 7 | Cache et Performance | Optimisation après fonctionnalités | ⬇️ Décalé (ex-Phase 6) |
| 8 | Observabilité | Monitoring du système complet | ⬇️ Décalé (ex-Phase 7) |

**Problèmes résolus** :
- ✅ Swagger configuré après JWT (Phase 4 au lieu de Phase 2)
- ✅ Spring Security configuré après entité User (Phase 4 après Phase 3)
- ✅ Authentification unifiée en une seule phase (Phase 4)
- ✅ Migrations Liquibase organisées en 2 vagues (base puis métier)

---

### Phase 1 : Initialisation Projet
Référence : **IMPLEMENTATION-BACKEND.md** (Phase 0)
1. Créer la structure de répertoires du projet Spring Boot via Spring Initializr
2. Configurer le fichier POM avec toutes les dépendances requises (Spring Boot starters, PostgreSQL, Redis, JWT, Lombok, MapStruct, Liquibase)
3. Définir les propriétés Maven (versions Java 21, encodage UTF-8, skip tests configurables)
4. Configurer le plugin compiler pour Lombok et MapStruct avec ordre de traitement correct
5. Ajouter le plugin JaCoCo pour la couverture de code

### Phase 2 : Architecture de Base
Référence : **IMPLEMENTATION-BACKEND.md** (Phase 1)
1. Créer la structure de packages hexagonale (domain, application, infrastructure)
2. Définir les exceptions métier de base (ResourceNotFoundException, BusinessException, UnauthorizedException)
3. Implémenter le gestionnaire global d'exceptions avec mapping vers codes HTTP appropriés
4. Configurer CORS selon les origines autorisées définies

### Phase 3 : Couche de Persistance de Base
Référence : **TEC.md** (Section Base de Données - Entités User, Role, Permission)
1. Créer les entités JPA de sécurité (User, Role, Permission) avec annotations appropriées
2. Définir les relations ManyToMany entre User-Role et Role-Permission avec tables de jointure
3. Créer les interfaces repository Spring Data JPA pour User, Role, Permission
4. Configurer Liquibase avec changelog master et structure de versioning
5. Implémenter le changeset initial pour tables de sécurité (users, roles, permissions, tables de jointure)
6. Créer le changeset de données de référence pour rôles et permissions par défaut (ADMIN, HR_ADMIN, MANAGER, EMPLOYEE, TRAINER)
7. Ajouter les indexes sur colonnes fréquemment requêtées (email, username)

### Phase 4 : Sécurité et Authentification
Référence : **TEC.md** (Section Configuration Spring Security) et **SECURITY.md**
1. Implémenter le service JWT pour génération et validation de tokens (utilise User de Phase 3)
2. Créer le filtre d'authentification JWT dans la chaîne Spring Security
3. Configurer Spring Security avec endpoints publics et protégés, intégration UserDetailsService
4. Implémenter le mécanisme de refresh token avec rotation et stockage en base
5. Ajouter le rate limiting par IP pour prévenir les attaques par force brute
6. Configurer les headers de sécurité (X-Frame-Options, CSP, HSTS)
7. Configurer Swagger/OpenAPI pour documentation automatique avec sécurité JWT

### Phase 5 : Couche de Persistance Métier
Référence : **TEC.md** (Section Modèle de Données) et **TEC.md** (Section Entités Additionnelles)
1. Créer les entités JPA métier : Skill, SkillCategory, Employee, Assessment, Training, TrainingSession, Enrollment, LearningPath, Department, Position, File, EmailTemplate, Budget, BudgetTransaction, TrainerFeedback
2. Définir toutes les relations entre entités métier avec stratégies de fetch optimisées
3. Créer les interfaces repository Spring Data JPA pour chaque entité métier avec méthodes personnalisées
4. Implémenter les changesets Liquibase pour création des tables métier
5. Créer les changesets de données de référence métier (catégories de compétences, niveaux de maîtrise, départements types)
6. Ajouter les indexes composites pour optimisation des requêtes métier fréquentes

### Phase 6 : Modules Métier - Services et API
Référence : **SPEC.md** (Sections Fonctionnalités) et **PLANNING.md**
1. Implémenter séquentiellement les modules selon l'ordre de dépendances : Compétences → Évaluations → Formations → Parcours → Reporting → Administration
2. Pour chaque module : créer les DTOs, les mappers MapStruct, les services métier, les contrôleurs REST
3. Ajouter la validation Bean Validation sur les DTOs avec messages d'erreur personnalisés
4. Implémenter les tests unitaires avec Mockito (couverture minimum 80%)
5. Implémenter les tests d'intégration avec Testcontainers pour PostgreSQL

### Phase 7 : Cache et Performance
Référence : **TEC.md** (Section Stratégie de Cache)
1. Configurer Caffeine comme cache L1 local avec taille maximale et TTL
2. Configurer Redis comme cache L2 distribué avec TTL différenciés par type de données
3. Annoter les méthodes services avec directives de cache (Cacheable, CachePut, CacheEvict)
4. Implémenter la stratégie d'invalidation event-driven lors des modifications
5. Ajouter le monitoring du cache via actuator et métriques Prometheus

### Phase 8 : Observabilité
Référence : **DEPLOYMENT.md** (Section Monitoring)
1. Activer Spring Boot Actuator avec endpoints health, info, metrics, prometheus
2. Configurer les loggers par package avec niveaux appropriés (DEBUG en dev, INFO en prod)
3. Structurer les logs en JSON pour ingestion par ELK ou Loki
4. Exposer les métriques Prometheus sur endpoint dédié
5. Configurer les health checks personnalisés (database, Redis, MinIO)

---

## 8. Données et Migrations

### Stratégie de Migration
Référence : **TEC.md** (Section Liquibase)
1. Utiliser Liquibase exclusivement pour toutes les modifications de schéma (jamais ddl-auto)
2. Organiser les changesets par version majeure dans répertoires séparés
3. Nommer les changesets avec convention : numéro-séquence-description-action
4. Tester chaque migration sur environnement local avant commit
5. Documenter les rollbacks possibles pour chaque changeset critique

### Données de Référence
Référence : **SPEC.md** (Section Référentiel de Compétences)
1. Créer un changeset dédié pour insertion des données de référence (rôles, permissions, catégories de compétences)
2. Charger le référentiel initial de compétences depuis fichier CSV via script Liquibase
3. Initialiser les niveaux de maîtrise standard (1-Débutant à 5-Expert)
4. Créer les départements et postes types de l'organisation

### Données de Test
Référence : **CONTRIBUTING.md** (Section Tests)
1. Maintenir un jeu de données de test cohérent pour environnement de développement
2. Utiliser des données anonymisées en staging (jamais de données production)
3. Implémenter des factories de test pour génération de données aléatoires cohérentes
4. Réinitialiser la base de test entre chaque suite de tests d'intégration

---

## 9. Tests et Qualité

### Types de Tests
Référence : **CONTRIBUTING.md** (Section Tests Backend)
1. **Tests unitaires** : couvrir tous les services métier, mappers, validators avec JUnit 5 et Mockito
2. **Tests d'intégration** : valider les contrôleurs REST avec MockMvc et base de données Testcontainers
3. **Tests de sécurité** : vérifier les permissions, les injections SQL, les attaques XSS
4. **Tests de performance** : charger le système avec JMeter ou Gatling selon profils utilisateur réels

### Critères de Qualité
Référence : **ARCHITECTURE.md** (Section Métriques de Qualité)
1. Couverture de code minimum 80% pour le backend
2. Zéro bug critique ou vulnérabilité détectés par SonarQube
3. Complexité cyclomatique maximum 10 par méthode
4. Duplication de code inférieure à 3%
5. Tous les tests passent avant chaque commit (hook Git)

### Pipeline CI/CD
Référence : **DEPLOYMENT.md** (Section CI/CD)
1. Build automatique à chaque push sur branche develop
2. Exécution tests unitaires et d'intégration dans le pipeline
3. Analyse statique avec SonarQube et rapport de couverture
4. Scan de vulnérabilités avec OWASP Dependency Check et Trivy
5. Build de l'image Docker si tous les tests passent

---

## 10. Sécurité

### Authentification et Autorisation
Référence : **SECURITY.md** (Section Authentification)
1. Implémenter l'authentification JWT stateless avec access et refresh tokens
2. Configurer l'expiration des tokens (1h pour access, 7 jours pour refresh)
3. Implémenter le mécanisme de rotation des refresh tokens à chaque utilisation
4. Stocker les refresh tokens en base avec possibilité de révocation
5. Appliquer le contrôle d'accès basé sur les rôles (RBAC) avec annotations Spring Security

### Protection des Données
Référence : **SECURITY.md** (Section Protection des Données)
1. Chiffrer toutes les communications avec TLS 1.3
2. Chiffrer les données sensibles au repos avec AES-256
3. Hasher les mots de passe avec BCrypt (cost factor 12)
4. Ne jamais logger de données sensibles (mots de passe, tokens, données personnelles)
5. Implémenter la validation stricte de toutes les entrées utilisateur

### Durcissement
Référence : **SECURITY.md** (Section Headers de Sécurité)
1. Configurer les headers de sécurité HTTP (X-Content-Type-Options, X-Frame-Options, CSP)
2. Désactiver CSRF pour API REST stateless mais valider l'origine des requêtes
3. Implémenter le rate limiting pour prévenir les abus (60 requêtes/minute par IP)
4. Bloquer automatiquement les IP après 5 tentatives de connexion échouées
5. Scanner régulièrement les dépendances pour détecter les vulnérabilités connues

---

## 11. Observabilité

### Logging
Référence : **DEPLOYMENT.md** (Section Logs)
1. Configurer Logback avec appenders console et fichier rotatif
2. Structurer les logs en JSON avec timestamp, niveau, logger, message, MDC context
3. Logger tous les événements de sécurité (authentification, changements de permissions)
4. Logger les erreurs avec stack traces complètes pour diagnostic
5. Centraliser les logs dans ELK Stack ou alternative (Loki) pour recherche et analyse

### Métriques
Référence : **TEC.md** (Section Monitoring du Cache)
1. Exposer les métriques Spring Boot Actuator au format Prometheus
2. Monitorer les métriques JVM (heap, GC, threads)
3. Monitorer les métriques applicatives (requêtes HTTP, temps de réponse, erreurs)
4. Monitorer les métriques de cache (hit rate, misses, evictions)
5. Créer des dashboards Grafana pour visualisation temps réel

### Traces
Référence : **STACK.md** (Section DevOps - Monitoring)
1. Intégrer Spring Cloud Sleuth pour distributed tracing
2. Exporter les traces vers Jaeger ou Zipkin
3. Tracer les requêtes critiques de bout en bout (login, évaluations, inscriptions)
4. Ajouter des tags personnalisés pour filtrage et analyse
5. Définir des seuils d'alerte sur les temps de réponse

### Alerting
1. Configurer Prometheus Alertmanager avec règles d'alerte
2. Alerter sur erreurs 5xx dépassant 1% du trafic
3. Alerter sur temps de réponse p95 > 500ms
4. Alerter sur utilisation mémoire > 80%
5. Router les alertes vers Slack, email, ou PagerDuty selon criticité

---

## 12. Déploiement

### Environnements
Référence : **DEPLOYMENT.md** (Section Environnements)
1. **Développement** : Docker Compose local, données de test, logs DEBUG
2. **Staging** : Kubernetes cluster dédié, données anonymisées, configuration proche production
3. **Production** : Kubernetes cluster haute disponibilité, données réelles, monitoring 24/7

### Stratégie de Déploiement
Référence : **DEPLOYMENT.md** (Section Mise à Jour)
1. Utiliser Rolling Update pour déploiements sans interruption
2. Configurer health checks (liveness et readiness probes) pour auto-healing
3. Définir des limites de ressources (CPU, mémoire) pour chaque pod
4. Configurer l'autoscaling horizontal basé sur CPU et mémoire
5. Tester sur staging avant tout déploiement production

### Prérequis Déploiement
Référence : **DEPLOYMENT.md** (Section Checklist)
1. Tous les tests CI/CD passent avec succès
2. Couverture de code respectée (>80%)
3. Aucune vulnérabilité critique détectée
4. Backup de la base de données production effectué
5. Fenêtre de maintenance communiquée aux utilisateurs si nécessaire
6. Plan de rollback préparé et validé

---

## 13. Exploitation et Support

### Opérations Quotidiennes
1. Vérifier les dashboards de monitoring pour détecter les anomalies
2. Consulter les logs d'erreurs et résoudre les problèmes récurrents
3. Surveiller la croissance de la base de données et planifier le scaling
4. Valider que les backups automatiques se sont exécutés correctement
5. Répondre aux alertes critiques selon procédure d'escalade

### Sauvegardes
Référence : **DEPLOYMENT.md** (Section Backup)
1. Sauvegarder PostgreSQL quotidiennement via pg_dump automatisé
2. Conserver 7 sauvegardes quotidiennes + 4 hebdomadaires + 12 mensuelles
3. Sauvegarder les fichiers MinIO quotidiennement
4. Stocker les sauvegardes sur stockage distant distinct (S3, Azure Blob)
5. Tester la restauration trimestriellement pour valider la procédure

### Escalade
Référence : **SECURITY.md** (Section Support)
1. Niveau 1 : Équipe support applicatif (heures ouvrées)
2. Niveau 2 : Développeurs backend (sur astreinte)
3. Niveau 3 : Tech lead et architecte (incidents majeurs)
4. Documenter chaque incident dans système de ticketing
5. Organiser post-mortem pour incidents critiques avec plan d'amélioration

---

## 14. Dépannage

### Symptômes Fréquents
Référence : **README.md** (Section Dépannage)

**Problème : Erreur de connexion à PostgreSQL**
- Cause probable : Service PostgreSQL non démarré ou mauvais credentials
- Actions : Vérifier statut du service, valider les variables d'environnement, consulter les logs PostgreSQL, tester la connectivité réseau

**Problème : Tokens JWT invalides**
- Cause probable : Secret JWT mal configuré ou désynchronisation d'horloge
- Actions : Valider le secret dans les variables d'environnement, synchroniser l'horloge système avec NTP, régénérer les tokens de test

**Problème : Cache Redis non accessible**
- Cause probable : Service Redis arrêté ou mot de passe incorrect
- Actions : Vérifier le statut du service Redis, valider le mot de passe dans la configuration, tester la connexion avec client Redis

**Problème : Performance dégradée**
- Cause probable : Pool de connexions saturé, requêtes N+1, cache inefficace
- Actions : Analyser les métriques JVM, identifier les requêtes lentes dans les logs SQL, optimiser les requêtes avec fetch join, augmenter la taille du cache

**Problème : Tests d'intégration échouent**
- Cause probable : Base de données de test non nettoyée, données incohérentes, Testcontainers non démarré
- Actions : Réinitialiser la base de test, vérifier les fixtures de données, consulter les logs Testcontainers, valider la version Docker

---

## 15. Risques et Points Ouverts

### Risques Identifiés

**Risque 1 : Complexité de migration Liquibase**
- Impact : Rollback difficile en production, corruption de données
- Probabilité : Moyenne
- Mitigation : Tester exhaustivement les migrations en staging, maintenir des scripts de rollback, effectuer backup avant migration

**Risque 2 : Performance du cache distribué**
- Impact : Latence accrue, expérience utilisateur dégradée
- Probabilité : Faible
- Mitigation : Monitorer les hit rates, ajuster les TTL, envisager cache L1 (Caffeine) pour données chaudes

**Risque 3 : Sécurité des secrets en production**
- Impact : Fuite de credentials, compromission système
- Probabilité : Faible
- Mitigation : Utiliser gestionnaire de secrets dédié (Vault, AWS Secrets Manager), rotation automatique, audit d'accès

### Questions Ouvertes

**Question 1 : Stratégie de scaling horizontal**
- Options : Autoscaling Kubernetes basé CPU/mémoire vs basé requêtes applicatives
- Recommandation : Commencer avec CPU/mémoire (plus simple), évoluer vers métriques applicatives si nécessaire
- Décision requise : Définir les seuils de scaling avec équipe Ops

**Question 2 : Gestion des fichiers volumineux**
- Options : Upload direct vers MinIO vs upload via backend avec stream
- Recommandation : Upload direct avec URLs pré-signées pour réduire charge backend
- Décision requise : Valider contraintes de sécurité avec équipe sécurité

**Question 3 : Stratégie de versioning API**
- Options : Versioning dans URL vs via header Accept
- Recommandation : Versioning dans URL (plus explicite et simple)
- Décision requise : Définir politique de dépréciation avec Product Owner

---

## 16. Critères d'Acceptation et Validation

### Critères Fonctionnels
Référence : **SPEC.md** (Sections User Stories)
1. Tous les modules métier implémentés conformément aux user stories
2. Workflows d'approbation fonctionnels pour inscriptions et évaluations
3. Rapports et dashboards affichent les données correctes en temps réel
4. Notifications emails envoyées aux moments appropriés
5. Import/export de données massif opérationnel

### Critères Non-Fonctionnels
Référence : **SPEC.md** (Section Exigences Non-Fonctionnelles)
1. Temps de réponse API p95 < 500ms pour 1000 utilisateurs simultanés
2. Disponibilité minimum 99.5% hors maintenance planifiée
3. Scalabilité vérifiée jusqu'à 5000 utilisateurs actifs
4. Conformité RGPD totale avec implémentation des droits des personnes
5. Sécurité validée par audit externe avec zéro vulnérabilité critique

### Critères Techniques
Référence : **ARCHITECTURE.md** (Section Métriques)
1. Couverture de tests > 80%
2. Zéro bug critique en production
3. Complexité cyclomatique < 10 par méthode
4. Temps de démarrage application < 60 secondes
5. Utilisation mémoire < 2 GB en conditions normales

---

## 17. Checklist Finale

### Avant Mise en Production
Référence : **DEPLOYMENT.md** (Section Checklist de Déploiement)

**Code et Tests**
- [ ] Tous les tests unitaires et d'intégration passent
- [ ] Couverture de code validée (>80%)
- [ ] Code review complété et approuvé par pairs
- [ ] Aucune vulnérabilité critique détectée (SonarQube, Trivy)
- [ ] Documentation API à jour (Swagger)

**Configuration**
- [ ] Variables d'environnement production validées
- [ ] Secrets gérés via gestionnaire dédié (pas de fichiers)
- [ ] Limites de ressources Kubernetes définies
- [ ] Health checks configurés et fonctionnels
- [ ] Autoscaling configuré avec seuils appropriés

**Données et Migration**
- [ ] Backup production récent disponible
- [ ] Migrations Liquibase testées en staging
- [ ] Scripts de rollback préparés et testés
- [ ] Données de référence chargées
- [ ] Index de base de données créés

**Sécurité**
- [ ] JWT secret robuste généré
- [ ] HTTPS/TLS configuré avec certificat valide
- [ ] CORS configuré avec origines strictes
- [ ] Rate limiting activé
- [ ] Logs de sécurité activés

**Observabilité**
- [ ] Logs centralisés fonctionnels
- [ ] Métriques Prometheus exposées
- [ ] Dashboards Grafana créés
- [ ] Alertes configurées avec destinataires
- [ ] Traces distribuées activées

**Opérations**
- [ ] Procédure de déploiement documentée
- [ ] Plan de rollback validé
- [ ] Équipe d'astreinte informée
- [ ] Fenêtre de maintenance communiquée
- [ ] Support niveau 1 formé

**Validation Finale**
- [ ] Tests de charge réussis en staging
- [ ] Tests de sécurité réussis (pentest)
- [ ] Validation Product Owner obtenue
- [ ] Go/No-Go production approuvé par équipe technique
- [ ] Tous les critères d'acceptation validés

---

**Document préparé par** : Équipe Développement Backend
**Date de rédaction** : 2025-01-15
**Version** : 1.0
**Prochaine revue** : 2025-04-15
