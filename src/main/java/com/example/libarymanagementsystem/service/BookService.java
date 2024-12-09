package com.example.libarymanagementsystem.service;

import com.example.libarymanagementsystem.model.Book;

import java.util.List;

public interface BookService {
    List<Book> getAllBooks();
    void addBook(Book book) throws Exception;
    void updateBook(Book book) throws Exception;
    void deleteBook(int bookId) throws Exception;
    List<Book> searchBooks(String keyword);
}
