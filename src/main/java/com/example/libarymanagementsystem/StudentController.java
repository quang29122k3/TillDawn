package com.example.libarymanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentController {
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Book> booksTable;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, Integer> availableColumn;
    @FXML
    private TableColumn<Book, ImageView> imageColumn;

    private ObservableList<Book> booksData = FXCollections.observableArrayList();

    // Hàm khởi tạo các cột cho bảng sách
    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        booksTable.setItems(booksData);
    }

    // Hàm tìm kiếm sách
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        booksData.clear();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tilldawn", "root", "password")) {
            String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + keyword + "%");
            statement.setString(2, "%" + keyword + "%");

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int available = resultSet.getInt("available");
                String imagePath = resultSet.getString("image"); // Đường dẫn ảnh từ CSDL

                ImageView imageView = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);

                booksData.add(new Book(title, author, available, imageView));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm mượn sách
    @FXML
    private void borrowBook() {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null && selectedBook.getAvailable() > 0) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tilldawn", "root", "password")) {
                String updateBookQuery = "UPDATE books SET available = available - 1 WHERE title = ?";
                PreparedStatement updateBookStmt = connection.prepareStatement(updateBookQuery);
                updateBookStmt.setString(1, selectedBook.getTitle());
                updateBookStmt.executeUpdate();

                String insertLoanQuery = "INSERT INTO loans (person_id, book_id, borrow_date) VALUES (?, ?, CURDATE())";
                PreparedStatement insertLoanStmt = connection.prepareStatement(insertLoanQuery);
                insertLoanStmt.setString(1, "student_id"); // ID sinh viên (lấy từ session hoặc thông tin đăng nhập)
                insertLoanStmt.setInt(2, selectedBook.getId());
                insertLoanStmt.executeUpdate();

                selectedBook.setAvailable(selectedBook.getAvailable() - 1);
                booksTable.refresh();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    // Hàm trả sách
//    @FXML
//    private void returnBook() {
//        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
//        if (selectedBook != null) {
//            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tilldawn", "root", "password")) {
//                String updateBookQuery = "UPDATE books SET available = available + 1 WHERE title = ?";
//                PreparedStatement updateBookStmt = connection.prepareStatement(updateBookQuery);
//                updateBookStmt.setString(1, selectedBook.getTitle());
//                updateBookStmt.executeUpdate();
//
//                String updateLoanQuery = "UPDATE loans SET return_date = CURDATE(), returned = 1 WHERE book_id = ? AND person_id = ? AND returned = 0";
//                PreparedStatement updateLoanStmt = connection.prepareStatement(updateLoanQuery);
//                updateLoanStmt.setInt(1, selectedBook.getId());
//                updateLoanStmt.setString(2, student.getId()); // ID sinh viên (lấy từ session hoặc thông tin đăng nhập)
//                updateLoanStmt.executeUpdate();
//
//                selectedBook.setAvailable(selectedBook.getAvailable() + 1);
//                booksTable.refresh();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
