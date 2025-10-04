#!/bin/bash

# Database restore script for Grocery POS System
# Usage: ./restore.sh <backup_file.sql.gz>

if [ $# -eq 0 ]; then
    echo "Usage: $0 <backup_file.sql.gz>"
    echo "Available backups:"
    ls -la /backups/pos_backup_*.sql.gz 2>/dev/null || echo "No backups found"
    exit 1
fi

BACKUP_FILE=$1
DB_HOST=${DB_HOST:-db}
DB_NAME=${MYSQL_DATABASE:-grocery_pos}
DB_USER=${MYSQL_USER:-posuser}
DB_PASSWORD=${MYSQL_PASSWORD:-pospass123}

# Check if backup file exists
if [ ! -f "$BACKUP_FILE" ]; then
    echo "Backup file not found: $BACKUP_FILE"
    exit 1
fi

# Confirm restore operation
echo "WARNING: This will restore the database from: $BACKUP_FILE"
echo "This will REPLACE all current data in the database: $DB_NAME"
read -p "Are you sure you want to continue? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "Restore cancelled."
    exit 0
fi

# Decompress if needed
if [[ $BACKUP_FILE == *.gz ]]; then
    echo "Decompressing backup file..."
    gunzip -c $BACKUP_FILE > /tmp/restore.sql
    RESTORE_FILE="/tmp/restore.sql"
else
    RESTORE_FILE=$BACKUP_FILE
fi

# Restore database
echo "Restoring database from: $RESTORE_FILE"
mysql -h $DB_HOST -u $DB_USER -p$DB_PASSWORD $DB_NAME < $RESTORE_FILE

if [ $? -eq 0 ]; then
    echo "Database restored successfully!"
else
    echo "Database restore failed!"
    exit 1
fi

# Clean up temporary file
if [ "$RESTORE_FILE" = "/tmp/restore.sql" ]; then
    rm -f /tmp/restore.sql
fi
