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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class StudentController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Book> bookTableView;
    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, Integer> availableColumn;

    @FXML
    private TableColumn<Book, ImageView> imageColumn;
    @FXML
    private TableView<Book> borrowedBooksTable;
    @FXML
    private TableColumn<Book, String> borrowedBookTitleColumn;
    @FXML
    private TableColumn<Book, String> borrowedBookAuthorColumn;
    @FXML
    private TableColumn<Book, LocalDate> borrowedBookBorrowDateColumn;
    @FXML
    private AnchorPane availableBooks_form;

    @FXML
    private AnchorPane savedBook_form;

    @FXML
    private Button availableBooks_btn;

    @FXML
    private AnchorPane googleBooks_form;

    @FXML
    private TextField googleBooksSearchField;


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
    private Button logout;

    @FXML
    private Text userName;

    private ObservableList<BookItem> googleBooksList = FXCollections.observableArrayList();

    private ObservableList<Book> availableBooks = FXCollections.observableArrayList();
    private ObservableList<Book> borrowedBooks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        userName.setText(GetData.getFullName());
        // Cấu hình bảng sách Google Books
        googleBookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        googleBookAuthorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
        googleBookPublisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        googleBookLinkColumn.setCellValueFactory(new PropertyValueFactory<>("infoLink"));
        googleBooksTableView.setItems(googleBooksList);

        // Cấu hình cột cho bảng sách có sẵn sử dụng PropertyValueFactory
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imageView"));

        // Cấu hình cột cho bảng sách đang mượn sử dụng PropertyValueFactory
        borrowedBookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        borrowedBookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        borrowedBookBorrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));

        // Gán danh sách vào bảng
        bookTableView.setItems(availableBooks);


        // Tải dữ liệu ban đầu
        loadBooks();
        loadBorrowedBooks();

    }

    @FXML
    private void handleGoogleBooksSearch() {
        String query = googleBooksSearchField.getText();
        List<BookItem> books = GoogleBooksService.searchBooks(query);

        googleBooksTableView.getItems().clear();
        googleBooksTableView.getItems().addAll(books);
    }

    public void loadBooks() {
        ObservableList<Book> bookList = FXCollections.observableArrayList();

        String query = "SELECT * FROM books";
        try (Connection conn = ConnectionJDBCUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int available = rs.getInt("available");
                String imagePath = rs.getString("image"); // Đường dẫn hình ảnh

                // Tạo ImageView từ imagePath
                ImageView imageView = null;
                if (imagePath != null && !imagePath.trim().isEmpty()) {
                    try {
                        Image image = new Image(imagePath, 50, 50, false, true);
                        imageView = new ImageView(image);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid image URL for book ID " + id + ": " + imagePath);
                        // Sử dụng hình ảnh mặc định nếu URL không hợp lệ
                        imageView = getDefaultImageView();
                    }
                } else {
                    // Sử dụng hình ảnh mặc định nếu không có imagePath
                    imageView = getDefaultImageView();
                }

                // Tạo đối tượng Book sử dụng constructor đúng
                Book book = new Book(id, title, author, available, imageView);
                // Thiết lập totalCopies
                bookList.add(book);
            }

            // Đặt dữ liệu vào TableView
            bookTableView.setItems(bookList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBorrowedBooks() {
        borrowedBooks.clear();
        String query = "SELECT loans.id AS loan_id, books.id AS book_id, books.title, books.author, loans.borrow_date " +
                "FROM loans INNER JOIN books ON loans.book_id = books.id " +
                "WHERE loans.returned = 0";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        0, // Không cần quan tâm số lượng
                        null
                );
                book.setLoanId(rs.getInt("loan_id"));
                book.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                borrowedBooks.add(book);
            }
            borrowedBooksTable.setItems(borrowedBooks);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Phương thức lấy đối tượng ImageView mặc định
     */
    private ImageView getDefaultImageView() {
        String defaultImagePath = "/images/manager_avatar.png";
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

    @FXML
    private void handleSearchAction() {
        String searchText = searchField.getText().trim();
        ObservableList<Book> bookList = FXCollections.observableArrayList();

        // Nếu thanh tìm kiếm trống, hiển thị tất cả sách
        String query;
        if (searchText.isEmpty()) {
            query = "SELECT * FROM books";
        } else {
            query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        }

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            if (!searchText.isEmpty()) {
                pstmt.setString(1, "%" + searchText + "%");
                pstmt.setString(2, "%" + searchText + "%");
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int available = rs.getInt("available");
                String imagePath = rs.getString("image");

                // Tạo ImageView từ imagePath
                ImageView imageView = null;
                if (imagePath != null && !imagePath.trim().isEmpty()) {
                    try {
                        Image image = new Image(imagePath, 50, 50, false, true);
                        imageView = new ImageView(image);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid image URL for book ID " + id + ": " + imagePath);
                        imageView = getDefaultImageView(); // Hình mặc định
                    }
                } else {
                    imageView = getDefaultImageView(); // Hình mặc định
                }

                // Tạo đối tượng Book
                Book book = new Book(id, title, author, available, imageView);
                bookList.add(book);
            }

            // Đặt dữ liệu vào TableView
            bookTableView.setItems(bookList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBorrowAction() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
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
            loadBooks();
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
            loadBooks();
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
    private Button savedBooks_btn;

    @FXML
    private Button googleBooksButton;


    @FXML
    private void navButtonDesign(ActionEvent event) {
        availableBooks_form.setVisible(false);
        savedBook_form.setVisible(false);
        googleBooks_form.setVisible(false);
        if (event.getSource() == availableBooks_btn) {
            availableBooks_form.setVisible(true);
        } else if (event.getSource() == savedBooks_btn) {
            savedBook_form.setVisible(true);
        } else if (event.getSource() == googleBooksButton) {
            googleBooks_form.setVisible(true);
        }
    }

    private double x = 0;
    private double y = 0;

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            if (event.getSource() == logout) {
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/libarymanagementsystem/hello-view.fxml"));

                Stage stage = new Stage();
                Scene scene = new Scene(root);

                root.setOnMousePressed((MouseEvent e) -> {
                    x = e.getSceneX();
                    y = e.getSceneY();

                });

                root.setOnMouseDragged((MouseEvent e) -> {
                    stage.setX(e.getScreenX() - x);
                    stage.setY(e.getScreenY() - y);
                });

                stage.initStyle(StageStyle.TRANSPARENT);

                stage.setScene(scene);
                stage.show();

                logout.getScene().getWindow().hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
