package com.example.config;

import com.example.model.Book;
import com.example.model.User;
import com.example.repository.BookRepository;
import com.example.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * DataInitializer - Loads sample data into the database on startup
 * 
 * This class runs after the application context is initialized
 * and tables are created by Hibernate
 * 
 * IMPORTANT: Package structure should be com.example.*
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * Initialize sample data
     * @PostConstruct runs after dependency injection is complete
     */
    @PostConstruct
    public void init() {
        log.info("Starting data initialization...");
        
        // Check if data already exists
        if (bookRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }

        loadBooks();
        loadUsers();
        
        log.info("Data initialization completed successfully!");
    }

    private void loadBooks() {
        List<Book> books = Arrays.asList(
            Book.builder()
                .title("The Great Gatsby")
                .author("F. Scott Fitzgerald")
                .isbn("9780743273565")
                .category("Fiction")
                .totalCopies(5)
                .availableCopies(5)
                .publicationYear(1925)
                .publisher("Scribner")
                .description("A classic American novel set in the Jazz Age")
                .build(),
                
            Book.builder()
                .title("To Kill a Mockingbird")
                .author("Harper Lee")
                .isbn("9780061120084")
                .category("Fiction")
                .totalCopies(4)
                .availableCopies(4)
                .publicationYear(1960)
                .publisher("J.B. Lippincott & Co.")
                .description("A gripping tale of racial injustice and childhood innocence")
                .build(),
                
            Book.builder()
                .title("1984")
                .author("George Orwell")
                .isbn("9780451524935")
                .category("Science Fiction")
                .totalCopies(6)
                .availableCopies(6)
                .publicationYear(1949)
                .publisher("Secker & Warburg")
                .description("A dystopian social science fiction novel")
                .build(),
                
            Book.builder()
                .title("Pride and Prejudice")
                .author("Jane Austen")
                .isbn("9780141439518")
                .category("Romance")
                .totalCopies(3)
                .availableCopies(3)
                .publicationYear(1813)
                .publisher("T. Egerton")
                .description("A romantic novel of manners")
                .build(),
                
            Book.builder()
                .title("The Catcher in the Rye")
                .author("J.D. Salinger")
                .isbn("9780316769174")
                .category("Fiction")
                .totalCopies(4)
                .availableCopies(4)
                .publicationYear(1951)
                .publisher("Little, Brown and Company")
                .description("A story about teenage rebellion")
                .build(),
                
            Book.builder()
                .title("Harry Potter and the Sorcerer's Stone")
                .author("J.K. Rowling")
                .isbn("9780439708180")
                .category("Fantasy")
                .totalCopies(8)
                .availableCopies(8)
                .publicationYear(1997)
                .publisher("Scholastic")
                .description("The first book in the Harry Potter series")
                .build(),
                
            Book.builder()
                .title("The Hobbit")
                .author("J.R.R. Tolkien")
                .isbn("9780547928227")
                .category("Fantasy")
                .totalCopies(5)
                .availableCopies(5)
                .publicationYear(1937)
                .publisher("George Allen & Unwin")
                .description("A fantasy novel and children's book")
                .build(),
                
            Book.builder()
                .title("Brave New World")
                .author("Aldous Huxley")
                .isbn("9780060850524")
                .category("Science Fiction")
                .totalCopies(4)
                .availableCopies(4)
                .publicationYear(1932)
                .publisher("Chatto & Windus")
                .description("A dystopian novel set in a futuristic World State")
                .build(),
                
            Book.builder()
                .title("The Lord of the Rings")
                .author("J.R.R. Tolkien")
                .isbn("9780544003415")
                .category("Fantasy")
                .totalCopies(6)
                .availableCopies(6)
                .publicationYear(1954)
                .publisher("George Allen & Unwin")
                .description("An epic high-fantasy novel")
                .build(),
                
            Book.builder()
                .title("Animal Farm")
                .author("George Orwell")
                .isbn("9780451526342")
                .category("Fiction")
                .totalCopies(5)
                .availableCopies(5)
                .publicationYear(1945)
                .publisher("Secker & Warburg")
                .description("An allegorical novella about Soviet Russia")
                .build()
        );

        bookRepository.saveAll(books);
        log.info("Loaded {} books", books.size());
    }

    private void loadUsers() {
        List<User> users = Arrays.asList(
            User.builder()
                .name("John Doe")
                .email("john.doe@email.com")
                .phone("1234567890")
                .address("123 Main St, City, State 12345")
                .isActive(true)
                .booksBorrowed(0)
                .build(),
                
            User.builder()
                .name("Jane Smith")
                .email("jane.smith@email.com")
                .phone("0987654321")
                .address("456 Oak Ave, City, State 12345")
                .isActive(true)
                .booksBorrowed(0)
                .build(),
                
            User.builder()
                .name("Mike Johnson")
                .email("mike.johnson@email.com")
                .phone("1112223333")
                .address("789 Pine Rd, City, State 12345")
                .isActive(true)
                .booksBorrowed(0)
                .build(),
                
            User.builder()
                .name("Sarah Williams")
                .email("sarah.williams@email.com")
                .phone("4445556666")
                .address("321 Elm St, City, State 12345")
                .isActive(true)
                .booksBorrowed(0)
                .build(),
                
            User.builder()
                .name("David Brown")
                .email("david.brown@email.com")
                .phone("7778889999")
                .address("654 Maple Dr, City, State 12345")
                .isActive(true)
                .booksBorrowed(0)
                .build()
        );

        userRepository.saveAll(users);
        log.info("Loaded {} users", users.size());
    }
}