package com.example.libarymanagementsystem;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Book {
    private IntegerProperty id;
    private StringProperty title;
    private StringProperty author;
    private IntegerProperty available;
    private StringProperty image;
    private ObjectProperty<LocalDate> borrowDate;  // Thêm thuộc tính borrowDate

    // Constructor
    public Book(int id, String title, String author, int available, String image) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.available = new SimpleIntegerProperty(available);
        this.image = new SimpleStringProperty(image);
        this.borrowDate = new SimpleObjectProperty<>(null); // Khởi tạo với giá trị null
    }

    // Getters and Setters cho id, title, author, available, image

    // Getters và Setters cho borrowDate
    public LocalDate getBorrowDate() {
        return borrowDate.get();
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate.set(borrowDate);
    }

    public ObjectProperty<LocalDate> borrowDateProperty() {
        return borrowDate;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getAuthor() {
        return author.get();
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public StringProperty authorProperty() {
        return author;
    }

    public int getAvailable() {
        return available.get();
    }

    public void setAvailable(int available) {
        this.available.set(available);
    }

    public IntegerProperty availableProperty() {
        return available;
    }

    public String getImage() {
        return image.get();
    }

    public void setImage(String image) {
        this.image.set(image);
    }

    public StringProperty imageProperty() {
        return image;
    }
}
