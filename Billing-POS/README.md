# 🧾 Grocery POS System (Open Source)

A modern, production-ready Point of Sale system built with **Java + MySQL + Docker**. This system provides a complete solution for grocery stores and retail businesses with inventory management, barcode scanning, and comprehensive reporting.

![Java](https://img.shields.io/badge/Java-17-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED)
![License](https://img.shields.io/badge/License-MIT-green)

## 🚀 Features

### 💼 Core POS Features
- **Modern Billing Interface** - Clean, intuitive billing with real-time calculations
- **Barcode Scanning** - Support for USB barcode scanners and webcam scanning
- **Multi-Payment Support** - Cash, Card, UPI, and Online payments
- **Receipt Printing** - Professional receipt generation with GST calculations
- **Sound Feedback** - Audio confirmation for successful scans and transactions

### 📊 Inventory Management
- **Stock Tracking** - Real-time inventory levels with low-stock alerts
- **Product Management** - Add, edit, and categorize products with barcodes
- **Inventory Movements** - Track purchases, sales, adjustments, and returns
- **Category Management** - Organize products by categories

### 👥 User Management
- **Role-Based Access** - Admin, Manager, and Cashier roles
- **Secure Authentication** - Password-based login with session management
- **User Activity Tracking** - Monitor user actions and transactions

### 📈 Reporting & Analytics
- **Sales Reports** - Daily, weekly, monthly sales summaries
- **Inventory Reports** - Stock levels, movement history, and valuation
- **Export Functionality** - CSV export for external analysis
- **Dashboard Analytics** - Visual charts and key performance indicators

### 🎨 Modern UI/UX
- **FlatLaf Themes** - Multiple modern themes including IntelliJ themes
- **Responsive Design** - Clean, professional interface
- **Keyboard Shortcuts** - Quick access to common functions
- **Toast Notifications** - User-friendly feedback system

## 🐳 Docker Setup (Recommended)

### Prerequisites
- Docker and Docker Compose installed
- Git (for cloning the repository)

### Quick Start

1. **Clone the repository:**
   ```bash
   git clone https://github.com/LuckysHorizon/pos-open-source.git
   cd pos-open-source
   ```

2. **Create environment file:**
   ```bash
   cp env.example .env
   ```

3. **Start the application:**
   ```bash
   docker-compose up --build
   ```

4. **Access the application:**
   - **POS Application:** http://localhost:8080
   - **Database Admin (phpMyAdmin):** http://localhost:8081

### Default Login Credentials
- **Username:** `admin`
- **Password:** `admin123`

## 🛠️ Manual Setup (Development)

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

### Installation Steps

1. **Clone and build:**
   ```bash
   git clone https://github.com/LuckysHorizon/pos-open-source.git
   cd pos-open-source
   mvn clean package
   ```

2. **Setup MySQL database:**
   ```sql
   CREATE DATABASE grocery_pos;
   ```

3. **Configure database connection:**
   - Update `src/main/resources/database.properties`
   - Set your MySQL credentials

4. **Run the application:**
   ```bash
   mvn exec:java -Dexec.mainClass="com.grocerypos.Main"
   ```

## 📁 Project Structure

```
pos-open-source/
├── src/main/java/com/grocerypos/
│   ├── Application.java              # Main application class
│   ├── dao/                         # Data Access Objects
│   ├── database/                    # Database utilities
│   ├── model/                       # Entity models
│   ├── ui/                          # User interface components
│   └── util/                        # Utility classes
├── src/main/resources/
│   ├── database.properties          # Database configuration
│   └── schema.sql                   # Database schema
├── database/
│   ├── schema.sql                   # Docker database schema
│   └── seed_data.sql               # Sample data
├── Dockerfile                       # Docker configuration
├── docker-compose.yml              # Docker services
├── env.example                      # Environment variables template
└── README.md                       # This file
```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `MYSQL_ROOT_PASSWORD` | MySQL root password | `rootpassword123` |
| `MYSQL_DATABASE` | Database name | `grocery_pos` |
| `MYSQL_USER` | Database user | `posuser` |
| `MYSQL_PASSWORD` | Database password | `pospass123` |
| `APP_PORT` | Application port | `8080` |

### Database Configuration

The application uses environment variables for database configuration:
- `DB_HOST` - Database host (default: localhost)
- `DB_PORT` - Database port (default: 3306)
- `DB_NAME` - Database name (default: grocery_pos)
- `DB_USER` - Database username
- `DB_PASSWORD` - Database password

## 🚀 Deployment

### Production Deployment

1. **Update environment variables:**
   ```bash
   # Copy and edit environment file
   cp env.example .env
   nano .env
   ```

2. **Deploy with Docker Compose:**
   ```bash
   docker-compose up -d
   ```

3. **Monitor logs:**
   ```bash
   docker-compose logs -f app
   ```

### Cloud Deployment

The application is ready for deployment on:
- **AWS** - Use ECS or EC2 with RDS
- **Google Cloud** - Use Cloud Run with Cloud SQL
- **Azure** - Use Container Instances with Azure Database
- **DigitalOcean** - Use App Platform with Managed Database

## 🧪 Testing

### Run Tests
```bash
# Using Maven
mvn test

# Using Docker
docker-compose exec app mvn test
```

### Test Database Connection
```bash
# Check database connectivity
docker-compose exec db mysql -u posuser -p grocery_pos
```

## 📊 Monitoring & Maintenance

### Health Checks
- Application health: `http://localhost:8080/health`
- Database health: Built-in MySQL health checks

### Backup
```bash
# Create database backup
docker-compose exec db mysqldump -u posuser -p grocery_pos > backup.sql

# Restore from backup
docker-compose exec -T db mysql -u posuser -p grocery_pos < backup.sql
```

### Logs
```bash
# View application logs
docker-compose logs app

# View database logs
docker-compose logs db
```

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch:**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit your changes:**
   ```bash
   git commit -m 'Add amazing feature'
   ```
4. **Push to the branch:**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### Development Guidelines
- Follow Java coding standards
- Add unit tests for new features
- Update documentation for API changes
- Ensure Docker compatibility

## 🐛 Bug Reports & Feature Requests

- **Bug Reports:** Use GitHub Issues with the "bug" label
- **Feature Requests:** Use GitHub Issues with the "enhancement" label
- **Security Issues:** Email security@luckyshorizon.com

## 📝 Changelog

### Version 1.0.0
- Initial release
- Complete POS functionality
- Docker containerization
- Modern UI with FlatLaf themes
- Comprehensive reporting
- Multi-user support

## 🏆 Roadmap

- [ ] **Web Interface** - React-based web UI
- [ ] **Mobile App** - Android/iOS companion app
- [ ] **API Integration** - RESTful API for third-party integrations
- [ ] **Advanced Analytics** - Machine learning insights
- [ ] **Multi-store Support** - Chain store management
- [ ] **Cloud Sync** - Real-time data synchronization

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Team

- **Lead Developer:** [LuckysHorizon](https://github.com/LuckysHorizon)
- **Contributors:** [View all contributors](https://github.com/LuckysHorizon/pos-open-source/graphs/contributors)

## 🙏 Acknowledgments

- **FlatLaf** - Modern Java Swing look and feel
- **ZXing** - Barcode scanning library
- **JFreeChart** - Charting and reporting
- **MySQL** - Database management
- **Docker** - Containerization platform

## 📞 Support

- **Documentation:** [Wiki](https://github.com/LuckysHorizon/pos-open-source/wiki)
- **Issues:** [GitHub Issues](https://github.com/LuckysHorizon/pos-open-source/issues)
- **Discussions:** [GitHub Discussions](https://github.com/LuckysHorizon/pos-open-source/discussions)
- **Email:** support@luckyshorizon.com

---

**⭐ Star this repository if you find it helpful!**

**🔗 Share with your network to help other developers!**

**💡 Contribute to make it even better!**