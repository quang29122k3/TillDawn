package com.example.libarymanagementsystem;

import javafx.scene.image.ImageView;

public class Book {
    private int id;
    private String title;
    private String author;
    private int available;
    private ImageView imageView;
    private int totalCopies;

    public Book(String title, String author, int available, ImageView imageView,int totalCopies) {
        this.title = title;
        this.author = author;
        this.available = available;
        this.imageView = imageView;
        this.totalCopies = totalCopies;
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

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }
}
