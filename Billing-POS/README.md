# Grocery POS Billing System

A comprehensive Point of Sale (POS) system designed for grocery stores with modern UI, inventory management, and reporting capabilities.

## Features

### 🛒 Billing System
- Barcode scanning support
- Quick item search and add to cart
- GST calculation
- Multiple payment methods (Cash, Card, UPI, Online)
- Receipt generation and printing
- Bill management and history

### 📦 Inventory Management
- Add, edit, and delete products
- Stock tracking with real-time updates
- Low stock alerts
- Category management
- Barcode generation and management
- Stock adjustment and movement tracking

### 👥 User Management
- Role-based access control (Admin, Manager, Cashier)
- Secure authentication with password hashing
- User activity tracking
- Session management

### 📊 Reports & Analytics
- Daily, weekly, and monthly sales reports
- Top-selling items analysis
- GST reports
- Stock reports
- Export to CSV/PDF
- Low stock alerts

### 🎨 Modern UI
- FlatLaf modern look and feel
- Responsive design
- Keyboard shortcuts
- Dark/Light theme support
- Intuitive navigation

## Technology Stack

- **Backend**: Java 11, MySQL 8.0
- **UI Framework**: Java Swing with FlatLaf
- **Database**: MySQL with JDBC
- **Build Tool**: Maven
- **Additional Libraries**: 
  - ZXing for barcode scanning
  - iText for PDF generation
  - Apache Commons for utilities

## Prerequisites

- Java 11 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Installation & Setup

### 1. Database Setup

1. Install MySQL 8.0 on your system
2. Create a database user with appropriate privileges:
   ```sql
   CREATE USER 'grocerypos'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON grocery_pos.* TO 'grocerypos'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. Update database configuration in `src/main/resources/database.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/grocery_pos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.username=grocerypos
   db.password=your_password
   ```

### 2. Build and Run

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd grocery-pos
   ```

2. Build the project:
   ```bash
   mvn clean compile
   ```

3. Run the application:
   ```bash
   mvn exec:java -Dexec.mainClass="com.grocerypos.Main"
   ```

   Or create a JAR file:
   ```bash
   mvn clean package
   java -jar target/grocery-pos-1.0.0.jar
   ```

### 3. First Time Setup

1. The application will automatically create the database schema on first run
2. Default admin credentials:
   - Username: `admin`
   - Password: `admin123`

**⚠️ Important**: Change the default admin password after first login!

## Usage

### Login
- Use the default admin credentials or create new users
- Different roles have different access levels:
  - **Admin**: Full access to all features
  - **Manager**: Access to billing, inventory, and reports
  - **Cashier**: Access to billing only

### Creating a New Bill
1. Click "New Bill" from the main menu or toolbar
2. Scan barcode or search for items
3. Add items to cart with quantities
4. Apply discounts if needed
5. Select payment method
6. Generate receipt

### Managing Inventory
1. Go to Inventory Management
2. Add new products with details:
   - Product name and description
   - Barcode (auto-generated or manual)
   - Price and cost price
   - GST percentage
   - Stock quantity
   - Category
3. Edit existing products
4. Track stock movements
5. Set low stock alerts

### Generating Reports
1. Go to Reports section
2. Select report type:
   - Sales reports (daily, weekly, monthly)
   - Stock reports
   - GST reports
   - Top-selling items
3. Set date range and filters
4. Export to CSV or PDF

## Configuration

### Database Configuration
Edit `src/main/resources/database.properties` to configure:
- Database connection settings
- Connection pool settings
- Application settings

### Receipt Configuration
Configure receipt settings in the database `settings` table:
- Shop name and address
- Contact information
- GST number
- Receipt footer message

## Keyboard Shortcuts

- `F1`: Focus barcode scanner
- `F5`: Process payment
- `F9`: New bill
- `Ctrl+N`: New bill
- `Ctrl+I`: Inventory management
- `Ctrl+R`: Reports
- `Ctrl+L`: Logout
- `Alt+F4`: Exit application

## Troubleshooting

### Database Connection Issues
1. Ensure MySQL is running
2. Check database credentials in `database.properties`
3. Verify database user has proper privileges
4. Check firewall settings

### Application Won't Start
1. Verify Java 11+ is installed
2. Check MySQL connection
3. Ensure all dependencies are resolved
4. Check application logs

### Performance Issues
1. Optimize database queries
2. Increase connection pool size
3. Check system resources
4. Consider database indexing

## Development

### Project Structure
```
src/
├── main/
│   ├── java/com/grocerypos/
│   │   ├── Main.java                 # Application entry point
│   │   ├── model/                    # Data models
│   │   ├── dao/                      # Data Access Objects
│   │   ├── ui/                       # User interface components
│   │   ├── util/                     # Utility classes
│   │   └── database/                # Database utilities
│   └── resources/
│       ├── schema.sql                # Database schema
│       ├── database.properties       # Database configuration
│       └── icons/                    # Application icons
└── test/                            # Unit tests
```

### Adding New Features
1. Create model classes in `model/` package
2. Create DAO classes in `dao/` package
3. Create UI components in `ui/` package
4. Add database migrations if needed
5. Update documentation

### Testing
```bash
mvn test
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Create an issue in the repository
- Check the documentation
- Review the troubleshooting section

## Roadmap

### Phase 1 - Core Features ✅
- [x] Basic POS functionality
- [x] Inventory management
- [x] User authentication
- [x] Database setup

### Phase 2 - Advanced Features 🚧
- [ ] Barcode scanning integration
- [ ] Receipt printing
- [ ] Advanced reporting
- [ ] Data export/import

### Phase 3 - Enhancements 📋
- [ ] Multi-store support
- [ ] Cloud synchronization
- [ ] Mobile app integration
- [ ] Advanced analytics

---

**Built with ❤️ for grocery store owners**
