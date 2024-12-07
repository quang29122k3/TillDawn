package com.example.libarymanagementsystem.model;

import java.time.LocalDate;

public class Loan {
    private int id;             // id trong loans
    private String personId;
    private int bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;
    private String status;

    // Thông tin sách để hiển thị mà không cần thay đổi Book
    private String bookTitle;
    private String bookAuthor;
    // Nếu cần hiển thị hình ảnh sách, có thể thêm bookImageView (ImageView) vào đây.

    public Loan(int id, String personId, int bookId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate, boolean returned, String status, String bookTitle, String bookAuthor) {
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
    }

    // Getter & Setter cho tất cả thuộc tính
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

    public void setStatus(String status) {
        this.status = status;
    }

    // Có thể thêm phương thức tiện ích
    public boolean isOverdue() {
        return !returned && LocalDate.now().isAfter(dueDate);
    }

    public boolean isBorrowed() {
        return !returned && !isOverdue();
    }
}