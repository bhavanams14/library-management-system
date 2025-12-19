package com.example.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

/**
 * User Entity - Represents a library member/user
 * 
 * This class maps to the 'users' table in the database
 * Uses JPA annotations for ORM (Object-Relational Mapping)
 * Uses Lombok annotations to reduce boilerplate code
 */
@Entity
@Table(name = "users")
@Data                    // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor       // Generates no-argument constructor
@AllArgsConstructor      // Generates constructor with all fields
@Builder                 // Enables builder pattern for object creation
public class User {
    
    /**
     * Primary Key - Auto-generated ID
     * Uses IDENTITY strategy which relies on auto-increment column
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * User's full name
     * - Cannot be blank (validation)
     * - Cannot be null in database
     */
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;
    
    /**
     * User's email address
     * - Must be valid email format
     * - Must be unique across all users
     * - Cannot be null
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;
    
    /**
     * User's phone number
     * - Must be exactly 10 digits
     * - Uses regex pattern for validation
     */
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;
    
    /**
     * User's address
     * - Can store up to 500 characters
     */
    @NotBlank(message = "Address is required")
    @Column(length = 500)
    private String address;
    
    /**
     * Date when user became a member
     * - Automatically set when user is created
     */
    @Column(name = "membership_date")
    private LocalDate membershipDate;
    
    /**
     * Whether the user account is active
     * - Active users can borrow books
     * - Inactive users cannot borrow
     */
    @Column(name = "is_active")
    private Boolean isActive;
    
    /**
     * Total number of books borrowed by user
     * - Increments when borrowing
     * - Used for statistics
     */
    @Column(name = "books_borrowed")
    private Integer booksBorrowed;
    
    /**
     * JPA Lifecycle Callback - Called before persisting new entity
     * Sets default values for new users
     */
    @PrePersist
    protected void onCreate() {
        // Set membership date to current date
        membershipDate = LocalDate.now();
        
        // Set active status to true if not specified
        if (isActive == null) {
            isActive = true;
        }
        
        // Initialize books borrowed counter
        if (booksBorrowed == null) {
            booksBorrowed = 0;
        }
    }
    
    /**
     * Custom method to check if user can borrow books
     * @return true if user is active, false otherwise
     */
    public boolean canBorrowBooks() {
        return isActive != null && isActive;
    }
    
    /**
     * Custom method to get user status as string
     * @return "Active" or "Inactive"
     */
    public String getStatusString() {
        return isActive ? "Active" : "Inactive";
    }
    
    /**
     * Increment the books borrowed counter
     */
    public void incrementBooksBorrowed() {
        if (booksBorrowed == null) {
            booksBorrowed = 0;
        }
        booksBorrowed++;
    }
    
    /**
     * Decrement the books borrowed counter
     */
    public void decrementBooksBorrowed() {
        if (booksBorrowed != null && booksBorrowed > 0) {
            booksBorrowed--;
        }
    }
}

/**
 * COLUMN MAPPINGS:
 * ================
 * Java Field            Database Column       Type          Constraints
 * ---------------------------------------------------------------------------
 * id                    id                    BIGINT        PRIMARY KEY, AUTO_INCREMENT
 * name                  name                  VARCHAR(255)  NOT NULL
 * email                 email                 VARCHAR(255)  NOT NULL, UNIQUE
 * phone                 phone                 VARCHAR(255)  NOT NULL
 * address               address               VARCHAR(500)  NOT NULL
 * membershipDate        membership_date       DATE          -
 * isActive              is_active             BOOLEAN       -
 * booksBorrowed         books_borrowed        INTEGER       -
 * 
 * 
 * VALIDATION RULES:
 * ================
 * - name: Required, not blank
 * - email: Required, valid email format, unique
 * - phone: Required, must be exactly 10 digits
 * - address: Required, max 500 characters
 * 
 * 
 * DEFAULT VALUES:
 * ===============
 * - membershipDate: Current date (set on creation)
 * - isActive: true (set on creation if not provided)
 * - booksBorrowed: 0 (set on creation if not provided)
 * 
 * 
 * EXAMPLE USAGE:
 * ==============
 * 
 * // Using Builder Pattern
 * User user = User.builder()
 *     .name("John Doe")
 *     .email("john@example.com")
 *     .phone("1234567890")
 *     .address("123 Main St, City")
 *     .build();
 * 
 * // Using Constructor
 * User user = new User();
 * user.setName("John Doe");
 * user.setEmail("john@example.com");
 * user.setPhone("1234567890");
 * user.setAddress("123 Main St");
 * 
 * // Check if user can borrow
 * if (user.canBorrowBooks()) {
 *     // Allow borrowing
 * }
 */