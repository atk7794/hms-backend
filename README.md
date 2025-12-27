# 🏥 Hospital Management System (HMS) – Backend

A comprehensive **Spring Boot–based backend** for a Hospital Management System (HMS), designed to manage core hospital operations such as patients, doctors, appointments, medical records, prescriptions, authentication, and system activity logging.

This project follows a **clean layered architecture** and modern backend best practices, with a strong focus on **security, scalability, and deployment readiness**.

---

## 🚀 Features

### 🔐 Authentication & Authorization
- JWT-based authentication
- Role-based access control (**ADMIN**, **DOCTOR**, **PATIENT**)
- Secure login & logout
- Email verification
- Password reset flow with token validation

### 🏥 Core Hospital Modules
- Patient management
- Doctor management
- Appointment scheduling
- Medical records
- Prescriptions (e-prescription support)

### 📊 Logging & Monitoring
- User activity logs (login / logout tracking)
- User action logs (CRUD operations)
- Email logs
- Scheduled background jobs

### 📧 Email Services
- Email verification
- Password reset emails
- SMTP-based mail integration
- Email delivery logging

---

## 🧱 Tech Stack

- **Java 17**
- **Spring Boot 3.x**
- **Spring Security (JWT)**
- **Spring Data JPA**
- **Hibernate 6**
- **PostgreSQL**
- **Maven**

---

## 📁 Project Structure


```
src/main/java/com/example/hms
├── config          # Web & application configuration
├── controller      # REST controllers
├── dto             # Auth, Request & response DTOs
├── exception       # Global exception handling
├── model           # JPA entities
├── repository      # Data access layer
├── scheduler       # Scheduled background jobs
├── security        # JWT & security configuration
├── service
│   ├── impl        # Service implementations
│   └── interfaces  # Service interfaces
```


---

## ⚙️ Configuration & Environment Variables

Sensitive configuration values are **NOT included** in the repository.

The application uses:
- `application.properties`
- Environment variables (`.env`)

### 📄 `.env.example`

> ⚠️ This file is for reference only. Do NOT commit real credentials.


```env
DB_URL=jdbc:postgresql://localhost:5432/hmsdb
DB_USERNAME=postgres
DB_PASSWORD=your_db_password

JWT_SECRET=your_jwt_secret_key

MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_email_app_password

CORS_ALLOWED_ORIGINS=http://localhost:4200
FRONTEND_URL=http://localhost:4200
```

---

## ▶️ Running the Project Locally

```bash
# Clone the repository
git clone https://github.com/atk7794/hms-backend.git

# Navigate to the project directory
cd hms-backend

# Run the application
./mvnw spring-boot:run
```

The backend will start on:

```
http://localhost:8080
```

---

## 🔐 API Authentication

All secured endpoints require a JWT token:
```
Authorization: Bearer <JWT_TOKEN>
```
JWT is returned after successful login.

---

## 🧪 Testing

```bash
./mvnw test
```

---

## 🔒 Security Notes

* All sensitive credentials are excluded from version control
* JWT tokens secure API communication
* Global exception handling is enabled
* Validation annotations are enforced
* CORS is configurable via environment variables

---

## 📌 Deployment Status

✅ Backend development completed  
✅ Deployment-ready configuration  
🚧 Dockerization (next step)  
🚧 Cloud deployment (Render / Railway)  
🚧 Frontend integration  

---

## 👨‍💻 Author

**Tuncay Köse**  
Computer Engineer  
GitHub: [https://github.com/atk7794](https://github.com/atk7794)  

---

⭐ If you find this project useful, feel free to give it a star!
