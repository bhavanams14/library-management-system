package com.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "borrow_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Column(name = "return_date")
    private LocalDate returnDate;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BorrowStatus status;
    
    @Column(name = "fine_amount")
    private Double fineAmount;
    
    @PrePersist
    protected void onCreate() {
        borrowDate = LocalDate.now();
        dueDate = borrowDate.plusDays(14); // 14 days borrowing period
        status = BorrowStatus.BORROWED;
        fineAmount = 0.0;
    }
    
    public enum BorrowStatus {
        BORROWED,
        RETURNED,
        OVERDUE
    }
    
    public boolean isOverdue() {
        return status == BorrowStatus.BORROWED && 
               LocalDate.now().isAfter(dueDate);
    }
    
    public void calculateFine() {
        if (status == BorrowStatus.RETURNED && returnDate != null && returnDate.isAfter(dueDate)) {
            long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, returnDate);
            fineAmount = daysLate * 5.0; // $5 per day fine
        }
    }
}