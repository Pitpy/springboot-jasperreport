# Deployment Guide

## Overview

This guide provides comprehensive instructions for deploying the Jasper Reports Console application in different environments.

## Prerequisites

### System Requirements

- **Java Runtime**: OpenJDK 21 or Oracle JDK 21+
- **Memory**: Minimum 512MB RAM, Recommended 2GB+
- **Storage**: 100MB for application, additional space for generated reports
- **Network**: Port 8081 (configurable)

### Dependencies

- No external database required (uses mock data)
- No external services required
- Optional: Reverse proxy (Nginx, Apache)
- Optional: Process manager (systemd, supervisord)

## Deployment Methods

### 1. Standalone JAR Deployment

#### Building the Application

```bash
# Clean and package the application
./mvnw clean package -DskipTests

# Verify the JAR file
ls -la target/jasper-*.jar
```

#### Running the JAR

```bash
# Basic execution
java -jar target/jasper-0.0.1-SNAPSHOT.jar

# With custom configuration
java -jar -Dserver.port=8081 target/jasper-0.0.1-SNAPSHOT.jar

# With external configuration file
java -jar target/jasper-0.0.1-SNAPSHOT.jar --spring.config.location=file:./config/application.properties
```

#### Production Configuration

Create an external configuration file (`application-prod.properties`):

```properties
# Server Configuration
server.port=8081
server.servlet.context-path=/reports

# Logging Configuration
logging.level.root=WARN
logging.level.com.report.jasper=INFO
logging.file.name=/var/log/jasper-reports/application.log

# JVM Configuration (set via environment or command line)
# -Xms1g -Xmx2g -XX:+UseG1GC
```

Run with production profile:

```bash
java -Xms1g -Xmx2g -XX:+UseG1GC \
     -jar target/jasper-0.0.1-SNAPSHOT.jar \
     --spring.profiles.active=prod \
     --spring.config.location=classpath:/application.properties,file:./application-prod.properties
```

### 2. Docker Deployment

#### Dockerfile

```dockerfile
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy application JAR
COPY target/jasper-*.jar app.jar

# Copy report templates
COPY src/main/resources/reports/ /app/reports/

# Create non-root user
RUN addgroup --system jasper && adduser --system --group jasper
RUN chown -R jasper:jasper /app
USER jasper

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# Start application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Building Docker Image

```bash
# Build the application first
./mvnw clean package -DskipTests

# Build Docker image
docker build -t jasper-reports:latest .

# Verify image
docker images | grep jasper-reports
```

#### Running Docker Container

```bash
# Basic run
docker run -p 8081:8081 jasper-reports:latest

# With environment variables
docker run -p 8081:8081 \
    -e SERVER_PORT=8081 \
    -e SPRING_PROFILES_ACTIVE=prod \
    jasper-reports:latest

# With volume for logs
docker run -p 8081:8081 \
    -v /var/log/jasper-reports:/app/logs \
    jasper-reports:latest

# Background execution
docker run -d \
    --name jasper-reports-app \
    --restart unless-stopped \
    -p 8081:8081 \
    jasper-reports:latest
```

#### Docker Compose

Create `docker-compose.yml`:

```yaml
version: "3.8"

services:
  jasper-reports:
    build: .
    ports:
      - "8081:8081"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SERVER_PORT=8081
    volumes:
      - ./logs:/app/logs
      - ./config:/app/config
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 30s

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
    depends_on:
      - jasper-reports
    restart: unless-stopped
```

Deploy with Docker Compose:

```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f jasper-reports

# Stop services
docker-compose down
```

### 3. Kubernetes Deployment

#### Deployment YAML

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: jasper-reports
  labels:
    app: jasper-reports
spec:
  replicas: 2
  selector:
    matchLabels:
      app: jasper-reports
  template:
    metadata:
      labels:
        app: jasper-reports
    spec:
      containers:
        - name: jasper-reports
          image: jasper-reports:latest
          ports:
            - containerPort: 8081
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "prod"
            - name: SERVER_PORT
              value: "8081"
          resources:
            requests:
              memory: "512Mi"
              cpu: "250m"
            limits:
              memory: "2Gi"
              cpu: "1"
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 8081
            initialDelaySeconds: 60
            periodSeconds: 30
          readinessProbe:
            httpGet:
              path: /actuator/health
              port: 8081
            initialDelaySeconds: 30
            periodSeconds: 10
          volumeMounts:
            - name: config-volume
              mountPath: /app/config
      volumes:
        - name: config-volume
          configMap:
            name: jasper-reports-config
---
apiVersion: v1
kind: Service
metadata:
  name: jasper-reports-service
spec:
  selector:
    app: jasper-reports
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8081
  type: ClusterIP
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: jasper-reports-config
data:
  application.properties: |
    server.port=8081
    logging.level.com.report.jasper=INFO
    management.endpoints.web.exposure.include=health,info,metrics
```

Deploy to Kubernetes:

```bash
# Apply deployment
kubectl apply -f k8s-deployment.yaml

# Check status
kubectl get pods -l app=jasper-reports
kubectl get services

# View logs
kubectl logs -l app=jasper-reports -f

# Port forward for testing
kubectl port-forward service/jasper-reports-service 8081:80
```

## Environment-Specific Configurations

### Development Environment

```properties
# application-dev.properties
server.port=8081
logging.level.com.report.jasper=DEBUG
spring.devtools.restart.enabled=true
management.endpoints.web.exposure.include=*
```

### Staging Environment

```properties
# application-staging.properties
server.port=8081
logging.level.com.report.jasper=INFO
logging.file.name=/var/log/jasper-reports/staging.log
management.endpoints.web.exposure.include=health,info,metrics
```

### Production Environment

```properties
# application-prod.properties
server.port=8081
server.servlet.context-path=/reports
logging.level.root=WARN
logging.level.com.report.jasper=INFO
logging.file.name=/var/log/jasper-reports/production.log
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=never
```

## Load Balancing and Scaling

### Nginx Configuration

Create `nginx.conf`:

```nginx
events {
    worker_connections 1024;
}

http {
    upstream jasper_backend {
        server jasper-reports-1:8081;
        server jasper-reports-2:8081;
        server jasper-reports-3:8081;
    }

    server {
        listen 80;
        server_name your-domain.com;

        location /reports/ {
            proxy_pass http://jasper_backend/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;

            # Increase timeout for report generation
            proxy_read_timeout 300s;
            proxy_connect_timeout 10s;
            proxy_send_timeout 60s;
        }

        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
    }
}
```

### Horizontal Pod Autoscaler (Kubernetes)

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: jasper-reports-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: jasper-reports
  minReplicas: 2
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

## Monitoring and Observability

### Prometheus Configuration

Add to `application.properties`:

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.metrics.enabled=true
management.endpoint.prometheus.enabled=true
management.metrics.export.prometheus.enabled=true
```

### Grafana Dashboard

Create monitoring dashboard with key metrics:

- Request rate and response times
- JVM memory usage
- Report generation success/failure rates
- Active sessions

### Log Aggregation

#### ELK Stack Configuration

Logstash configuration (`logstash.conf`):

```ruby
input {
  file {
    path => "/var/log/jasper-reports/*.log"
    start_position => "beginning"
    codec => "json"
  }
}

filter {
  if [level] == "ERROR" {
    mutate {
      add_tag => [ "error" ]
    }
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "jasper-reports-%{+YYYY.MM.dd}"
  }
}
```

## Security Considerations

### HTTPS Configuration

Add SSL configuration to `application.properties`:

```properties
# HTTPS Configuration
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=changeit
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=jasper-reports

# Redirect HTTP to HTTPS
server.ssl.enabled-protocols=TLSv1.2,TLSv1.3
```

### Firewall Configuration

```bash
# Ubuntu/Debian
sudo ufw allow 8081/tcp
sudo ufw enable

# CentOS/RHEL
sudo firewall-cmd --permanent --add-port=8081/tcp
sudo firewall-cmd --reload
```

### Security Headers

Add security configuration:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                .frameOptions().deny()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubdomains(true))
            );
        return http.build();
    }
}
```

## Backup and Recovery

### Application Backup

```bash
#!/bin/bash
# backup-script.sh

BACKUP_DIR="/backup/jasper-reports"
DATE=$(date +%Y%m%d_%H%M%S)

# Create backup directory
mkdir -p $BACKUP_DIR/$DATE

# Backup application JAR
cp target/jasper-*.jar $BACKUP_DIR/$DATE/

# Backup configuration
cp -r config/ $BACKUP_DIR/$DATE/

# Backup logs (if needed)
cp -r logs/ $BACKUP_DIR/$DATE/

# Create archive
tar -czf $BACKUP_DIR/jasper-reports-$DATE.tar.gz -C $BACKUP_DIR/$DATE .

echo "Backup completed: jasper-reports-$DATE.tar.gz"
```

### Disaster Recovery

1. **Application Recovery**:

   ```bash
   # Stop current instance
   sudo systemctl stop jasper-reports

   # Restore from backup
   tar -xzf jasper-reports-backup.tar.gz

   # Start application
   sudo systemctl start jasper-reports
   ```

2. **Configuration Recovery**:
   - Restore configuration files
   - Update environment variables
   - Restart application

## Troubleshooting

### Common Deployment Issues

1. **Port Already in Use**:

   ```bash
   # Find process using port
   sudo netstat -tlnp | grep :8081

   # Kill process or change port
   sudo kill -9 <PID>
   ```

2. **Memory Issues**:

   ```bash
   # Increase JVM heap size
   java -Xms1g -Xmx4g -jar app.jar
   ```

3. **Permission Issues**:
   ```bash
   # Fix file permissions
   sudo chown -R jasper:jasper /app
   sudo chmod +x app.jar
   ```

### Health Checks

```bash
# Application health
curl http://localhost:8081/actuator/health

# Detailed health info
curl http://localhost:8081/actuator/health | jq

# Check if reports can be generated
curl -o test.pdf http://localhost:8081/api/reports/employee/pdf
```

### Performance Tuning

1. **JVM Tuning**:

   ```bash
   -Xms2g -Xmx4g
   -XX:+UseG1GC
   -XX:MaxGCPauseMillis=200
   -XX:+HeapDumpOnOutOfMemoryError
   ```

2. **Connection Pool Tuning** (if using database):
   ```properties
   spring.datasource.hikari.maximum-pool-size=20
   spring.datasource.hikari.minimum-idle=5
   spring.datasource.hikari.connection-timeout=20000
   ```

This deployment guide covers all major deployment scenarios and should help you successfully deploy the Jasper Reports Console application in any environment.
