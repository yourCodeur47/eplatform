# Guide de Déploiement en Production

Ce guide détaille les procédures de déploiement de la plateforme en production sur Kubernetes.

---

## Table des Matières

1. [Prérequis](#prérequis)
2. [Architecture de Déploiement](#architecture-de-déploiement)
3. [Configuration](#configuration)
4. [Déploiement Initial](#déploiement-initial)
5. [Mise à Jour (Rolling Update)](#mise-à-jour-rolling-update)
6. [Rollback](#rollback)
7. [Monitoring](#monitoring)
8. [Backup et Restore](#backup-et-restore)
9. [Troubleshooting](#troubleshooting)

---

## Prérequis

### Infrastructure

- **Kubernetes Cluster** : 1.28+ (EKS, AKS, GKE ou on-premise)
  - Minimum 3 worker nodes
  - Node type : 4 CPU, 16 GB RAM
- **Helm 3.12+**
- **kubectl 1.28+**
- **Base de données PostgreSQL** : Instance managée (RDS, Cloud SQL) ou self-hosted
- **Redis Cluster** : Mode haute disponibilité
- **Object Storage** : S3, Azure Blob, ou MinIO cluster

### Accès et Permissions

- Accès kubectl au cluster de production
- Accès admin au repository Docker
- Secrets de production (DB, Redis, S3, JWT, etc.)
- Certificats SSL/TLS

### Outils Requis

```bash
# Installer kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

# Installer Helm
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash

# Vérifier les installations
kubectl version --client
helm version
```

---

## Architecture de Déploiement

### Diagramme d'Infrastructure

```
                      ┌─────────────────┐
                      │  Load Balancer  │
                      │   (Ingress)     │
                      └────────┬────────┘
                               │
           ┌───────────────────┴───────────────────┐
           │                                        │
    ┌──────▼──────┐                        ┌───────▼──────┐
    │   Frontend  │                        │   Backend    │
    │  (nginx)    │                        │ (Spring Boot)│
    │  3 replicas │                        │  3 replicas  │
    └─────────────┘                        └──────┬───────┘
                                                  │
                      ┌───────────────────────────┼──────────────┐
                      │                           │              │
               ┌──────▼──────┐            ┌──────▼──────┐  ┌───▼────┐
               │ PostgreSQL  │            │   Redis     │  │ MinIO  │
               │   Primary   │            │   Cluster   │  │Cluster │
               │             │            │             │  │        │
               │  + Replica  │            │ 3 instances │  │4 nodes │
               └─────────────┘            └─────────────┘  └────────┘
```

### Namespaces Kubernetes

```yaml
production:  # Environnement de production
staging:     # Environnement de pré-production
monitoring:  # Prometheus, Grafana
logging:     # ELK Stack
```

---

## Configuration

### 1. Créer les Secrets

**secrets.yaml**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: eplatform-secrets
  namespace: production
type: Opaque
stringData:
  # Database
  DB_HOST: "postgres.company.com"
  DB_PORT: "5432"
  DB_NAME: "eplatform_prod"
  DB_USERNAME: "eplatform_user"
  DB_PASSWORD: "CHANGEME_DB_PASSWORD"

  # Redis
  REDIS_HOST: "redis.company.com"
  REDIS_PORT: "6379"
  REDIS_PASSWORD: "CHANGEME_REDIS_PASSWORD"

  # MinIO / S3
  S3_ENDPOINT: "https://s3.company.com"
  S3_ACCESS_KEY: "CHANGEME_S3_ACCESS_KEY"
  S3_SECRET_KEY: "CHANGEME_S3_SECRET_KEY"
  S3_BUCKET: "eplatform-prod-files"

  # JWT
  JWT_SECRET: "CHANGEME_JWT_SECRET_256_BITS"
  JWT_EXPIRATION: "3600000"
  JWT_REFRESH_EXPIRATION: "604800000"

  # SMTP
  SMTP_HOST: "smtp.company.com"
  SMTP_PORT: "587"
  SMTP_USERNAME: "noreply@company.com"
  SMTP_PASSWORD: "CHANGEME_SMTP_PASSWORD"
  SMTP_FROM: "noreply@company.com"
```

**Créer les secrets :**
```bash
kubectl create namespace production
kubectl apply -f secrets.yaml
```

### 2. ConfigMap pour Configuration

**configmap.yaml**
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: eplatform-config
  namespace: production
data:
  APPLICATION_NAME: "E-Learning Platform"
  ENVIRONMENT: "production"
  LOG_LEVEL: "INFO"
  CORS_ALLOWED_ORIGINS: "https://app.company.com"
  TIMEZONE: "Europe/Paris"
```

```bash
kubectl apply -f configmap.yaml
```

---

## Déploiement Initial

### 1. Créer le Namespace

```bash
kubectl create namespace production
kubectl label namespace production env=production
```

### 2. Déployer PostgreSQL (si self-hosted)

**postgres-deployment.yaml**
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres
  namespace: production
spec:
  serviceName: postgres
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:16
        ports:
        - containerPort: 5432
        env:
        - name: POSTGRES_DB
          valueFrom:
            secretKeyRef:
              name: eplatform-secrets
              key: DB_NAME
        - name: POSTGRES_USER
          valueFrom:
            secretKeyRef:
              name: eplatform-secrets
              key: DB_USERNAME
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: eplatform-secrets
              key: DB_PASSWORD
        volumeMounts:
        - name: postgres-storage
          mountPath: /var/lib/postgresql/data
  volumeClaimTemplates:
  - metadata:
      name: postgres-storage
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 100Gi
---
apiVersion: v1
kind: Service
metadata:
  name: postgres
  namespace: production
spec:
  ports:
  - port: 5432
  selector:
    app: postgres
  type: ClusterIP
```

```bash
kubectl apply -f postgres-deployment.yaml
```

### 3. Déployer Redis

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: redis
  namespace: production
spec:
  replicas: 3
  selector:
    matchLabels:
      app: redis
  template:
    metadata:
      labels:
        app: redis
    spec:
      containers:
      - name: redis
        image: redis:7-alpine
        ports:
        - containerPort: 6379
        command:
        - redis-server
        - --requirepass
        - $(REDIS_PASSWORD)
        env:
        - name: REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: eplatform-secrets
              key: REDIS_PASSWORD
---
apiVersion: v1
kind: Service
metadata:
  name: redis
  namespace: production
spec:
  ports:
  - port: 6379
  selector:
    app: redis
```

### 4. Déployer le Backend

**backend-deployment.yaml**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: eplatform-backend
  namespace: production
spec:
  replicas: 3
  selector:
    matchLabels:
      app: eplatform-backend
  template:
    metadata:
      labels:
        app: eplatform-backend
    spec:
      containers:
      - name: backend
        image: registry.company.com/eplatform-backend:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:postgresql://$(DB_HOST):$(DB_PORT)/$(DB_NAME)"
        - name: SPRING_DATASOURCE_USERNAME
          valueFrom:
            secretKeyRef:
              name: eplatform-secrets
              key: DB_USERNAME
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: eplatform-secrets
              key: DB_PASSWORD
        - name: SPRING_REDIS_HOST
          valueFrom:
            secretKeyRef:
              name: eplatform-secrets
              key: REDIS_HOST
        - name: SPRING_REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: eplatform-secrets
              key: REDIS_PASSWORD
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: eplatform-secrets
              key: JWT_SECRET
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: eplatform-backend
  namespace: production
spec:
  selector:
    app: eplatform-backend
  ports:
  - protocol: TCP
    port: 8080
    targetPort: 8080
  type: ClusterIP
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: eplatform-backend-hpa
  namespace: production
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: eplatform-backend
  minReplicas: 3
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

```bash
kubectl apply -f backend-deployment.yaml
```

### 5. Déployer le Frontend

**frontend-deployment.yaml**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: eplatform-frontend
  namespace: production
spec:
  replicas: 3
  selector:
    matchLabels:
      app: eplatform-frontend
  template:
    metadata:
      labels:
        app: eplatform-frontend
    spec:
      containers:
      - name: frontend
        image: registry.company.com/eplatform-frontend:1.0.0
        ports:
        - containerPort: 80
        resources:
          requests:
            memory: "256Mi"
            cpu: "100m"
          limits:
            memory: "512Mi"
            cpu: "200m"
---
apiVersion: v1
kind: Service
metadata:
  name: eplatform-frontend
  namespace: production
spec:
  selector:
    app: eplatform-frontend
  ports:
  - protocol: TCP
    port: 80
    targetPort: 80
  type: ClusterIP
```

```bash
kubectl apply -f frontend-deployment.yaml
```

### 6. Configurer l'Ingress (Nginx Ingress Controller)

**ingress.yaml**
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: eplatform-ingress
  namespace: production
  annotations:
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/rate-limit: "100"
spec:
  ingressClassName: nginx
  tls:
  - hosts:
    - app.company.com
    - api.company.com
    secretName: eplatform-tls
  rules:
  - host: app.company.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: eplatform-frontend
            port:
              number: 80
  - host: api.company.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: eplatform-backend
            port:
              number: 8080
```

```bash
kubectl apply -f ingress.yaml
```

---

## Mise à Jour (Rolling Update)

### Méthode 1 : Kubectl Set Image

```bash
# Backend
kubectl set image deployment/eplatform-backend \
  backend=registry.company.com/eplatform-backend:1.1.0 \
  -n production

# Frontend
kubectl set image deployment/eplatform-frontend \
  frontend=registry.company.com/eplatform-frontend:1.1.0 \
  -n production

# Surveiller le rollout
kubectl rollout status deployment/eplatform-backend -n production
```

### Méthode 2 : Modifier le YAML

```bash
# Modifier le fichier deployment.yaml avec la nouvelle version
nano backend-deployment.yaml

# Appliquer
kubectl apply -f backend-deployment.yaml

# Surveiller
kubectl rollout status deployment/eplatform-backend -n production
```

### Vérifications Post-Déploiement

```bash
# Vérifier les pods
kubectl get pods -n production

# Vérifier les logs
kubectl logs -f deployment/eplatform-backend -n production

# Tester l'API
curl -k https://api.company.com/actuator/health

# Tester le frontend
curl -k https://app.company.com
```

---

## Rollback

### Rollback Automatique (en cas d'erreur)

```bash
# Voir l'historique des révisions
kubectl rollout history deployment/eplatform-backend -n production

# Rollback à la révision précédente
kubectl rollout undo deployment/eplatform-backend -n production

# Rollback à une révision spécifique
kubectl rollout undo deployment/eplatform-backend --to-revision=3 -n production
```

### Rollback Base de Données (Liquibase)

```bash
# Se connecter au pod backend
kubectl exec -it deployment/eplatform-backend -n production -- /bin/sh

# Rollback de 1 changeset
./mvnw liquibase:rollback -Dliquibase.rollbackCount=1

# Rollback vers une date
./mvnw liquibase:rollback -Dliquibase.rollbackDate="2025-01-15"
```

---

## Monitoring

### Prometheus + Grafana

**prometheus-values.yaml**
```yaml
server:
  persistentVolume:
    enabled: true
    size: 50Gi

alertmanager:
  enabled: true

nodeExporter:
  enabled: true

kubeStateMetrics:
  enabled: true

serviceMonitors:
  - name: eplatform-backend
    selector:
      matchLabels:
        app: eplatform-backend
    endpoints:
    - port: metrics
      path: /actuator/prometheus
```

```bash
# Installer Prometheus via Helm
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm install prometheus prometheus-community/kube-prometheus-stack \
  -f prometheus-values.yaml \
  -n monitoring \
  --create-namespace

# Installer Grafana (inclus dans kube-prometheus-stack)
# Accéder à Grafana
kubectl port-forward -n monitoring svc/prometheus-grafana 3000:80
```

### Dashboards Grafana Recommandés

- **Spring Boot Statistics** : Dashboard ID 6756
- **Kubernetes Cluster Monitoring** : Dashboard ID 7249
- **PostgreSQL Database** : Dashboard ID 9628
- **Redis** : Dashboard ID 11835

---

## Backup et Restore

### Backup PostgreSQL

```bash
# Backup manuel
kubectl exec -n production postgres-0 -- \
  pg_dump -U eplatform_user eplatform_prod > backup-$(date +%Y%m%d).sql

# Upload vers S3
aws s3 cp backup-$(date +%Y%m%d).sql s3://eplatform-backups/
```

**CronJob pour Backup Automatique**
```yaml
apiVersion: batch/v1
kind: CronJob
metadata:
  name: postgres-backup
  namespace: production
spec:
  schedule: "0 2 * * *"  # Tous les jours à 2h du matin
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: backup
            image: postgres:16
            command:
            - /bin/sh
            - -c
            - |
              pg_dump -h postgres -U $DB_USERNAME $DB_NAME | \
              gzip > /backups/backup-$(date +%Y%m%d).sql.gz
            env:
            - name: DB_USERNAME
              valueFrom:
                secretKeyRef:
                  name: eplatform-secrets
                  key: DB_USERNAME
            - name: DB_NAME
              valueFrom:
                secretKeyRef:
                  name: eplatform-secrets
                  key: DB_NAME
            - name: PGPASSWORD
              valueFrom:
                secretKeyRef:
                  name: eplatform-secrets
                  key: DB_PASSWORD
            volumeMounts:
            - name: backup-storage
              mountPath: /backups
          volumes:
          - name: backup-storage
            persistentVolumeClaim:
              claimName: backup-pvc
          restartPolicy: OnFailure
```

### Restore PostgreSQL

```bash
# Télécharger le backup
aws s3 cp s3://eplatform-backups/backup-20250115.sql .

# Restore
kubectl exec -i -n production postgres-0 -- \
  psql -U eplatform_user eplatform_prod < backup-20250115.sql
```

---

## Troubleshooting

### Pods ne démarrent pas

```bash
# Voir les événements
kubectl get events -n production --sort-by='.lastTimestamp'

# Décrire le pod
kubectl describe pod <pod-name> -n production

# Voir les logs
kubectl logs <pod-name> -n production --previous
```

### Erreurs de Connexion Base de Données

```bash
# Tester la connexion depuis un pod
kubectl run -it --rm debug --image=postgres:16 -n production -- bash
psql -h postgres -U eplatform_user -d eplatform_prod
```

### Performance Dégradée

```bash
# Vérifier les ressources des pods
kubectl top pods -n production

# Vérifier les métriques du cluster
kubectl top nodes

# Voir les HPA
kubectl get hpa -n production
```

### Certificat SSL Expiré

```bash
# Vérifier les certificats
kubectl get certificate -n production

# Renouveler manuellement (cert-manager)
kubectl delete certificate eplatform-tls -n production
kubectl apply -f ingress.yaml
```

---

## Checklist de Déploiement

### Avant le Déploiement

- [ ] Tests E2E passés sur staging
- [ ] Backup de la base de données effectué
- [ ] Revue de code terminée et approuvée
- [ ] Documentation mise à jour (CHANGELOG.md)
- [ ] Secrets de production vérifiés
- [ ] Équipe d'astreinte notifiée
- [ ] Fenêtre de maintenance communiquée

### Pendant le Déploiement

- [ ] Déploiement backend lancé
- [ ] Health checks OK (5 minutes)
- [ ] Déploiement frontend lancé
- [ ] Tests de fumée réussis
- [ ] Monitoring actif (Grafana, logs)

### Après le Déploiement

- [ ] Vérification des endpoints principaux
- [ ] Vérification des logs (pas d'erreurs)
- [ ] Tests utilisateur sur fonctionnalités critiques
- [ ] Performance baseline vérifiée
- [ ] Backup post-déploiement
- [ ] Documentation de déploiement complétée
- [ ] Communication déploiement réussi

---

**Version** : 1.0.0
**Dernière mise à jour** : 2025-01-15
**Maintenu par** : Équipe DevOps
