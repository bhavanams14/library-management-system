package com.example.controller;

import com.example.dto.BorrowRequest;
import com.example.dto.ReturnRequest;
import com.example.model.Book;
import com.example.model.BorrowRecord;
import com.example.service.BookService;
import com.example.service.FileExportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class BookController {
    
    private final BookService bookService;
    private final FileExportService fileExportService;
    
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<Book>> getAvailableBooks() {
        return ResponseEntity.ok(bookService.getAvailableBooks());
    }
    
    @GetMapping("/search/title")
    public ResponseEntity<List<Book>> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.searchBooksByTitle(title));
    }
    
    @GetMapping("/search/author")
    public ResponseEntity<List<Book>> searchByAuthor(@RequestParam String author) {
        return ResponseEntity.ok(bookService.searchBooksByAuthor(author));
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Book>> getBooksByCategory(@PathVariable String category) {
        return ResponseEntity.ok(bookService.getBooksByCategory(category));
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        return ResponseEntity.ok(bookService.getAllCategories());
    }
    
    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBook(book));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book book) {
        return ResponseEntity.ok(bookService.updateBook(id, book));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Book deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/borrow")
    public ResponseEntity<BorrowRecord> borrowBook(@Valid @RequestBody BorrowRequest request) {
        return ResponseEntity.ok(bookService.borrowBook(request.getUserId(), request.getBookId()));
    }
    
    @PostMapping("/return")
    public ResponseEntity<BorrowRecord> returnBook(@Valid @RequestBody ReturnRequest request) {
        return ResponseEntity.ok(bookService.returnBook(request.getBorrowRecordId()));
    }
    
    @GetMapping("/borrow-records")
    public ResponseEntity<List<BorrowRecord>> getAllBorrowRecords() {
        return ResponseEntity.ok(bookService.getAllBorrowRecords());
    }
    
    @GetMapping("/borrow-records/user/{userId}")
    public ResponseEntity<List<BorrowRecord>> getBorrowRecordsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookService.getBorrowRecordsByUser(userId));
    }
    
    @GetMapping("/borrow-records/overdue")
    public ResponseEntity<List<BorrowRecord>> getOverdueRecords() {
        return ResponseEntity.ok(bookService.getOverdueRecords());
    }
    
    @GetMapping("/export/excel")
    public ResponseEntity<ByteArrayResource> exportBooksToExcel() {
        try {
            List<Book> books = bookService.getAllBooks();
            ByteArrayOutputStream outputStream = fileExportService.exportBooksToExcel(books);
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/export/csv")
    public ResponseEntity<ByteArrayResource> exportBooksToCSV() {
        try {
            List<Book> books = bookService.getAllBooks();
            ByteArrayOutputStream outputStream = fileExportService.exportBooksToCSV(books);
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(resource.contentLength())
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}