package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Author is required")
    @Column(nullable = false)
    private String author;
    
    @NotBlank(message = "ISBN is required")
    @Column(unique = true, nullable = false)
    private String isbn;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    @NotNull(message = "Total copies is required")
    @Min(value = 1, message = "Total copies must be at least 1")
    @Column(name = "total_copies")
    private Integer totalCopies;
    
    @Column(name = "available_copies")
    private Integer availableCopies;
    
    @Column(name = "publication_year")
    private Integer publicationYear;
    
    private String publisher;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "created_date")
    private LocalDate createdDate;
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDate.now();
        if (availableCopies == null) {
            availableCopies = totalCopies;
        }
    }
    
    public boolean isAvailable() {
        return availableCopies != null && availableCopies > 0;
    }
}