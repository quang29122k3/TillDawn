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
    private int borrowCount;
    private boolean isPinned;

    // Constructor cho sách có sẵn (không có loanId, không ghim)
    public Book(int id, String title, String author, int available, ImageView imageView) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = available;
        this.imageView = imageView;
        this.borrowDate = null; // Khởi tạo với giá trị null
        this.totalCopies = available; // Giả sử totalCopies ban đầu bằng available
        this.loanId = 0; // 0 nghĩa là không có loanId
        this.isPinned = false; // Mặc định chưa ghim
    }

    // Constructor cho sách đang mượn (có loanId, không ghim)
    public Book(int id, String title, String author, int available, ImageView imageView, int loanId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = available;
        this.imageView = imageView;
        this.borrowDate = null; // Khởi tạo với giá trị null
        this.totalCopies = available; // Giả sử totalCopies ban đầu bằng available
        this.loanId = loanId;
        this.isPinned = false; // Mặc định chưa ghim
    }

    // Constructor đầy đủ bao gồm isPinned (có thể dùng khi tải dữ liệu từ DB)
    public Book(int id, String title, String author, int available, ImageView imageView, int loanId, int borrowCount, boolean isPinned) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = available;
        this.imageView = imageView;
        this.borrowDate = null; // Khởi tạo với giá trị null
        this.totalCopies = available; // Giả sử totalCopies ban đầu bằng available
        this.loanId = loanId;
        this.borrowCount = borrowCount;
        this.isPinned = isPinned;
    }

    // Getters và Setters cho tất cả các thuộc tính

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

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getBorrowCount() {
        return borrowCount;
    }

    public void setBorrowCount(int borrowCount) {
        this.borrowCount = borrowCount;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    // Phương thức để tăng số lượt mượn
    public void incrementBorrowCount() {
        this.borrowCount++;
    }

    // Phương thức để giảm số lượng có sẵn khi mượn sách
    public void decrementAvailable() {
        if (this.available > 0) {
            this.available--;
        }
    }

    // Phương thức để tăng số lượng có sẵn khi trả sách
    public void incrementAvailable() {
        this.available++;
    }

    // Phương thức toString để hiển thị thông tin sách (tùy chọn)
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", available=" + available +
                ", isPinned=" + isPinned +
                '}';
    }

    // Phương thức equals và hashCode (tùy chọn, nếu cần so sánh đối tượng)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        return id == book.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}