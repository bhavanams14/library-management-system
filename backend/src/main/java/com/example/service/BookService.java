package com.example.service;

import com.example.model.Book;
import com.example.model.BorrowRecord;
import com.example.model.User;
import com.example.repository.BookRepository;
import com.example.repository.BorrowRecordRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    
    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;
    
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }
    
    public List<Book> getAvailableBooks() {
        return bookRepository.findAvailableBooks();
    }
    
    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
    
    public List<Book> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }
    
    public List<Book> getBooksByCategory(String category) {
        return bookRepository.findByCategoryIgnoreCase(category);
    }
    
    public List<String> getAllCategories() {
        return bookRepository.findAllCategories();
    }
    
    @Transactional
    public Book addBook(Book book) {
        if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new RuntimeException("Book with ISBN " + book.getIsbn() + " already exists");
        }
        return bookRepository.save(book);
    }
    
    @Transactional
    public Book updateBook(Long id, Book bookDetails) {
        Book book = getBookById(id);
        
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());
        book.setCategory(bookDetails.getCategory());
        book.setTotalCopies(bookDetails.getTotalCopies());
        book.setPublicationYear(bookDetails.getPublicationYear());
        book.setPublisher(bookDetails.getPublisher());
        book.setDescription(bookDetails.getDescription());
        
        // Adjust available copies based on total copies change
        int difference = bookDetails.getTotalCopies() - book.getTotalCopies();
        book.setAvailableCopies(book.getAvailableCopies() + difference);
        
        return bookRepository.save(book);
    }
    
    @Transactional
    public void deleteBook(Long id) {
        Book book = getBookById(id);
        
        // Check if book has active borrows
        List<BorrowRecord> activeRecords = borrowRecordRepository.findByBookId(id).stream()
            .filter(record -> record.getStatus() == BorrowRecord.BorrowStatus.BORROWED)
            .toList();
        
        if (!activeRecords.isEmpty()) {
            throw new RuntimeException("Cannot delete book with active borrows");
        }
        
        bookRepository.delete(book);
    }
    
    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Book book = getBookById(bookId);
        
        // Validations
        if (!user.getIsActive()) {
            throw new RuntimeException("User account is not active");
        }
        
        if (!book.isAvailable()) {
            throw new RuntimeException("Book is not available");
        }
        
        Long activeBorrows = borrowRecordRepository.countActiveBorrowsByUser(userId);
        if (activeBorrows >= 5) {
            throw new RuntimeException("User has reached maximum borrow limit (5 books)");
        }
        
        // Create borrow record
        BorrowRecord record = BorrowRecord.builder()
            .book(book)
            .user(user)
            .build();
        
        // Update book availability
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        
        // Update user borrow count
        user.setBooksBorrowed(user.getBooksBorrowed() + 1);
        userRepository.save(user);
        
        return borrowRecordRepository.save(record);
    }
    
    @Transactional
    public BorrowRecord returnBook(Long borrowRecordId) {
        BorrowRecord record = borrowRecordRepository.findById(borrowRecordId)
            .orElseThrow(() -> new RuntimeException("Borrow record not found"));
        
        if (record.getStatus() != BorrowRecord.BorrowStatus.BORROWED) {
            throw new RuntimeException("Book is already returned");
        }
        
        // Update record
        record.setReturnDate(LocalDate.now());
        record.setStatus(BorrowRecord.BorrowStatus.RETURNED);
        record.calculateFine();
        
        // Update book availability
        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        
        return borrowRecordRepository.save(record);
    }
    
    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAll();
    }
    
    public List<BorrowRecord> getBorrowRecordsByUser(Long userId) {
        return borrowRecordRepository.findByUserId(userId);
    }
    
    public List<BorrowRecord> getOverdueRecords() {
        return borrowRecordRepository.findOverdueRecords(LocalDate.now());
    }
}
