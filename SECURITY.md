# Politique de Sécurité

## Versions Supportées

| Version | Supportée          |
| ------- | ------------------ |
| 1.x.x   | :white_check_mark: |
| < 1.0   | :x:                |

---

## Signaler une Vulnérabilité

### Processus de Signalement

Si vous découvrez une vulnérabilité de sécurité, **ne créez PAS d'issue publique**. Suivez ce processus :

1. **Email** : Envoyez un email à `security@company.com` avec :
   - Description détaillée de la vulnérabilité
   - Étapes pour reproduire
   - Impact potentiel
   - Votre nom/pseudo (optionnel pour crédit)

2. **Réponse** : Nous répondrons sous **48 heures** pour :
   - Confirmer la réception
   - Évaluer la gravité
   - Estimer le délai de correction

3. **Correction** :
   - **Critique** : Correction sous 7 jours
   - **Haute** : Correction sous 14 jours
   - **Moyenne** : Correction sous 30 jours
   - **Basse** : Correction dans la prochaine release

4. **Divulgation** : Une fois la correction déployée :
   - Publication d'un security advisory
   - Crédit au découvreur (si souhaité)
   - Mise à jour du CHANGELOG

### Récompense (Bug Bounty)

Nous offrons des récompenses selon la gravité :

| Gravité   | Récompense |
|-----------|------------|
| Critique  | 500€ - 1000€ |
| Haute     | 200€ - 500€ |
| Moyenne   | 50€ - 200€ |
| Basse     | Reconnaissance publique |

---

## Mesures de Sécurité Implémentées

### Authentification et Autorisation

✅ **JWT avec refresh tokens**
- Tokens signés avec HMAC-SHA256
- Access token : 1h de validité
- Refresh token : 7 jours de validité
- Rotation des refresh tokens

✅ **MFA (Authentification à deux facteurs)**
- TOTP (Time-based One-Time Password)
- Optionnel pour les utilisateurs
- Obligatoire pour les administrateurs

✅ **Politique de mots de passe**
- Minimum 12 caractères
- Au moins 1 majuscule, 1 minuscule, 1 chiffre, 1 caractère spécial
- Historique des 5 derniers mots de passe
- Expiration tous les 90 jours

✅ **Verrouillage de compte**
- Blocage après 5 tentatives échouées
- Durée de blocage : 15 minutes
- Notification par email

### Protection des Données

✅ **Chiffrement**
- TLS 1.3 pour les communications
- AES-256 pour les données au repos
- Chiffrement des sauvegardes
- Hash BCrypt (cost factor 12) pour les mots de passe

✅ **Conformité RGPD**
- Consentement explicite
- Droit à l'oubli implémenté
- Portabilité des données
- Anonymisation après 5 ans
- Logs d'audit pendant 12 mois

✅ **Protection des fichiers uploadés**
- Scan antivirus (ClamAV)
- Vérification du type MIME
- Limite de taille : 10 MB par fichier
- Stockage isolé (MinIO avec ACLs)

### Protection Applicative

✅ **Injections SQL**
- JPA/Hibernate avec requêtes préparées
- Pas de requêtes SQL dynamiques
- Validation des paramètres

✅ **XSS (Cross-Site Scripting)**
- Sanitisation des entrées (OWASP Java HTML Sanitizer)
- Content Security Policy (CSP) headers
- HttpOnly cookies

✅ **CSRF (Cross-Site Request Forgery)**
- Tokens CSRF pour les formulaires
- SameSite cookies
- Vérification de l'origine

✅ **Rate Limiting**
- 60 requêtes/minute par IP
- 1000 requêtes/heure par utilisateur authentifié
- Protection contre le brute-force

✅ **Headers de sécurité**
```
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000; includeSubDomains
Content-Security-Policy: default-src 'self'
```

### Logging et Monitoring

✅ **Logs de sécurité**
- Authentifications (succès/échec)
- Modifications de permissions
- Accès aux données sensibles
- Actions administratives
- Export de données personnelles

✅ **Alertes temps réel**
- Tentatives de connexion multiples échouées
- Changement de mot de passe
- Modification de permissions
- Accès anormal (géolocalisation inhabituelle)

✅ **Audit trail**
- Traçabilité complète des actions
- Who, What, When, Where
- Conservation 12 mois minimum
- Export pour analyse forensique

---

## Tests de Sécurité

### Tests Automatisés

- **OWASP Dependency Check** : Scan des dépendances vulnérables
- **SonarQube** : Analyse statique de code
- **Trivy** : Scan des images Docker
- **npm audit / mvn dependency:check** : Audit des dépendances

### Tests Manuels

- **Pentest annuel** : Par organisme externe certifié
- **Code review sécurité** : Pour chaque PR critique
- **Tests de régression sécurité** : Avant chaque release majeure

---

## Checklist de Sécurité pour les Développeurs

Avant chaque commit :

- [ ] Pas de secrets (API keys, passwords) en dur dans le code
- [ ] Validation de toutes les entrées utilisateur
- [ ] Échappement des sorties pour prévenir XSS
- [ ] Requêtes SQL paramétrées (pas de concaténation)
- [ ] Vérification des autorisations (qui peut faire quoi ?)
- [ ] Logs sans données sensibles (pas de passwords, tokens, données perso)
- [ ] Dépendances à jour (pas de vulnérabilités connues)
- [ ] Tests de sécurité passés

---

## Incidents de Sécurité Passés

### 2025

*Aucun incident à ce jour*

---

## Ressources

### Standards et Frameworks

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OWASP ASVS](https://owasp.org/www-project-application-security-verification-standard/)
- [CWE Top 25](https://cwe.mitre.org/top25/)

### Outils de Test

- [OWASP ZAP](https://www.zaproxy.org/) - Proxy de sécurité
- [Burp Suite](https://portswigger.net/burp) - Tests d'intrusion
- [SQLMap](https://sqlmap.org/) - Tests d'injection SQL

### Formation

- [OWASP WebGoat](https://owasp.org/www-project-webgoat/) - Apprentissage pratique
- [PortSwigger Academy](https://portswigger.net/web-security) - Cours gratuits

---

## Contact Sécurité

- **Email sécurité** : security@company.com
- **PGP Key** : [Télécharger la clé publique](https://company.com/pgp-key.asc)
- **Bug Bounty** : https://company.com/security/bug-bounty
- **Security Advisories** : https://github.com/company/eplatform/security/advisories

---

**Dernière mise à jour** : 2025-11-13
**Prochaine revue** : 2025-12-13
