# GitHub Actions Workflows

Ce répertoire contient les workflows GitHub Actions pour le projet E-Platform.

## 📋 Liste des Workflows

### 1. Backend CI (`backend-ci.yml`)

**Déclencheurs:**
- Push sur `main` et `develop` (chemins: `backend/**`)
- Pull requests vers `main` et `develop` (chemins: `backend/**`)

**Actions:**
- ✅ Build Maven avec JDK 21
- ✅ Tests unitaires et d'intégration avec PostgreSQL et Redis
- ✅ Génération de rapport de couverture JaCoCo (minimum 80%)
- ✅ Upload des artefacts (JAR et rapports)
- ✅ Commentaire de couverture sur les PRs
- ✅ Analyse de qualité de code avec SonarCloud
- ✅ Quality Gate check (si SonarCloud configuré)

**Services:**
- PostgreSQL 15 (port 5432)
- Redis 7 (port 6379)

**Artefacts générés:**
- `jacoco-report`: Rapport de couverture de code
- `eplatform-backend-jar`: Application packagée

---

### 2. Security Scan (`security.yml`)

**Déclencheurs:**
- Push sur `main` et `develop`
- Pull requests
- Planifié: Tous les lundis à 9h UTC

**Actions:**
- 🔒 OWASP Dependency Check (fail si CVSS ≥ 7)
- 🔒 CodeQL Analysis pour Java
- 🔒 TruffleHog Secret Scanning
- 🔒 Trivy Vulnerability Scan

**Artefacts:**
- Rapports de sécurité uploadés dans l'onglet Security

---

### 3. Docker Build & Push (`docker-build.yml`)

**Déclencheurs:**
- Push sur `main` et `develop`
- Tags `v*`
- Pull requests vers `main`

**Actions:**
- 🐳 Build multi-architecture (amd64, arm64)
- 🐳 Push vers GitHub Container Registry
- 🐳 Scan de sécurité Trivy sur l'image
- 🐳 Tagging automatique basé sur les branches/tags

**Tags générés:**
- `latest` (branche par défaut)
- `main`, `develop` (noms de branches)
- `v1.0.0`, `v1.0` (versions sémantiques)
- `main-abc123` (SHA du commit)

---

### 4. PR Labeler (`pr-labeler.yml`)

**Déclencheurs:**
- Ouverture, synchronisation, réouverture de PR

**Actions:**
- 🏷️ Labellisation automatique basée sur les fichiers modifiés
- 🏷️ Ajout de labels de taille (`size/xs` à `size/xl`)

**Labels disponibles:**
- `backend`, `frontend`, `documentation`
- `dependencies`, `docker`, `ci/cd`
- `security`, `database`
- `size/xs`, `size/s`, `size/m`, `size/l`, `size/xl`

---

### 5. Deploy (`deploy.yml`)

**Déclencheurs:**
- Push sur `main` → Production
- Push sur `develop` → Staging
- Manuel via `workflow_dispatch`

**Environnements:**
- **Production**: Déploiement depuis `main`
- **Staging**: Déploiement depuis `develop`

**Actions:**
- 🚀 Déploiement automatique
- 🧪 Smoke tests post-déploiement
- 🔄 Rollback automatique en cas d'échec

> ⚠️ **Note**: Le workflow de déploiement contient des placeholders. Vous devez le personnaliser selon votre infrastructure (AWS, GCP, Azure, Kubernetes, etc.).

---

## 🔐 Secrets Requis

Configurez ces secrets dans les paramètres du repository:

### Pour l'analyse de qualité (recommandé):

| Secret | Description | Workflow |
|--------|-------------|----------|
| `SONAR_TOKEN` | Token d'authentification SonarCloud | backend-ci.yml |
| `SONAR_ORGANIZATION` | Nom de votre organisation SonarCloud | backend-ci.yml |

> 📖 **Voir le guide complet** : [SONARCLOUD-SETUP.md](../SONARCLOUD-SETUP.md)

### Pour le déploiement:

À configurer selon votre infrastructure cible:
- `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` (AWS)
- `GCP_SA_KEY` (Google Cloud)
- `AZURE_CREDENTIALS` (Azure)
- `KUBECONFIG` (Kubernetes)

---

## 🔧 Configuration

### Activer SonarCloud (recommandé)

SonarCloud analyse la qualité du code Java (et TypeScript/JavaScript quand le frontend sera ajouté).

**Configuration rapide :**
1. Créer un compte sur [sonarcloud.io](https://sonarcloud.io) avec GitHub
2. Importer votre projet
3. Configurer les secrets (voir [SONARCLOUD-SETUP.md](../SONARCLOUD-SETUP.md))

Le workflow activera automatiquement l'analyse dès que les secrets sont configurés.

### Configurer le déploiement

1. Modifier `.github/workflows/deploy.yml`
2. Remplacer les placeholders par vos commandes de déploiement
3. Configurer les environnements dans GitHub:
   - Settings → Environments → Créer `staging` et `production`
   - Ajouter des règles de protection (reviewers, wait timer, etc.)

### Personnaliser les labels de PR

Modifier `.github/labeler.yml` pour ajouter/modifier les règles de labellisation automatique.

---

## 📊 Badges de Statut

Ajoutez ces badges à votre README.md principal:

```markdown
![Backend CI](https://github.com/VOTRE_USERNAME/eplatform/workflows/Backend%20CI/badge.svg)
![Security Scan](https://github.com/VOTRE_USERNAME/eplatform/workflows/Security%20Scan/badge.svg)
![Docker Build](https://github.com/VOTRE_USERNAME/eplatform/workflows/Docker%20Build%20%26%20Push/badge.svg)
```

---

## 🐛 Dépannage

### Les tests échouent avec PostgreSQL

Vérifiez que les variables d'environnement de connexion correspondent aux services définis dans le workflow.

### JaCoCo échoue sur le seuil de 80%

Vous pouvez ajuster le seuil dans `backend/pom.xml`:
```xml
<jacoco.minimum.coverage>0.80</jacoco.minimum.coverage>
```

### Docker build échoue

- Vérifiez que le `Dockerfile` existe dans `backend/`
- Vérifiez que le fichier JAR final est bien `eplatform-backend.jar`

### Secrets non détectés

Les secrets doivent être configurés au niveau du repository (Settings → Secrets and variables → Actions).

---

## 📚 Ressources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Docker Build Push Action](https://github.com/docker/build-push-action)
- [CodeQL](https://codeql.github.com/)
- [Trivy](https://aquasecurity.github.io/trivy/)
- [OWASP Dependency Check](https://owasp.org/www-project-dependency-check/)
