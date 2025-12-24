# 🏥 Hospital Management System (HMS) – Backend

A comprehensive **Spring Boot–based backend** for a Hospital Management System, designed to manage core hospital operations such as patients, doctors, appointments, medical records, prescriptions, authentication, and system activity logging.

This project is built with a **layered architecture** and follows best practices for **security, scalability, and maintainability**.

---

## 🚀 Features

* 🔐 **Authentication & Authorization**

  * JWT-based authentication
  * Role-based access control (Admin, Doctor, Patient)
  * Secure login, logout, and password reset flows

* 🏥 **Core Hospital Modules**

  * Patient management
  * Doctor management
  * Appointment scheduling
  * Medical records
  * Prescriptions

* 📊 **Logging & Monitoring**

  * User activity logs
  * User action logs
  * Email logs
  * Scheduled background jobs

* 📧 **Email Services**

  * Email verification
  * Password reset emails
  * System email logging

---

## 🧱 Tech Stack

* **Java 17**
* **Spring Boot**
* **Spring Security (JWT)**
* **Spring Data JPA**
* **Hibernate**
* **PostgreSQL**
* **Maven**

---

## 📁 Project Structure

```
src/main/java/com/example/hms
├── config          # Web & application configuration
├── controller      # REST controllers
├── dto             # Request & response DTOs
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

## ⚙️ Configuration

Sensitive data such as database credentials, JWT secrets, and email credentials are **NOT included** in the repository.

They are managed using:

* `application.properties`
* Environment variables (`.env`)

Example configuration keys:

```
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
MAIL_USERNAME
MAIL_PASSWORD
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

## 🧪 Testing

```bash
./mvnw test
```

---

## 🔒 Security Notes

* All sensitive credentials are excluded via `.gitignore`
* JWT tokens are used for secure API communication
* CORS and production configurations will be handled during deployment

---

## 📌 Project Status

✅ Core backend development completed
🚧 Deployment & Dockerization planned
🚧 Frontend integration ongoing

---

## 👨‍💻 Author

**Tuncay Köse**
Computer Engineer
GitHub: [https://github.com/atk7794](https://github.com/atk7794)

---

⭐ If you find this project useful, feel free to give it a star!
