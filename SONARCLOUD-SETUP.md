# Configuration SonarCloud pour E-Platform

Ce guide vous aidera à configurer SonarCloud pour analyser automatiquement la qualité du code de votre projet.

## 📋 Prérequis

- Compte GitHub
- Projet hébergé sur GitHub
- Accès administrateur au repository

## 🚀 Étapes de configuration

### 1. Créer un compte SonarCloud

1. Rendez-vous sur [sonarcloud.io](https://sonarcloud.io)
2. Cliquez sur **"Sign up"**
3. Choisissez **"With GitHub"**
4. Autorisez SonarCloud à accéder à votre compte GitHub

### 2. Importer votre projet

1. Une fois connecté, cliquez sur **"+"** en haut à droite
2. Sélectionnez **"Analyze new project"**
3. Choisissez votre organization GitHub
4. Sélectionnez le repository **eplatform**
5. Cliquez sur **"Set Up"**

### 3. Configurer le projet

1. Choisissez **"With GitHub Actions"** comme méthode d'analyse
2. SonarCloud va vous donner deux informations importantes :
   - **`SONAR_TOKEN`** : Token d'authentification
   - **`SONAR_ORGANIZATION`** : Nom de votre organisation

### 4. Ajouter les secrets GitHub

1. Allez dans les paramètres de votre repository GitHub :
   ```
   https://github.com/VOTRE_USERNAME/eplatform/settings/secrets/actions
   ```

2. Cliquez sur **"New repository secret"**

3. Ajoutez le premier secret :
   - **Name** : `SONAR_TOKEN`
   - **Value** : Collez le token fourni par SonarCloud
   - Cliquez sur **"Add secret"**

4. Ajoutez le second secret :
   - **Name** : `SONAR_ORGANIZATION`
   - **Value** : Votre nom d'organisation (généralement votre username GitHub)
   - Cliquez sur **"Add secret"**

### 5. Mettre à jour sonar-project.properties

Éditez le fichier `sonar-project.properties` à la racine du projet :

```properties
# Remplacez ces valeurs par les vôtres
sonar.projectKey=VOTRE_USERNAME_eplatform
sonar.links.homepage=https://github.com/VOTRE_USERNAME/eplatform
sonar.links.ci=https://github.com/VOTRE_USERNAME/eplatform/actions
sonar.links.issue=https://github.com/VOTRE_USERNAME/eplatform/issues
sonar.links.scm=https://github.com/VOTRE_USERNAME/eplatform
```

**Important** : Le `sonar.projectKey` doit correspondre exactement à celui fourni par SonarCloud.

### 6. Tester la configuration

1. Créez une branche de test :
   ```bash
   git checkout -b test-sonarcloud
   ```

2. Faites un petit changement (par exemple, ajoutez un commentaire dans un fichier Java)

3. Commitez et pushez :
   ```bash
   git add .
   git commit -m "Test SonarCloud integration"
   git push origin test-sonarcloud
   ```

4. Créez une Pull Request sur GitHub

5. Le workflow **Backend CI** devrait se déclencher automatiquement

6. Vérifiez les logs du workflow pour voir l'analyse SonarCloud

7. Rendez-vous sur [sonarcloud.io](https://sonarcloud.io) pour voir les résultats

## 📊 Ce qui sera analysé

SonarCloud analysera :

- ✅ **Bugs** : Erreurs de code qui peuvent causer des problèmes
- ✅ **Vulnérabilités** : Problèmes de sécurité potentiels
- ✅ **Code Smells** : Problèmes de maintenabilité
- ✅ **Couverture** : Pourcentage de code testé (via JaCoCo)
- ✅ **Duplication** : Code dupliqué
- ✅ **Complexité** : Complexité cyclomatique du code

## 🎯 Quality Gate

Le Quality Gate par défaut échouera si :

- Note de fiabilité < A
- Note de sécurité < A
- Note de maintenabilité < A
- Couverture < 80% (configuré dans votre pom.xml)
- Duplication > 3%

Vous pouvez personnaliser ces seuils dans SonarCloud → Your Project → Quality Gates.

## 🔧 Configuration avancée

### Exclure des fichiers de l'analyse

Éditez `sonar-project.properties` :

```properties
sonar.exclusions=\
  **/*Application.java,\
  **/config/**,\
  **/VotreFichier.java
```

### Désactiver des règles spécifiques

1. Allez sur SonarCloud
2. Sélectionnez votre projet
3. Allez dans **Quality Profiles** → **Java**
4. Désactivez les règles qui ne vous conviennent pas

### Pull Request Decoration

SonarCloud commentera automatiquement vos Pull Requests avec :
- Nouveaux bugs/vulnérabilités introduits
- Couverture de code des nouvelles lignes
- Code smells ajoutés

## 🆘 Dépannage

### L'analyse ne se déclenche pas

Vérifiez que :
- Les secrets `SONAR_TOKEN` et `SONAR_ORGANIZATION` sont bien configurés
- Le workflow est déclenché (push sur main/develop ou PR)
- Les chemins dans le trigger incluent des modifications backend

### L'analyse échoue

Vérifiez :
- Les logs du workflow GitHub Actions
- Que `sonar.projectKey` correspond à celui de SonarCloud
- Que le token n'a pas expiré

### Quality Gate échoue

C'est normal au début ! Améliorez progressivement :
1. Corrigez les bugs critiques
2. Corrigez les vulnérabilités de sécurité
3. Augmentez la couverture de tests
4. Réduisez les code smells

## 📚 Ressources

- [Documentation SonarCloud](https://docs.sonarcloud.io/)
- [Règles Java](https://rules.sonarsource.com/java/)
- [Quality Gates](https://docs.sonarcloud.io/improving/quality-gates/)
- [Pull Request Decoration](https://docs.sonarcloud.io/enriching/pr-decoration/)

## 🔮 Prochaines étapes

Quand vous ajouterez le frontend Angular :

1. Décommentez les lignes frontend dans `sonar-project.properties`
2. Configurez la couverture Angular (Karma/Jest)
3. SonarCloud analysera automatiquement TypeScript/JavaScript

---

**Note** : Le workflow actuel ne bloquera PAS les PRs si SonarCloud n'est pas configuré. Cela vous laisse le temps de le mettre en place sans bloquer le développement.
