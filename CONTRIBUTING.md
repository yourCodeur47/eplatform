# Guide de Contribution - Plateforme de Gestion de Compétences

Merci de contribuer à ce projet ! Ce guide vous aidera à comprendre notre processus de développement et nos conventions.

---

## Table des Matières

1. [Prérequis](#prérequis)
2. [Setup du Projet](#setup-du-projet)
3. [Workflow Git](#workflow-git)
4. [Conventions de Code](#conventions-de-code)
5. [Tests](#tests)
6. [Code Review](#code-review)
7. [Documentation](#documentation)

---

## Prérequis

### Outils Requis

- **Java 21 LTS** ([Installation](https://adoptium.net/))
- **Node.js 20 LTS** ([Installation](https://nodejs.org/))
- **Docker Desktop** ([Installation](https://www.docker.com/products/docker-desktop))
- **Git 2.40+**
- **IntelliJ IDEA Ultimate** (recommandé pour backend)
- **VS Code** (recommandé pour frontend)

### Extensions VS Code Recommandées

```json
{
  "recommendations": [
    "angular.ng-template",
    "dbaeumer.vscode-eslint",
    "esbenp.prettier-vscode",
    "firsttris.vscode-jest-runner",
    "streetsidesoftware.code-spell-checker"
  ]
}
```

### Plugins IntelliJ Recommandés

- Lombok
- MapStruct Support
- SonarLint
- CheckStyle-IDEA

---

## Setup du Projet

### 1. Cloner le Repository

```bash
git clone https://github.com/votre-org/eplatform.git
cd eplatform
```

### 2. Configuration des Hooks Git

```bash
# Installer les pre-commit hooks
cp .githooks/pre-commit .git/hooks/
chmod +x .git/hooks/pre-commit
```

### 3. Backend Setup

```bash
cd backend

# Copier le fichier de configuration
cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml

# Modifier les variables si nécessaire
nano src/main/resources/application-local.yml

# Installer les dépendances
./mvnw clean install

# Lancer les services Docker
docker-compose up -d

# Vérifier que PostgreSQL et Redis sont démarrés
docker-compose ps

# Lancer l'application
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### 4. Frontend Setup

```bash
cd frontend

# Installer les dépendances
npm install

# Copier l'environnement de dev
cp src/environments/environment.local.ts.example src/environments/environment.local.ts

# Lancer l'application
ng serve

# Accéder à http://localhost:4200
```

---

## Workflow Git

### Modèle de Branching (GitFlow)

Nous utilisons une version simplifiée de GitFlow :

```
main (production)
  └── develop (intégration)
       ├── feature/AUTH-001-jwt-authentication
       ├── feature/SKILL-012-skill-category-crud
       ├── bugfix/TRAIN-045-registration-validation
       └── hotfix/SEC-089-xss-vulnerability
```

### Types de Branches

| Type | Préfixe | Description | Exemple |
|------|---------|-------------|---------|
| Fonctionnalité | `feature/` | Nouvelle fonctionnalité | `feature/AUTH-001-jwt-authentication` |
| Correction | `bugfix/` | Correction de bug non-critique | `bugfix/TRAIN-045-registration-validation` |
| Hotfix | `hotfix/` | Correction critique en production | `hotfix/SEC-089-xss-vulnerability` |
| Release | `release/` | Préparation de release | `release/v1.2.0` |

### Workflow Standard

#### 1. Créer une Branche

```bash
# Se mettre sur develop
git checkout develop
git pull origin develop

# Créer une nouvelle branche
git checkout -b feature/AUTH-001-jwt-authentication
```

#### 2. Développer

```bash
# Faire vos modifications
# Commiter régulièrement

git add .
git commit -m "feat(auth): implement JWT token generation"
```

#### 3. Pousser et Créer une PR

```bash
# Pousser la branche
git push origin feature/AUTH-001-jwt-authentication

# Créer une Pull Request sur GitHub/GitLab
# Assigner un reviewer
# Lier au ticket Jira/GitHub Issue
```

#### 4. Mettre à Jour avec develop

```bash
# Si develop a avancé pendant votre développement
git checkout develop
git pull origin develop

git checkout feature/AUTH-001-jwt-authentication
git rebase develop

# Résoudre les conflits si nécessaire
git push --force-with-lease origin feature/AUTH-001-jwt-authentication
```

---

## Conventions de Code

### Messages de Commit (Conventional Commits)

Format : `<type>(<scope>): <description>`

**Types autorisés :**
- `feat`: Nouvelle fonctionnalité
- `fix`: Correction de bug
- `docs`: Documentation uniquement
- `style`: Formatage (sans changement de logique)
- `refactor`: Refactoring (sans nouvelle fonctionnalité ni fix)
- `perf`: Amélioration de performance
- `test`: Ajout ou correction de tests
- `chore`: Tâches de maintenance (build, config, etc.)
- `ci`: Modifications CI/CD

**Exemples :**
```bash
feat(auth): add JWT refresh token mechanism
fix(training): correct registration validation logic
docs(api): update swagger documentation for skills endpoints
refactor(evaluation): extract validation logic to separate service
test(skills): add unit tests for SkillService
chore(deps): upgrade Spring Boot to 3.2.2
```

**Breaking Changes :**
```bash
feat(api)!: change User API response structure

BREAKING CHANGE: User API now returns nested roles object instead of flat array
```

### Code Style Backend (Java)

#### Conventions de Nommage

```java
// Classes : PascalCase
public class SkillEvaluationService { }

// Méthodes : camelCase
public SkillDTO findById(Long id) { }

// Variables : camelCase
private String userName;

// Constantes : UPPER_SNAKE_CASE
private static final int MAX_RETRY_ATTEMPTS = 3;

// Packages : lowercase
package com.eplatform.skills.application.dto;
```

#### Règles de Formatage

- **Indentation** : 4 espaces (pas de tabs)
- **Longueur de ligne** : Max 120 caractères
- **Accolades** : Style K&R (même ligne pour ouverture)
- **Imports** : Pas d'imports `*` (wildcard)

**Exemple :**
```java
@Service
@Transactional
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public SkillService(SkillRepository skillRepository, SkillMapper skillMapper) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
    }

    @Cacheable(value = "skills", key = "#id")
    public SkillDTO findById(Long id) {
        return skillRepository.findById(id)
            .map(skillMapper::toDTO)
            .orElseThrow(() -> new SkillNotFoundException(id));
    }
}
```

#### Annotations Lombok

Utiliser Lombok pour réduire le boilerplate :

```java
@Data                    // Génère getters, setters, equals, hashCode, toString
@Builder                 // Pattern Builder
@NoArgsConstructor       // Constructeur sans args
@AllArgsConstructor      // Constructeur avec tous les args
@Slf4j                   // Logger SLF4J
```

#### Gestion des Exceptions

```java
// Custom exceptions dans le domain layer
public class SkillNotFoundException extends ResourceNotFoundException {
    public SkillNotFoundException(Long id) {
        super("Skill", "id", id);
    }
}

// Global exception handler
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
```

### Code Style Frontend (Angular/TypeScript)

#### Conventions de Nommage

```typescript
// Classes, Interfaces : PascalCase
export class SkillService { }
export interface Skill { }

// Fonctions, Méthodes : camelCase
getSkillById(id: number): Observable<Skill> { }

// Variables : camelCase
private currentUser: User;

// Constantes : UPPER_SNAKE_CASE
const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

// Fichiers : kebab-case
skill-list.component.ts
auth.service.ts
user.model.ts
```

#### Structure des Composants

```typescript
@Component({
  selector: 'app-skill-list',
  templateUrl: './skill-list.component.html',
  styleUrls: ['./skill-list.component.scss']
})
export class SkillListComponent implements OnInit, OnDestroy {
  // 1. Propriétés publiques
  skills: Skill[] = [];
  isLoading = false;

  // 2. Propriétés privées
  private destroy$ = new Subject<void>();

  // 3. Signals (Angular 18+)
  skillsSignal = signal<Skill[]>([]);

  // 4. Injection de dépendances
  constructor(
    private skillsStore: SkillsStore,
    private router: Router
  ) {}

  // 5. Lifecycle hooks
  ngOnInit(): void {
    this.loadSkills();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // 6. Méthodes publiques
  loadSkills(): void {
    this.skillsStore.loadSkills();
  }

  onSkillClick(skill: Skill): void {
    this.router.navigate(['/skills', skill.id]);
  }

  // 7. Méthodes privées
  private handleError(error: Error): void {
    console.error('Error loading skills:', error);
  }
}
```

#### RxJS Best Practices

```typescript
// ❌ Mauvais : Subscription non nettoyée
this.userService.getUser().subscribe(user => {
  this.user = user;
});

// ✅ Bon : Unsubscribe avec takeUntil
this.userService.getUser()
  .pipe(takeUntil(this.destroy$))
  .subscribe(user => {
    this.user = user;
  });

// ✅ Meilleur : Utiliser async pipe dans le template
users$ = this.userService.getUsers();

// Template
<div *ngFor="let user of users$ | async">
  {{ user.name }}
</div>
```

#### Types TypeScript

```typescript
// Toujours typer les paramètres et retours
function getSkillById(id: number): Observable<Skill> {
  return this.http.get<Skill>(`/api/skills/${id}`);
}

// Utiliser des interfaces pour les objets
interface CreateSkillRequest {
  name: string;
  categoryId: number;
  description?: string; // Optionnel
}

// Éviter any, utiliser unknown si type inconnu
function processData(data: unknown): void {
  if (typeof data === 'string') {
    console.log(data.toUpperCase());
  }
}
```

### Documentation du Code

#### Javadoc (Backend)

```java
/**
 * Service de gestion des compétences.
 * Fournit les opérations CRUD et la logique métier pour les compétences.
 *
 * @author Jean Dupont
 * @since 1.0.0
 */
@Service
public class SkillService {

    /**
     * Recherche une compétence par son identifiant.
     *
     * @param id L'identifiant de la compétence
     * @return La compétence trouvée
     * @throws SkillNotFoundException Si la compétence n'existe pas
     */
    public SkillDTO findById(Long id) {
        // Implementation
    }
}
```

#### JSDoc (Frontend)

```typescript
/**
 * Service de gestion des compétences.
 * Gère les appels API et le state management pour les compétences.
 */
@Injectable({ providedIn: 'root' })
export class SkillService {

  /**
   * Récupère une compétence par son ID
   * @param id - L'identifiant de la compétence
   * @returns Observable contenant la compétence ou une erreur
   * @throws SkillNotFoundError Si la compétence n'existe pas
   */
  getSkillById(id: number): Observable<Skill> {
    return this.http.get<Skill>(`/api/skills/${id}`);
  }
}
```

---

## Tests

### Tests Backend (JUnit 5 + Mockito)

#### Tests Unitaires

```java
@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillService skillService;

    @Test
    @DisplayName("findById should return skill when exists")
    void findById_WhenSkillExists_ShouldReturnSkill() {
        // Given
        Long skillId = 1L;
        Skill skill = new Skill();
        skill.setId(skillId);
        skill.setName("Java");

        SkillDTO expectedDTO = new SkillDTO();
        expectedDTO.setId(skillId);
        expectedDTO.setName("Java");

        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillMapper.toDTO(skill)).thenReturn(expectedDTO);

        // When
        SkillDTO result = skillService.findById(skillId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(skillId);
        assertThat(result.getName()).isEqualTo("Java");

        verify(skillRepository).findById(skillId);
        verify(skillMapper).toDTO(skill);
    }

    @Test
    @DisplayName("findById should throw exception when skill not found")
    void findById_WhenSkillNotFound_ShouldThrowException() {
        // Given
        Long skillId = 999L;
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> skillService.findById(skillId))
            .isInstanceOf(SkillNotFoundException.class)
            .hasMessageContaining("Skill not found with id: 999");
    }
}
```

#### Tests d'Intégration

```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SkillControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SkillRepository skillRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createSkill_WithValidData_ShouldReturn201() throws Exception {
        // Given
        String requestBody = """
            {
                "name": "Spring Boot",
                "categoryId": 1,
                "description": "Framework Java"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Spring Boot"))
            .andExpect(jsonPath("$.id").exists());
    }
}
```

#### Couverture de Tests

- **Cible minimale** : 80% coverage
- **Tests unitaires** : Services, Mappers, Validators
- **Tests d'intégration** : Controllers, Repositories
- **Exclure** : DTOs, Configurations, Main class

```bash
# Générer le rapport de couverture
./mvnw verify

# Voir le rapport
open target/site/jacoco/index.html
```

### Tests Frontend (Jasmine/Karma)

#### Tests de Services

```typescript
describe('SkillService', () => {
  let service: SkillService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SkillService]
    });

    service = TestBed.inject(SkillService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch skill by id', () => {
    const mockSkill: Skill = { id: 1, name: 'Java', categoryId: 1 };

    service.getSkillById(1).subscribe(skill => {
      expect(skill).toEqual(mockSkill);
    });

    const req = httpMock.expectOne('/api/skills/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockSkill);
  });
});
```

#### Tests de Composants

```typescript
describe('SkillListComponent', () => {
  let component: SkillListComponent;
  let fixture: ComponentFixture<SkillListComponent>;
  let skillsStore: SkillsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SkillListComponent],
      providers: [
        {
          provide: SkillsStore,
          useValue: jasmine.createSpyObj('SkillsStore', ['loadSkills'])
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SkillListComponent);
    component = fixture.componentInstance;
    skillsStore = TestBed.inject(SkillsStore);
  });

  it('should load skills on init', () => {
    fixture.detectChanges();
    expect(skillsStore.loadSkills).toHaveBeenCalled();
  });
});
```

---

## Code Review

### Checklist pour le Reviewer

#### Général
- [ ] Le code respecte les conventions de nommage
- [ ] Pas de code commenté inutile
- [ ] Pas de console.log() ou System.out.println() oubliés
- [ ] Les messages de commit suivent Conventional Commits

#### Fonctionnalité
- [ ] Le code fait ce qu'il est censé faire
- [ ] Les cas d'erreur sont gérés
- [ ] Les validations sont en place
- [ ] Les permissions sont vérifiées

#### Tests
- [ ] Les tests unitaires passent
- [ ] Couverture de tests >= 80%
- [ ] Les tests d'intégration passent
- [ ] Pas de tests commentés

#### Sécurité
- [ ] Pas d'injection SQL
- [ ] Pas de XSS
- [ ] Validation des entrées utilisateur
- [ ] Pas de données sensibles en logs

#### Performance
- [ ] Pas de requêtes N+1
- [ ] Utilisation appropriée du cache
- [ ] Pas de boucles infinies possibles
- [ ] Pagination pour les listes

#### Documentation
- [ ] Javadoc/JSDoc pour les méthodes publiques
- [ ] README mis à jour si nécessaire
- [ ] Swagger mis à jour si changement API

### Processus de Review

1. **Auto-review** : Reviewer son propre code avant de soumettre
2. **Reviewer assigné** : Assigner au moins 1 reviewer (2 pour code critique)
3. **Feedback** : Le reviewer commente en 24h max
4. **Corrections** : L'auteur corrige et re-push
5. **Approbation** : Le reviewer approuve
6. **Merge** : Squash and merge dans develop

### Commentaires de Review

```bash
# Suggestion mineure (optionnel)
💡 Nit: Consider renaming `data` to `skillData` for clarity

# Problème à corriger
❌ Issue: This can cause a NullPointerException if user is null

# Question
❓ Question: Why are we using a HashMap here instead of a TreeMap?

# Compliment
✅ Nice: Good use of Optional here!
```

---

## Documentation

### README de Module

Chaque module fonctionnel doit avoir un README.md :

```markdown
# Module Skills

## Description
Gestion du référentiel de compétences.

## Endpoints API
- `GET /api/skills` - Liste toutes les compétences
- `GET /api/skills/{id}` - Détail d'une compétence
- `POST /api/skills` - Créer une compétence (ADMIN)
- `PUT /api/skills/{id}` - Modifier une compétence (ADMIN)
- `DELETE /api/skills/{id}` - Supprimer une compétence (ADMIN)

## Modèles
- `Skill` : Entité JPA
- `SkillDTO` : DTO pour API
- `CreateSkillRequest` : Requête de création
- `UpdateSkillRequest` : Requête de mise à jour

## Services
- `SkillService` : Logique métier
- `SkillMapper` : Mapping Entity <-> DTO

## Tests
```bash
./mvnw test -Dtest=SkillServiceTest
```
```

### API Documentation (Swagger)

Documenter tous les endpoints :

```java
@RestController
@RequestMapping("/api/skills")
@Tag(name = "Skills", description = "Gestion des compétences")
public class SkillController {

    @Operation(
        summary = "Récupérer une compétence par ID",
        description = "Retourne les détails complets d'une compétence"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Compétence trouvée"),
        @ApiResponse(responseCode = "404", description = "Compétence non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SkillDTO> getSkill(
        @Parameter(description = "ID de la compétence")
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(skillService.findById(id));
    }
}
```

---

## Questions ?

- **Slack** : #dev-eplatform
- **Email** : dev-team@company.com
- **Wiki** : https://wiki.company.com/eplatform

---

**Dernière mise à jour** : 2025-11-13
**Version** : 1.0.0
