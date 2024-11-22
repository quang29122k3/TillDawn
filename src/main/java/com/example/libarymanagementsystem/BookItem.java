package com.example.libarymanagementsystem;

public class BookItem {
    private String title;
    private String authors;
    private String publisher;
    private String infoLink;

    public BookItem(String title, String authors, String publisher, String infoLink) {
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.infoLink = infoLink;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getInfoLink() {
        return infoLink;
    }
}