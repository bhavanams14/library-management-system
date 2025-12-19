package com.example.service;


import com.example.model.Book;
import com.example.model.BorrowRecord;
import com.example.model.User;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileExportService {
    
    private static final String EXPORT_DIR = "exports";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Export Books to Excel
    public ByteArrayOutputStream exportBooksToExcel(List<Book> books) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Books");
        
        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Title", "Author", "ISBN", "Category", "Total Copies", 
                           "Available Copies", "Publication Year", "Publisher"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Fill data
        int rowNum = 1;
        for (Book book : books) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(book.getId());
            row.createCell(1).setCellValue(book.getTitle());
            row.createCell(2).setCellValue(book.getAuthor());
            row.createCell(3).setCellValue(book.getIsbn());
            row.createCell(4).setCellValue(book.getCategory());
            row.createCell(5).setCellValue(book.getTotalCopies());
            row.createCell(6).setCellValue(book.getAvailableCopies());
            row.createCell(7).setCellValue(book.getPublicationYear() != null ? book.getPublicationYear() : 0);
            row.createCell(8).setCellValue(book.getPublisher() != null ? book.getPublisher() : "");
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream;
    }
    
    // Export Users to Excel
    public ByteArrayOutputStream exportUsersToExcel(List<User> users) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");
        
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Name", "Email", "Phone", "Address", 
                           "Membership Date", "Books Borrowed", "Status"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getName());
            row.createCell(2).setCellValue(user.getEmail());
            row.createCell(3).setCellValue(user.getPhone());
            row.createCell(4).setCellValue(user.getAddress());
            row.createCell(5).setCellValue(user.getMembershipDate().format(DATE_FORMATTER));
            row.createCell(6).setCellValue(user.getBooksBorrowed());
            row.createCell(7).setCellValue(user.getIsActive() ? "Active" : "Inactive");
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream;
    }
    
    // Export Borrow Records to Excel
    public ByteArrayOutputStream exportBorrowRecordsToExcel(List<BorrowRecord> records) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Borrow Records");
        
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Book Title", "User Name", "Borrow Date", 
                           "Due Date", "Return Date", "Status", "Fine Amount"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (BorrowRecord record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getId());
            row.createCell(1).setCellValue(record.getBook().getTitle());
            row.createCell(2).setCellValue(record.getUser().getName());
            row.createCell(3).setCellValue(record.getBorrowDate().format(DATE_FORMATTER));
            row.createCell(4).setCellValue(record.getDueDate().format(DATE_FORMATTER));
            row.createCell(5).setCellValue(record.getReturnDate() != null ? 
                record.getReturnDate().format(DATE_FORMATTER) : "Not Returned");
            row.createCell(6).setCellValue(record.getStatus().toString());
            row.createCell(7).setCellValue(record.getFineAmount());
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream;
    }
    
    // Export Books to CSV
    public ByteArrayOutputStream exportBooksToCSV(List<Book> books) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);
        
        // Write header
        writer.println("ID,Title,Author,ISBN,Category,Total Copies,Available Copies,Publication Year,Publisher");
        
        // Write data
        for (Book book : books) {
            writer.println(String.format("%d,\"%s\",\"%s\",%s,%s,%d,%d,%d,\"%s\"",
                book.getId(),
                book.getTitle().replace("\"", "\"\""),
                book.getAuthor().replace("\"", "\"\""),
                book.getIsbn(),
                book.getCategory(),
                book.getTotalCopies(),
                book.getAvailableCopies(),
                book.getPublicationYear() != null ? book.getPublicationYear() : 0,
                book.getPublisher() != null ? book.getPublisher().replace("\"", "\"\"") : ""
            ));
        }
        
        writer.flush();
        return outputStream;
    }
    
    // Save file locally (optional)
    public void saveFileLocally(ByteArrayOutputStream data, String filename) throws IOException {
        Path exportPath = Paths.get(EXPORT_DIR);
        if (!Files.exists(exportPath)) {
            Files.createDirectories(exportPath);
        }
        
        Path filePath = exportPath.resolve(filename);
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            data.writeTo(fos);
        }
    }
}
