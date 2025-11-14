# Documentation Technique - Modélisation UML et Conception
## Plateforme de Gestion de Compétences et Formation

---

## 1. Vue d'ensemble de l'architecture

### 1.1 Architecture Hexagonale (Clean Architecture)

L'application suit une architecture hexagonale pour garantir la séparation des préoccupations et la testabilité.[1][2]

```
├── domain/               # Cœur métier
│   ├── model/           # Entités métier
│   ├── repository/      # Interfaces de repositories
│   └── service/         # Services métier
├── application/         # Couche application
│   ├── usecase/        # Cas d'utilisation
│   └── dto/            # Data Transfer Objects
└── infrastructure/      # Couche infrastructure
    ├── adapter/        # Adaptateurs (DB, API, etc.)
    ├── config/         # Configurations Spring
    └── security/       # Sécurité
```

***

## 2. Diagramme de Classes Principal

### 2.1 Module Gestion des Utilisateurs et Authentification

#### Classes principales

**User**
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false)
    private LocalDate birthDate;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime lastLogin;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status; // ACTIVE, INACTIVE, SUSPENDED
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;
    
    @OneToMany(mappedBy = "manager")
    private Set<User> subordinates = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "job_position_id")
    private JobPosition jobPosition;
    
    // Getters, setters, equals, hashCode
}
```

**Role**
```java
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name; // ADMIN, MANAGER, EMPLOYEE, HR_ADMIN, TRAINER
    
    @Column
    private String description;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
    
    // Getters, setters
}
```

**Permission**
```java
@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name; // READ_SKILL, WRITE_SKILL, etc.
    
    @Column
    private String resource; // SKILL, TRAINING, EVALUATION
    
    @Enumerated(EnumType.STRING)
    private PermissionType type; // CREATE, READ, UPDATE, DELETE
    
    // Getters, setters
}
```

**Department**
```java
@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String code;
    
    @ManyToOne
    @JoinColumn(name = "parent_department_id")
    private Department parentDepartment;
    
    @OneToMany(mappedBy = "parentDepartment")
    private Set<Department> subDepartments = new HashSet<>();
    
    @OneToMany(mappedBy = "department")
    private Set<User> employees = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;
    
    @Column
    private BigDecimal trainingBudget;
    
    // Getters, setters
}
```

**Diagramme de relations :**
```
User "*" -----> "*" Role
Role "*" -----> "*" Permission
User "*" -----> "1" Department
User "*" -----> "1" User (manager)
User "1" -----> "*" User (subordinates)
Department "1" -----> "*" User
Department "*" -----> "1" Department (parent)
```

***

### 2.2 Module Référentiel de Compétences

**Skill (Compétence)**
```java
@Entity
@Table(name = "skills")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private SkillCategory category;
    
    @Enumerated(EnumType.STRING)
    private SkillType type; // TECHNICAL, SOFT_SKILL, BUSINESS, MANAGERIAL
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @Version
    private Integer version;
    
    // Getters, setters
}
```

**SkillCategory**
```java
@Entity
@Table(name = "skill_categories")
public class SkillCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private SkillCategory parentCategory;
    
    @OneToMany(mappedBy = "parentCategory")
    private Set<SkillCategory> subCategories = new HashSet<>();
    
    @OneToMany(mappedBy = "category")
    private Set<Skill> skills = new HashSet<>();
    
    @Column
    private Integer displayOrder;
    
    // Getters, setters
}
```

**SkillLevel (Niveau de maîtrise)**
```java
@Entity
@Table(name = "skill_levels")
public class SkillLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name; // Débutant, Intermédiaire, Confirmé, Expert, Maître
    
    @Column(nullable = false)
    private Integer level; // 1, 2, 3, 4, 5
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String criteria; // Critères pour atteindre ce niveau
    
    // Getters, setters
}
```

**JobPosition (Poste/Métier)**
```java
@Entity
@Table(name = "job_positions")
public class JobPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    
    @OneToMany(mappedBy = "jobPosition", cascade = CascadeType.ALL)
    private Set<JobPositionSkill> requiredSkills = new HashSet<>();
    
    @Column
    private LocalDateTime createdAt;
    
    @Version
    private Integer version;
    
    // Getters, setters
}
```

**JobPositionSkill (Association Métier-Compétence)**
```java
@Entity
@Table(name = "job_position_skills")
public class JobPositionSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "job_position_id", nullable = false)
    private JobPosition jobPosition;
    
    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
    
    @ManyToOne
    @JoinColumn(name = "minimum_level_id")
    private SkillLevel minimumLevel;
    
    @ManyToOne
    @JoinColumn(name = "optimal_level_id")
    private SkillLevel optimalLevel;
    
    @Enumerated(EnumType.STRING)
    private SkillImportance importance; // CRITICAL, IMPORTANT, DESIRABLE
    
    @Column
    private Integer weight; // Pondération 1-10
    
    // Getters, setters
}
```

**Diagramme de relations :**
```
Skill "*" -----> "1" SkillCategory
SkillCategory "*" -----> "1" SkillCategory (parent)
JobPosition "1" -----> "*" JobPositionSkill
Skill "1" -----> "*" JobPositionSkill
SkillLevel "1" -----> "*" JobPositionSkill (min/optimal)
JobPosition "*" -----> "1" Department
```

***

### 2.3 Module Évaluation des Compétences

**SkillEvaluation (Évaluation de compétence)**
```java
@Entity
@Table(name = "skill_evaluations")
public class SkillEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User employee;
    
    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
    
    @ManyToOne
    @JoinColumn(name = "self_assessed_level_id")
    private SkillLevel selfAssessedLevel;
    
    @ManyToOne
    @JoinColumn(name = "manager_assessed_level_id")
    private SkillLevel managerAssessedLevel;
    
    @ManyToOne
    @JoinColumn(name = "validated_level_id")
    private SkillLevel validatedLevel; // Niveau final validé
    
    @Column(columnDefinition = "TEXT")
    private String selfComment;
    
    @Column(columnDefinition = "TEXT")
    private String managerComment;
    
    @ManyToOne
    @JoinColumn(name = "evaluator_id")
    private User evaluator; // Manager qui évalue
    
    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private EvaluationCampaign campaign;
    
    @Enumerated(EnumType.STRING)
    private EvaluationStatus status; // DRAFT, SUBMITTED, VALIDATED, CLOSED
    
    @Column
    private LocalDateTime selfEvaluationDate;
    
    @Column
    private LocalDateTime managerEvaluationDate;
    
    @Column
    private LocalDateTime validationDate;
    
    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL)
    private Set<EvaluationEvidence> evidences = new HashSet<>();
    
    // Getters, setters
}
```

**EvaluationEvidence (Justificatif d'évaluation)**
```java
@Entity
@Table(name = "evaluation_evidences")
public class EvaluationEvidence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "evaluation_id", nullable = false)
    private SkillEvaluation evaluation;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String filePath;
    
    @Column
    private String fileType;
    
    @Column
    private Long fileSize;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column
    private LocalDateTime uploadedAt;
    
    // Getters, setters
}
```

**EvaluationCampaign (Campagne d'évaluation)**
```java
@Entity
@Table(name = "evaluation_campaigns")
public class EvaluationCampaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false)
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    private CampaignStatus status; // PLANNED, ACTIVE, CLOSED, ARCHIVED
    
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    
    @Column
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "campaign")
    private Set<SkillEvaluation> evaluations = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "campaign_target_users",
        joinColumns = @JoinColumn(name = "campaign_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> targetUsers = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "campaign_target_departments",
        joinColumns = @JoinColumn(name = "campaign_id"),
        inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private Set<Department> targetDepartments = new HashSet<>();
    
    // Getters, setters
}
```

**Evaluation360 (Évaluation 360°)**
```java
@Entity
@Table(name = "evaluations_360")
public class Evaluation360 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "evaluated_user_id", nullable = false)
    private User evaluatedUser;
    
    @ManyToOne
    @JoinColumn(name = "evaluator_id", nullable = false)
    private User evaluator;
    
    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
    
    @ManyToOne
    @JoinColumn(name = "assessed_level_id")
    private SkillLevel assessedLevel;
    
    @Column(columnDefinition = "TEXT")
    private String feedback;
    
    @Column
    private Boolean isAnonymous = false;
    
    @Enumerated(EnumType.STRING)
    private EvaluatorType evaluatorType; // PEER, SUBORDINATE, MANAGER, CUSTOMER
    
    @Column
    private LocalDateTime evaluationDate;
    
    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private EvaluationCampaign campaign;
    
    // Getters, setters
}
```

**SkillTest (Test de compétence)**
```java
@Entity
@Table(name = "skill_tests")
public class SkillTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
    
    @Column(nullable = false)
    private Integer duration; // En minutes
    
    @Column(nullable = false)
    private Integer passingScore; // Score minimum pour réussir (%)
    
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private Set<TestQuestion> questions = new HashSet<>();
    
    @Column
    private Boolean isActive = true;
    
    // Getters, setters
}
```

**TestQuestion**
```java
@Entity
@Table(name = "test_questions")
public class TestQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private SkillTest test;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;
    
    @Enumerated(EnumType.STRING)
    private QuestionType type; // MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER
    
    @Column
    private Integer points;
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private Set<QuestionOption> options = new HashSet<>();
    
    @Column
    private Integer displayOrder;
    
    // Getters, setters
}
```

**TestAttempt (Tentative de test)**
```java
@Entity
@Table(name = "test_attempts")
public class TestAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private SkillTest test;
    
    @Column
    private LocalDateTime startedAt;
    
    @Column
    private LocalDateTime completedAt;
    
    @Column
    private Integer score; // Score obtenu (%)
    
    @Column
    private Boolean passed;
    
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL)
    private Set<TestAnswer> answers = new HashSet<>();
    
    // Getters, setters
}
```

**Diagramme de relations :**
```
SkillEvaluation "*" -----> "1" User (employee)
SkillEvaluation "*" -----> "1" Skill
SkillEvaluation "*" -----> "1" SkillLevel (self/manager/validated)
SkillEvaluation "*" -----> "1" User (evaluator)
SkillEvaluation "*" -----> "1" EvaluationCampaign
SkillEvaluation "1" -----> "*" EvaluationEvidence
EvaluationCampaign "1" -----> "*" SkillEvaluation
EvaluationCampaign "*" -----> "*" User (targets)
Evaluation360 "*" -----> "1" User (evaluated/evaluator)
Evaluation360 "*" -----> "1" Skill
SkillTest "*" -----> "1" Skill
SkillTest "1" -----> "*" TestQuestion
TestAttempt "*" -----> "1" User
TestAttempt "*" -----> "1" SkillTest
```

***

### 2.4 Module Analyse des Écarts

**SkillGap (Écart de compétence)**
```java
@Entity
@Table(name = "skill_gaps")
public class SkillGap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User employee;
    
    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
    
    @ManyToOne
    @JoinColumn(name = "current_level_id")
    private SkillLevel currentLevel;
    
    @ManyToOne
    @JoinColumn(name = "required_level_id")
    private SkillLevel requiredLevel;
    
    @Column(nullable = false)
    private Integer gap; // Différence entre requis et actuel
    
    @Enumerated(EnumType.STRING)
    private GapPriority priority; // CRITICAL, HIGH, MEDIUM, LOW
    
    @Column
    private Integer estimatedDevelopmentTime; // En heures
    
    @Column
    private LocalDateTime calculatedAt;
    
    @Column
    private LocalDateTime closedAt;
    
    @Enumerated(EnumType.STRING)
    private GapStatus status; // IDENTIFIED, IN_PROGRESS, CLOSED
    
    // Getters, setters
}
```

**SkillGapAnalysis (Analyse d'écarts)**
```java
@Entity
@Table(name = "skill_gap_analyses")
public class SkillGapAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    
    @ManyToOne
    @JoinColumn(name = "job_position_id")
    private JobPosition jobPosition;
    
    @Column
    private LocalDateTime analysisDate;
    
    @Column
    private Integer totalGapsIdentified;
    
    @Column
    private Integer criticalGaps;
    
    @Column
    private Integer highPriorityGaps;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column(columnDefinition = "TEXT")
    private String recommendations;
    
    @ManyToOne
    @JoinColumn(name = "analyst_id")
    private User analyst;
    
    // Getters, setters
}
```

**Diagramme de relations :**
```
SkillGap "*" -----> "1" User
SkillGap "*" -----> "1" Skill
SkillGap "*" -----> "1" SkillLevel (current/required)
SkillGapAnalysis "*" -----> "1" Department
SkillGapAnalysis "*" -----> "1" JobPosition
```

***

### 2.5 Module Formations et Catalogue

**Training (Formation)**
```java
@Entity
@Table(name = "trainings")
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String objectives;
    
    @Enumerated(EnumType.STRING)
    private TrainingType type; // IN_PERSON, E_LEARNING, BLENDED, EXTERNAL
    
    @Column(nullable = false)
    private Integer duration; // En heures
    
    @Column
    private BigDecimal price;
    
    @Column
    private String provider; // Organisme de formation
    
    @Column(columnDefinition = "TEXT")
    private String prerequisites;
    
    @Column
    private Integer maxParticipants;
    
    @Column
    private Boolean isActive = true;
    
    @ManyToMany
    @JoinTable(
        name = "training_skills",
        joinColumns = @JoinColumn(name = "training_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> targetedSkills = new HashSet<>();
    
    @OneToMany(mappedBy = "training")
    private Set<TrainingSkillGain> skillGains = new HashSet<>();
    
    @OneToMany(mappedBy = "training")
    private Set<TrainingSession> sessions = new HashSet<>();
    
    @OneToMany(mappedBy = "training")
    private Set<TrainingRating> ratings = new HashSet<>();
    
    @Column
    private LocalDateTime createdAt;
    
    @Version
    private Integer version;
    
    // Getters, setters
}
```

**TrainingSkillGain (Gain de compétence par formation)**
```java
@Entity
@Table(name = "training_skill_gains")
public class TrainingSkillGain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "training_id", nullable = false)
    private Training training;
    
    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
    
    @Column(nullable = false)
    private Integer levelGain; // Gain attendu en niveau (ex: +1, +2)
    
    // Getters, setters
}
```

**TrainingSession (Session de formation)**
```java
@Entity
@Table(name = "training_sessions")
public class TrainingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "training_id", nullable = false)
    private Training training;
    
    @Column(nullable = false)
    private String reference;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false)
    private LocalDate endDate;
    
    @Column
    private String location;
    
    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private User trainer;
    
    @Column
    private Integer availableSeats;
    
    @Column
    private Integer registeredCount = 0;
    
    @Enumerated(EnumType.STRING)
    private SessionStatus status; // PLANNED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private Set<TrainingRegistration> registrations = new HashSet<>();
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private Set<SessionAttendance> attendances = new HashSet<>();
    
    // Getters, setters
}
```

**TrainingRegistration (Inscription à une formation)**
```java
@Entity
@Table(name = "training_registrations")
public class TrainingRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User employee;
    
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private TrainingSession session;
    
    @Column(columnDefinition = "TEXT")
    private String justification;
    
    @Column
    private LocalDateTime registrationDate;
    
    @Enumerated(EnumType.STRING)
    private RegistrationStatus status; // PENDING, APPROVED_MANAGER, APPROVED_HR, REJECTED, CONFIRMED, COMPLETED, CANCELLED
    
    @ManyToOne
    @JoinColumn(name = "manager_validator_id")
    private User managerValidator;
    
    @Column
    private LocalDateTime managerValidationDate;
    
    @Column(columnDefinition = "TEXT")
    private String managerComment;
    
    @ManyToOne
    @JoinColumn(name = "hr_validator_id")
    private User hrValidator;
    
    @Column
    private LocalDateTime hrValidationDate;
    
    @Column(columnDefinition = "TEXT")
    private String hrComment;
    
    @Column
    private BigDecimal cost; // Coût de la formation
    
    @Column
    private Boolean certificateIssued = false;
    
    // Getters, setters
}
```

**SessionAttendance (Présence à une session)**
```java
@Entity
@Table(name = "session_attendances")
public class SessionAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "registration_id", nullable = false)
    private TrainingRegistration registration;
    
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private TrainingSession session;
    
    @Column
    private LocalDate attendanceDate;
    
    @Column
    private LocalTime checkInTime;
    
    @Column
    private LocalTime checkOutTime;
    
    @Column
    private Boolean isPresent = false;
    
    @Column
    private String qrCodeSignature;
    
    // Getters, setters
}
```

**TrainingRating (Évaluation de formation)**
```java
@Entity
@Table(name = "training_ratings")
public class TrainingRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "training_id", nullable = false)
    private Training training;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "session_id")
    private TrainingSession session;
    
    @Column(nullable = false)
    private Integer contentRating; // 1-5
    
    @Column(nullable = false)
    private Integer trainerRating; // 1-5
    
    @Column(nullable = false)
    private Integer organizationRating; // 1-5
    
    @Column(nullable = false)
    private Integer overallRating; // 1-5
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @Enumerated(EnumType.STRING)
    private EvaluationType evaluationType; // HOT (à chaud), COLD (à froid)
    
    @Column
    private LocalDateTime evaluationDate;
    
    @Column
    private Boolean skillsApplied; // Pour évaluation à froid
    
    // Getters, setters
}
```

**Certificate (Certificat/Attestation)**
```java
@Entity
@Table(name = "certificates")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String certificateNumber;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "training_id")
    private Training training;
    
    @ManyToOne
    @JoinColumn(name = "certification_id")
    private Certification certification;
    
    @Column(nullable = false)
    private LocalDate issueDate;
    
    @Column
    private LocalDate expiryDate;
    
    @Column
    private String filePath;
    
    @Enumerated(EnumType.STRING)
    private CertificateStatus status; // ACTIVE, EXPIRED, REVOKED
    
    // Getters, setters
}
```

**Certification (Certification)**
```java
@Entity
@Table(name = "certifications")
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column
    private Integer validityPeriod; // En mois
    
    @ManyToMany
    @JoinTable(
        name = "certification_skills",
        joinColumns = @JoinColumn(name = "certification_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> validatedSkills = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "certification_prerequisites",
        joinColumns = @JoinColumn(name = "certification_id"),
        inverseJoinColumns = @JoinColumn(name = "prerequisite_id")
    )
    private Set<Certification> prerequisites = new HashSet<>();
    
    @Column
    private String badgeImageUrl;
    
    @Column
    private Boolean isInternal = true;
    
    // Getters, setters
}
```

**Diagramme de relations :**
```
Training "1" -----> "*" TrainingSession
Training "*" -----> "*" Skill
Training "1" -----> "*" TrainingSkillGain
TrainingSession "1" -----> "*" TrainingRegistration
TrainingSession "*" -----> "1" User (trainer)
TrainingRegistration "*" -----> "1" User (employee)
TrainingRegistration "*" -----> "1" TrainingSession
TrainingRegistration "1" -----> "*" SessionAttendance
Training "1" -----> "*" TrainingRating
Certificate "*" -----> "1" User
Certificate "*" -----> "1" Training
Certificate "*" -----> "1" Certification
Certification "*" -----> "*" Skill
Certification "*" -----> "*" Certification (prerequisites)
```

***

### 2.6 Module Parcours de Formation

**LearningPath (Parcours de formation)**
```java
@Entity
@Table(name = "learning_paths")
public class LearningPath {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User employee; // Si personnalisé pour un employé
    
    @ManyToOne
    @JoinColumn(name = "job_position_id")
    private JobPosition targetJobPosition; // Si parcours type
    
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    
    @Column
    private LocalDate startDate;
    
    @Column
    private LocalDate targetEndDate;
    
    @Column
    private LocalDate actualEndDate;
    
    @Enumerated(EnumType.STRING)
    private PathStatus status; // DRAFT, ACTIVE, COMPLETED, CANCELLED
    
    @Column
    private Integer totalDuration; // Durée totale estimée en heures
    
    @Column
    private BigDecimal totalBudget;
    
    @OneToMany(mappedBy = "learningPath", cascade = CascadeType.ALL)
    private Set<PathStep> steps = new HashSet<>();
    
    @OneToMany(mappedBy = "learningPath")
    private Set<PathMilestone> milestones = new HashSet<>();
    
    @Column
    private Integer currentProgress = 0; // Pourcentage 0-100
    
    @Column
    private Boolean isSigned = false;
    
    @Column
    private LocalDateTime signatureDate;
    
    @Column
    private Boolean isTemplate = false; // Si c'est un parcours type
    
    // Getters, setters
}
```

**PathStep (Étape d'un parcours)**
```java
@Entity
@Table(name = "path_steps")
public class PathStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "learning_path_id", nullable = false)
    private LearningPath learningPath;
    
    @ManyToOne
    @JoinColumn(name = "training_id", nullable = false)
    private Training training;
    
    @Column(nullable = false)
    private Integer stepOrder;
    
    @Enumerated(EnumType.STRING)
    private StepStatus status; // NOT_STARTED, IN_PROGRESS, COMPLETED, SKIPPED
    
    @Column
    private Boolean isMandatory = true;
    
    @Column
    private LocalDate plannedStartDate;
    
    @Column
    private LocalDate plannedEndDate;
    
    @Column
    private LocalDate actualCompletionDate;
    
    @ManyToOne
    @JoinColumn(name = "registration_id")
    private TrainingRegistration registration;
    
    // Getters, setters
}
```

**PathMilestone (Jalon d'un parcours)**
```java
@Entity
@Table(name = "path_milestones")
public class PathMilestone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "learning_path_id", nullable = false)
    private LearningPath learningPath;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private LocalDate targetDate;
    
    @Column
    private LocalDate actualDate;
    
    @Enumerated(EnumType.STRING)
    private MilestoneStatus status; // PENDING, ACHIEVED, MISSED
    
    @Column
    private Integer displayOrder;
    
    // Getters, setters
}
```

**UserXP (Expérience et gamification)**
```java
@Entity
@Table(name = "user_xp")
public class UserXP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private Integer totalXP = 0;
    
    @Column(nullable = false)
    private Integer currentLevel = 1;
    
    @Enumerated(EnumType.STRING)
    private XPTier tier; // BRONZE, SILVER, GOLD, PLATINUM
    
    @Column
    private Integer currentStreak = 0; // Jours consécutifs d'activité
    
    @Column
    private Integer longestStreak = 0;
    
    @Column
    private LocalDate lastActivityDate;
    
    @OneToMany(mappedBy = "userXP")
    private Set<XPTransaction> transactions = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "user_badges",
        joinColumns = @JoinColumn(name = "user_xp_id"),
        inverseJoinColumns = @JoinColumn(name = "badge_id")
    )
    private Set<Badge> earnedBadges = new HashSet<>();
    
    // Getters, setters
}
```

**Badge**
```java
@Entity
@Table(name = "badges")
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column
    private String imageUrl;
    
    @Column
    private Integer requiredXP;
    
    @Enumerated(EnumType.STRING)
    private BadgeType type; // ACHIEVEMENT, MILESTONE, SPECIAL
    
    // Getters, setters
}
```

**XPTransaction**
```java
@Entity
@Table(name = "xp_transactions")
public class XPTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_xp_id", nullable = false)
    private UserXP userXP;
    
    @Column(nullable = false)
    private Integer xpAmount;
    
    @Enumerated(EnumType.STRING)
    private XPSource source; // TRAINING_COMPLETED, SKILL_EVALUATED, CERTIFICATION, etc.
    
    @Column
    private String description;
    
    @Column
    private LocalDateTime earnedAt;
    
    // Getters, setters
}
```

**Diagramme de relations :**
```
LearningPath "*" -----> "1" User (employee)
LearningPath "*" -----> "1" JobPosition
LearningPath "1" -----> "*" PathStep
LearningPath "1" -----> "*" PathMilestone
PathStep "*" -----> "1" Training
PathStep "*" -----> "1" TrainingRegistration
UserXP "1" -----> "1" User
UserXP "1" -----> "*" XPTransaction
UserXP "*" -----> "*" Badge
```

***

### 2.7 Module Notifications et Communication

**Notification**
```java
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type; // INFO, WARNING, SUCCESS, ERROR
    
    @Enumerated(EnumType.STRING)
    private NotificationCategory category; // EVALUATION, TRAINING, REGISTRATION, SYSTEM
    
    @Column
    private String relatedEntityType; // Training, Evaluation, etc.
    
    @Column
    private Long relatedEntityId;
    
    @Column
    private Boolean isRead = false;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime readAt;
    
    @Column
    private String actionUrl;
    
    // Getters, setters
}
```

**Message (Messagerie interne)**
```java
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;
    
    @ManyToOne
    @JoinColumn(name = "thread_id")
    private MessageThread thread;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column
    private LocalDateTime sentAt;
    
    @Column
    private Boolean isRead = false;
    
    @Column
    private LocalDateTime readAt;
    
    @OneToMany(mappedBy = "message")
    private Set<MessageAttachment> attachments = new HashSet<>();
    
    // Getters, setters
}
```

**MessageThread**
```java
@Entity
@Table(name = "message_threads")
public class MessageThread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String subject;
    
    @ManyToOne
    @JoinColumn(name = "initiated_by_id")
    private User initiatedBy;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime lastMessageAt;
    
    @ManyToOne
    @JoinColumn(name = "related_learning_path_id")
    private LearningPath relatedLearningPath;
    
    @OneToMany(mappedBy = "thread")
    private Set<Message> messages = new HashSet<>();
    
    // Getters, setters
}
```

**Announcement (Annonce)**
```java
@Entity
@Table(name = "announcements")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    @Column
    private LocalDateTime publishedAt;
    
    @Column
    private LocalDateTime expiresAt;
    
    @Enumerated(EnumType.STRING)
    private AnnouncementPriority priority; // LOW, MEDIUM, HIGH, URGENT
    
    @ManyToMany
    @JoinTable(
        name = "announcement_target_departments",
        joinColumns = @JoinColumn(name = "announcement_id"),
        inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private Set<Department> targetDepartments = new HashSet<>();
    
    @Column
    private Boolean isActive = true;
    
    // Getters, setters
}
```

**Diagramme de relations :**
```
Notification "*" -----> "1" User
Message "*" -----> "1" User (sender/receiver)
Message "*" -----> "1" MessageThread
MessageThread "*" -----> "1" User (initiator)
MessageThread "*" -----> "1" LearningPath
Announcement "*" -----> "1" User (author)
Announcement "*" -----> "*" Department
```

***

### 2.8 Module Administration et Configuration

**SystemConfig (Configuration système)**
```java
@Entity
@Table(name = "system_configs")
public class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String configKey;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String configValue;
    
    @Column
    private String description;
    
    @Enumerated(EnumType.STRING)
    private ConfigType type; // STRING, INTEGER, BOOLEAN, JSON
    
    @Column
    private Boolean isEditable = true;
    
    @Column
    private LocalDateTime lastModifiedAt;
    
    @ManyToOne
    @JoinColumn(name = "last_modified_by_id")
    private User lastModifiedBy;
    
    // Getters, setters
}
```

**AuditLog (Journal d'audit)**
```java
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(nullable = false)
    private String action; // CREATE, UPDATE, DELETE, LOGIN, etc.
    
    @Column(nullable = false)
    private String entityType;
    
    @Column
    private Long entityId;
    
    @Column(columnDefinition = "TEXT")
    private String oldValue;
    
    @Column(columnDefinition = "TEXT")
    private String newValue;
    
    @Column
    private String ipAddress;
    
    @Column
    private LocalDateTime timestamp;
    
    @Enumerated(EnumType.STRING)
    private AuditLevel level; // INFO, WARNING, CRITICAL
    
    // Getters, setters
}
```

**Webhook**
```java
@Entity
@Table(name = "webhooks")
public class Webhook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String url;
    
    @Enumerated(EnumType.STRING)
    private WebhookEvent event; // USER_CREATED, TRAINING_COMPLETED, etc.
    
    @Column(nullable = false)
    private String secretKey;
    
    @Column
    private Boolean isActive = true;
    
    @Column
    private Integer maxRetries = 3;
    
    @OneToMany(mappedBy = "webhook")
    private Set<WebhookLog> logs = new HashSet<>();
    
    // Getters, setters
}
```

**WebhookLog**
```java
@Entity
@Table(name = "webhook_logs")
public class WebhookLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "webhook_id", nullable = false)
    private Webhook webhook;
    
    @Column(columnDefinition = "TEXT")
    private String payload;
    
    @Column
    private Integer statusCode;
    
    @Column(columnDefinition = "TEXT")
    private String response;
    
    @Column
    private Boolean success;
    
    @Column
    private Integer attemptNumber;
    
    @Column
    private LocalDateTime sentAt;
    
    // Getters, setters
}
```

**Diagramme de relations :**
```
SystemConfig "*" -----> "1" User (modifier)
AuditLog "*" -----> "1" User
Webhook "1" -----> "*" WebhookLog
```

***

## 3. Diagrammes UML Additionnels

### 3.1 Diagramme de Cas d'Utilisation Général

**Acteurs et Cas d'Utilisation:**[3][1]

```
┌─────────────────────────────────────────────────────────────┐
│                 Système de Gestion de Compétences           │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Employé:                                                    │
│    - S'authentifier                                          │
│    - Consulter ses compétences                               │
│    - Effectuer une auto-évaluation                           │
│    - S'inscrire à une formation                              │
│    - Suivre son parcours de formation                        │
│    - Consulter ses certifications                            │
│    - Télécharger ses attestations                            │
│                                                              │
│  Manager:                                                    │
│    - Valider les auto-évaluations                            │
│    - Visualiser les compétences de l'équipe                  │
│    - Créer un parcours de développement                      │
│    - Approuver les inscriptions à des formations             │
│    - Analyser les écarts de compétences                      │
│    - Consulter les tableaux de bord d'équipe                 │
│                                                              │
│  Administrateur RH:                                          │
│    - Gérer le référentiel de compétences                     │
│    - Créer et gérer les métiers                              │
│    - Gérer le catalogue de formations                        │
│    - Organiser des campagnes d'évaluation                    │
│    - Planifier des sessions de formation                     │
│    - Générer des rapports stratégiques                       │
│    - Analyser les besoins de l'entreprise                    │
│                                                              │
│  Formateur:                                                  │
│    - Gérer les contenus de formation                         │
│    - Animer les sessions                                     │
│    - Émarger les présences                                   │
│    - Évaluer les participants                                │
│                                                              │
│  Administrateur Système:                                     │
│    - Gérer les utilisateurs et rôles                         │
│    - Configurer le système                                   │
│    - Gérer les intégrations                                  │
│    - Consulter les logs d'audit                              │
│    - Gérer les sauvegardes                                   │
└─────────────────────────────────────────────────────────────┘
```

***

### 3.2 Diagramme de Séquence : Inscription à une Formation

```
Employé     Interface     BackendAPI    Manager     RH     EmailService
  │             │              │           │         │           │
  │─Rechercher──>│              │           │         │           │
  │<─Catalogue───│              │           │         │           │
  │             │              │           │         │           │
  │─Soumettre──>│              │           │         │           │
  │  demande    │──POST────────>│           │         │           │
  │             │  /registrations│          │         │           │
  │             │              │─Sauvegarder│         │           │
  │             │              │  PENDING    │         │           │
  │             │              │            │         │           │
  │             │              │──Notifier───────────>│           │
  │             │              │   manager   │         │           │
  │             │              │            │──Email──────────────>│
  │             │<─201 Created─│            │         │           │
  │<─Confirmation│              │            │         │           │
  │             │              │            │         │           │
  │             │              │<─Approuver─│         │           │
  │             │              │   /approve  │         │           │
  │             │              │            │         │           │
  │             │              │─APPROVED────│         │           │
  │             │              │  _MANAGER   │         │           │
  │             │              │            │         │           │
  │             │              │──Notifier───────────────────────>│
  │             │              │    RH       │         │           │
  │             │              │            │         │──Email────>│
  │             │              │            │         │           │
  │             │              │            │<─Valider─          │
  │             │              │<───────────────────────          │
  │             │              │   /validate │         │           │
  │             │              │            │         │           │
  │             │              │─CONFIRMED───│         │           │
  │             │              │            │         │           │
  │             │              │──Envoyer convocation─────────────>│
  │<─Notification│<─WebSocket──│            │         │           │
  │  confirmée  │              │            │         │           │
```

***

### 3.3 Diagramme d'Activité : Campagne d'Évaluation

```
                    [Début]
                       │
                       ▼
            ┌─────────────────────┐
            │ Admin RH crée       │
            │ campagne            │
            └──────────┬──────────┘
                       │
                       ▼
            ┌─────────────────────┐
            │ Définir périmètre   │
            │ (users/departments) │
            └──────────┬──────────┘
                       │
                       ▼
            ┌─────────────────────┐
            │ Lancer la campagne  │
            └──────────┬──────────┘
                       │
                       ▼
            ┌─────────────────────┐
            │ Notifier tous les   │
            │ participants        │
            └──────────┬──────────┘
                       │
                       ├───────────────────┐
                       ▼                   ▼
            ┌──────────────────┐  ┌──────────────────┐
            │ Employé fait     │  │ Relances auto    │
            │ auto-évaluation  │  │ J-7, J-3, J-1    │
            └────────┬─────────┘  └──────────────────┘
                     │
                     ▼
            ┌──────────────────┐
            │ Soumet évaluation│
            └────────┬─────────┘
                     │
                     ▼
            ┌──────────────────┐
            │ Notifier manager │
            └────────┬─────────┘
                     │
                     ▼
            ┌──────────────────┐
            │ Manager valide   │
            │ ou ajuste        │
            └────────┬─────────┘
                     │
                     ▼
            ┌──────────────────┐
            │ Consolidation    │
            │ des résultats    │
            └────────┬─────────┘
                     │
                     ▼
        ┌────────────────────────┐
        │ Calculer écarts de     │
        │ compétences            │
        └────────┬───────────────┘
                 │
                 ▼
        ┌────────────────────────┐
        │ Générer rapports       │
        │ et analyses            │
        └────────┬───────────────┘
                 │
                 ▼
        ┌────────────────────────┐
        │ Clôturer campagne      │
        └────────┬───────────────┘
                 │
                 ▼
              [Fin]
```

***

### 3.4 Diagramme d'États : TrainingRegistration

```
              [Création]
                  │
                  ▼
           ┌───────────┐
           │  PENDING  │ ◄──── Soumise par employé
           └─────┬─────┘
                 │
        ┌────────┼────────┐
        │                 │
        ▼                 ▼
┌──────────────┐   ┌─────────────┐
│   REJECTED   │   │  APPROVED_  │
│              │   │   MANAGER   │
└──────────────┘   └──────┬──────┘
                          │
                 ┌────────┼────────┐
                 │                 │
                 ▼                 ▼
         ┌──────────────┐   ┌─────────────┐
         │   REJECTED   │   │  APPROVED_  │
         │              │   │     HR      │
         └──────────────┘   └──────┬──────┘
                                   │
                                   ▼
                            ┌─────────────┐
                            │  CONFIRMED  │
                            └──────┬──────┘
                                   │
                          ┌────────┼────────┐
                          │                 │
                          ▼                 ▼
                   ┌─────────────┐   ┌─────────────┐
                   │  COMPLETED  │   │  CANCELLED  │
                   └─────────────┘   └─────────────┘
```

***

## 4. Modèle de Données Relationnel (ERD Simplifié)

### 4.1 Relations Principales

**Cardinalités clés:**[2][4]

- User (1) ←──→ (N) Role : Many-to-Many
- User (N) ────→ (1) Department : Many-to-One
- User (1) ←──→ (1) User : Self-referencing (manager)
- Skill (N) ────→ (1) SkillCategory : Many-to-One
- JobPosition (1) ←──→ (N) JobPositionSkill ←──→ (N) Skill : Many-to-Many avec attributs
- SkillEvaluation (N) ────→ (1) User : Many-to-One
- SkillEvaluation (N) ────→ (1) Skill : Many-to-One
- SkillEvaluation (N) ────→ (1) EvaluationCampaign : Many-to-One
- Training (1) ←──→ (N) TrainingSession : One-to-Many
- TrainingSession (1) ←──→ (N) TrainingRegistration : One-to-Many
- TrainingRegistration (N) ────→ (1) User : Many-to-One
- LearningPath (1) ←──→ (N) PathStep ←──→ (N) Training : One-to-Many et Many-to-One
- User (1) ←──→ (1) UserXP : One-to-One

***

## 5. Structure de la Base de Données

### 5.1 Tables Principales (57 tables au total)

**Tables de sécurité et utilisateurs (7):**[4][5]
- users
- roles
- permissions
- role_permissions (table de liaison)
- user_roles (table de liaison)
- departments
- job_positions

**Tables de compétences (6):**
- skills
- skill_categories
- skill_levels
- job_position_skills (table de liaison avec attributs)

**Tables d'évaluation (9):**
- skill_evaluations
- evaluation_evidences
- evaluation_campaigns
- campaign_target_users (table de liaison)
- campaign_target_departments (table de liaison)
- evaluations_360
- skill_tests
- test_questions
- test_attempts
- test_answers

**Tables d'analyse (2):**
- skill_gaps
- skill_gap_analyses

**Tables de formations (11):**
- trainings
- training_skills (table de liaison)
- training_skill_gains
- training_sessions
- training_registrations
- session_attendances
- training_ratings
- certificates
- certifications
- certification_skills (table de liaison)
- certification_prerequisites (table de liaison)

**Tables de parcours (7):**
- learning_paths
- path_steps
- path_milestones
- user_xp
- badges
- user_badges (table de liaison)
- xp_transactions

**Tables de communication (6):**
- notifications
- messages
- message_threads
- message_attachments
- announcements
- announcement_target_departments (table de liaison)

**Tables d'administration (5):**
- system_configs
- audit_logs
- webhooks
- webhook_logs
- data_imports

***

## 6. Index et Contraintes Recommandés

### 6.1 Index de Performance[5][4]

```sql
-- Index sur les clés étrangères
CREATE INDEX idx_user_department ON users(department_id);
CREATE INDEX idx_user_manager ON users(manager_id);
CREATE INDEX idx_skill_category ON skills(category_id);
CREATE INDEX idx_evaluation_user ON skill_evaluations(user_id);
CREATE INDEX idx_evaluation_skill ON skill_evaluations(skill_id);
CREATE INDEX idx_evaluation_campaign ON skill_evaluations(campaign_id);
CREATE INDEX idx_registration_user ON training_registrations(user_id);
CREATE INDEX idx_registration_session ON training_registrations(session_id);

-- Index sur les colonnes fréquemment recherchées
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_status ON users(status);
CREATE INDEX idx_skill_code ON skills(code);
CREATE INDEX idx_training_code ON trainings(code);
CREATE INDEX idx_evaluation_status ON skill_evaluations(status);
CREATE INDEX idx_registration_status ON training_registrations(status);

-- Index composites pour les requêtes complexes
CREATE INDEX idx_evaluation_user_campaign ON skill_evaluations(user_id, campaign_id);
CREATE INDEX idx_registration_user_status ON training_registrations(user_id, status);
CREATE INDEX idx_gap_user_priority ON skill_gaps(user_id, priority);

-- Index sur les dates pour les filtres temporels
CREATE INDEX idx_session_dates ON training_sessions(start_date, end_date);
CREATE INDEX idx_campaign_dates ON evaluation_campaigns(start_date, end_date);
CREATE INDEX idx_notification_created ON notifications(created_at);
```

### 6.2 Contraintes d'Intégrité

```sql
-- Contraintes de vérification
ALTER TABLE skill_levels ADD CONSTRAINT chk_level_range 
  CHECK (level BETWEEN 1 AND 5);

ALTER TABLE training_ratings ADD CONSTRAINT chk_rating_range 
  CHECK (content_rating BETWEEN 1 AND 5 
    AND trainer_rating BETWEEN 1 AND 5
    AND organization_rating BETWEEN 1 AND 5
    AND overall_rating BETWEEN 1 AND 5);

ALTER TABLE test_attempts ADD CONSTRAINT chk_score_range 
  CHECK (score BETWEEN 0 AND 100);

ALTER TABLE user_xp ADD CONSTRAINT chk_xp_positive 
  CHECK (total_xp >= 0 AND current_level >= 1);

-- Contraintes d'unicité
ALTER TABLE users ADD CONSTRAINT uk_user_email UNIQUE (email);
ALTER TABLE skills ADD CONSTRAINT uk_skill_code UNIQUE (code);
ALTER TABLE trainings ADD CONSTRAINT uk_training_code UNIQUE (code);
ALTER TABLE certificates ADD CONSTRAINT uk_certificate_number UNIQUE (certificate_number);

-- Contraintes de clés étrangères avec cascade
ALTER TABLE skill_evaluations 
  ADD CONSTRAINT fk_evaluation_user 
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE training_registrations 
  ADD CONSTRAINT fk_registration_user 
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
```

***

## 7. Vues SQL Utiles

### 7.1 Vues Métier[4]

```sql
-- Vue des compétences actuelles par employé
CREATE VIEW v_user_current_skills AS
SELECT 
    u.id AS user_id,
    u.first_name,
    u.last_name,
    s.id AS skill_id,
    s.name AS skill_name,
    sc.name AS category_name,
    sl.level AS current_level,
    sl.name AS level_name,
    se.validation_date
FROM users u
JOIN skill_evaluations se ON u.id = se.user_id
JOIN skills s ON se.skill_id = s.id
JOIN skill_categories sc ON s.category_id = sc.id
JOIN skill_levels sl ON se.validated_level_id = sl.id
WHERE se.status = 'VALIDATED'
AND se.id IN (
    SELECT MAX(id) 
    FROM skill_evaluations 
    WHERE user_id = u.id AND skill_id = s.id
);

-- Vue des écarts de compétences
CREATE VIEW v_skill_gaps_summary AS
SELECT 
    u.id AS user_id,
    u.first_name,
    u.last_name,
    d.name AS department_name,
    jp.title AS job_position,
    COUNT(sg.id) AS total_gaps,
    SUM(CASE WHEN sg.priority = 'CRITICAL' THEN 1 ELSE 0 END) AS critical_gaps,
    SUM(CASE WHEN sg.priority = 'HIGH' THEN 1 ELSE 0 END) AS high_priority_gaps,
    AVG(sg.gap) AS avg_gap
FROM users u
LEFT JOIN skill_gaps sg ON u.id = sg.user_id
LEFT JOIN departments d ON u.department_id = d.id
LEFT JOIN job_positions jp ON u.job_position_id = jp.id
WHERE sg.status = 'IDENTIFIED'
GROUP BY u.id, u.first_name, u.last_name, d.name, jp.title;

-- Vue des formations complétées par utilisateur
CREATE VIEW v_user_training_history AS
SELECT 
    u.id AS user_id,
    u.first_name,
    u.last_name,
    t.title AS training_title,
    t.duration,
    ts.start_date,
    ts.end_date,
    tr.status,
    COUNT(sa.id) AS attendance_count,
    tr.certificate_issued
FROM users u
JOIN training_registrations tr ON u.id = tr.user_id
JOIN training_sessions ts ON tr.session_id = ts.id
JOIN trainings t ON ts.training_id = t.id
LEFT JOIN session_attendances sa ON tr.id = sa.registration_id
WHERE tr.status = 'COMPLETED'
GROUP BY u.id, u.first_name, u.last_name, t.title, t.duration, 
         ts.start_date, ts.end_date, tr.status, tr.certificate_issued;

-- Vue des statistiques de département
CREATE VIEW v_department_stats AS
SELECT 
    d.id AS department_id,
    d.name AS department_name,
    COUNT(DISTINCT u.id) AS employee_count,
    COUNT(DISTINCT sg.skill_id) AS unique_skills_needed,
    AVG(uxp.total_xp) AS avg_xp,
    SUM(d.training_budget) AS total_budget
FROM departments d
LEFT JOIN users u ON d.id = u.department_id
LEFT JOIN skill_gaps sg ON u.id = sg.user_id
LEFT JOIN user_xp uxp ON u.id = uxp.user_id
GROUP BY d.id, d.name;
```

***

## 8. Diagramme de Déploiement

### 8.1 Architecture de Déploiement avec Kubernetes[6][7]

```
┌─────────────────────────────────────────────────────────┐
│                    Load Balancer (Ingress)              │
└────────────────────┬────────────────────────────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
        ▼                         ▼
┌──────────────┐         ┌──────────────┐
│   Angular    │         │   Angular    │
│  Frontend    │         │  Frontend    │
│   (Nginx)    │         │   (Nginx)    │
└──────┬───────┘         └──────┬───────┘
       │                        │
       └────────────┬───────────┘
                    │
                    ▼
         ┌──────────────────┐
         │   API Gateway    │
         │    (Spring)      │
         └──────┬───────────┘
                │
      ┌─────────┴─────────┐
      │                   │
      ▼                   ▼
┌─────────────┐     ┌─────────────┐
│  Spring Boot│     │  Spring Boot│
│   Backend   │     │   Backend   │
│   Service   │     │   Service   │
└──────┬──────┘     └──────┬──────┘
       │                   │
       └─────────┬─────────┘
                 │
       ┌─────────┼─────────┬─────────┐
       │         │         │         │
       ▼         ▼         ▼         ▼
┌──────────┐ ┌─────┐ ┌────────┐ ┌────────┐
│PostgreSQL│ │Redis│ │MinIO   │ │RabbitMQ│
│ (Primary)│ │Cache│ │(Files) │ │(Queue) │
└────┬─────┘ └─────┘ └────────┘ └────────┘
     │
     ▼
┌──────────┐
│PostgreSQL│
│ (Replica)│
└──────────┘
```

***

## 9. Patterns de Conception Utilisés

### 9.1 Patterns Backend[1][2]

**Repository Pattern:**
```java
public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByCode(String code);
    List<Skill> findByCategoryId(Long categoryId);
    List<Skill> findByIsActiveTrue();
}
```

**Service Pattern:**
```java
@Service
@Transactional
public class SkillEvaluationService {
    private final SkillEvaluationRepository repository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    
    public SkillEvaluationDTO createSelfEvaluation(CreateEvaluationRequest request) {
        // Business logic
    }
}
```

**DTO Pattern:**
```java
@Data
@Builder
public class SkillEvaluationDTO {
    private Long id;
    private UserSummaryDTO employee;
    private SkillDTO skill;
    private SkillLevelDTO selfAssessedLevel;
    private SkillLevelDTO managerAssessedLevel;
    private String selfComment;
    private EvaluationStatus status;
    private LocalDateTime selfEvaluationDate;
}
```

**Mapper Pattern (MapStruct):**
```java
@Mapper(componentModel = "spring")
public interface SkillEvaluationMapper {
    SkillEvaluationDTO toDTO(SkillEvaluation entity);
    SkillEvaluation toEntity(SkillEvaluationDTO dto);
    List<SkillEvaluationDTO> toDTOList(List<SkillEvaluation> entities);
}
```

**Strategy Pattern pour les notifications:**
```java
public interface NotificationStrategy {
    void send(Notification notification);
}

@Component
public class EmailNotificationStrategy implements NotificationStrategy {
    @Override
    public void send(Notification notification) {
        // Email sending logic
    }
}

@Component
public class PushNotificationStrategy implements NotificationStrategy {
    @Override
    public void send(Notification notification) {
        // Push notification logic
    }
}
```

**Observer Pattern pour les événements:**
```java
@Component
public class TrainingCompletedEventListener {
    
    @EventListener
    public void handleTrainingCompleted(TrainingCompletedEvent event) {
        // Update skill levels
        // Award XP
        // Send notifications
    }
}
```

**Factory Pattern pour la création de rapports:**
```java
public interface ReportFactory {
    Report createReport(ReportType type, ReportParameters params);
}

@Component
public class SkillGapReportFactory implements ReportFactory {
    @Override
    public Report createReport(ReportType type, ReportParameters params) {
        // Report creation logic
    }
}
```

***

## 10. Configuration Spring Boot

### 10.1 application.yml Principal[8][6]

```yaml
spring:
  application:
    name: skills-management-platform
  
  datasource:
    url: jdbc:postgresql://localhost:5432/skills_db
    username: ${DB_USERNAME:admin}
    password: ${DB_PASSWORD:secret}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
    show-sql: false
  
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.xml
  
  data:
    redis:
      host: localhost
      port: 6379
      password: ${REDIS_PASSWORD:}
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 2
  
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
  
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 15MB

jwt:
  secret: ${JWT_SECRET:your-secret-key-here-change-in-production}
  expiration: 86400000 # 24 hours
  refresh-expiration: 604800000 # 7 days

app:
  file-storage:
    location: ./uploads
  cors:
    allowed-origins: http://localhost:4200
  notification:
    enabled: true
  cache:
    ttl:
      users: 3600
      skills: 7200
      trainings: 3600

logging:
  level:
    com.company.skillsmanagement: DEBUG
    org.hibernate.SQL: DEBUG
    org.springframework.security: DEBUG
  file:
    name: logs/application.log
    max-size: 10MB
    max-history: 30

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

***

## 11. Scripts de Migration Liquibase

### 11.1 Exemple de Changelog[6]

```xml
<!-- db/changelog/db.changelog-master.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.9.xsd">

    <include file="db/changelog/v1.0/01-create-users-tables.xml"/>
    <include file="db/changelog/v1.0/02-create-skills-tables.xml"/>
    <include file="db/changelog/v1.0/03-create-evaluations-tables.xml"/>
    <include file="db/changelog/v1.0/04-create-trainings-tables.xml"/>
    <include file="db/changelog/v1.0/05-create-learning-paths-tables.xml"/>
    <include file="db/changelog/v1.0/06-create-notifications-tables.xml"/>
    <include file="db/changelog/v1.0/07-create-admin-tables.xml"/>
    <include file="db/changelog/v1.0/08-insert-reference-data.xml"/>
    <include file="db/changelog/v1.0/09-create-indexes.xml"/>
    
</databaseChangeLog>
```

***

***

## 12. Entités Additionnelles

### 12.1 Gestion des Fichiers

**File (Document générique)**
```java
@Entity
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String storagePath; // MinIO path

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long fileSize; // bytes

    @Column
    private String checksum; // MD5/SHA256

    @Enumerated(EnumType.STRING)
    private FileCategory category; // EVALUATION_EVIDENCE, TRAINING_MATERIAL, CERTIFICATE, etc.

    @ManyToOne
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    private User uploadedBy;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column
    private LocalDateTime lastAccessedAt;

    @Column
    private Boolean isPublic = false;

    @Column
    private Boolean isDeleted = false;

    @Column
    private LocalDateTime deletedAt;

    // Getters, setters
}
```

### 12.2 Templates d'Emails

**EmailTemplate**
```java
@Entity
@Table(name = "email_templates")
public class EmailTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String templateCode; // REGISTRATION_APPROVED, TRAINING_REMINDER, etc.

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String subject; // Peut contenir des placeholders {{userName}}

    @Column(columnDefinition = "TEXT", nullable = false)
    private String bodyHtml;

    @Column(columnDefinition = "TEXT")
    private String bodyText; // Version texte pour fallback

    @Column(columnDefinition = "TEXT")
    private String placeholders; // JSON: {"userName": "Nom de l'utilisateur", ...}

    @Enumerated(EnumType.STRING)
    private TemplateCategory category; // AUTHENTICATION, TRAINING, EVALUATION, NOTIFICATION

    @Column
    private Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastModifiedAt;

    @Version
    private Integer version;

    // Getters, setters
}
```

### 12.3 Suivi Budgétaire

**Budget**
```java
@Entity
@Table(name = "budgets")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer fiscalYear;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Budget individuel (optionnel)

    @Column(nullable = false)
    private BigDecimal allocatedAmount;

    @Column(nullable = false)
    private BigDecimal committedAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal spentAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal availableAmount;

    @Enumerated(EnumType.STRING)
    private BudgetStatus status; // DRAFT, ACTIVE, FROZEN, CLOSED

    @Column
    private LocalDate validFrom;

    @Column
    private LocalDate validTo;

    @OneToMany(mappedBy = "budget")
    private Set<BudgetTransaction> transactions = new HashSet<>();

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastUpdatedAt;

    // Getters, setters

    public void recalculateAvailable() {
        this.availableAmount = allocatedAmount
            .subtract(committedAmount)
            .subtract(spentAmount);
    }
}
```

**BudgetTransaction**
```java
@Entity
@Table(name = "budget_transactions")
public class BudgetTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "registration_id")
    private TrainingRegistration registration;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // COMMITMENT, EXPENSE, REIMBURSEMENT, ADJUSTMENT

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Column
    private String referenceNumber; // Invoice number, etc.

    // Getters, setters
}
```

### 12.4 Feedback Formateur

**TrainerFeedback**
```java
@Entity
@Table(name = "trainer_feedbacks")
public class TrainerFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "registration_id", nullable = false)
    private TrainingRegistration registration;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private User trainer;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;

    @Column(nullable = false)
    private Integer participationRating; // 1-5

    @Column(nullable = false)
    private Integer skillAcquisitionRating; // 1-5

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String areasForImprovement;

    @Column(columnDefinition = "TEXT")
    private String recommendedNextSteps;

    @OneToMany(mappedBy = "feedback", cascade = CascadeType.ALL)
    private Set<FeedbackSkillProgress> skillProgresses = new HashSet<>();

    @Column
    private LocalDateTime feedbackDate;

    @Column
    private Boolean isSharedWithParticipant = false;

    @Column
    private Boolean isSharedWithManager = false;

    // Getters, setters
}
```

**FeedbackSkillProgress**
```java
@Entity
@Table(name = "feedback_skill_progresses")
public class FeedbackSkillProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "feedback_id", nullable = false)
    private TrainerFeedback feedback;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @ManyToOne
    @JoinColumn(name = "level_before_id")
    private SkillLevel levelBefore;

    @ManyToOne
    @JoinColumn(name = "level_after_id")
    private SkillLevel levelAfter;

    @Column(columnDefinition = "TEXT")
    private String observations;

    // Getters, setters
}
```

**Diagramme de relations additionnelles :**
```
File "*" -----> "1" User (uploader)
EmailTemplate "*" -----> "1" User (creator)
Budget "1" -----> "*" BudgetTransaction
Budget "*" -----> "1" Department
Budget "*" -----> "1" User (optionnel)
BudgetTransaction "*" -----> "1" TrainingRegistration
TrainerFeedback "*" -----> "1" TrainingRegistration
TrainerFeedback "*" -----> "1" User (trainer)
TrainerFeedback "*" -----> "1" User (participant)
TrainerFeedback "1" -----> "*" FeedbackSkillProgress
FeedbackSkillProgress "*" -----> "1" Skill
LearningPath "*" -----> "*" SkillGap (via table de liaison)
```

***

## 13. Architecture Frontend Angular

### 13.1 Structure Modulaire

**Organisation des modules:**
```
frontend/src/app/
├── core/                          # Module singleton
│   ├── services/
│   │   ├── auth.service.ts
│   │   ├── api.service.ts
│   │   ├── notification.service.ts
│   │   ├── websocket.service.ts
│   │   └── cache.service.ts
│   ├── guards/
│   │   ├── auth.guard.ts
│   │   ├── role.guard.ts
│   │   └── unsaved-changes.guard.ts
│   ├── interceptors/
│   │   ├── auth.interceptor.ts
│   │   ├── error.interceptor.ts
│   │   ├── loading.interceptor.ts
│   │   └── cache.interceptor.ts
│   └── models/
│       ├── user.model.ts
│       ├── skill.model.ts
│       └── api-response.model.ts
│
├── features/                      # Modules fonctionnels
│   ├── auth/
│   │   ├── login/
│   │   ├── register/
│   │   └── auth.routes.ts
│   ├── skills/
│   │   ├── skill-list/
│   │   ├── skill-detail/
│   │   ├── skill-category/
│   │   └── skills.routes.ts
│   ├── evaluations/
│   │   ├── self-evaluation/
│   │   ├── manager-validation/
│   │   ├── evaluation-360/
│   │   ├── campaigns/
│   │   └── evaluations.routes.ts
│   ├── trainings/
│   │   ├── training-catalog/
│   │   ├── training-detail/
│   │   ├── training-registration/
│   │   ├── my-trainings/
│   │   └── trainings.routes.ts
│   ├── learning-paths/
│   │   ├── path-list/
│   │   ├── path-create/
│   │   ├── path-progress/
│   │   └── paths.routes.ts
│   ├── dashboards/
│   │   ├── employee-dashboard/
│   │   ├── manager-dashboard/
│   │   ├── hr-dashboard/
│   │   └── dashboards.routes.ts
│   └── admin/
│       ├── users/
│       ├── departments/
│       ├── system-config/
│       └── admin.routes.ts
│
├── shared/                        # Module partagé
│   ├── components/
│   │   ├── header/
│   │   ├── sidebar/
│   │   ├── footer/
│   │   ├── skill-badge/
│   │   ├── skill-level-indicator/
│   │   ├── file-upload/
│   │   ├── data-table/
│   │   ├── confirmation-dialog/
│   │   └── loading-spinner/
│   ├── pipes/
│   │   ├── skill-level.pipe.ts
│   │   ├── date-locale.pipe.ts
│   │   └── file-size.pipe.ts
│   ├── directives/
│   │   ├── permission.directive.ts
│   │   └── tooltip.directive.ts
│   └── validators/
│       ├── email.validator.ts
│       └── custom.validators.ts
│
└── app.component.ts
```

### 13.2 Services Principaux

**AuthService (core/services/auth.service.ts)**
```typescript
@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser: Observable<User | null>;
  private tokenRefreshTimer: any;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    const storedUser = localStorage.getItem('currentUser');
    this.currentUserSubject = new BehaviorSubject<User | null>(
      storedUser ? JSON.parse(storedUser) : null
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/auth/login', { email, password })
      .pipe(
        tap(response => {
          localStorage.setItem('accessToken', response.accessToken);
          localStorage.setItem('refreshToken', response.refreshToken);
          localStorage.setItem('currentUser', JSON.stringify(response.user));
          this.currentUserSubject.next(response.user);
          this.startTokenRefreshTimer();
        })
      );
  }

  logout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    this.stopTokenRefreshTimer();
    this.router.navigate(['/login']);
  }

  refreshToken(): Observable<TokenResponse> {
    const refreshToken = localStorage.getItem('refreshToken');
    return this.http.post<TokenResponse>('/api/auth/refresh', { refreshToken })
      .pipe(
        tap(response => {
          localStorage.setItem('accessToken', response.accessToken);
        })
      );
  }

  hasRole(role: string): boolean {
    const user = this.currentUserSubject.value;
    return user?.roles?.some(r => r.name === role) ?? false;
  }

  hasPermission(permission: string): boolean {
    const user = this.currentUserSubject.value;
    return user?.roles?.some(role =>
      role.permissions?.some(p => p.name === permission)
    ) ?? false;
  }

  private startTokenRefreshTimer(): void {
    // Refresh token 5 minutes before expiration
    const tokenExpirationMs = this.getTokenExpiration() - (5 * 60 * 1000);
    this.tokenRefreshTimer = setTimeout(() => {
      this.refreshToken().subscribe();
    }, tokenExpirationMs);
  }

  private stopTokenRefreshTimer(): void {
    if (this.tokenRefreshTimer) {
      clearTimeout(this.tokenRefreshTimer);
    }
  }

  private getTokenExpiration(): number {
    // Decode JWT and get expiration
    const token = localStorage.getItem('accessToken');
    if (!token) return 0;
    const decoded = JSON.parse(atob(token.split('.')[1]));
    return decoded.exp * 1000;
  }
}
```

**ApiService (core/services/api.service.ts)**
```typescript
@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  get<T>(endpoint: string, params?: any): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${endpoint}`, { params });
  }

  post<T>(endpoint: string, body: any): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${endpoint}`, body);
  }

  put<T>(endpoint: string, body: any): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}${endpoint}`, body);
  }

  delete<T>(endpoint: string): Observable<T> {
    return this.http.delete<T>(`${this.baseUrl}${endpoint}`);
  }

  uploadFile(endpoint: string, file: File, additionalData?: any): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    if (additionalData) {
      Object.keys(additionalData).forEach(key => {
        formData.append(key, additionalData[key]);
      });
    }
    return this.http.post(`${this.baseUrl}${endpoint}`, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }
}
```

### 13.3 State Management avec Signals (Angular 18+)

**SkillsStore (features/skills/store/skills.store.ts)**
```typescript
@Injectable({ providedIn: 'root' })
export class SkillsStore {
  // Signals
  private skillsState = signal<Skill[]>([]);
  private loadingState = signal<boolean>(false);
  private errorState = signal<string | null>(null);
  private selectedSkillState = signal<Skill | null>(null);

  // Computed signals
  readonly skills = this.skillsState.asReadonly();
  readonly loading = this.loadingState.asReadonly();
  readonly error = this.errorState.asReadonly();
  readonly selectedSkill = this.selectedSkillState.asReadonly();

  readonly technicalSkills = computed(() =>
    this.skillsState().filter(s => s.type === 'TECHNICAL')
  );

  readonly softSkills = computed(() =>
    this.skillsState().filter(s => s.type === 'SOFT_SKILL')
  );

  constructor(private apiService: ApiService) {}

  loadSkills(): void {
    this.loadingState.set(true);
    this.errorState.set(null);

    this.apiService.get<Skill[]>('/api/skills').subscribe({
      next: (skills) => {
        this.skillsState.set(skills);
        this.loadingState.set(false);
      },
      error: (error) => {
        this.errorState.set(error.message);
        this.loadingState.set(false);
      }
    });
  }

  selectSkill(skillId: number): void {
    const skill = this.skillsState().find(s => s.id === skillId);
    this.selectedSkillState.set(skill ?? null);
  }

  addSkill(skill: Skill): void {
    this.skillsState.update(skills => [...skills, skill]);
  }

  updateSkill(updatedSkill: Skill): void {
    this.skillsState.update(skills =>
      skills.map(s => s.id === updatedSkill.id ? updatedSkill : s)
    );
  }

  deleteSkill(skillId: number): void {
    this.skillsState.update(skills => skills.filter(s => s.id !== skillId));
  }
}
```

### 13.4 Interceptors

**AuthInterceptor (core/interceptors/auth.interceptor.ts)**
```typescript
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('accessToken');

  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req);
};
```

**ErrorInterceptor (core/interceptors/error.interceptor.ts)**
```typescript
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const notificationService = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // Unauthorized - logout
        authService.logout();
      } else if (error.status === 403) {
        notificationService.error('Accès refusé');
      } else if (error.status === 500) {
        notificationService.error('Erreur serveur');
      }

      return throwError(() => error);
    })
  );
};
```

### 13.5 Routing avec Guards

**app.routes.ts**
```typescript
export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes')
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadChildren: () => import('./features/dashboards/dashboards.routes')
  },
  {
    path: 'skills',
    canActivate: [authGuard],
    loadChildren: () => import('./features/skills/skills.routes')
  },
  {
    path: 'trainings',
    canActivate: [authGuard],
    loadChildren: () => import('./features/trainings/trainings.routes')
  },
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN', 'HR_ADMIN'] },
    loadChildren: () => import('./features/admin/admin.routes')
  },
  { path: '**', redirectTo: '/dashboard' }
];
```

**AuthGuard (core/guards/auth.guard.ts)**
```typescript
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const currentUser = authService.currentUserValue;

  if (currentUser) {
    return true;
  }

  router.navigate(['/auth/login'], { queryParams: { returnUrl: state.url } });
  return false;
};
```

**RoleGuard (core/guards/role.guard.ts)**
```typescript
export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const requiredRoles = route.data['roles'] as string[];

  if (requiredRoles && requiredRoles.some(role => authService.hasRole(role))) {
    return true;
  }

  router.navigate(['/']);
  return false;
};
```

***

## 14. Stratégie de Cache

### 14.1 Cache Multi-Niveaux

**Architecture de cache:**
```
┌─────────────────────────────────────────────────────┐
│               Frontend (Angular)                     │
│  ┌──────────────────────────────────────────────┐  │
│  │  Cache Browser (LocalStorage/SessionStorage)  │  │
│  │  - User profile, preferences                  │  │
│  │  - Recent searches, filters                   │  │
│  │  TTL: Session based                           │  │
│  └──────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│               Backend (Spring Boot)                  │
│  ┌──────────────────────────────────────────────┐  │
│  │  L1: Caffeine Cache (In-Memory)              │  │
│  │  - Recently accessed entities                 │  │
│  │  - Max 1000 entries, 5 min TTL               │  │
│  └──────────────────────────────────────────────┘  │
│                         │                            │
│                         ▼                            │
│  ┌──────────────────────────────────────────────┐  │
│  │  L2: Redis Cache (Distributed)               │  │
│  │  - Shared across instances                    │  │
│  │  - Different TTL per data type                │  │
│  └──────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│               PostgreSQL Database                    │
└─────────────────────────────────────────────────────┘
```

### 14.2 Configuration Redis Cache

**CacheConfig.java**
```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(60))
            .disableCachingNullValues()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()
                )
            );
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Skills: 2 hours TTL (rarely change)
        cacheConfigurations.put("skills",
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(2)));

        // Skill categories: 4 hours TTL
        cacheConfigurations.put("skillCategories",
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(4)));

        // Users: 1 hour TTL (can change frequently)
        cacheConfigurations.put("users",
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)));

        // Trainings: 30 minutes TTL
        cacheConfigurations.put("trainings",
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)));

        // Training sessions: 15 minutes TTL (availability changes)
        cacheConfigurations.put("trainingSessions",
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(15)));

        // Evaluations: 10 minutes TTL (active data)
        cacheConfigurations.put("evaluations",
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)));

        // Statistics: 1 hour TTL
        cacheConfigurations.put("statistics",
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)));

        // Dashboards: 5 minutes TTL
        cacheConfigurations.put("dashboards",
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(cacheConfiguration())
            .withInitialCacheConfigurations(cacheConfigurations)
            .transactionAware()
            .build();
    }

    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
            "users", "skills", "trainings"
        );
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats());
        return cacheManager;
    }
}
```

### 14.3 Annotations de Cache

**SkillService.java (exemple)**
```java
@Service
@CacheConfig(cacheNames = "skills")
public class SkillService {

    @Cacheable(key = "#id")
    public SkillDTO findById(Long id) {
        // Query database only if not in cache
        return skillRepository.findById(id)
            .map(skillMapper::toDTO)
            .orElseThrow(() -> new NotFoundException("Skill not found"));
    }

    @Cacheable(key = "'all'")
    public List<SkillDTO> findAll() {
        return skillRepository.findAll().stream()
            .map(skillMapper::toDTO)
            .collect(Collectors.toList());
    }

    @CachePut(key = "#result.id")
    public SkillDTO update(Long id, UpdateSkillRequest request) {
        Skill skill = skillRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Skill not found"));
        // Update logic
        Skill updated = skillRepository.save(skill);
        return skillMapper.toDTO(updated);
    }

    @CacheEvict(allEntries = true)
    public void create(CreateSkillRequest request) {
        // Create new skill
        // Evict all cache entries because list changed
    }

    @CacheEvict(key = "#id")
    public void delete(Long id) {
        skillRepository.deleteById(id);
    }
}
```

### 14.4 Cache Invalidation Strategy

**Event-based cache eviction:**
```java
@Component
public class CacheEvictionListener {

    @Autowired
    private CacheManager cacheManager;

    @EventListener
    public void onSkillUpdated(SkillUpdatedEvent event) {
        Cache cache = cacheManager.getCache("skills");
        if (cache != null) {
            cache.evict(event.getSkillId());
            cache.evict("all"); // Also evict list
        }
    }

    @EventListener
    public void onUserUpdated(UserUpdatedEvent event) {
        Cache userCache = cacheManager.getCache("users");
        if (userCache != null) {
            userCache.evict(event.getUserId());
        }

        // Also evict related caches
        Cache evaluationCache = cacheManager.getCache("evaluations");
        if (evaluationCache != null) {
            evaluationCache.clear(); // Clear all if user data affects evaluations
        }
    }

    @Scheduled(cron = "0 0 3 * * ?") // Every day at 3 AM
    public void evictAllCachesAtNight() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
    }
}
```

### 14.5 Monitoring du Cache

**CacheMetrics.java**
```java
@Component
public class CacheMetrics {

    @Autowired
    private CacheManager cacheManager;

    @Scheduled(fixedRate = 60000) // Every minute
    public void logCacheStatistics() {
        if (cacheManager instanceof CaffeineCacheManager) {
            CaffeineCacheManager caffeineCacheManager = (CaffeineCacheManager) cacheManager;

            caffeineCacheManager.getCacheNames().forEach(cacheName -> {
                Cache cache = caffeineCacheManager.getCache(cacheName);
                if (cache instanceof CaffeineCache) {
                    com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                        ((CaffeineCache) cache).getNativeCache();

                    CacheStats stats = nativeCache.stats();
                    log.info("Cache '{}': hits={}, misses={}, hitRate={}%",
                        cacheName,
                        stats.hitCount(),
                        stats.missCount(),
                        stats.hitRate() * 100);
                }
            });
        }
    }

    @GetMapping("/actuator/cache-stats")
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();

        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                stats.put(cacheName, Map.of(
                    "name", cacheName,
                    "size", cache.getNativeCache().toString()
                ));
            }
        });

        return stats;
    }
}
```

***

## 15. Configuration Spring Security Détaillée

### 15.1 Security Configuration

**SecurityConfig.java**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                // Admin endpoints
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SYSTEM_ADMIN")

                // HR endpoints
                .requestMatchers("/api/skills/admin/**").hasAnyRole("HR_ADMIN", "ADMIN")
                .requestMatchers("/api/campaigns/**").hasAnyRole("HR_ADMIN", "ADMIN")

                // Manager endpoints
                .requestMatchers("/api/evaluations/validate/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers("/api/registrations/approve/**").hasAnyRole("MANAGER", "ADMIN")

                // All authenticated users
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"error\": \"Unauthorized\", \"message\": \"" +
                        authException.getMessage() + "\"}"
                    );
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"error\": \"Forbidden\", \"message\": \"Access denied\"}"
                    );
                })
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200",
            "https://app.company.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Total-Count"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

### 15.2 JWT Configuration

**JwtService.java**
```java
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String generateToken(
        Map<String, Object> extraClaims,
        UserDetails userDetails,
        long expiration
    ) {
        return Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

### 15.3 Rate Limiting

**RateLimitingFilter.java**
```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final int MAX_REQUESTS_PER_MINUTE = 60;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String clientIp = getClientIP(request);
        String key = "rate_limit:" + clientIp;

        Long requests = redisTemplate.opsForValue().increment(key);

        if (requests == 1) {
            redisTemplate.expire(key, 1, TimeUnit.MINUTES);
        }

        if (requests > MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("{\"error\": \"Too many requests\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
```

***

Cette documentation technique complète fournit tous les éléments nécessaires pour démarrer le développement de la plateforme de gestion de compétences et formation avec une architecture moderne, performante et sécurisée.