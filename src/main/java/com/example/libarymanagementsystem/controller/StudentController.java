package com.example.libarymanagementsystem.controller;

import com.example.libarymanagementsystem.model.Book;
import com.example.libarymanagementsystem.model.BookItem;
import com.example.libarymanagementsystem.model.GetData;
import com.example.libarymanagementsystem.utils.ConnectionJDBCUtils;
import com.example.libarymanagementsystem.utils.GoogleBooksService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class StudentController {

    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private Button borrowButton;
    @FXML
    private Button returnButton;
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, String> bookTitleColumn;
    @FXML
    private TableColumn<Book, String> bookAuthorColumn;
    @FXML
    private TableColumn<Book, Integer> bookAvailableColumn;
    @FXML
    private TableView<Book> borrowedBooksTable;
    @FXML
    private TableColumn<Book, String> borrowedBookTitleColumn;
    @FXML
    private TableColumn<Book, String> borrowedBookAuthorColumn;
    @FXML
    private TableColumn<Book, LocalDate> borrowedBookBorrowDateColumn;
    @FXML
    private ImageView bookImage;
    @FXML
    private ImageView borrowedBookImage;

    @FXML
    private AnchorPane availableBooks_form;

    @FXML
    private AnchorPane savedBook_form;

    @FXML
    private Button availableBooks_btn;

    @FXML
    private Button savedBooks_btn;
    @FXML
    private AnchorPane googleBooks_form;

    @FXML
    private TextField googleBooksSearchField;

    @FXML
    private Button googleBooksSearchButton;

    @FXML
    private TableView<BookItem> googleBooksTableView;

    @FXML
    private TableColumn<BookItem, String> googleBookTitleColumn;

    @FXML
    private TableColumn<BookItem, String> googleBookAuthorsColumn;

    @FXML
    private TableColumn<BookItem, String> googleBookPublisherColumn;

    @FXML
    private TableColumn<BookItem, String> googleBookLinkColumn;

    @FXML
    private Button googleBooks_btn;

    private ObservableList<BookItem> googleBooksList = FXCollections.observableArrayList();

    private ObservableList<Book> availableBooks = FXCollections.observableArrayList();
    private ObservableList<Book> borrowedBooks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Cấu hình bảng sách Google Books
        googleBookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        googleBookAuthorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
        googleBookPublisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        googleBookLinkColumn.setCellValueFactory(new PropertyValueFactory<>("infoLink"));
        googleBooksTableView.setItems(googleBooksList);

        // Cấu hình cột cho bảng sách có sẵn sử dụng PropertyValueFactory
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        bookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        // Cấu hình cột cho bảng sách đang mượn sử dụng PropertyValueFactory
        borrowedBookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        borrowedBookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        borrowedBookBorrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));

        // Gán danh sách vào bảng
        bookTable.setItems(availableBooks);
        borrowedBooksTable.setItems(borrowedBooks);

        // Tải dữ liệu ban đầu
        loadAvailableBooks();
        loadBorrowedBooks();

        // Xử lý sự kiện khi chọn một cuốn sách từ bảng sách có sẵn
        bookTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedBook) -> {
            if (selectedBook != null && selectedBook.getImageView() != null) {
                bookImage.setImage(selectedBook.getImageView().getImage());
            } else {
                bookImage.setImage(null); // Không có ảnh
            }
        });

        // Xử lý sự kiện khi chọn một cuốn sách từ bảng sách đang mượn
        borrowedBooksTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedBorrowedBook) -> {
            if (selectedBorrowedBook != null && selectedBorrowedBook.getImageView() != null) {
                borrowedBookImage.setImage(selectedBorrowedBook.getImageView().getImage());
            } else {
                borrowedBookImage.setImage(null); // Không có ảnh
            }
        });
    }

    @FXML
    private void handleGoogleBooksSearch() {
        String query = googleBooksSearchField.getText();
        List<BookItem> books = GoogleBooksService.searchBooks(query);

        googleBooksTableView.getItems().clear();
        googleBooksTableView.getItems().addAll(books);
    }

    private void loadAvailableBooks() {
        availableBooks.clear();
        try (Connection conn = ConnectionJDBCUtils.getConnection();
             Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM books WHERE available > 0";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                // Tạo ImageView từ đường dẫn hình ảnh
                ImageView imageView = null;
                String imagePath = rs.getString("image");
                if (imagePath != null && !imagePath.isEmpty()) {
                    Image image = new Image(imagePath, 100, 150, true, true); // Điều chỉnh kích thước nếu cần
                    imageView = new ImageView(image);
                }

                // Tạo đối tượng Book với loanId = 0 (không mượn)
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("available"),
                        imageView
                );
                availableBooks.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBorrowedBooks() {
        borrowedBooks.clear();
        String personId = GetData.getUsername(); // Sử dụng mã người dùng hiện tại

        String query = "SELECT loans.id AS loan_id, books.id AS book_id, books.title, books.author, books.image, loans.borrow_date " +
                "FROM loans INNER JOIN books ON loans.book_id = books.id " +
                "WHERE loans.person_id = ? AND loans.returned = 0";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, personId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Tạo ImageView từ đường dẫn hình ảnh
                ImageView imageView = null;
                String imagePath = rs.getString("image");
                if (imagePath != null && !imagePath.isEmpty()) {
                    Image image = new Image(imagePath, 100, 150, true, true); // Điều chỉnh kích thước nếu cần
                    imageView = new ImageView(image);
                }

                // Tạo đối tượng Book với loanId
                Book book = new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        0, // Số lượng hiện tại không quan trọng trong bảng mượn
                        imageView,
                        rs.getInt("loan_id") // Gán loanId
                );
                book.setBorrowDate(rs.getDate("borrow_date").toLocalDate()); // Thiết lập thời gian mượn
                borrowedBooks.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchAction() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadAvailableBooks();
            return;
        }

        availableBooks.clear();
        String query = "SELECT * FROM books WHERE (title LIKE ? OR author LIKE ?) AND available > 0";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + searchText + "%");
            stmt.setString(2, "%" + searchText + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Tạo ImageView từ đường dẫn hình ảnh
                ImageView imageView = null;
                String imagePath = rs.getString("image");
                if (imagePath != null && !imagePath.isEmpty()) {
                    Image image = new Image(imagePath, 100, 150, true, true); // Điều chỉnh kích thước nếu cần
                    imageView = new ImageView(image);
                }

                // Tạo đối tượng Book với loanId = 0 (không mượn)
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("available"),
                        imageView
                );
                availableBooks.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBorrowAction() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Chọn sách", "Vui lòng chọn sách để mượn.");
            return;
        }

        String personId = GetData.getUsername();

        try (Connection conn = ConnectionJDBCUtils.getConnection()) {
            conn.setAutoCommit(false);

            // Thêm bản ghi vào bảng loans
            String loanQuery = "INSERT INTO loans (person_id, book_id, borrow_date, returned, status) VALUES (?, ?, ?, 0, 'borrowed')";
            try (PreparedStatement stmt = conn.prepareStatement(loanQuery)) {
                stmt.setString(1, personId);
                stmt.setInt(2, selectedBook.getId());
                stmt.setDate(3, Date.valueOf(LocalDate.now()));
                stmt.executeUpdate();
            }

            // Cập nhật số lượng sách
            String updateBookQuery = "UPDATE books SET available = available - 1 WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateBookQuery)) {
                stmt.setInt(1, selectedBook.getId());
                stmt.executeUpdate();
            }

            conn.commit();
            loadAvailableBooks();
            loadBorrowedBooks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReturnAction() {
        Book selectedBook = borrowedBooksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Chọn sách", "Vui lòng chọn sách để trả.");
            return;
        }

        String personId = GetData.getUsername();
        int loanId = selectedBook.getLoanId();

        try (Connection conn = ConnectionJDBCUtils.getConnection()) {
            conn.setAutoCommit(false);

            // Cập nhật bản ghi mượn đã trả dựa trên loanId
            String updateLoanQuery = "UPDATE loans SET returned = 1, return_date = ?, status = 'returned' WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateLoanQuery)) {
                stmt.setDate(1, Date.valueOf(LocalDate.now()));
                stmt.setInt(2, loanId);
                stmt.executeUpdate();
            }

            // Cập nhật số lượng sách trong bảng books
            String updateBookQuery = "UPDATE books SET available = available + 1 WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateBookQuery)) {
                stmt.setInt(1, selectedBook.getId());
                stmt.executeUpdate();
            }

            conn.commit();
            loadAvailableBooks();
            loadBorrowedBooks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void navButtonDesign(ActionEvent event) {
        availableBooks_form.setVisible(false);
        savedBook_form.setVisible(false);
        googleBooks_form.setVisible(false);
        if (event.getSource() == availableBooks_btn) {
            availableBooks_form.setVisible(true);
        } else if (event.getSource() == savedBooks_btn) {
            savedBook_form.setVisible(true);
        } else if (event.getSource() == googleBooks_btn) {
            googleBooks_form.setVisible(true);
        }
    }
}
