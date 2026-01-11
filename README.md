# FreelanceFlow Desktop

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-21.0.6-blue?style=for-the-badge&logo=java)
![SQLite](https://img.shields.io/badge/SQLite-3-003B57?style=for-the-badge&logo=sqlite)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=for-the-badge&logo=apache-maven)

**A comprehensive desktop application for freelance work management, team collaboration, and client-freelancer interaction**

[Features](#features) • [Installation](#installation) • [Usage](#usage) • [Technologies](#technologies) • [Database](#database-schema) • [Screenshots](#screenshots)

</div>

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
  - [User Management](#user-management)
  - [Freelancer Features](#freelancer-features)
  - [Client Features](#client-features)
  - [Team Management](#team-management)
  - [Notification System](#notification-system)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Database Schema](#database-schema)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

---

## 🌟 Overview

**FreelanceFlow Desktop** is a robust, full-featured desktop application designed to streamline freelance work management and team collaboration. Built with JavaFX, it provides an intuitive interface for both freelancers and clients to manage projects, form teams, handle job postings, track applications, and communicate effectively through a built-in notification system.

### Key Highlights

- 🎨 **Modern UI/UX**: Clean, professional interface built with JavaFX
- 👥 **Dual User Roles**: Separate dashboards and features for Freelancers and Clients
- 🤝 **Team Collaboration**: Create, join, and manage teams with role-based access
- 📊 **Project Management**: Organize work, track tasks, and manage team projects
- 🔔 **Real-time Notifications**: Stay updated with team invitations, job postings, and more
- 💾 **SQLite Database**: Lightweight, embedded database for data persistence
- 🔐 **Secure Authentication**: User registration and login system

---

## ✨ Features

### 🔐 User Management

#### **Registration & Authentication**
- **Dual Role Sign-up**: Users can register as either a **Freelancer** or **Client**
- **Secure Account Creation**: 
  - Email validation and uniqueness checking
  - Password requirements (minimum 8 characters)
  - Automatic role assignment based on registration type
- **User Profile Management**:
  - First name and last name
  - Email address (unique identifier)
  - Role-based access control
  - Session management across the application

#### **Login System**
- Secure email and password authentication
- Automatic redirection based on user role:
  - Freelancers → Freelancer Dashboard or Team Dashboard (if in a team)
  - Clients → Client Dashboard
- Session persistence throughout the application
- Password validation and error handling

#### **Welcome Flow**
- Personalized welcome message after registration
- Option to complete detailed profile setup
- Quick navigation to main dashboard

---

### 👨‍💻 Freelancer Features

#### **Freelancer Dashboard**
The main hub for freelancers with comprehensive navigation and statistics:

**Dashboard Statistics**
- **My Teams Count**: Number of teams the freelancer is part of
- **Jobs Count**: Active job applications and opportunities
- **Recruitments Count**: Open recruitment posts from teams
- **Notification Badge**: Real-time unread notification counter

**Main Navigation Sections**

##### 1. **My Teams**
- View all teams the freelancer is a member of
- Display team name, description, required skills, and member count
- Quick access to each team's dashboard
- Team status indicators (Admin/Member)
- Options to:
  - View team details
  - Access team dashboard
  - Leave team (with confirmation)

##### 2. **Create Team**
- **Team Creation Form**:
  - Team Name (required)
  - Team Description (required)
  - Required Skills (comma-separated)
  - Maximum Members (default: 10)
- Automatic admin assignment to team creator
- Validation for all required fields
- Success feedback and auto-navigation to team dashboard

##### 3. **Browse Teams**
- Discover available teams looking for members
- Filter and search capabilities
- View team details before joining
- Request to join teams
- Display:
  - Team name and description
  - Required skills
  - Current member count / Max members
  - Admin information

##### 4. **Recruitments**
- View all recruitment posts from various teams
- Filter by skills, team type, or availability
- Apply to recruitment opportunities
- Track application status
- Notification system for recruitment updates

##### 5. **Jobs**
- Browse job postings from clients
- Filter jobs by:
  - Budget range
  - Duration
  - Required skills
  - Project type
- Submit applications
- Track application status
- Save jobs for later

##### 6. **Notifications**
- Real-time notification center
- Notification types:
  - Team invitations
  - Recruitment acceptances/rejections
  - Job application updates
  - Team activity updates
  - Project assignments
- Mark as read/unread functionality
- Visual badge for unread count
- Notification history

#### **Freelancer Profile Details**
- **Complete Profile Setup**:
  - Display name (professional name)
  - Phone number
  - Location/City
  - Hourly rate
  - Profile photo upload
- **Skills & Expertise**:
  - List of technical skills
  - Years of experience
  - Portfolio links
- **Availability Status**: Open to work, Busy, etc.

---

### 👔 Client Features

#### **Client Dashboard**
Comprehensive control center for clients to manage projects and hiring:

**Main Navigation Sections**

##### 1. **Post Job**
- **Job Creation Form**:
  - Job Title (required)
  - Job Description (required)
  - Budget (required)
  - Project Duration
  - Required Skills (comma-separated)
- Form validation and error handling
- Success confirmation
- Automatic listing in job postings

##### 2. **My Jobs**
- View all posted jobs
- Job management:
  - Edit job details
  - Close/Archive job postings
  - View applicant list
  - Track hiring status
- Job statistics:
  - Number of applications received
  - Views count
  - Time posted

##### 3. **Teams**
- Browse available freelancer teams
- View team portfolios and past projects
- Contact teams for project collaboration
- Hire entire teams for projects
- View team capabilities and skills
- Team ratings and reviews

##### 4. **Applications**
- Centralized application management
- View all freelancer applications
- Filter and sort applications by:
  - Date submitted
  - Skills match
  - Ratings
- Application actions:
  - Review applicant profile
  - Accept/Reject applications
  - Send interview invitations
  - Communicate with applicants

#### **Client Profile Details**
- **Company Information**:
  - Display name (company name)
  - Phone number
  - Location
  - Project budget range
  - Company logo upload
- **Project Preferences**:
  - Preferred project types
  - Industry focus
  - Budget constraints

---

### 🤝 Team Management

#### **Team Dashboard**
Comprehensive team collaboration interface with role-based access:

**Dashboard Overview**
- **Team Statistics**:
  - Total members count
  - Active projects count
  - Pending tasks count
- **Team Information Display**:
  - Team name
  - User role (Admin/Member)
  - Team description
  - Required skills
- **Quick Action Buttons**:
  - Invite Member (Admin only)
  - Create Project
  - Create Recruitment Post

**Main Sections**

##### 1. **Overview**
- **Team Summary**:
  - Team name and description
  - Required skills and expertise
  - Team creation date
- **Activity Feed**:
  - Recent team activities
  - Member joins/leaves
  - Project updates
  - Task completions
  - Milestone achievements

##### 2. **Members**
- **Member List**:
  - Display all team members with cards showing:
    - Full name
    - Email address
    - Role (Admin/Member) with color coding
  - Member count statistics
- **Member Management** (Admin only):
  - Invite new members by email
  - Remove members (with confirmation)
  - Change member roles
  - View member contributions
- **Empty State**: Helpful message with invite action when no members

##### 3. **Projects**
- **Project Management**:
  - Create new projects
  - View all team projects
  - Project status tracking (Planning, In Progress, Completed)
  - Assign projects to members
- **Project Cards**:
  - Project name and description
  - Deadline and priority
  - Assigned members
  - Progress indicators
- **Task Management**:
  - Create tasks within projects
  - Assign tasks to members
  - Track task completion

##### 4. **Recruitment**
- **Recruitment Posts**:
  - Create recruitment announcements
  - Specify required skills
  - Set member capacity
  - Define role requirements
- **Application Management**:
  - View incoming applications
  - Review applicant profiles
  - Accept/reject applications with notifications
  - Automatic team member addition upon acceptance

##### 5. **Settings** (Admin only)
- **Team Configuration**:
  - Edit team name
  - Update team description
  - Modify required skills
  - Change maximum members
  - Team visibility settings
- **Danger Zone**:
  - Delete team (with confirmation)
  - Transfer admin rights
  - Archive team

**Role-Based Access Control**
- **Admin Permissions**:
  - Full team management access
  - Member invitation and removal
  - Project creation and assignment
  - Team settings modification
  - Recruitment post management
- **Member Permissions**:
  - View team information
  - Participate in projects
  - View other members
  - Leave team

---

### 🔔 Notification System

#### **Real-time Notifications**
- **Notification Types**:
  - **Team Invitations**: Receive invites to join teams
  - **Application Status**: Updates on job/recruitment applications
  - **Team Activity**: Project assignments, task updates
  - **System Messages**: Important announcements
  
#### **Notification Features**
- **Visual Badge**: Unread count display on notification icon
- **Notification Center**:
  - View all notifications in organized list
  - Read/unread status indicators (● = unread, ✓ = read)
  - Timestamps for each notification
  - Auto-mark as read when viewed
- **Notification Management**:
  - Click to view details
  - Automatic updates after actions
  - Related entity links (team, job, project)
  - Clear notification history

#### **Database-Backed Notifications**
- Persistent notification storage
- User-specific notification querying
- Efficient unread count retrieval
- Related ID tracking for navigation

---

## 🛠️ Technologies

### **Core Technologies**
- **Java 21**: Modern Java with latest LTS features
- **JavaFX 21.0.6**: Rich desktop UI framework
  - `javafx-controls`: UI components
  - `javafx-fxml`: Declarative UI with FXML
  - `javafx-web`: Web content rendering
  - `javafx-media`: Media playback support
  - `javafx-swing`: Swing integration
- **SQLite**: Embedded database for data persistence
- **Maven**: Build automation and dependency management

### **UI/UX Libraries**
- **ControlsFX 11.2.1**: Extended JavaFX controls
- **FormsFX 11.6.0**: Advanced form handling
- **ValidatorFX 0.6.1**: Input validation framework
- **Ikonli 12.3.1**: Icon library for JavaFX
- **BootstrapFX 0.4.0**: Bootstrap-inspired styling
- **TilesFX 21.0.9**: Dashboard tiles and widgets
- **FXGL 17.3**: Game development library (if needed for gamification)

### **Development Tools**
- **JUnit 5.12.1**: Unit testing framework
- **Maven Wrapper**: Platform-independent builds

### **Design Patterns**
- **MVC (Model-View-Controller)**: Separation of concerns
- **DAO (Data Access Object)**: Database abstraction
- **Singleton**: Session management
- **Factory**: Object creation patterns

---

## 📦 Prerequisites

Before installing FreelanceFlow Desktop, ensure you have:

### **Required Software**
1. **Java Development Kit (JDK) 21 or higher**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Verify installation: `java -version`

2. **Apache Maven 3.8+ (optional - Maven Wrapper included)**
   - Download from [Maven](https://maven.apache.org/download.cgi)
   - Verify installation: `mvn -version`

3. **IDE (recommended)**
   - IntelliJ IDEA (Community or Ultimate)
   - Eclipse with JavaFX plugin
   - NetBeans with JavaFX support

### **System Requirements**
- **Operating System**: Windows 10/11, macOS 10.14+, or Linux
- **RAM**: Minimum 4GB (8GB recommended)
- **Disk Space**: 500MB free space
- **Display**: 1280x720 minimum resolution

---

## 🚀 Installation

### **Method 1: Clone and Build**

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/FreelanceFlow_Desktop.git
   cd FreelanceFlow_Desktop
   ```

2. **Build the project**
   
   Using Maven Wrapper (recommended):
   ```bash
   # Windows
   mvnw.cmd clean install
   
   # macOS/Linux
   ./mvnw clean install
   ```
   
   Or using Maven:
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   # Windows
   mvnw.cmd javafx:run
   
   # macOS/Linux
   ./mvnw javafx:run
   ```

### **Method 2: IDE Setup**

#### **IntelliJ IDEA**
1. Open IntelliJ IDEA
2. Click **File → Open** and select the project directory
3. Wait for Maven to download dependencies
4. Right-click on `Launcher.java` → **Run 'Launcher.main()'**

#### **Eclipse**
1. Open Eclipse
2. Click **File → Import → Maven → Existing Maven Projects**
3. Select the project directory
4. Right-click on project → **Maven → Update Project**
5. Right-click on `Launcher.java` → **Run As → Java Application**

### **Method 3: Package as JAR**

1. **Build executable JAR**
   ```bash
   mvn clean package
   ```

2. **Run the JAR**
   ```bash
   java -jar target/FreelanceFlow_Desktop-1.0-SNAPSHOT.jar
   ```

---

## 📖 Usage

### **Getting Started**

1. **Launch the Application**
   - Run the application using one of the methods above
   - The Welcome Page will appear

2. **Create an Account**
   - Click **"Sign Up as Freelancer"** or **"Sign Up as Client"**
   - Fill in your details:
     - First Name
     - Last Name
     - Email (must be unique)
     - Password (minimum 8 characters)
   - Click **"Create Account"**

3. **Complete Your Profile** (Optional)
   - After signup, you'll see a welcome message
   - Click **"Complete Profile"** to add:
     - Freelancers: Display name, phone, location, hourly rate, profile photo
     - Clients: Company name, phone, location, budget range, logo
   - Or click **"Skip"** to go directly to dashboard

4. **Login**
   - If you already have an account, click **"Login"** on the Welcome Page
   - Enter your email and password
   - Click **"Login"**

### **For Freelancers**

#### **Creating a Team**
1. Navigate to **"Create Team"** from the sidebar
2. Fill in team details:
   - Team name
   - Description
   - Required skills
   - Maximum members
3. Click **"Create Team"**
4. You'll be automatically assigned as Admin

#### **Joining a Team**
1. Navigate to **"Browse Teams"**
2. Find a team that interests you
3. Click **"Request to Join"**
4. Wait for admin approval notification

#### **Managing Your Team**
1. Go to **"My Teams"** and select a team
2. Click **"Go to Team Dashboard"**
3. From Team Dashboard:
   - **Overview**: View team summary and activity
   - **Members**: See all team members (Admin: invite or remove)
   - **Projects**: Create and manage projects
   - **Recruitment**: Post recruitment announcements
   - **Settings** (Admin only): Edit team details

#### **Applying for Jobs**
1. Navigate to **"Jobs"** from the sidebar
2. Browse available job postings
3. Click on a job to view details
4. Click **"Apply"** to submit your application
5. Track application status in notifications

### **For Clients**

#### **Posting a Job**
1. Navigate to **"Post Job"** from the sidebar
2. Fill in job details:
   - Job title
   - Description
   - Budget
   - Duration
   - Required skills
3. Click **"Post Job"**
4. View in **"My Jobs"** section

#### **Managing Applications**
1. Navigate to **"Applications"** from the sidebar
2. View all applicants for your jobs
3. Click on an application to view details
4. Accept or reject applications
5. Contact selected freelancers

#### **Hiring Teams**
1. Navigate to **"Teams"** from the sidebar
2. Browse available freelancer teams
3. View team portfolios and capabilities
4. Contact teams for collaboration

### **Notifications**
- Click the **bell icon** in the top-right corner
- Badge shows unread notification count
- View all notifications in one place
- Notifications auto-mark as read when viewed

### **Logging Out**
- Click your **profile icon** or **"Logout"** button
- Returns to Welcome Page
- Session cleared for security

---

## 🗄️ Database Schema

FreelanceFlow uses SQLite with the following schema:

### **Tables**

#### **users**
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL,  -- 'freelancer' or 'client'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### **freelancer_details**
```sql
CREATE TABLE freelancer_details (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    display_name TEXT,
    phone TEXT,
    location TEXT,
    rate_per_hour TEXT,
    profile_photo_path TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### **client_details**
```sql
CREATE TABLE client_details (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    display_name TEXT,
    phone TEXT,
    location TEXT,
    project_budget TEXT,
    company_logo_path TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### **teams**
```sql
CREATE TABLE teams (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    skills TEXT,
    max_members INTEGER,
    admin_id INTEGER NOT NULL,
    created_date TEXT,
    FOREIGN KEY (admin_id) REFERENCES users(id)
);
```

#### **team_members**
```sql
CREATE TABLE team_members (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    team_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    role TEXT NOT NULL,  -- 'Admin' or 'Member'
    joined_date TEXT,
    FOREIGN KEY (team_id) REFERENCES teams(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### **notifications**
```sql
CREATE TABLE notifications (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    type TEXT NOT NULL,  -- 'TEAM_INVITE', 'JOB_UPDATE', etc.
    message TEXT NOT NULL,
    related_id TEXT,  -- Reference to related entity
    is_read INTEGER DEFAULT 0,
    created_date TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### **Database Location**
- Database file: `freelanceflow.db`
- Created automatically in project root directory
- Tables initialized on first run

---

## 📁 Project Structure

```
FreelanceFlow_Desktop/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/freelanceflow_desktop/
│       │       ├── controllers/
│       │       │   ├── ClientDashboardController.java
│       │       │   ├── ClientSignupController.java
│       │       │   ├── FreelancerDashboardController.java
│       │       │   ├── FreelancerSignupController.java
│       │       │   ├── LoginController.java
│       │       │   ├── TeamDashboardController.java
│       │       │   ├── WelcomeMessageController.java
│       │       │   └── WelcomePageController.java
│       │       │
│       │       ├── dao/
│       │       │   ├── ClientDetailsDAO.java
│       │       │   ├── FreelancerDetailsDAO.java
│       │       │   ├── NotificationDAO.java
│       │       │   ├── TeamDAO.java
│       │       │   └── UserDAO.java
│       │       │
│       │       ├── models/
│       │       │   ├── Notification.java
│       │       │   ├── Team.java
│       │       │   └── User.java
│       │       │
│       │       ├── utils/
│       │       │   ├── DatabaseManager.java
│       │       │   └── SessionManager.java
│       │       │
│       │       ├── Launcher.java
│       │       └── WelcomePage.java
│       │
│       └── resources/
│           └── com/example/freelanceflow_desktop/
│               ├── ClientDashboard.fxml
│               ├── ClientDetails.fxml
│               ├── ClientSignup.fxml
│               ├── FreelancerDashboard.fxml
│               ├── FreelancerDetails.fxml
│               ├── FreelancerSignup.fxml
│               ├── Login.fxml
│               ├── TeamDashboard.fxml
│               ├── WelcomeMessage.fxml
│               └── WelcomePage.fxml
│
├── target/                    # Compiled classes (generated)
├── pom.xml                    # Maven configuration
├── mvnw                       # Maven wrapper (Unix)
├── mvnw.cmd                   # Maven wrapper (Windows)
├── freelanceflow.db           # SQLite database (auto-generated)
└── README.md                  # This file
```

### **Key Components**

#### **Controllers**
- Handle user interactions and UI logic
- Connect views (FXML) with business logic
- Manage scene transitions

#### **DAO (Data Access Objects)**
- Abstract database operations
- Provide CRUD operations for entities
- Handle SQL queries and result mapping

#### **Models**
- Represent data entities
- POJOs (Plain Old Java Objects) with getters/setters
- Mirror database table structure

#### **Utils**
- `DatabaseManager`: Database connection and initialization
- `SessionManager`: User session management (singleton pattern)

#### **FXML Files**
- Declarative UI definitions
- Linked to controllers via `fx:controller` attribute
- Styled with CSS (inline and external)

---

## 🎨 Screenshots

### Welcome Page
*Clean, modern landing page with role selection for Freelancers and Clients*

### Freelancer Dashboard
*Comprehensive dashboard showing teams, jobs, recruitments, and notifications*

### Team Dashboard
*Team collaboration interface with member management, projects, and recruitment*

### Client Dashboard
*Client control center for job posting, application management, and team hiring*

### Login & Signup
*Secure authentication with validation and error handling*

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/AmazingFeature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add some AmazingFeature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/AmazingFeature
   ```
5. **Open a Pull Request**

### **Coding Standards**
- Follow Java naming conventions
- Add JavaDoc comments for public methods
- Write unit tests for new features
- Ensure code compiles without warnings

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👥 Authors

- **Your Name** - *Initial work* - [YourGitHub](https://github.com/yourusername)

---

## 🙏 Acknowledgments

- JavaFX community for excellent documentation
- ControlsFX and other UI library contributors
- SQLite for lightweight database solution
- Maven for dependency management

---

## 📞 Support

For support, email your-email@example.com or open an issue on GitHub.

---

## 🔮 Future Enhancements

- [ ] Real-time chat between team members
- [ ] File sharing and document management
- [ ] Payment integration for job completion
- [ ] Video call integration for interviews
- [ ] Calendar and scheduling system
- [ ] Project timeline and Gantt charts
- [ ] Advanced search and filtering
- [ ] Export reports (PDF, Excel)
- [ ] Mobile app companion (Android/iOS)
- [ ] Email notifications
- [ ] Two-factor authentication
- [ ] Dark mode theme

---

<div align="center">

**Made with ❤️ using JavaFX**

⭐ Star this repository if you find it helpful!

</div>
