package com.example.repository;


import com.example.model.BorrowRecord;
import com.example.model.BorrowRecord.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByUserId(Long userId);
    List<BorrowRecord> findByBookId(Long bookId);
    List<BorrowRecord> findByStatus(BorrowStatus status);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'BORROWED' AND br.dueDate < :date")
    List<BorrowRecord> findOverdueRecords(LocalDate date);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.user.id = :userId AND br.status = 'BORROWED'")
    List<BorrowRecord> findActiveBorrowsByUser(Long userId);
    
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.user.id = :userId AND br.status = 'BORROWED'")
    Long countActiveBorrowsByUser(Long userId);
}