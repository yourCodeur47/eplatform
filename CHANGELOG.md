# Changelog

Tous les changements notables de ce projet seront documentés dans ce fichier.

Le format est basé sur [Keep a Changelog](https://keepachangelog.com/fr/1.0.0/),
et ce projet adhère au [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### À Venir (Roadmap)

- Module Feedback Formateur
- Module Gestion Budgétaire Avancée
- Module Archivage et Purge RGPD
- Tests de performance et optimisation
- Intégration avec SIRH externe

---

## [1.0.0] - 2025-12-15 (Planifié)

### Release Initiale

Première version production-ready de la plateforme de gestion de compétences et formation.

#### Ajouté
- ✅ Module Authentification et Gestion des Utilisateurs
  - Inscription et connexion JWT
  - Gestion des rôles et permissions (RBAC)
  - Récupération de mot de passe
  - Import CSV d'utilisateurs

- ✅ Module Référentiel de Compétences
  - CRUD compétences avec catégorisation
  - Niveaux de maîtrise (5 niveaux)
  - Association compétences-métiers
  - Import/export du référentiel

- ✅ Module Évaluation des Compétences
  - Auto-évaluation employé avec justificatifs
  - Validation manager
  - Évaluation 360°
  - Tests de compétences automatisés (QCM)
  - Campagnes d'évaluation planifiées

- ✅ Module Analyse des Écarts
  - Cartographie des compétences (individuelle et collective)
  - Calcul automatique des écarts (requis vs actuel)
  - Recommandations de formations
  - Prédiction des besoins futurs

- ✅ Module Catalogue de Formations
  - Gestion du catalogue (présentiel, e-learning, blended)
  - Organisation des sessions
  - Contenus e-learning (SCORM)
  - Certifications et badges numériques

- ✅ Module Parcours de Formation
  - Création de parcours personnalisés
  - Parcours types par métier
  - Suivi de progression avec timeline
  - Gamification (XP, badges, classements)

- ✅ Module Gestion des Inscriptions
  - Inscription avec workflow de validation (Manager → RH → Budget)
  - Planning individuel avec synchronisation calendrier
  - Planning des ressources (formateurs, salles)

- ✅ Module Suivi et Évaluation des Formations
  - Émargement électronique (QR code)
  - Évaluation à chaud et à froid
  - Génération d'attestations automatique
  - Calcul du ROI des formations

- ✅ Module Tableaux de Bord et Reporting
  - Dashboard employé (mes compétences, mes formations)
  - Dashboard manager (équipe, écarts, budget)
  - Dashboard RH (indicateurs globaux, ROI)
  - Dashboard direction (vision stratégique)
  - Générateur de rapports personnalisés

- ✅ Module Notifications et Communication
  - Notifications en temps réel (WebSocket)
  - Emails automatiques avec templates personnalisables
  - Messagerie interne
  - Annonces et actualités

- ✅ Module Administration Système
  - Configuration générale paramétrable
  - Gestion de l'organisation (départements, hiérarchie)
  - Import/export de données massif
  - Logs d'audit complets
  - Sauvegardes automatiques

- ✅ Module API et Intégrations
  - API REST documentée (Swagger/OpenAPI)
  - Webhooks pour événements
  - Support SSO (SAML 2.0)
  - Endpoints pour intégration SIRH

#### Sécurité
- 🔒 Authentification JWT avec refresh tokens
- 🔒 Chiffrement TLS 1.3
- 🔒 Chiffrement AES-256 des données au repos
- 🔒 Protection CSRF, XSS, SQL Injection
- 🔒 Rate limiting (60 req/min par IP)
- 🔒 MFA optionnelle (TOTP)
- 🔒 Politique de mots de passe robuste
- 🔒 Logs d'audit et traçabilité

#### Performance
- ⚡ Cache multi-niveaux (Caffeine + Redis)
- ⚡ Pagination sur toutes les listes
- ⚡ Lazy loading Angular
- ⚡ API réponse < 500ms (p95)
- ⚡ Support de 1000 utilisateurs simultanés

#### Conformité
- 📋 Conformité RGPD complète
  - Droit d'accès, rectification, oubli, portabilité
  - Consentements granulaires
  - Anonymisation automatique après 5 ans
  - Export de données personnelles
- 📋 Conformité WCAG 2.1 niveau AA (accessibilité)
- 📋 Support multilingue (FR, EN)

#### Infrastructure
- 🐳 Conteneurisation Docker
- ☸️ Déploiement Kubernetes
- 📊 Monitoring Prometheus + Grafana
- 📝 Logs centralisés (ELK Stack)
- 🔄 CI/CD GitLab pipelines
- 💾 Base de données PostgreSQL 16
- 🗄️ Cache Redis 7
- 📁 Stockage fichiers MinIO

---

## [0.9.0] - 2025-11-30 (Beta)

### Version Beta pour Tests Utilisateurs

#### Ajouté
- Version beta de tous les modules fonctionnels
- Tests E2E complets (Cypress)
- Documentation utilisateur
- Vidéos tutoriels

#### Modifié
- Optimisations de performance
- Amélioration de l'UX suite aux retours
- Corrections de bugs identifiés en alpha

#### Corrigé
- [TRAIN-145] Validation des inscriptions ne fonctionnait pas pour les formations externes
- [EVAL-089] Calcul des écarts incorrect pour évaluations 360°
- [DASH-067] Dashboard manager affichait des données obsolètes
- [NOTIF-034] Notifications emails dupliquées

---

## [0.8.0] - 2025-11-01 (Alpha)

### Version Alpha pour Tests Internes

#### Ajouté
- Modules principaux implémentés (80% des fonctionnalités)
- Tests unitaires et d'intégration
- Documentation technique
- Environnement de staging

#### Connu
- Performance non optimisée
- UX à améliorer
- Documentation utilisateur incomplète

---

## [0.5.0] - 2025-09-15 (Prototype)

### Prototype Fonctionnel (MVP)

#### Ajouté
- Authentification JWT
- CRUD Compétences
- Auto-évaluation basique
- Catalogue de formations
- Dashboard simple

---

## [0.1.0] - 2025-06-01 (POC)

### Proof of Concept

#### Ajouté
- Setup initial du projet
- Architecture hexagonale
- Configuration Docker
- CI/CD basique
- README et documentation de base

---

## Légende des Types de Changements

- `Ajouté` : Nouvelles fonctionnalités
- `Modifié` : Changements dans les fonctionnalités existantes
- `Déprécié` : Fonctionnalités qui seront supprimées dans les futures versions
- `Supprimé` : Fonctionnalités supprimées
- `Corrigé` : Corrections de bugs
- `Sécurité` : Corrections de vulnérabilités

---

## Liens Utiles

- [Releases](https://github.com/company/eplatform/releases)
- [Issues](https://github.com/company/eplatform/issues)
- [Roadmap](https://github.com/company/eplatform/projects/1)
- [Documentation](https://docs.eplatform.company.com)

---

**Format** : [Keep a Changelog](https://keepachangelog.com/fr/1.0.0/)
**Versioning** : [Semantic Versioning](https://semver.org/spec/v2.0.0.html)
