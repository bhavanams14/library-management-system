package com.example.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Application Class for Library Management System
 * 
 * IMPORTANT: Enables component scanning for the entire com.example package
 * since repositories and other components are in different sub-packages
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.example")           // Scan all com.example packages
@EnableJpaRepositories(basePackages = "com.example")   // Enable JPA repositories
@EntityScan(basePackages = "com.example")              // Scan for entities
public class LibraryApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("Library Management System Started!");
        System.out.println("API: http://localhost:8080");
        System.out.println("H2 Console: http://localhost:8080/h2-console");
        System.out.println("===========================================\n");
    }
}

/**
 * PACKAGE STRUCTURE EXPLANATION:
 * ==============================
 * 
 * com.example.library       - Main application class
 * com.example.model         - Entity classes (Book, User, etc.)
 * com.example.repository    - JPA repositories
 * com.example.service       - Business logic services
 * com.example.controller    - REST controllers
 * com.example.config        - Configuration classes (DataInitializer)
 * 
 * Without @ComponentScan, Spring Boot only scans:
 * - com.example.library and sub-packages
 * 
 * With @ComponentScan(basePackages = "com.example"):
 * - Scans ALL packages under com.example
 * - Finds repositories, services, controllers, and config classes
 */
