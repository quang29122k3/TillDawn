package com.example.libarymanagementsystem.service;

public class ServiceFactory {
    private static final BookService bookService = new BookServiceImpl();

    public static BookService getBookService() {
        return bookService;
    }
}