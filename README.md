# WorkShare - Project Management System

A collaborative project management application built with **JavaServer Faces (JSF)**, **Hibernate ORM**, and **Jakarta EE**.

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Database Configuration](#database-configuration)
- [Running the Project](#running-the-project)
- [Project URLs](#project-urls)
- [Features](#features)
- [Project Structure](#project-structure)
- [API & Endpoints](#api--endpoints)
- [Configuration Files](#configuration-files)
- [Troubleshooting](#troubleshooting)
- [Class Diagram](#class-diagram)

---

## 📱 Project Overview

**WorkShare** is a web-based project management system that enables students to:
- Create collaborative projects
- Invite team members to projects
- Create and assign tasks within projects
- Track task status (TODO, IN_PROGRESS, DONE)
- Manage user authentication and sessions
- View dashboards with project and task statistics

### Key Features
- ✅ User registration and authentication
- ✅ Create projects with multiple members
- ✅ Add/remove project members by email
- ✅ Create tasks within projects
- ✅ Assign tasks to team members
- ✅ Update task status
- ✅ Dashboard with statistics
- ✅ Responsive web interface

---

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Framework** | Jakarta EE / JSF | 4.0 |
| **ORM** | Hibernate | 7.0.4 |
| **Web Framework** | PrimeFaces/Jakarta Faces | - |
| **Database** | MySQL | 8.0+ |
| **Build Tool** | Maven | 3.6+ |
| **Server** | Apache Tomcat / WildFly | 10+ |
| **Java Version** | Java | 17+ |
| **Password Hashing** | BCrypt (jBCrypt) | 0.4 |

---

## 📋 Prerequisites

Before running the project, ensure you have the following installed:

### Required
- **Java Development Kit (JDK)**: Version 17 or higher
  ```bash
  java -version  # Should show version 17 or above
  ```
  
- **MySQL Server**: Version 8.0 or higher
  ```bash
  mysql --version
  ```
  
- **Maven**: Version 3.6 or higher
  ```bash
  mvn --version
  ```
  
- **Apache Tomcat** (or similar application server)
  - Download: https://tomcat.apache.org/
  - Version: 10.0+

### Optional
- **IntelliJ IDEA** or **Eclipse IDE** (for development)
- **MySQL Workbench** (for database management)
- **Postman** (for API testing)

---

## 🚀 Installation & Setup

### Step 1: Clone/Extract the Project

```bash
# Navigate to the project directory
cd C:\Users\hp\Desktop\Projects\WorkShare
```

### Step 2: Maven Build

```bash
# Clean and build the project
mvn clean package

# Or using the included Maven wrapper
.\mvnw clean package
```

Expected output:
```
[INFO] BUILD SUCCESS
[INFO] WorkShare web application built successfully
[INFO] WAR file: target/gestionprojets-1.0-SNAPSHOT.war
```

### Step 3: Database Setup

See [Database Configuration](#database-configuration) section below.

### Step 4: Deploy to Tomcat

1. **Locate Tomcat installation**: `C:\path\to\apache-tomcat-10.x`

2. **Copy WAR file to webapps**:
   ```bash
   copy target\gestionprojets-1.0-SNAPSHOT.war "C:\path\to\apache-tomcat-10.x\webapps\"
   ```

3. **Start Tomcat**:
   - **Windows**:
     ```bash
     C:\path\to\apache-tomcat-10.x\bin\startup.bat
     ```
   - **Linux/Mac**:
     ```bash
     /path/to/apache-tomcat-10.x/bin/startup.sh
     ```

4. **Check deployment**:
   - Tomcat will auto-extract the WAR file
   - Access: http://localhost:8080/gestionprojets-1.0-SNAPSHOT

---

## 🗄️ Database Configuration

### Step 1: Create MySQL Database

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE gestionprojets;
USE gestionprojets;
```

### Step 2: Database Schema

The following tables will be automatically created by Hibernate:

```sql
-- Students Table
CREATE TABLE students (
    id_student BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Projects Table
CREATE TABLE projects (
    id_project BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    creator_id BIGINT NOT NULL,
    FOREIGN KEY (creator_id) REFERENCES students(id_student)
);

-- Tasks Table
CREATE TABLE tasks (
    id_task BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'TODO',
    project_id BIGINT NOT NULL,
    creator_id BIGINT NOT NULL,
    assigned_student_id BIGINT,
    FOREIGN KEY (project_id) REFERENCES projects(id_project),
    FOREIGN KEY (creator_id) REFERENCES students(id_student),
    FOREIGN KEY (assigned_student_id) REFERENCES students(id_student)
);

-- Project Members Junction Table (Many-to-Many)
CREATE TABLE project_members (
    project_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    PRIMARY KEY (project_id, student_id),
    FOREIGN KEY (project_id) REFERENCES projects(id_project),
    FOREIGN KEY (student_id) REFERENCES students(id_student)
);
```

### Step 3: Configure Hibernate Connection

Edit: `src/main/resources/hibernate.cfg.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">

<hibernate-configuration>
    <session-factory>
        <!-- MySQL Database Connection Properties -->
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/gestionprojets</property>
        <property name="hibernate.connection.username">root</property>
        <property name="hibernate.connection.password">YOUR_PASSWORD</property>
        
        <!-- Hibernate Dialect -->
        <property name="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</property>
        
        <!-- Connection Pool -->
        <property name="hibernate.connection.pool_size">10</property>
        
        <!-- Auto-generate DDL -->
        <property name="hibernate.hbm2ddl.auto">update</property>
        
        <!-- Show SQL for debugging -->
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>
        
        <!-- Entity Mapping -->
        <mapping class="com.example.gestionprojets.entity.Student"/>
        <mapping class="com.example.gestionprojets.entity.Project"/>
        <mapping class="com.example.gestionprojets.entity.Task"/>
    </session-factory>
</hibernate-configuration>
```

### Step 4: Update JDBC Credentials

**Important**: Replace the following in `hibernate.cfg.xml`:
- `YOUR_PASSWORD`: Your MySQL root password
- `localhost`: Your MySQL server address
- `3306`: Your MySQL port (default is 3306)

### Hibernation Auto-DDL Options

```xml
<!-- Options for hibernate.hbm2ddl.auto -->
<property name="hibernate.hbm2ddl.auto">
    <!-- Options: -->
    <!-- validate: only validates, doesn't change schema -->
    <!-- update: update schema if needed -->
    <!-- create: drops and recreates schema -->
    <!-- create-drop: create schema and drop on session close -->
    update
</property>
```

---

## 🏃 Running the Project

### Option 1: Using Maven (Development)

```bash
# From project root directory
mvn clean install

# Build and deploy WAR
mvn package

# For development with hot reload (if using IDE with live reload)
mvn tomcat:run
```

### Option 2: Using IDE (IntelliJ IDEA)

1. **Open Project**: File → Open → Select WorkShare folder
2. **Configure Tomcat**: Run → Edit Configurations
   - Click "+" → Tomcat Server → Local
   - Select Tomcat installation directory
   - Set deployment to `gestionprojets-1.0-SNAPSHOT`
3. **Run**: Run → Run 'Tomcat 10.x'

### Option 3: Using IDE (Eclipse)

1. **Import Project**: File → Import → Existing Maven Projects
2. **Configure Server**: Window → Preferences → Server Runtime Environments → Apache Tomcat v10.0
3. **Run on Server**: Right-click Project → Run As → Run on Server

### Option 4: Docker (Optional)

```dockerfile
# Dockerfile for containerized deployment
FROM tomcat:10-jdk17

COPY target/gestionprojets-1.0-SNAPSHOT.war \
     /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
```

```bash
# Build image
docker build -t workship:latest .

# Run container (with MySQL connection)
docker run -p 8080:8080 \
  -e MYSQL_HOST=host.docker.internal \
  -e MYSQL_PORT=3306 \
  workship:latest
```

---

## 🌐 Project URLs

Once the application is running, access it via:

### Main Application URLs

| URL | Page | Description |
|-----|------|-------------|
| `http://localhost:8080/gestionprojets-1.0-SNAPSHOT/` | Home | Landing page with login/register |
| `http://localhost:8080/gestionprojets-1.0-SNAPSHOT/index.xhtml` | Index | Home/login page |
| `http://localhost:8080/gestionprojets-1.0-SNAPSHOT/dashboard.xhtml` | Dashboard | User dashboard with statistics |
| `http://localhost:8080/gestionprojets-1.0-SNAPSHOT/projects.xhtml` | Projects | List and manage projects |
| `http://localhost:8080/gestionprojets-1.0-SNAPSHOT/project-details.xhtml` | Project Details | View project members and tasks |
| `http://localhost:8080/gestionprojets-1.0-SNAPSHOT/tasks.xhtml` | Tasks | View and manage tasks |

### Default Access

```
Base URL: http://localhost:8080/gestionprojets-1.0-SNAPSHOT/

Application loads at:
http://localhost:8080/gestionprojets-1.0-SNAPSHOT/index.xhtml
```

### API Endpoints (Backend Services)

These are internal services (not REST APIs):

| Bean | Scope | Methods |
|------|-------|---------|
| `@Named("authBean")` | SessionScoped | login(), register(), logout(), getCurrentUser() |
| `@Named("projectBean")` | ViewScoped | loadProjects(), createProject(), selectProject(), addMember() |
| `@Named("taskBean")` | ViewScoped | loadTasks(), createTask(), updateTask(), deleteTask() |
| `@Named("dashboardBean")` | ViewScoped | loadDashboardData() |

---

## 🎯 Features

### Authentication & Authorization
- ✅ User registration with email validation
- ✅ Secure login with BCrypt password hashing
- ✅ Session-based authentication
- ✅ Logout functionality
- ✅ Role-based access control (Creator vs Member)

### Project Management
- ✅ Create new projects
- ✅ Set project dates (start and end)
- ✅ Add project description
- ✅ Invite members by email
- ✅ View all project members
- ✅ Remove members from projects
- ✅ Delete projects (creator only)

### Task Management
- ✅ Create tasks within projects
- ✅ Assign tasks to team members
- ✅ Update task status (TODO → IN_PROGRESS → DONE)
- ✅ Set task dates and descriptions
- ✅ View all project tasks
- ✅ Delete tasks

### Dashboard
- ✅ Total projects count
- ✅ Projects created by user
- ✅ Total tasks statistics
- ✅ Recent activities

---

## 📁 Project Structure

```
WorkShare/
├── src/
│   ├── main/
│   │   ├── java/com/example/gestionprojets/
│   │   │   ├── bean/              # JSF Managed Beans
│   │   │   │   ├── AuthBean.java
│   │   │   │   ├── ProjectBean.java
│   │   │   │   ├── TaskBean.java
│   │   │   │   └── DashboardBean.java
│   │   │   ├── dao/               # Data Access Objects
│   │   │   │   ├── ProjectDao.java
│   │   │   │   ├── StudentDao.java
│   │   │   │   └── TaskDao.java
│   │   │   ├── entity/            # JPA Entities
│   │   │   │   ├── Student.java
│   │   │   │   ├── Project.java
│   │   │   │   └── Task.java
│   │   │   ├── enums/             # Enumerations
│   │   │   │   └── TaskStatus.java
│   │   │   ├── service/           # Business Logic
│   │   │   │   ├── ProjectService.java
│   │   │   │   ├── StudentService.java
│   │   │   │   └── TaskService.java
│   │   │   └── util/              # Utility Classes
│   │   │       ├── HibernateUtil.java
│   │   │       └── PasswordUtil.java
│   │   ├── resources/
│   │   │   ├── hibernate.cfg.xml  # Hibernate configuration
│   │   │   ├── persistence.xml    # JPA persistence unit
│   │   │   └── META-INF/
│   │   │       └── beans.xml      # CDI configuration
│   │   └── webapp/
│   │       ├── index.xhtml        # Login/Register page
│   │       ├── dashboard.xhtml    # Dashboard
│   │       ├── projects.xhtml     # Projects page
│   │       ├── project-details.xhtml
│   │       ├── tasks.xhtml        # Tasks page
│   │       ├── resources/
│   │       │   ├── css/style.css
│   │       │   ├── js/main.js
│   │       │   └── images/
│   │       └── WEB-INF/
│   │           └── web.xml        # Deployment descriptor
│   └── test/                       # Unit tests (optional)
├── target/                         # Build output
├── pom.xml                         # Maven configuration
├── mvnw & mvnw.cmd                # Maven wrapper
├── CLASS_DIAGRAM.puml             # UML class diagram
├── CLASS_DIAGRAM.html             # HTML diagram
├── CLASS_DIAGRAM.md               # Markdown documentation
├── CLASS_DIAGRAM.json             # JSON schema
└── README.md                       # This file
```

---

## 🔗 API & Endpoints

### Service Layer Methods

#### ProjectService
```java
// Create a new project
void createProject(Project project, Student creator, List<String> memberEmails)

// Get all projects for a student
List<Project> getProjectsOfStudent(Student student)

// Get projects created by a student
List<Project> getProjectsCreatedBy(Student student)

// Find project by ID
Project findById(Long id)

// Delete project (creator only)
boolean deleteProject(Long projectId, Student currentUser)

// Add member to project
String addMember(Long projectId, String email, Student currentUser)
// Returns: SUCCESS, PROJECT_NOT_FOUND, NOT_ALLOWED, EMAIL_NOT_FOUND, ALREADY_MEMBER
```

#### StudentService
```java
// Register new student
boolean register(Student student)

// Authenticate student
Student login(String email, String password)

// Find student by email
Student findByEmail(String email)
```

#### TaskService
```java
// Create new task
void createTask(Task task, Student creator)

// Get all tasks in a project
List<Task> getTasksByProject(Project project)

// Get tasks assigned to a student
List<Task> getTasksAssignedTo(Student student)

// Update task status
boolean updateTaskStatus(Long taskId, TaskStatus status)

// Assign task to student
String assignTask(Long taskId, String email, Student currentUser)

// Delete task
boolean deleteTask(Long taskId, Student currentUser)
```

---

## ⚙️ Configuration Files

### 1. hibernate.cfg.xml

Location: `src/main/resources/hibernate.cfg.xml`

**Configuration Options**:
```xml
<!-- Database Connection -->
hibernate.connection.driver_class = com.mysql.cj.jdbc.Driver
hibernate.connection.url = jdbc:mysql://localhost:3306/gestionprojets
hibernate.connection.username = root
hibernate.connection.password = password

<!-- Dialect (Database Type) -->
hibernate.dialect = org.hibernate.dialect.MySQL8Dialect

<!-- DDL Generation -->
hibernate.hbm2ddl.auto = update  # or validate, create, create-drop

<!-- Connection Pool -->
hibernate.connection.pool_size = 10

<!-- Logging -->
hibernate.show_sql = true/false
hibernate.format_sql = true/false
```

### 2. persistence.xml

Location: `src/main/resources/META-INF/persistence.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence
             https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">
    
    <persistence-unit name="gestionprojets" transaction-type="RESOURCE_LOCAL">
        <class>com.example.gestionprojets.entity.Student</class>
        <class>com.example.gestionprojets.entity.Project</class>
        <class>com.example.gestionprojets.entity.Task</class>
        
        <properties>
            <!-- Hibernate specific properties -->
            <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL8Dialect"/>
            <property name="hibernate.hbm2ddl.auto" value="update"/>
        </properties>
    </persistence-unit>
</persistence>
```

### 3. web.xml

Location: `src/main/webapp/WEB-INF/web.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/webprofile"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/webprofile
         https://jakarta.ee/xml/ns/webprofile/web-app_4_0.xsd"
         version="4.0">
    
    <display-name>WorkShare</display-name>
    <welcome-file-list>
        <welcome-file>index.xhtml</welcome-file>
    </welcome-file-list>
    
    <!-- JSF Servlet -->
    <servlet>
        <servlet-name>Faces Servlet</servlet-name>
        <servlet-class>jakarta.faces.webapp.FacesServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>
    
    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>*.xhtml</url-pattern>
    </servlet-mapping>
</web-app>
```

### 4. pom.xml

Key dependencies configured in Maven:
- Jakarta EE / Faces
- Hibernate 7.0.4
- MySQL Connector/J 9.3.0
- jBCrypt 0.4
- JUnit (testing)

---

## 🔧 Troubleshooting

### Issue 1: MySQL Connection Error

**Error**: `No suitable driver found for jdbc:mysql://...`

**Solution**:
```bash
# Ensure MySQL driver is in pom.xml
mvn dependency:tree | grep mysql

# Rebuild project
mvn clean package
```

### Issue 2: Hibernate Mapping Error

**Error**: `Persister for [class] not found`

**Solution**:
- Verify entity classes are annotated with `@Entity`
- Check `@Table(name="...")` annotations match database tables
- Ensure classes are listed in `persistence.xml` or `hibernate.cfg.xml`

### Issue 3: Port Already in Use

**Error**: `Address already in use: JVM_Bind`

**Solution**:
```bash
# Change Tomcat port in catalina.properties or server.xml
# Default HTTP port: 8080
# Default HTTPS port: 8443
# Default Shutdown port: 8005

# Or find and kill process using port 8080:
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

### Issue 4: Authentication Failed

**Error**: `Email ou mot de passe incorrect` (Email or password incorrect)

**Solution**:
- Verify student exists in database: `SELECT * FROM students WHERE email='user@email.com';`
- Check password is hashed properly (uses BCrypt)
- Ensure PasswordUtil.verifyPassword() is working

### Issue 5: Transaction/Session Errors

**Error**: `Could not obtain transaction-synchronized Session for current thread`

**Solution**:
- Check Hibernate session is properly closed in DAO methods
- Use try-with-resources for session management
- Verify HibernateUtil.getSessionFactory() is initialized

### Issue 6: WAR Not Deploying

**Error**: `Deployment failed`

**Solution**:
```bash
# Clean Tomcat
rm -rf $CATALINA_HOME/work/*
rm -rf $CATALINA_HOME/webapps/gestionprojets-1.0-SNAPSHOT

# Rebuild and redeploy
mvn clean package
cp target/gestionprojets-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/

# Restart Tomcat
```

---

## 📊 Class Diagram

A comprehensive class diagram has been generated showing:
- Entity relationships (Student, Project, Task)
- DAO layer structure
- Service layer design
- JSF Bean architecture
- Utility classes

**Files**:
- `CLASS_DIAGRAM.puml` - PlantUML format (use PlantUML Online Editor)
- `CLASS_DIAGRAM.html` - Interactive HTML visualization
- `CLASS_DIAGRAM.md` - Detailed markdown documentation
- `CLASS_DIAGRAM.json` - Structured JSON schema

View the HTML version:
```bash
# Open in browser
start CLASS_DIAGRAM.html

# Or use PlantUML Online
https://www.plantuml.com/plantuml/uml/
# Copy content of CLASS_DIAGRAM.puml
```

---

## 📧 Default Test Credentials

After first launch, you can create test accounts through the registration page.

**Test Account**:
```
Email: test@example.com
Password: Test123!
Name: John Doe
```

---

## 🔒 Security Best Practices

1. **Password Security**
   - Passwords are hashed using BCrypt
   - Never store plain-text passwords
   - Use strong passwords (min 8 characters)

2. **Session Management**
   - AuthBean uses SessionScoped for user session
   - Session automatically invalidates on logout
   - Set session timeout in web.xml:
     ```xml
     <session-config>
         <cookie-config>
             <secure>true</secure>
             <http-only>true</http-only>
         </cookie-config>
     </session-config>
     ```

3. **Authorization**
   - Creator-only operations validated at service level
   - Email uniqueness enforced at database level
   - Method-level authorization checks

4. **Input Validation**
   - Date validation (end date > start date)
   - Email format validation
   - Required field validation
   - XSS protection through JSF

---

## 📝 Logging

### Enable Debug Logging

Edit `hibernate.cfg.xml`:
```xml
<property name="hibernate.show_sql">true</property>
<property name="hibernate.format_sql">true</property>
<property name="hibernate.generate_statistics">true</property>
<property name="hibernate.use_sql_comments">true</property>
```

### Tomcat Logs

```bash
# Windows
C:\path\to\apache-tomcat\logs\catalina.out

# Linux/Mac
/path/to/apache-tomcat/logs/catalina.out
```

---

## 📦 Build & Deployment

### Maven Build Commands

```bash
# Clean build
mvn clean build

# Build with tests
mvn clean test package

# Skip tests
mvn clean package -DskipTests

# Install locally
mvn install

# Deploy to remote repository
mvn deploy
```

### WAR File

The built WAR file is located at:
```
target/gestionprojets-1.0-SNAPSHOT.war
```

Size: ~15-20 MB (including dependencies in WEB-INF/lib)

---

## 🚨 Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| 404 Error | Wrong URL path | Use `/gestionprojets-1.0-SNAPSHOT/index.xhtml` |
| Login fails | DB connection issue | Check hibernate.cfg.xml credentials |
| CSS/JS not loading | Resource path error | Verify resources folder structure |
| Member not added | Email not found | Ensure member is registered first |
| Task status stuck | Session issue | Clear browser cache and login again |

---

## 📞 Support & Documentation

- **Hibernate Documentation**: https://hibernate.org/
- **Jakarta EE**: https://jakarta.ee/
- **JSF Documentation**: https://projects.eclipse.org/projects/ee4j.faces
- **MySQL Documentation**: https://dev.mysql.com/doc/
- **Apache Tomcat**: https://tomcat.apache.org/

---

## 📄 Additional Documentation

Additional documentation files are available:
- `CLASS_DIAGRAM.md` - Detailed class structure
- `CLASS_DIAGRAM.html` - Visual diagram
- `CLASS_DIAGRAM.puml` - PlantUML format
- `CLASS_DIAGRAM.json` - Structured data

---

## 📅 Version Information

```
Project: WorkShare
Version: 1.0-SNAPSHOT
Build Date: March 18, 2026
Framework: Jakarta EE 4.0
ORM: Hibernate 7.0.4
Database: MySQL 8.0+
```

---

## 📝 License & Contributing

This project is created as a student project management system. 

For questions or contributions, please refer to the project documentation.

---

**Last Updated**: March 18, 2026

**Project Location**: `C:\Users\hp\Desktop\Projects\WorkShare`

---

## Quick Start Checklist

- [ ] Install JDK 17+
- [ ] Install MySQL 8.0+
- [ ] Install Apache Tomcat 10+
- [ ] Create database: `CREATE DATABASE gestionprojets;`
- [ ] Update `hibernate.cfg.xml` with DB credentials
- [ ] Run: `mvn clean package`
- [ ] Copy WAR to Tomcat webapps
- [ ] Start Tomcat
- [ ] Access: `http://localhost:8080/gestionprojets-1.0-SNAPSHOT/`
- [ ] Create account and login
- [ ] Start managing projects!

---

**Enjoy using WorkShare!** 🚀

