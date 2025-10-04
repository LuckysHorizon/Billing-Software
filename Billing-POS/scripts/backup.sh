#!/bin/bash

# Database backup script for Grocery POS System
# This script creates daily backups of the MySQL database

# Configuration
DB_HOST=${DB_HOST:-db}
DB_NAME=${MYSQL_DATABASE:-grocery_pos}
DB_USER=${MYSQL_USER:-posuser}
DB_PASSWORD=${MYSQL_PASSWORD:-pospass123}
BACKUP_DIR="/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/pos_backup_${DATE}.sql"
RETENTION_DAYS=${BACKUP_RETENTION_DAYS:-30}

# Create backup directory if it doesn't exist
mkdir -p $BACKUP_DIR

# Create database backup
echo "Creating database backup: $BACKUP_FILE"
mysqldump -h $DB_HOST -u $DB_USER -p$DB_PASSWORD \
    --single-transaction \
    --routines \
    --triggers \
    --events \
    $DB_NAME > $BACKUP_FILE

# Check if backup was successful
if [ $? -eq 0 ]; then
    echo "Backup completed successfully: $BACKUP_FILE"
    
    # Compress the backup
    gzip $BACKUP_FILE
    echo "Backup compressed: ${BACKUP_FILE}.gz"
    
    # Remove old backups (older than retention period)
    find $BACKUP_DIR -name "pos_backup_*.sql.gz" -mtime +$RETENTION_DAYS -delete
    echo "Old backups removed (older than $RETENTION_DAYS days)"
    
    # List current backups
    echo "Current backups:"
    ls -la $BACKUP_DIR/pos_backup_*.sql.gz 2>/dev/null || echo "No backups found"
else
    echo "Backup failed!"
    exit 1
fi
