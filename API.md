# Documentation API - Plateforme de Gestion de Compétences

## Base URL

```
Development: http://localhost:8080/api
Staging: https://staging-api.company.com/api
Production: https://api.company.com/api
```

## Authentification

Toutes les requêtes (sauf `/auth/*`) nécessitent un token JWT dans le header :

```http
Authorization: Bearer {access_token}
```

### Obtenir un Token

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@company.com",
  "password": "password123"
}
```

**Réponse (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "dGVzdC1yZWZyZXNo...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "email": "user@company.com",
    "firstName": "Jean",
    "lastName": "Dupont",
    "roles": ["EMPLOYEE", "MANAGER"]
  }
}
```

### Rafraîchir un Token

```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "dGVzdC1yZWZyZXNo..."
}
```

---

## Conventions API

### Format de Requête/Réponse

- **Content-Type**: `application/json`
- **Date Format**: ISO 8601 (`2025-01-15T10:30:00Z`)
- **Pagination**: Query params `page`, `size`, `sort`

### Codes de Statut HTTP

| Code | Description |
|------|-------------|
| 200 | OK - Succès |
| 201 | Created - Ressource créée |
| 204 | No Content - Suppression réussie |
| 400 | Bad Request - Validation échouée |
| 401 | Unauthorized - Token manquant/invalide |
| 403 | Forbidden - Permissions insuffisantes |
| 404 | Not Found - Ressource introuvable |
| 409 | Conflict - Conflit (ex: email déjà existant) |
| 429 | Too Many Requests - Rate limit dépassé |
| 500 | Internal Server Error - Erreur serveur |

### Format des Erreurs

```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation échouée",
  "errors": [
    {
      "field": "email",
      "message": "Email invalide"
    },
    {
      "field": "password",
      "message": "Le mot de passe doit contenir au moins 12 caractères"
    }
  ],
  "path": "/api/users"
}
```

### Pagination

**Requête:**
```http
GET /api/skills?page=0&size=20&sort=name,asc
```

**Réponse:**
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0,
    "paged": true
  },
  "totalElements": 125,
  "totalPages": 7,
  "last": false,
  "first": true,
  "numberOfElements": 20
}
```

---

## Endpoints Principaux

### 🔐 Authentification

#### POST /api/auth/register
Inscription d'un nouvel utilisateur

**Body:**
```json
{
  "email": "new.user@company.com",
  "password": "SecurePass123!",
  "firstName": "Marie",
  "lastName": "Martin",
  "birthDate": "1990-05-15",
  "departmentId": 3,
  "jobPositionId": 5
}
```

**Réponse (201):**
```json
{
  "id": 42,
  "email": "new.user@company.com",
  "firstName": "Marie",
  "lastName": "Martin",
  "status": "PENDING_ACTIVATION"
}
```

#### POST /api/auth/logout
Déconnexion (invalide le refresh token)

---

### 👤 Utilisateurs

#### GET /api/users
Liste tous les utilisateurs (ADMIN/HR)

**Query Params:**
- `page`, `size`, `sort`: Pagination
- `search`: Recherche texte libre
- `departmentId`: Filtrer par département
- `status`: Filtrer par statut (ACTIVE, INACTIVE, SUSPENDED)

**Réponse (200):**
```json
{
  "content": [
    {
      "id": 1,
      "email": "john.doe@company.com",
      "firstName": "John",
      "lastName": "Doe",
      "department": {
        "id": 2,
        "name": "IT"
      },
      "jobPosition": {
        "id": 5,
        "title": "Développeur Senior"
      },
      "status": "ACTIVE",
      "roles": ["EMPLOYEE", "MANAGER"]
    }
  ],
  "totalElements": 42
}
```

#### GET /api/users/{id}
Détail d'un utilisateur

**Réponse (200):**
```json
{
  "id": 1,
  "email": "john.doe@company.com",
  "firstName": "John",
  "lastName": "Doe",
  "birthDate": "1985-03-20",
  "createdAt": "2024-01-10T09:00:00Z",
  "lastLogin": "2025-01-15T08:30:00Z",
  "department": {
    "id": 2,
    "name": "IT",
    "code": "IT-DEV"
  },
  "jobPosition": {
    "id": 5,
    "title": "Développeur Senior"
  },
  "manager": {
    "id": 10,
    "firstName": "Alice",
    "lastName": "Manager"
  },
  "status": "ACTIVE",
  "roles": ["EMPLOYEE", "MANAGER"]
}
```

#### GET /api/users/me
Profil de l'utilisateur connecté

#### PUT /api/users/{id}
Modifier un utilisateur (ADMIN/HR ou soi-même pour certains champs)

**Body:**
```json
{
  "firstName": "John Updated",
  "lastName": "Doe",
  "departmentId": 3,
  "jobPositionId": 6
}
```

---

### 🎯 Compétences

#### GET /api/skills
Liste toutes les compétences

**Query Params:**
- `categoryId`: Filtrer par catégorie
- `type`: Filtrer par type (TECHNICAL, SOFT_SKILL, BUSINESS, MANAGERIAL)
- `search`: Recherche par nom

**Réponse (200):**
```json
{
  "content": [
    {
      "id": 1,
      "code": "JAVA",
      "name": "Java",
      "description": "Langage de programmation orienté objet",
      "type": "TECHNICAL",
      "category": {
        "id": 5,
        "name": "Langages de Programmation"
      },
      "isActive": true
    }
  ]
}
```

#### POST /api/skills
Créer une compétence (HR_ADMIN/ADMIN)

**Body:**
```json
{
  "code": "SPRING",
  "name": "Spring Framework",
  "description": "Framework Java pour applications d'entreprise",
  "type": "TECHNICAL",
  "categoryId": 5
}
```

**Réponse (201):**
```json
{
  "id": 15,
  "code": "SPRING",
  "name": "Spring Framework",
  "description": "Framework Java pour applications d'entreprise",
  "type": "TECHNICAL",
  "category": {
    "id": 5,
    "name": "Langages de Programmation"
  },
  "createdAt": "2025-01-15T10:30:00Z"
}
```

#### GET /api/skills/{id}
Détail d'une compétence

#### PUT /api/skills/{id}
Modifier une compétence (HR_ADMIN/ADMIN)

#### DELETE /api/skills/{id}
Supprimer une compétence (HR_ADMIN/ADMIN)

---

### 📊 Évaluations

#### GET /api/evaluations
Liste les évaluations

**Query Params:**
- `userId`: Filtrer par utilisateur
- `campaignId`: Filtrer par campagne
- `status`: Filtrer par statut (DRAFT, SUBMITTED, VALIDATED, CLOSED)

**Réponse (200):**
```json
{
  "content": [
    {
      "id": 100,
      "employee": {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe"
      },
      "skill": {
        "id": 1,
        "name": "Java"
      },
      "selfAssessedLevel": {
        "id": 3,
        "level": 3,
        "name": "Confirmé"
      },
      "managerAssessedLevel": {
        "id": 4,
        "level": 4,
        "name": "Expert"
      },
      "validatedLevel": {
        "id": 3,
        "level": 3,
        "name": "Confirmé"
      },
      "status": "VALIDATED",
      "selfEvaluationDate": "2025-01-10T14:00:00Z",
      "managerEvaluationDate": "2025-01-12T16:00:00Z"
    }
  ]
}
```

#### POST /api/evaluations
Créer une auto-évaluation

**Body:**
```json
{
  "skillId": 1,
  "selfAssessedLevelId": 3,
  "selfComment": "J'ai travaillé sur plusieurs projets Java ces 2 dernières années",
  "evidences": [
    {
      "fileName": "certification-java.pdf",
      "filePath": "/uploads/cert-123.pdf",
      "description": "Certification Oracle Java SE 11"
    }
  ]
}
```

#### PUT /api/evaluations/{id}/validate
Valider une évaluation (MANAGER)

**Body:**
```json
{
  "managerAssessedLevelId": 4,
  "managerComment": "Excellent niveau technique, projets complexes réussis",
  "validatedLevelId": 4
}
```

---

### 🎓 Formations

#### GET /api/trainings
Liste les formations du catalogue

**Query Params:**
- `type`: Filtrer par type (IN_PERSON, E_LEARNING, BLENDED, EXTERNAL)
- `skillId`: Formations développant une compétence
- `search`: Recherche texte

**Réponse (200):**
```json
{
  "content": [
    {
      "id": 20,
      "code": "JAVA-ADV",
      "title": "Java Avancé",
      "description": "Formation approfondie sur Java",
      "type": "IN_PERSON",
      "duration": 21,
      "price": 1200.00,
      "provider": "Formation Inc.",
      "maxParticipants": 12,
      "averageRating": 4.5,
      "targetedSkills": [
        {
          "id": 1,
          "name": "Java"
        }
      ]
    }
  ]
}
```

#### GET /api/trainings/{id}/sessions
Sessions d'une formation

**Réponse (200):**
```json
{
  "content": [
    {
      "id": 50,
      "reference": "JAVA-ADV-2025-01",
      "startDate": "2025-02-10",
      "endDate": "2025-02-12",
      "location": "Paris - Salle A",
      "trainer": {
        "id": 15,
        "firstName": "Pierre",
        "lastName": "Formateur"
      },
      "availableSeats": 5,
      "registeredCount": 7,
      "status": "CONFIRMED"
    }
  ]
}
```

#### POST /api/registrations
S'inscrire à une formation

**Body:**
```json
{
  "sessionId": 50,
  "justification": "Besoin d'approfondir mes compétences Java pour le projet X"
}
```

**Réponse (201):**
```json
{
  "id": 200,
  "session": {
    "id": 50,
    "reference": "JAVA-ADV-2025-01"
  },
  "status": "PENDING",
  "registrationDate": "2025-01-15T10:30:00Z"
}
```

#### PUT /api/registrations/{id}/approve
Approuver une inscription (MANAGER)

**Body:**
```json
{
  "approved": true,
  "comment": "Formation alignée avec les objectifs de développement"
}
```

---

### 📈 Écarts de Compétences

#### GET /api/skill-gaps
Écarts de compétences d'un utilisateur

**Query Params:**
- `userId`: ID utilisateur (obligatoire sauf pour soi-même)
- `priority`: Filtrer par priorité (CRITICAL, HIGH, MEDIUM, LOW)

**Réponse (200):**
```json
{
  "content": [
    {
      "id": 300,
      "employee": {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe"
      },
      "skill": {
        "id": 5,
        "name": "Angular"
      },
      "currentLevel": {
        "id": 2,
        "level": 2,
        "name": "Intermédiaire"
      },
      "requiredLevel": {
        "id": 4,
        "level": 4,
        "name": "Expert"
      },
      "gap": 2,
      "priority": "HIGH",
      "estimatedDevelopmentTime": 40,
      "status": "IDENTIFIED"
    }
  ]
}
```

#### GET /api/skill-gaps/team
Écarts de l'équipe (MANAGER)

**Réponse (200):**
```json
{
  "totalGaps": 45,
  "criticalGaps": 5,
  "highPriorityGaps": 15,
  "bySkill": [
    {
      "skill": {
        "id": 5,
        "name": "Angular"
      },
      "employeesWithGap": 8,
      "avgGap": 1.5
    }
  ]
}
```

---

### 🛤️ Parcours de Formation

#### GET /api/learning-paths
Mes parcours de formation

**Réponse (200):**
```json
{
  "content": [
    {
      "id": 100,
      "name": "Parcours Développeur Full-Stack",
      "description": "Montée en compétence complète",
      "status": "ACTIVE",
      "currentProgress": 45,
      "startDate": "2025-01-01",
      "targetEndDate": "2025-06-30",
      "totalDuration": 120,
      "steps": [
        {
          "id": 1,
          "training": {
            "id": 20,
            "title": "Java Avancé"
          },
          "stepOrder": 1,
          "status": "COMPLETED"
        },
        {
          "id": 2,
          "training": {
            "id": 25,
            "title": "Angular Avancé"
          },
          "stepOrder": 2,
          "status": "IN_PROGRESS"
        }
      ]
    }
  ]
}
```

#### POST /api/learning-paths
Créer un parcours (MANAGER pour employé, EMPLOYEE pour soi)

**Body:**
```json
{
  "name": "Parcours Architecture",
  "description": "Formation vers rôle d'architecte",
  "employeeId": 1,
  "startDate": "2025-02-01",
  "targetEndDate": "2025-08-31",
  "steps": [
    {
      "trainingId": 30,
      "stepOrder": 1,
      "plannedStartDate": "2025-02-01"
    },
    {
      "trainingId": 35,
      "stepOrder": 2,
      "plannedStartDate": "2025-04-01"
    }
  ]
}
```

---

### 📊 Dashboards

#### GET /api/dashboards/employee
Dashboard employé

**Réponse (200):**
```json
{
  "skillsCount": 25,
  "averageSkillLevel": 3.2,
  "currentTrainings": 2,
  "completedTrainings": 12,
  "totalTrainingHours": 180,
  "certifications": 5,
  "xpPoints": 2450,
  "level": "Gold",
  "nextTraining": {
    "id": 50,
    "title": "Java Avancé",
    "startDate": "2025-02-10"
  }
}
```

#### GET /api/dashboards/manager
Dashboard manager

**Réponse (200):**
```json
{
  "teamSize": 8,
  "criticalGaps": 12,
  "trainingsInProgress": 5,
  "budgetAllocated": 50000.00,
  "budgetSpent": 28500.00,
  "pendingApprovals": 3,
  "averageTeamLevel": 3.5,
  "topSkillGaps": [
    {
      "skill": "Angular",
      "employeesAffected": 5
    }
  ]
}
```

---

## Rate Limiting

| Authentifié | Non-authentifié |
|-------------|------------------|
| 1000 req/h  | 60 req/min       |

**Headers de réponse:**
```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 995
X-RateLimit-Reset: 1642234567
```

---

## Webhooks

Configuration des webhooks pour recevoir des événements.

### Événements Disponibles

| Événement | Description |
|-----------|-------------|
| `user.created` | Nouvel utilisateur créé |
| `evaluation.completed` | Évaluation terminée |
| `training.registered` | Inscription à une formation |
| `training.completed` | Formation terminée |
| `certificate.issued` | Certificat émis |

### Configuration

```http
POST /api/webhooks
Authorization: Bearer {admin_token}

{
  "name": "Mon Webhook SIRH",
  "url": "https://sirh.company.com/webhooks/eplatform",
  "event": "training.completed",
  "secretKey": "your-secret-key"
}
```

### Payload Exemple

```json
{
  "event": "training.completed",
  "timestamp": "2025-01-15T10:30:00Z",
  "data": {
    "userId": 1,
    "trainingId": 20,
    "sessionId": 50,
    "completionDate": "2025-01-15T09:00:00Z",
    "certificate": {
      "id": 100,
      "number": "CERT-2025-001"
    }
  },
  "signature": "sha256=..."
}
```

---

## Documentation Interactive

- **Swagger UI** : http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON** : http://localhost:8080/v3/api-docs
- **Postman Collection** : [Télécharger](https://api.company.com/postman-collection.json)

---

**Version API** : 1.0.0
**Dernière mise à jour** : 2025-01-15
