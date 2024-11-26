package com.example.libarymanagementsystem.model;

import javafx.scene.image.ImageView;

import java.time.LocalDate;

public class Book {
    private int id;
    private String title;
    private String author;
    private int available;
    private ImageView imageView;
    private LocalDate borrowDate;
    private int totalCopies;
    private int loanId;

    // Constructor cho sách có sẵn (không có loanId)
    public Book(int id, String title, String author, int available, ImageView imageView) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = available;
        this.imageView = imageView;
        this.borrowDate = null; // Khởi tạo với giá trị null
        this.totalCopies = available; // Giả sử totalCopies ban đầu bằng available
        this.loanId = 0; // 0 nghĩa là không có loanId
    }

    // Constructor cho sách đang mượn (có loanId)
    public Book(int id, String title, String author, int available, ImageView imageView, int loanId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = available;
        this.imageView = imageView;
        this.borrowDate = null; // Khởi tạo với giá trị null
        this.totalCopies = available; // Giả sử totalCopies ban đầu bằng available
        this.loanId = loanId;
    }


    // Getters và Setters cho tất cả các thuộc tính

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }
}
