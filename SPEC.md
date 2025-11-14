# Documentation Complète des Fonctionnalités
## Plateforme de Gestion de Compétences et Formation

***

## 1. Vue d'ensemble du système

La plateforme de gestion de compétences et formation est une application web full-stack permettant aux entreprises de cartographier, évaluer et développer les compétences de leurs employés de manière structurée et mesurable.[1][2]

---

## 2. Acteurs du système

### 2.1 Administrateur Système
Responsable de la configuration globale de la plateforme, gestion des accès et paramètres généraux.[3]

### 2.2 Administrateur RH
Gère les référentiels de compétences, les formations disponibles et supervise l'ensemble des parcours.[4]

### 2.3 Manager
Supervise les compétences de son équipe, valide les évaluations et définit les objectifs de développement.[4]

### 2.4 Employé
Consulte ses compétences, suit ses formations et effectue ses auto-évaluations.[4]

### 2.5 Formateur
Crée et anime les contenus de formation, suit la progression des apprenants.[4]

***

## 3. Modules fonctionnels principaux

### 3.1 Module Gestion des Utilisateurs et Authentification

**F-AUTH-001 : Inscription et création de compte**
- L'administrateur peut créer des comptes utilisateurs individuels ou par import CSV
- Chaque utilisateur reçoit un email d'activation avec lien temporaire
- Les données obligatoires incluent : nom, prénom, email, département, poste
- Attribution automatique du rôle selon le département[2][3]

**F-AUTH-002 : Authentification sécurisée**
- Connexion via email/mot de passe avec validation JWT
- Authentification à deux facteurs optionnelle (2FA)
- Gestion des sessions avec expiration configurable (30 minutes par défaut)
- Mécanisme de rafraîchissement de token automatique[2]

**F-AUTH-003 : Gestion des rôles et permissions**
- Système de permissions granulaires par module
- Possibilité de créer des rôles personnalisés
- Héritage de permissions entre rôles
- Audit trail de toutes les modifications de permissions[3]

**F-AUTH-004 : Récupération de mot de passe**
- Demande de réinitialisation par email
- Lien de réinitialisation valide 24 heures
- Historique des 5 derniers mots de passe interdits
- Politique de complexité configurable[2]

---

### 3.2 Module Référentiel de Compétences

**F-COMP-001 : Création et organisation du référentiel**
- L'administrateur RH peut créer des catégories de compétences (Techniques, Soft Skills, Métier, Managériales)
- Chaque compétence possède : code unique, libellé, description détaillée, catégorie
- Organisation hiérarchique en domaines > sous-domaines > compétences
- Import/export du référentiel au format JSON ou Excel[5][4]

**F-COMP-002 : Définition des niveaux de maîtrise**
- Système de 5 niveaux standards : Débutant, Intermédiaire, Confirmé, Expert, Maître
- Description détaillée des critères pour chaque niveau
- Possibilité de personnaliser les libellés selon le contexte métier
- Mapping avec d'autres référentiels (ESCO, ROME, O*NET)[4]

**F-COMP-003 : Association compétences-métiers**
- Création de fiches métiers avec compétences requises
- Définition du niveau minimal et optimal pour chaque compétence
- Pondération de l'importance de chaque compétence (critique, importante, souhaitable)
- Versioning des référentiels métiers[3][4]

**F-COMP-004 : Taxonomie intelligente**
- Suggestions automatiques de compétences similaires lors de la création
- Détection des doublons et synonymes
- Regroupement intelligent par domaine
- Recherche full-text multi-critères[4]

---

### 3.3 Module Évaluation des Compétences

**F-EVAL-001 : Auto-évaluation employé**
- Interface de saisie intuitive avec échelle de 1 à 5
- Possibilité de joindre des justificatifs (certificats, projets)
- Commentaires libres pour contextualiser l'évaluation
- Sauvegarde en brouillon et finalisation
- Historique des auto-évaluations précédentes[6][4]

**F-EVAL-002 : Évaluation par le manager**
- Validation ou ajustement des auto-évaluations
- Évaluation directe pour les compétences observables
- Système de notation avec écarts affichés
- Commentaires obligatoires en cas d'écart > 1 niveau
- Planning des entretiens d'évaluation[4]

**F-EVAL-003 : Évaluation 360°**
- Sollicitation de pairs, collaborateurs et clients internes
- Questionnaire anonyme ou identifié (configurable)
- Agrégation automatique des résultats
- Graphiques radar pour visualisation
- Export des feedbacks qualitatifs[6][4]

**F-EVAL-004 : Tests de compétences automatisés**
- Bibliothèque de QCM par compétence
- Génération de questions aléatoires
- Notation automatique avec seuil de validation
- Certificats de réussite téléchargeables
- Statistiques de taux de réussite[4]

**F-EVAL-005 : Campagnes d'évaluation**
- Planification annuelle ou semestrielle
- Notifications automatiques aux participants
- Tableau de suivi du taux de complétion
- Relances automatiques configurables
- Clôture et archivage des campagnes[5][3]

***

### 3.4 Module Analyse des Écarts et Besoins

**F-ANAL-001 : Cartographie des compétences**
- Matrice compétences actuelles vs requises par employé
- Vue agrégée par équipe, département, entreprise
- Identification des compétences critiques manquantes
- Heatmap visuelle des écarts
- Export en format PDF ou Excel[6][4]

**F-ANAL-002 : Analyse des écarts individuels**
- Calcul automatique : (Niveau requis - Niveau actuel)
- Priorisation des écarts selon l'importance de la compétence
- Recommandations de formations adaptées
- Estimation du temps de développement nécessaire
- Plan de développement personnalisé[4]

**F-ANAL-003 : Analyse des écarts collectifs**
- Identification des compétences manquantes à l'échelle de l'équipe
- Détection des risques de compétences critiques détenues par une seule personne
- Planification des besoins de recrutement ou formation
- Tableaux de bord stratégiques pour la direction
- Alertes sur les compétences en voie de disparition[4]

**F-ANAL-004 : Prédiction des besoins futurs**
- Intégration des projets à venir et compétences nécessaires
- Anticipation des départs (retraite, mobilité)
- Calcul de la capacité de montée en compétence
- Scénarios de simulation
- Recommandations d'actions préventives[4]

***

### 3.5 Module Catalogue de Formations

**F-FORM-001 : Gestion du catalogue**
- Création de fiches formation complètes (titre, description, objectifs pédagogiques, durée, prérequis)
- Association aux compétences développées avec gain de niveau attendu
- Types de formation : présentiel, e-learning, blended, externe
- Prix, fournisseur, nombre de places disponibles
- Notation et avis des participants[4]

**F-FORM-002 : Organisation des sessions**
- Planification des sessions avec dates, lieux, formateur
- Gestion des inscriptions avec validation manager
- Liste d'attente automatique si session complète
- Envoi de convocations et rappels automatiques
- Gestion des annulations et reports[3][4]

**F-FORM-003 : Contenus e-learning**
- Upload de modules SCORM/xAPI
- Création de parcours pédagogiques séquencés
- Intégration de vidéos, quiz, documents
- Suivi de progression (% de complétion)
- Téléchargement des ressources pédagogiques[4]

**F-FORM-004 : Certifications et badges**
- Définition de certifications internes
- Attribution automatique après validation des prérequis
- Bibliothèque de badges numériques
- Export vers LinkedIn ou autres plateformes
- Renouvellement périodique des certifications[4]

***

### 3.6 Module Parcours de Formation

**F-PARC-001 : Création de parcours personnalisés**
- Le manager définit un parcours de développement pour son employé
- Sélection des compétences cibles avec niveaux visés
- Association automatique des formations recommandées
- Définition des jalons et échéances
- Validation par l'employé et signature électronique[6][4]

**F-PARC-002 : Parcours types par métier**
- Bibliothèque de parcours prédéfinis pour chaque métier
- Clone et personnalisation possible
- Durée totale et budget estimés
- Prérequis et ordre de réalisation
- Taux de réussite moyen du parcours[4]

**F-PARC-003 : Suivi de progression**
- Tableau de bord visuel de l'avancement
- Timeline avec formations réalisées et à venir
- Notifications des prochaines échéances
- Évaluation de la montée en compétence après chaque formation
- Ajustement dynamique du parcours[4]

**F-PARC-004 : Gamification**
- Système de points d'expérience (XP) gagnés
- Niveaux de progression (Bronze, Argent, Or, Platine)
- Défis mensuels avec récompenses
- Classements par équipe
- Streaks de développement continu[4]

---

### 3.7 Module Gestion des Inscriptions et Planning

**F-INSC-001 : Inscription aux formations**
- Catalogue avec filtres (compétence, type, durée, disponibilité)
- Demande d'inscription avec justification optionnelle
- Workflow de validation (Manager > RH > Budget)
- Confirmation automatique ou liste d'attente
- Annulation avec délai de carence configurable[3][4]

**F-INSC-002 : Planning individuel**
- Vue calendrier des formations planifiées
- Synchronisation avec calendrier externe (Outlook, Google)
- Gestion des conflits d'agenda
- Réservation de créneaux pour les formations internes
- Export iCal[4]

**F-INSC-003 : Planning des ressources**
- Vue des formateurs et leur disponibilité
- Réservation des salles de formation
- Gestion du matériel pédagogique
- Optimisation automatique de l'utilisation des ressources
- Alertes de sur-réservation[3]

***

### 3.8 Module Suivi et Évaluation des Formations

**F-SUIV-001 : Feuille de présence**
- Émargement électronique via QR code
- Suivi en temps réel des présences
- Génération automatique des feuilles d'émargement PDF
- Calcul du temps de formation effectif
- Export pour organismes certificateurs[3]

**F-SUIV-002 : Évaluation à chaud**
- Questionnaire de satisfaction post-formation immédiat
- Notation de la qualité (contenu, formateur, organisation)
- Commentaires libres
- Calcul automatique des moyennes
- Dashboard qualité des formations[4]

**F-SUIV-003 : Évaluation à froid**
- Questionnaire envoyé 3 mois après la formation
- Mesure de l'application des acquis en situation de travail
- Évaluation de la montée en compétence effective
- Comparaison avant/après
- Calcul du ROI de la formation[4]

**F-SUIV-004 : Attestations et certificats**
- Génération automatique après validation de présence
- Modèles personnalisables par type de formation
- Signature électronique automatique
- Archive numérique sécurisée
- Envoi automatique par email[3]

***

### 3.9 Module Tableaux de Bord et Reporting

**F-DASH-001 : Tableau de bord employé**
- Synthèse de mes compétences actuelles
- Mes formations en cours et à venir
- Progression vers mes objectifs
- Compétences à développer en priorité
- Statistiques personnelles (heures de formation, certifications)[4]

**F-DASH-002 : Tableau de bord manager**
- Cartographie des compétences de l'équipe
- Écarts critiques à combler
- Suivi des parcours de développement
- Taux de complétion des évaluations
- Budget formation consommé vs alloué[4]

**F-DASH-003 : Tableau de bord RH**
- Indicateurs globaux (taux de couverture, écarts moyens)
- Analyse par département, métier, compétence
- Efficacité des formations (taux de réussite, satisfaction)
- Prédiction des besoins futurs
- ROI des investissements formation[4]

**F-DASH-004 : Tableau de bord direction**
- Vision stratégique des compétences clés
- Risques identifiés (compétences critiques, dépendances)
- Comparaison avec benchmarks sectoriels
- Évolution des capacités dans le temps
- Coûts et bénéfices des programmes de développement[4]

**F-DASH-005 : Rapports personnalisés**
- Générateur de rapports ad-hoc
- Filtres multi-critères (période, périmètre, compétences)
- Exports en PDF, Excel, CSV
- Planification de rapports récurrents
- Envoi automatique par email[5][3]

***

### 3.10 Module Notifications et Communication

**F-NOTIF-001 : Notifications système**
- Alertes en temps réel (formation approuvée, évaluation à compléter)
- Centre de notifications avec historique
- Badge de compteur non lus
- Marquage comme lu/archivé
- Paramétrage des préférences par type[3]

**F-NOTIF-002 : Emails automatiques**
- Templates personnalisables par événement
- Envoi différé selon les préférences utilisateur
- Récapitulatif hebdomadaire optionnel
- Relances automatiques configurables
- Tracking d'ouverture et de clic[2]

**F-NOTIF-003 : Messagerie interne**
- Chat entre manager et employé sur le développement
- Fil de discussion par parcours de formation
- Pièces jointes et partage de documents
- Notifications en temps réel
- Archivage des conversations[4]

**F-NOTIF-004 : Annonces et actualités**
- Publication d'actualités formation
- Mise en avant de nouvelles compétences stratégiques
- Calendrier des événements (salons, conférences)
- Succès stories d'employés
- Flux RSS intégré[4]

***

### 3.11 Module Administration Système

**F-ADMIN-001 : Configuration générale**
- Paramétrage des échelles d'évaluation
- Définition des workflows de validation
- Personnalisation de la charte graphique
- Configuration SMTP pour les emails
- Paramètres de sécurité (politique de mots de passe, durée de session)[2][3]

**F-ADMIN-002 : Gestion de l'organisation**
- Structure hiérarchique de l'entreprise
- Départements, services, équipes
- Rattachements managériaux
- Synchronisation avec l'annuaire LDAP/Active Directory
- Gestion des sites géographiques[3]

**F-ADMIN-003 : Import/Export de données**
- Import massif d'utilisateurs (CSV, Excel)
- Import du référentiel de compétences
- Export des données pour analyse externe
- Mappings de colonnes configurables
- Validation et rapport d'erreurs[7][3]

**F-ADMIN-004 : Logs et audit**
- Traçabilité de toutes les actions sensibles
- Logs de connexion et d'accès
- Historique des modifications sur les données critiques
- Recherche et filtrage des logs
- Archivage automatique selon politique de rétention[7][3]

**F-ADMIN-005 : Sauvegarde et restauration**
- Sauvegardes automatiques quotidiennes
- Sauvegarde manuelle à la demande
- Restauration avec sélection de point dans le temps
- Vérification de l'intégrité des sauvegardes
- Stockage sécurisé externe[3]

---

### 3.12 Module API et Intégrations

**F-API-001 : API REST documentée**
- Documentation OpenAPI/Swagger complète
- Endpoints pour toutes les fonctionnalités principales
- Authentification via JWT ou API Key
- Rate limiting configurable
- Sandbox de test[5][2]

**F-API-002 : Webhooks**
- Notifications d'événements vers systèmes tiers
- Configuration des URLs de callback
- Retry automatique en cas d'échec
- Logs des appels webhook
- Signatures pour vérification d'authenticité[3]

**F-API-003 : Intégrations SIRH**
- Synchronisation bidirectionnelle des données employés
- Import des structures organisationnelles
- Export des données de formation pour paie
- Connecteurs préfabriqués (SAP, Workday, etc.)
- Mapping de champs personnalisable[3]

**F-API-004 : Intégrations LMS tierces**
- Import de catalogues de formation externes
- Suivi de progression depuis d'autres plateformes
- SSO (Single Sign-On) avec SAML 2.0
- API xAPI pour traçabilité
- Certificats interopérables[4]

***

## 4. Exigences non fonctionnelles

### 4.1 Performance
- Temps de réponse < 2 secondes pour 95% des requêtes
- Support de 1000 utilisateurs simultanés
- Scalabilité horizontale via containers
- Cache Redis pour optimisation[2][3]

### 4.2 Sécurité
- Chiffrement HTTPS obligatoire
- Données sensibles chiffrées en base (AES-256)
- Conformité RGPD (droit à l'oubli, portabilité)
- Tests de pénétration annuels
- Politique de sauvegarde 3-2-1[2][3]

### 4.3 Disponibilité
- SLA de 99.5% de disponibilité
- Maintenance programmée en dehors des heures ouvrées
- Plan de reprise d'activité (PRA) documenté
- Monitoring temps réel avec alertes
- Redondance des composants critiques[3]

### 4.4 Accessibilité
- Conformité WCAG 2.1 niveau AA
- Support des lecteurs d'écran
- Navigation au clavier complète
- Contrastes et tailles de police ajustables
- Support multilingue (français, anglais, espagnol)[2]

### 4.5 Compatibilité
- Navigateurs : Chrome, Firefox, Edge, Safari (2 dernières versions)
- Responsive design (desktop, tablette, mobile)
- Support offline progressif (PWA)
- Export compatible Office 365 et Google Workspace[2]

---

## 5. User Stories principales

### Employé
**US-EMP-001**: En tant qu'employé, je veux visualiser mes compétences actuelles et celles attendues pour mon poste afin d'identifier mes axes de développement.[6]

**US-EMP-002**: En tant qu'employé, je veux m'auto-évaluer sur mes compétences avec justificatifs afin de partager ma perception à mon manager.[6]

**US-EMP-003**: En tant qu'employé, je veux rechercher et m'inscrire à des formations correspondant à mes besoins afin de progresser.[6]

**US-EMP-004**: En tant qu'employé, je veux suivre ma progression dans mon parcours de développement afin de rester motivé.[6][4]

### Manager
**US-MAN-001**: En tant que manager, je veux avoir une cartographie visuelle des compétences de mon équipe afin d'identifier les forces et faiblesses.[6][4]

**US-MAN-002**: En tant que manager, je veux valider ou ajuster les auto-évaluations de mes collaborateurs afin d'avoir une vision réaliste.[6]

**US-MAN-003**: En tant que manager, je veux créer des parcours de développement personnalisés pour mes collaborateurs afin de les faire progresser.[6][4]

**US-MAN-004**: En tant que manager, je veux approuver les demandes de formation avec visibilité sur le budget afin de prioriser les investissements.[6]

### Administrateur RH
**US-RH-001**: En tant qu'admin RH, je veux créer et maintenir le référentiel de compétences de l'entreprise afin d'avoir une base commune.[6][3]

**US-RH-002**: En tant qu'admin RH, je veux organiser des campagnes d'évaluation planifiées afin d'assurer un suivi régulier.[6][3]

**US-RH-003**: En tant qu'admin RH, je veux analyser les écarts de compétences à l'échelle de l'entreprise afin de définir la stratégie formation.[6][4]

**US-RH-004**: En tant qu'admin RH, je veux gérer le catalogue de formations et les sessions afin d'offrir une offre adaptée.[6][4]

***

## 6. Workflows principaux

### 6.1 Workflow d'évaluation de compétences
1. Lancement de campagne d'évaluation par RH (dates début/fin)
2. Notification envoyée à tous les employés concernés
3. Employé complète son auto-évaluation et justificatifs
4. Notification au manager pour validation
5. Manager valide ou ajuste avec commentaires
6. Consolidation automatique des résultats
7. Génération de la cartographie des écarts
8. Clôture de campagne et archivage[3][6]

### 6.2 Workflow d'inscription à une formation
1. Employé parcourt le catalogue et sélectionne une formation
2. Soumission de demande d'inscription avec justification
3. Notification au manager pour approbation
4. Vérification budget disponible par RH
5. Si approuvé : inscription confirmée et convocation envoyée
6. Si refusé : notification avec motif à l'employé
7. Rappels automatiques J-7 et J-1 avant la session
8. Émargement le jour J et attestation post-formation[3][6]

### 6.3 Workflow de création de parcours
1. Manager identifie besoin de développement pour un employé
2. Consultation des écarts de compétences actuels
3. Définition des compétences cibles et niveaux visés
4. Système recommande formations adaptées
5. Sélection et ordonnancement des formations
6. Définition des jalons et échéances
7. Partage du parcours à l'employé pour validation
8. Signature électronique et lancement du parcours[6][4]

---

## 7. Règles métier

**R-001**: Un employé ne peut s'auto-évaluer qu'une seule fois par campagne d'évaluation.[3]

**R-002**: L'écart entre auto-évaluation et évaluation manager ne peut excéder 2 niveaux sans commentaire justificatif obligatoire.[6]

**R-003**: Une formation ne peut être validée que si le taux de présence est >= 80%.[3]

**R-004**: Les compétences critiques nécessitent une réévaluation tous les 6 mois maximum.[4]

**R-005**: Un employé ne peut pas être inscrit à plus de 3 formations simultanées.[3]

**R-006**: Le budget formation d'une équipe ne peut être dépassé de plus de 10% sans validation direction.[3]

**R-007**: Les certifications ont une durée de validité configurable nécessitant un renouvellement.[4]

**R-008**: Un parcours de formation doit comporter au minimum 2 formations.[4]

**R-009**: Une session de formation doit avoir au moins 5 inscrits pour être maintenue.[3]

**R-010**: Les données personnelles d'évaluation ne sont accessibles qu'à l'employé, son manager et les RH.[2]

**R-011**: Les formations annulées moins de 48h avant le début entraînent des frais d'annulation.[3]

**R-012**: Un employé doit avoir complété tous les prérequis avant de s'inscrire à une formation avancée.[4]

**R-013**: Les données d'évaluation de plus de 5 ans sont automatiquement archivées et anonymisées.[2]

**R-014**: Le formateur ne peut valider une session que si au moins 80% des participants ont émargé.[3]

**R-015**: Le budget formation d'un employé ne peut excéder 10 000€ par an sans validation direction.[3]

***

## 8. Fonctionnalités Additionnelles (Phase 2)

### 8.1 Module Gestion des Congés et Conflits

**F-LEAVE-001 : Détection des conflits d'agenda**
- Vérification automatique des congés lors de l'inscription à une formation
- Alerte en cas de chevauchement avec des absences planifiées
- Intégration avec le système RH de gestion des congés
- Proposition automatique de sessions alternatives[3]

**F-LEAVE-002 : Gestion des absences en formation**
- Signalement d'absence imprévu pendant une formation
- Calcul automatique du taux de présence
- Proposition de rattrapage si absence justifiée
- Notification automatique au formateur et RH[3]

### 8.2 Module Feedback Formateur

**F-FEEDBACK-001 : Évaluation individuelle post-formation**
- Le formateur peut évaluer chaque participant individuellement
- Notation sur compétences acquises (1-5)
- Commentaires personnalisés sur points forts et axes d'amélioration
- Recommandations de formations complémentaires
- Historique des feedbacks formateur par employé[4]

**F-FEEDBACK-002 : Plan d'action post-formation**
- Génération automatique d'un plan d'action basé sur le feedback
- Suivi de la mise en application des acquis
- Rappels automatiques à J+30, J+60, J+90
- Dashboard de suivi manager[4]

### 8.3 Module Gestion Budgétaire Avancée

**F-BUDGET-001 : Suivi budgétaire détaillé**
- Budget formation par employé, équipe, département
- Suivi en temps réel des dépenses engagées vs consommées
- Prévisionnel budgétaire annuel avec projection
- Alertes de dépassement de budget (50%, 75%, 90%, 100%)
- Historique des dépenses sur 5 ans[3]

**F-BUDGET-002 : Workflow d'approbation budgétaire**
- Validation budgétaire multi-niveaux selon montant
- < 500€ : Manager seul
- 500€ - 2000€ : Manager + RH
- > 2000€ : Manager + RH + Direction
- Justification obligatoire pour formations > 5000€
- Tableau de bord budgétaire pour la direction[3]

**F-BUDGET-003 : Optimisation budgétaire**
- Suggestions de formations équivalentes moins coûteuses
- Regroupement d'inscriptions pour tarifs de groupe
- Analyse du ROI par formation (coût vs gain de compétence)
- Reporting budgétaire trimestriel automatique[4]

### 8.4 Module Annulation et Remboursement

**F-CANCEL-001 : Workflow d'annulation**
- Demande d'annulation par l'employé avec justification
- Calcul automatique des frais d'annulation selon délai
- Validation manager pour annulations tardives
- Libération automatique de la place
- Notification de la liste d'attente[3]

**F-CANCEL-002 : Gestion des remboursements**
- Calcul du montant remboursable selon politique
- Workflow de validation du remboursement
- Réimputation budgétaire automatique
- Suivi des remboursements en attente
- Reporting des annulations par motif[3]

### 8.5 Module Archivage et Purge RGPD

**F-ARCHIVE-001 : Archivage automatique**
- Archivage automatique des données de plus de 3 ans
- Compression et stockage sur support archive
- Index de recherche pour retrouver les données archivées
- Restauration à la demande des données archivées[2]

**F-ARCHIVE-002 : Anonymisation**
- Anonymisation automatique des données à 5 ans
- Conservation des statistiques agrégées anonymes
- Suppression des données identifiantes
- Logs d'anonymisation pour audit[2]

**F-ARCHIVE-003 : Purge et droit à l'oubli**
- Workflow de demande de suppression (droit RGPD)
- Validation légale avant suppression définitive
- Suppression en cascade de toutes les données personnelles
- Conservation minimale pour obligations légales (paie, etc.)
- Certificat de suppression émis à l'utilisateur[2]

***

## 9. Conformité RGPD et Sécurité des Données

### 9.1 Principes RGPD

**Licéité du traitement**
- Consentement explicite pour les traitements optionnels (newsletter, photo)
- Base légale : exécution du contrat de travail pour les données RH
- Intérêt légitime pour l'amélioration des compétences
- Information claire via politique de confidentialité[2]

**Minimisation des données**
- Collecte uniquement des données nécessaires
- Pas de collecte de données sensibles (origine, religion, santé)
- Champs optionnels clairement identifiés
- Revue annuelle des données collectées[2]

**Limitation de la conservation**
- Données employés actifs : durée du contrat + 5 ans
- Données formations : 5 ans après la formation
- Données d'évaluation : 5 ans puis anonymisation
- Logs système : 12 mois
- Sauvegardes : 7 ans (obligations comptables)[2]

**Exactitude des données**
- Auto-vérification annuelle obligatoire par l'employé
- Notification de mise à jour nécessaire
- Correction en ligne possible
- Historique des modifications[2]

### 9.2 Droits des Personnes

**Droit d'accès**
- Export complet des données personnelles en JSON/PDF
- Délai de réponse : 48 heures
- Traçabilité des demandes d'accès
- Interface self-service dans le profil[2]

**Droit de rectification**
- Modification en ligne des données personnelles
- Demande de correction pour données RH (validation RH)
- Historique des modifications conservé
- Notification des modifications aux tiers si nécessaire[2]

**Droit à l'effacement (droit à l'oubli)**
- Demande via interface dédiée
- Validation RH et légal avant suppression
- Conservation minimale pour obligations légales
- Suppression effective sous 30 jours
- Certificat de suppression fourni[2]

**Droit à la portabilité**
- Export de toutes les données dans un format structuré
- Format JSON standard ou CSV
- Inclut : profil, compétences, formations, évaluations
- Téléchargement sécurisé[2]

**Droit d'opposition**
- Opposition au traitement pour marketing (newsletter)
- Opposition aux notifications non essentielles
- Paramétrage granulaire des consentements
- Respect immédiat de l'opposition[2]

**Droit à la limitation**
- Gel temporaire du traitement en cas de litige
- Marquage des données contestées
- Reprise du traitement après résolution
- Notification de la limitation[2]

### 9.3 Sécurité Technique

**Chiffrement des données**
- TLS 1.3 pour toutes les communications
- Chiffrement AES-256 des données au repos
- Chiffrement des sauvegardes
- Chiffrement des données sensibles en base (salaires, etc.)
- Gestion sécurisée des clés (HSM ou KMS)[2][3]

**Gestion des accès**
- Authentification forte (MFA optionnelle)
- Politique de mots de passe robuste (12 caractères min)
- Expiration des mots de passe (90 jours)
- Blocage après 5 tentatives échouées
- Déconnexion automatique après 30 min d'inactivité[2][3]

**Traçabilité**
- Logs d'accès aux données personnelles
- Logs de modifications des données sensibles
- Logs d'export de données
- Conservation des logs : 12 mois
- Alertes sur accès anormaux[2]

**Sauvegardes sécurisées**
- Sauvegardes quotidiennes automatiques
- Sauvegardes chiffrées
- Stockage sur site distant
- Tests de restauration trimestriels
- Rétention : 30 jours (incrémentielles) + 7 ans (annuelles)[3]

**Tests de sécurité**
- Pentests annuels par organisme externe
- Scan de vulnérabilités mensuels
- Bug bounty program (optionnel)
- Corrections des vulnérabilités critiques sous 48h[2]

### 9.4 Organisation RGPD

**Responsable du traitement**
- Entreprise utilisatrice de la plateforme
- Responsabilité de la conformité RGPD
- Publication du registre des traitements
- Désignation d'un DPO si nécessaire[2]

**Sous-traitant**
- Éditeur de la plateforme (nous)
- Contrat de sous-traitance conforme RGPD
- Mesures de sécurité documentées
- Audits de conformité annuels[2]

**Délégué à la Protection des Données (DPO)**
- Contact : dpo@company.com
- Conseil sur la conformité RGPD
- Point de contact avec la CNIL
- Réponse aux demandes d'exercice des droits[2]

**Analyse d'impact (PIA)**
- PIA réalisée pour le traitement des évaluations 360°
- PIA pour le traitement des données de performance
- Revue annuelle des PIA
- Documentation des risques et mesures[2]

### 9.5 Violations de Données

**Procédure de notification**
- Détection de la violation par le monitoring
- Investigation sous 24h
- Notification CNIL sous 72h si risque
- Notification personnes concernées si risque élevé
- Documentation de la violation[2]

**Plan de réponse aux incidents**
- Équipe de réponse dédiée
- Procédure d'escalade documentée
- Communication de crise préparée
- Post-mortem systématique
- Amélioration continue[2]

***

Cette documentation complète couvre l'ensemble des fonctionnalités nécessaires pour développer une plateforme robuste, évolutive et conforme RGPD de gestion des compétences et formations.