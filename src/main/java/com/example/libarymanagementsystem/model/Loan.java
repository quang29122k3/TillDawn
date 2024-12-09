package com.example.libarymanagementsystem.model;

import javafx.scene.image.ImageView;

import java.time.LocalDate;

public class Loan {
    private int id;             // ID trong loans
    private String personId;
    private int bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;
    private String status;

    // Thông tin sách để hiển thị
    private String bookTitle;
    private String bookAuthor;

    // Thông tin người mượn (dành cho Manager)
    private String borrowerName;
    private String borrowerClass;
    private String borrowerRole;

    // Ảnh sách
    private ImageView bookImage;

    // Constructor với 14 tham số (dành cho Manager)
    public Loan(int id, String personId, int bookId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate,
                boolean returned, String status, String bookTitle, String bookAuthor,
                String borrowerName, String borrowerClass, String borrowerRole, ImageView bookImage) {
        this.id = id;
        this.personId = personId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.returned = returned;
        this.status = status;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.borrowerName = borrowerName;
        this.borrowerClass = borrowerClass;
        this.borrowerRole = borrowerRole;
        this.bookImage = bookImage;
    }

    // Constructor overloaded với 10 tham số (dành cho Student)
    public Loan(int id, String personId, int bookId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate,
                boolean returned, String status, String bookTitle, String bookAuthor) {
        this(id, personId, bookId, borrowDate, dueDate, returnDate, returned, status, bookTitle, bookAuthor,
                null, null, null, null); // Các thuộc tính mở rộng được đặt là null
    }

    // Getters và Setters cho tất cả các thuộc tính
    public int getId() { return id; }
    public String getPersonId() { return personId; }
    public int getBookId() { return bookId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return returned; }
    public String getStatus() { return status; }
    public String getBookTitle() { return bookTitle; }
    public String getBookAuthor() { return bookAuthor; }

    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }

    public String getBorrowerClass() { return borrowerClass; }
    public void setBorrowerClass(String borrowerClass) { this.borrowerClass = borrowerClass; }

    public String getBorrowerRole() { return borrowerRole; }
    public void setBorrowerRole(String borrowerRole) { this.borrowerRole = borrowerRole; }

    public ImageView getBookImage() { return bookImage; }
    public void setBookImage(ImageView bookImage) { this.bookImage = bookImage; }

    public void setStatus(String status) {
        this.status = status;
    }

    // Phương thức tiện ích
    public boolean isOverdue() {
        return !returned && LocalDate.now().isAfter(dueDate);
    }

    public boolean isBorrowed() {
        return !returned && !isOverdue();
    }
}