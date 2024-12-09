package com.example.libarymanagementsystem.service;

import com.example.libarymanagementsystem.model.Loan;
import com.example.libarymanagementsystem.utils.ConnectionJDBCUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanServiceImpl implements LoanService {

    @Override
    public List<Loan> getAllLoans() {
        List<Loan> loanList = new ArrayList<>();
        String query = "SELECT l.*, b.title, b.author, p.fullname, p.class, r.name AS role " +
                "FROM loans l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN person p ON l.person_id = p.id " +
                "JOIN role r ON p.role_id = r.id " +
                "WHERE l.returned = 0";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            LocalDate today = LocalDate.now();
            while (rs.next()) {
                int loanId = rs.getInt("id");
                String personId = rs.getString("person_id");
                int bookId = rs.getInt("book_id");
                LocalDate borrowDate = rs.getDate("borrow_date").toLocalDate();
                Date dueDateSql = rs.getDate("due_date");
                LocalDate dueDate = (dueDateSql != null) ? dueDateSql.toLocalDate() : null;
                Date returnDateSql = rs.getDate("return_date");
                LocalDate returnDate = (returnDateSql != null) ? returnDateSql.toLocalDate() : null;
                boolean returned = rs.getBoolean("returned");
                String dbStatus = rs.getString("status");

                String bookTitle = rs.getString("title");
                String bookAuthor = rs.getString("author");
                String borrowerName = rs.getString("fullname");
                String borrowerClass = rs.getString("class");
                String borrowerRole = rs.getString("role");

                String displayStatus;
                if (returned) {
                    displayStatus = "Đã Trả";
                } else {
                    if (dueDate == null) {
                        displayStatus = "Đang Mượn";
                    } else {
                        if (today.isAfter(dueDate)) {
                            displayStatus = "Quá Hạn";
                        } else {
                            displayStatus = "Đang Mượn";
                        }
                    }
                }

                String imagePath = rs.getString("image");
                ImageView imageView = getImageView(imagePath);

                Loan loan = new Loan(
                        loanId,
                        personId,
                        bookId,
                        borrowDate,
                        dueDate,
                        returnDate,
                        returned,
                        dbStatus,
                        bookTitle,
                        bookAuthor,
                        borrowerName,
                        borrowerClass,
                        borrowerRole,
                        imageView
                );
                loan.setStatus(displayStatus);
                loanList.add(loan);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loanList;
    }

    @Override
    public void borrowBook(String personId, int bookId, int days) throws Exception {
        String loanQuery = "INSERT INTO loans (person_id, book_id, borrow_date, due_date, returned, status) VALUES (?, ?, ?, ?, 0, 'borrowed')";
        String updateBookQuery = "UPDATE books SET available = available - 1 WHERE id = ?";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmtLoan = conn.prepareStatement(loanQuery);
             PreparedStatement pstmtUpdateBook = conn.prepareStatement(updateBookQuery)) {

            conn.setAutoCommit(false);

            LocalDate borrowDate = LocalDate.now();
            LocalDate dueDate = borrowDate.plusDays(days);

            // Thêm vào loans
            pstmtLoan.setString(1, personId);
            pstmtLoan.setInt(2, bookId);
            pstmtLoan.setDate(3, Date.valueOf(borrowDate));
            pstmtLoan.setDate(4, Date.valueOf(dueDate));
            pstmtLoan.executeUpdate();

            // Cập nhật số lượng sách
            pstmtUpdateBook.setInt(1, bookId);
            pstmtUpdateBook.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Đã xảy ra lỗi khi mượn sách.");
        }
    }

    @Override
    public void returnBook(int loanId) throws Exception {
        String returnQuery = "UPDATE loans SET returned = 1, return_date = ?, status = 'returned' WHERE id = ?";
        String updateBookQuery = "UPDATE books SET available = available + 1 WHERE id = (SELECT book_id FROM loans WHERE id = ?)";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmtReturn = conn.prepareStatement(returnQuery);
             PreparedStatement pstmtUpdateBook = conn.prepareStatement(updateBookQuery)) {

            conn.setAutoCommit(false);

            LocalDate returnDate = LocalDate.now();

            // Cập nhật trạng thái mượn
            pstmtReturn.setDate(1, Date.valueOf(returnDate));
            pstmtReturn.setInt(2, loanId);
            pstmtReturn.executeUpdate();

            // Cập nhật số lượng sách
            pstmtUpdateBook.setInt(1, loanId);
            pstmtUpdateBook.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Đã xảy ra lỗi khi trả sách.");
        }
    }

    @Override
    public void extendLoan(int loanId, int days) throws Exception {
        String query = "UPDATE loans SET due_date = due_date + INTERVAL ? DAY, status = 'Đang Mượn' WHERE id = ?";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, days);
            pstmt.setInt(2, loanId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new Exception("Không tìm thấy lượt mượn để gia hạn.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Đã xảy ra lỗi khi gia hạn sách.");
        }
    }

    // Helper method to get ImageView
    private ImageView getImageView(String imagePath) {
        ImageView imageView = null;
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            try {
                Image image = new Image(imagePath, 50, 50, false, true);
                imageView = new ImageView(image);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid image URL: " + imagePath);
                imageView = getDefaultImageView();
            }
        } else {
            imageView = getDefaultImageView();
        }
        return imageView;
    }

    // Method to get default ImageView
    private ImageView getDefaultImageView() {
        String defaultImagePath = "/images/default_book.png"; // Đảm bảo bạn có hình ảnh này trong thư mục resources
        URL defaultImageURL = getClass().getResource(defaultImagePath);
        if (defaultImageURL != null) {
            try {
                Image defaultImage = new Image(defaultImageURL.toExternalForm(), 50, 50, false, true);
                return new ImageView(defaultImage);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid default image URL: " + defaultImagePath);
            }
        } else {
            System.err.println("Default image not found at " + defaultImagePath);
        }
        return null;
    }
}
