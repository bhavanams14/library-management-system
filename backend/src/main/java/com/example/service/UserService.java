package com.example.service;

import com.example.model.BorrowRecord;
import com.example.model.User;
import com.example.repository.BorrowRecordRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * UserService - Business Logic Layer for User Management
 * 
 * This service class handles all business logic related to users
 * - CRUD operations
 * - User validation
 * - User activation/deactivation
 * - Borrow history management
 * 
 * @Service - Marks this as a Spring service component
 * @RequiredArgsConstructor - Lombok generates constructor for final fields
 * @Slf4j - Lombok generates logger instance
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    // Dependencies injected through constructor
    private final UserRepository userRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    
    /**
     * Retrieve all users from database
     * 
     * @return List of all users
     */
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }
    
    /**
     * Retrieve a single user by ID
     * 
     * @param id - User ID
     * @return User object
     * @throws RuntimeException if user not found
     */
    public User getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        return userRepository.findById(id)
            .orElseThrow(() -> {
                log.error("User not found with id: {}", id);
                return new RuntimeException("User not found with id: " + id);
            });
    }
    
    /**
     * Retrieve a user by email address
     * 
     * @param email - User's email
     * @return User object
     * @throws RuntimeException if user not found
     */
    public User getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
            .orElseThrow(() -> {
                log.error("User not found with email: {}", email);
                return new RuntimeException("User not found with email: " + email);
            });
    }
    
    /**
     * Search users by name (case-insensitive, partial match)
     * 
     * @param name - Search term
     * @return List of matching users
     */
    public List<User> searchUsersByName(String name) {
        log.info("Searching users with name containing: {}", name);
        return userRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Get all active users
     * 
     * @return List of active users
     */
    public List<User> getActiveUsers() {
        log.info("Fetching all active users");
        return userRepository.findByIsActive(true);
    }
    
    /**
     * Get all inactive users
     * 
     * @return List of inactive users
     */
    public List<User> getInactiveUsers() {
        log.info("Fetching all inactive users");
        return userRepository.findByIsActive(false);
    }
    
    /**
     * Add a new user to the system
     * 
     * @param user - User object to be saved
     * @return Saved user with generated ID
     * @throws RuntimeException if email already exists
     * 
     * @Transactional - Ensures all database operations complete successfully
     */
    @Transactional
    public User addUser(User user) {
        log.info("Adding new user: {}", user.getEmail());
        
        // Check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            log.error("User with email {} already exists", user.getEmail());
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        
        // Save and return the user
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return savedUser;
    }
    
    /**
     * Update existing user details
     * 
     * @param id - User ID to update
     * @param userDetails - New user details
     * @return Updated user
     * @throws RuntimeException if user not found
     * 
     * Note: ID and membership date cannot be changed
     */
    @Transactional
    public User updateUser(Long id, User userDetails) {
        log.info("Updating user with id: {}", id);
        
        // Fetch existing user
        User user = getUserById(id);
        
        // Update fields
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setAddress(userDetails.getAddress());
        
        // Update active status if provided
        if (userDetails.getIsActive() != null) {
            user.setIsActive(userDetails.getIsActive());
        }
        
        // Save and return
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getId());
        return updatedUser;
    }
    
    /**
     * Delete a user from the system
     * 
     * @param id - User ID to delete
     * @throws RuntimeException if user has active borrows
     * 
     * Business Rule: Users with active borrows cannot be deleted
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("Attempting to delete user with id: {}", id);
        
        // Fetch user
        User user = getUserById(id);
        
        // Check for active borrows
        List<BorrowRecord> activeRecords = borrowRecordRepository.findActiveBorrowsByUser(id);
        
        if (!activeRecords.isEmpty()) {
            log.error("Cannot delete user {} - has {} active borrows", 
                     id, activeRecords.size());
            throw new RuntimeException(
                "Cannot delete user with active borrows. Please return all books first."
            );
        }
        
        // Delete user
        userRepository.delete(user);
        log.info("User deleted successfully: {}", id);
    }
    
    /**
     * Deactivate a user account
     * Deactivated users cannot borrow books
     * 
     * @param id - User ID
     * @return Updated user
     */
    @Transactional
    public User deactivateUser(Long id) {
        log.info("Deactivating user with id: {}", id);
        
        User user = getUserById(id);
        user.setIsActive(false);
        
        User deactivatedUser = userRepository.save(user);
        log.info("User deactivated successfully: {}", id);
        return deactivatedUser;
    }
    
    /**
     * Activate a user account
     * Activated users can borrow books
     * 
     * @param id - User ID
     * @return Updated user
     */
    @Transactional
    public User activateUser(Long id) {
        log.info("Activating user with id: {}", id);
        
        User user = getUserById(id);
        user.setIsActive(true);
        
        User activatedUser = userRepository.save(user);
        log.info("User activated successfully: {}", id);
        return activatedUser;
    }
    
    /**
     * Get user's borrow history
     * 
     * @param userId - User ID
     * @return List of all borrow records for the user
     */
    public List<BorrowRecord> getUserBorrowHistory(Long userId) {
        log.info("Fetching borrow history for user: {}", userId);
        
        // Verify user exists
        getUserById(userId);
        
        return borrowRecordRepository.findByUserId(userId);
    }
    
    /**
     * Get user's active borrows (currently borrowed books)
     * 
     * @param userId - User ID
     * @return List of active borrow records
     */
    public List<BorrowRecord> getUserActiveBorrows(Long userId) {
        log.info("Fetching active borrows for user: {}", userId);
        
        // Verify user exists
        getUserById(userId);
        
        return borrowRecordRepository.findActiveBorrowsByUser(userId);
    }
    
    /**
     * Get count of books currently borrowed by user
     * 
     * @param userId - User ID
     * @return Number of active borrows
     */
    public Long getActiveBorrowCount(Long userId) {
        log.info("Fetching active borrow count for user: {}", userId);
        
        // Verify user exists
        getUserById(userId);
        
        return borrowRecordRepository.countActiveBorrowsByUser(userId);
    }
    
    /**
     * Check if user can borrow more books
     * Business Rule: Maximum 5 books per user
     * 
     * @param userId - User ID
     * @return true if user can borrow, false otherwise
     */
    public boolean canUserBorrowMoreBooks(Long userId) {
        log.info("Checking if user {} can borrow more books", userId);
        
        User user = getUserById(userId);
        
        // Check if user is active
        if (!user.canBorrowBooks()) {
            log.warn("User {} is not active", userId);
            return false;
        }
        
        // Check borrow limit (max 5 books)
        Long activeBorrows = getActiveBorrowCount(userId);
        boolean canBorrow = activeBorrows < 5;
        
        log.info("User {} has {} active borrows, can borrow: {}", 
                userId, activeBorrows, canBorrow);
        
        return canBorrow;
    }
    
    /**
     * Get user statistics
     * 
     * @param userId - User ID
     * @return Map containing user statistics
     */
    public UserStatistics getUserStatistics(Long userId) {
        log.info("Fetching statistics for user: {}", userId);
        
        User user = getUserById(userId);
        Long activeBorrows = getActiveBorrowCount(userId);
        List<BorrowRecord> history = getUserBorrowHistory(userId);
        
        long returnedBooks = history.stream()
            .filter(record -> record.getStatus() == BorrowRecord.BorrowStatus.RETURNED)
            .count();
        
        long overdueBooks = history.stream()
            .filter(BorrowRecord::isOverdue)
            .count();
        
        return UserStatistics.builder()
            .userId(userId)
            .userName(user.getName())
            .totalBooksBorrowed(user.getBooksBorrowed())
            .currentlyBorrowed(activeBorrows.intValue())
            .returnedBooks((int) returnedBooks)
            .overdueBooks((int) overdueBooks)
            .memberSince(user.getMembershipDate())
            .isActive(user.getIsActive())
            .build();
    }
    
    /**
     * Inner class for user statistics
     */
    @lombok.Data
    @lombok.Builder
    public static class UserStatistics {
        private Long userId;
        private String userName;
        private Integer totalBooksBorrowed;
        private Integer currentlyBorrowed;
        private Integer returnedBooks;
        private Integer overdueBooks;
        private java.time.LocalDate memberSince;
        private Boolean isActive;
    }
}

/**
 * SERVICE LAYER RESPONSIBILITIES:
 * ================================
 * 
 * 1. BUSINESS LOGIC
 *    - Validate business rules (e.g., max 5 books per user)
 *    - Handle complex operations involving multiple entities
 *    - Enforce data integrity
 * 
 * 2. TRANSACTION MANAGEMENT
 *    - @Transactional ensures ACID properties
 *    - Rollback on exceptions
 *    - Manage multiple database operations as single unit
 * 
 * 3. ERROR HANDLING
 *    - Throw meaningful exceptions
 *    - Log errors and important events
 *    - Validate inputs before database operations
 * 
 * 4. DATA TRANSFORMATION
 *    - Convert between DTOs and entities
 *    - Aggregate data from multiple sources
 *    - Calculate derived values
 * 
 * 
 * USAGE EXAMPLES:
 * ===============
 * 
 * // In a Controller or another Service
 * 
 * @Autowired
 * private UserService userService;
 * 
 * // Get all users
 * List<User> users = userService.getAllUsers();
 * 
 * // Add new user
 * User newUser = User.builder()
 *     .name("John Doe")
 *     .email("john@example.com")
 *     .phone("1234567890")
 *     .address("123 Main St")
 *     .build();
 * User savedUser = userService.addUser(newUser);
 * 
 * // Update user
 * User updates = new User();
 * updates.setName("John Updated");
 * User updated = userService.updateUser(1L, updates);
 * 
 * // Deactivate user
 * userService.deactivateUser(1L);
 * 
 * // Check if can borrow
 * boolean canBorrow = userService.canUserBorrowMoreBooks(1L);
 * 
 * // Get statistics
 * UserStatistics stats = userService.getUserStatistics(1L);
 * 
 * 
 * TRANSACTION BEHAVIOR:
 * ====================
 * 
 * Methods marked with @Transactional:
 * - Begin transaction at method start
 * - Commit transaction if method completes successfully
 * - Rollback transaction if exception is thrown
 * - Ensure data consistency
 * 
 * Example:
 * try {
 *     userService.deleteUser(1L); // Fails if user has active borrows
 * } catch (RuntimeException e) {
 *     // Handle error - no changes made to database
 * }
 */