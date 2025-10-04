# Contributing to Grocery POS System

Thank you for your interest in contributing to the Grocery POS System! This document provides guidelines and information for contributors.

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- Git

### Development Setup

1. **Fork and clone the repository:**
   ```bash
   git clone https://github.com/your-username/pos-open-source.git
   cd pos-open-source
   ```

2. **Set up development environment:**
   ```bash
   # Copy environment file
   cp env.example .env
   
   # Start development services
   docker-compose up -d db
   
   # Build and run locally
   mvn clean package
   mvn exec:java -Dexec.mainClass="com.grocerypos.Main"
   ```

## 📋 Development Guidelines

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add Javadoc comments for public methods
- Keep methods focused and small
- Use consistent indentation (4 spaces)

### Commit Messages
Use clear, descriptive commit messages:
```
feat: add barcode scanning functionality
fix: resolve database connection timeout issue
docs: update README with new features
refactor: improve error handling in billing module
```

### Testing
- Write unit tests for new features
- Test database operations with test data
- Verify UI components work correctly
- Test Docker deployment locally

## 🐛 Bug Reports

When reporting bugs, please include:
- Description of the issue
- Steps to reproduce
- Expected vs actual behavior
- Screenshots if applicable
- Environment details (OS, Java version, etc.)

## ✨ Feature Requests

For new features, please:
- Describe the feature clearly
- Explain the use case
- Consider implementation complexity
- Check for existing similar requests

## 🔧 Pull Request Process

1. **Create a feature branch:**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes:**
   - Write clean, well-documented code
   - Add tests for new functionality
   - Update documentation if needed

3. **Test your changes:**
   ```bash
   mvn test
   docker-compose up --build
   ```

4. **Commit your changes:**
   ```bash
   git add .
   git commit -m "feat: add your feature description"
   ```

5. **Push and create PR:**
   ```bash
   git push origin feature/your-feature-name
   ```

## 📁 Project Structure

```
src/main/java/com/grocerypos/
├── Application.java          # Main application entry point
├── dao/                     # Data Access Objects
├── database/                # Database utilities
├── model/                   # Entity models
├── ui/                      # User interface
│   ├── components/          # Reusable UI components
│   └── panels/             # Main application panels
└── util/                   # Utility classes
```

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
# Start test database
docker-compose -f docker-compose.test.yml up -d

# Run integration tests
mvn verify

# Cleanup
docker-compose -f docker-compose.test.yml down
```

### Manual Testing
1. Test all user roles (Admin, Manager, Cashier)
2. Test barcode scanning functionality
3. Test payment processing
4. Test report generation
5. Test data export features

## 📚 Documentation

### Code Documentation
- Use Javadoc for all public methods
- Include parameter descriptions
- Document return values
- Add usage examples where helpful

### User Documentation
- Update README.md for new features
- Add screenshots for UI changes
- Document configuration options
- Include troubleshooting guides

## 🚀 Release Process

### Version Numbering
We use semantic versioning (MAJOR.MINOR.PATCH):
- MAJOR: Breaking changes
- MINOR: New features (backward compatible)
- PATCH: Bug fixes

### Release Checklist
- [ ] All tests pass
- [ ] Documentation updated
- [ ] Version numbers updated
- [ ] Changelog updated
- [ ] Docker images built and tested
- [ ] Release notes prepared

## 🤝 Community Guidelines

### Code of Conduct
- Be respectful and inclusive
- Help others learn and grow
- Provide constructive feedback
- Focus on the code, not the person

### Communication
- Use GitHub Issues for bug reports
- Use GitHub Discussions for questions
- Use Pull Requests for code changes
- Be patient with responses

## 📞 Getting Help

- **Documentation:** Check the README and Wiki
- **Issues:** Search existing GitHub Issues
- **Discussions:** Use GitHub Discussions
- **Email:** Contact maintainers for urgent issues

## 🏆 Recognition

Contributors will be recognized in:
- README.md contributors section
- Release notes
- GitHub contributors page
- Project documentation

Thank you for contributing to the Grocery POS System! 🎉
