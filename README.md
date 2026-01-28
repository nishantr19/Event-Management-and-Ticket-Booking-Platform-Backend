🎟️ Event Management & Ticket Booking Platform

A full-stack Java-based platform that allows users to browse events, book tickets, and receive QR code–based digital passes. Built with Spring Boot and modern backend engineering practices, the system emphasizes security, scalability, and transactional consistency.

🚀 Overview

This project implements a secure event booking system with role-based access, real-time seat management, and QR-based ticketing. Administrators can manage events, while users can browse, book tickets, and access digital QR passes.

The backend follows clean layered architecture and microservice-ready design principles.

🧩 Architecture

The application is structured around three core services:

Auth Service – User registration, authentication, and JWT-based authorization (USER / ADMIN)

Event Service – Event creation, listing, search, filtering, and capacity management

Booking Service – Ticket booking workflow, availability validation, and QR code generation

Services communicate via REST APIs, ensuring loose coupling and clear separation of concerns.

✨ Key Features

JWT-based authentication with role-based access control

Admin event creation and management

Event browsing, search, and filtering

Transaction-safe ticket booking with real-time availability

QR code digital ticket generation per booking

Booking history and QR retrieval

Overbooking prevention using pessimistic locking

🎫 QR Code Ticketing

Each confirmed booking generates a unique QR code containing booking metadata (Booking ID, Event ID, User ID).
This QR code acts as a digital ticket that can be scanned and validated at event entry.

🧠 Engineering Highlights

Spring Security + JWT for stateless authentication

Transactional booking workflow ensuring atomic operations

Pessimistic database locking to prevent race conditions

QR code generation using ZXing

RESTful API design with layered architecture

Modular structure extensible to API Gateway or asynchronous messaging

🛠 Tech Stack

Java 17

Spring Boot

Spring Security + JWT

Hibernate / JPA

H2 Database (Development)

Maven

ZXing (QR Code)

Swagger / OpenAPI
