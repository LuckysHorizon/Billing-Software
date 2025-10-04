# 🚀 Deployment Guide

This guide covers different deployment scenarios for the Grocery POS System.

## 🐳 Docker Deployment (Recommended)

### Prerequisites
- Docker and Docker Compose installed
- At least 2GB RAM available
- Ports 8080 and 3306 available

### Quick Start
```bash
# Clone the repository
git clone https://github.com/LuckysHorizon/pos-open-source.git
cd pos-open-source

# Copy environment file
cp env.example .env

# Start the application
docker-compose up --build -d

# Check status
docker-compose ps
```

### Access Points
- **POS Application**: http://localhost:8080
- **Database Admin**: http://localhost:8081
- **Default Login**: admin / admin123

## 🏭 Production Deployment

### Using Production Configuration
```bash
# Use production docker-compose file
docker-compose -f docker-compose.prod.yml up -d

# With custom environment
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d
```

### Production Environment Variables
```bash
# .env.prod
MYSQL_ROOT_PASSWORD=your-secure-root-password
MYSQL_DATABASE=grocery_pos
MYSQL_USER=posuser
MYSQL_PASSWORD=your-secure-password
APP_PORT=8080
```

### Security Considerations
1. **Change default passwords**
2. **Use strong database passwords**
3. **Enable SSL/TLS in production**
4. **Configure firewall rules**
5. **Regular security updates**

## ☁️ Cloud Deployment

### AWS Deployment
```bash
# Using AWS ECS
aws ecs create-cluster --cluster-name pos-cluster
aws ecs register-task-definition --cli-input-json file://task-definition.json

# Using EC2 with Docker
# Launch EC2 instance with Docker installed
# Follow Docker deployment steps
```

### Google Cloud Deployment
```bash
# Using Cloud Run
gcloud run deploy pos-app --source . --platform managed --region us-central1

# Using GKE
gcloud container clusters create pos-cluster --num-nodes=3
kubectl apply -f k8s/
```

### Azure Deployment
```bash
# Using Container Instances
az container create --resource-group myResourceGroup --name pos-app --image your-registry/pos-app

# Using AKS
az aks create --resource-group myResourceGroup --name pos-cluster --node-count 3
```

## 🔧 Configuration

### Database Configuration
```properties
# src/main/resources/database.properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:grocery_pos}
db.username=${DB_USER:root}
db.password=${DB_PASSWORD:}
```

### Application Configuration
```bash
# Environment variables
export DB_HOST=your-db-host
export DB_PORT=3306
export DB_NAME=grocery_pos
export DB_USER=posuser
export DB_PASSWORD=your-password
```

## 📊 Monitoring

### Health Checks
```bash
# Application health
curl http://localhost:8080/health

# Database health
docker-compose exec db mysqladmin ping -h localhost
```

### Logs
```bash
# Application logs
docker-compose logs app

# Database logs
docker-compose logs db

# All logs
docker-compose logs
```

### Performance Monitoring
```bash
# Container resource usage
docker stats

# Database performance
docker-compose exec db mysql -e "SHOW PROCESSLIST;"
```

## 🔄 Backup and Restore

### Automated Backup
```bash
# Backup script runs daily
./scripts/backup.sh

# Manual backup
docker-compose exec db mysqldump -u posuser -p grocery_pos > backup.sql
```

### Restore from Backup
```bash
# Restore database
./scripts/restore.sh backup.sql

# Or manually
docker-compose exec -T db mysql -u posuser -p grocery_pos < backup.sql
```

## 🔧 Troubleshooting

### Common Issues

#### Database Connection Issues
```bash
# Check database status
docker-compose exec db mysqladmin ping -h localhost

# Check connection logs
docker-compose logs db
```

#### Application Won't Start
```bash
# Check application logs
docker-compose logs app

# Check resource usage
docker stats
```

#### Port Conflicts
```bash
# Check port usage
netstat -tulpn | grep :8080
netstat -tulpn | grep :3306

# Change ports in docker-compose.yml
```

### Performance Issues

#### Memory Issues
```bash
# Increase Java heap size
export JAVA_OPTS="-Xmx2g -Xms1g"
docker-compose up -d
```

#### Database Performance
```bash
# Check slow queries
docker-compose exec db mysql -e "SHOW PROCESSLIST;"

# Optimize database
docker-compose exec db mysql -e "OPTIMIZE TABLE items, bills, bill_items;"
```

## 📈 Scaling

### Horizontal Scaling
```yaml
# docker-compose.scale.yml
version: "3.8"
services:
  app:
    deploy:
      replicas: 3
    ports:
      - "8080-8082:8080"
```

### Load Balancing
```nginx
# nginx/nginx.conf
upstream pos_app {
    server app1:8080;
    server app2:8080;
    server app3:8080;
}
```

## 🔒 Security

### SSL/TLS Configuration
```nginx
# Enable HTTPS
server {
    listen 443 ssl;
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
}
```

### Firewall Configuration
```bash
# Allow only necessary ports
ufw allow 80
ufw allow 443
ufw allow 22
ufw deny 3306
```

### Database Security
```sql
-- Create restricted user
CREATE USER 'posuser'@'%' IDENTIFIED BY 'secure-password';
GRANT SELECT, INSERT, UPDATE, DELETE ON grocery_pos.* TO 'posuser'@'%';
FLUSH PRIVILEGES;
```

## 📋 Maintenance

### Regular Tasks
1. **Database backups** (daily)
2. **Log rotation** (weekly)
3. **Security updates** (monthly)
4. **Performance monitoring** (continuous)

### Update Process
```bash
# Pull latest changes
git pull origin main

# Rebuild containers
docker-compose down
docker-compose up --build -d

# Verify deployment
docker-compose ps
```

## 🆘 Support

### Getting Help
- **Documentation**: Check README.md and Wiki
- **Issues**: GitHub Issues for bug reports
- **Discussions**: GitHub Discussions for questions
- **Email**: support@luckyshorizon.com

### Emergency Procedures
1. **Service Down**: Check logs and restart containers
2. **Data Loss**: Restore from latest backup
3. **Security Breach**: Change all passwords and review logs
4. **Performance Issues**: Check resource usage and optimize

---

For more detailed information, see the main README.md file.
