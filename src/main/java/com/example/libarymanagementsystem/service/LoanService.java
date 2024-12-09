package com.example.libarymanagementsystem.service;

import com.example.libarymanagementsystem.model.Loan;

import java.util.List;

public interface LoanService {
    List<Loan> getAllLoans();
    void borrowBook(String personId, int bookId, int days) throws Exception;
    void returnBook(int loanId) throws Exception;
    void extendLoan(int loanId, int days) throws Exception;
}
