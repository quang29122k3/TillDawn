package com.example.libarymanagementsystem.service;

import com.example.libarymanagementsystem.model.Book;
import com.example.libarymanagementsystem.utils.ConnectionJDBCUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookServiceImpl implements BookService {

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int available = rs.getInt("available");
                int totalCopies = rs.getInt("total_copies");
                String imagePath = rs.getString("image"); // Đường dẫn hình ảnh

                Book book = new Book(id, title, author, available, imagePath);
                book.setTotalCopies(totalCopies);

                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    @Override
    public void addBook(Book book) throws Exception {
        String query = "INSERT INTO books (title, author, available, total_copies, image) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setInt(3, book.getAvailable());
            pstmt.setInt(4, book.getTotalCopies());
            pstmt.setString(5, book.getImagePath());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Không thể thêm sách.");
        }
    }

    @Override
    public void updateBook(Book book) throws Exception {
        String query = "UPDATE books SET title = ?, author = ?, available = ?, total_copies = ?, image = ? WHERE id = ?";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setInt(3, book.getAvailable());
            pstmt.setInt(4, book.getTotalCopies());
            pstmt.setString(5, book.getImagePath());
            pstmt.setInt(6, book.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new Exception("Không tìm thấy sách để cập nhật.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Đã xảy ra lỗi khi cập nhật sách.");
        }
    }

    @Override
    public void deleteBook(int bookId) throws Exception {
        String deleteLoansQuery = "DELETE FROM loans WHERE book_id = ?";
        String deleteBookQuery = "DELETE FROM books WHERE id = ?";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmtDeleteLoans = conn.prepareStatement(deleteLoansQuery);
             PreparedStatement pstmtDeleteBook = conn.prepareStatement(deleteBookQuery)) {

            // Xóa các bản ghi trong bảng loans liên quan đến cuốn sách
            pstmtDeleteLoans.setInt(1, bookId);
            pstmtDeleteLoans.executeUpdate();

            // Xóa sách trong bảng books
            pstmtDeleteBook.setInt(1, bookId);
            int rowsAffected = pstmtDeleteBook.executeUpdate();

            if (rowsAffected == 0) {
                throw new Exception("Không tìm thấy sách để xóa.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Đã xảy ra lỗi khi xóa sách.");
        }
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int available = rs.getInt("available");
                int totalCopies = rs.getInt("total_copies");
                String imagePath = rs.getString("image");

                Book book = new Book(id, title, author, available, imagePath);
                book.setTotalCopies(totalCopies);

                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }
}