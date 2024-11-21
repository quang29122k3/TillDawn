package com.example.libarymanagementsystem;

import com.example.libarymanagementsystem.utils.ConnectionJDBCUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.input.MouseEvent;


import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class DashBoardControllerManager {
    @FXML
    private Button minimizeButton;
    @FXML
    private Button logout;
    @FXML
    private Button closeButton;

    @FXML
    private TableView<Book> bookTableView;

    @FXML
    private TableColumn<Book, Integer> idColumn;

    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, Integer> availableColumn;

    @FXML
    private TableColumn<Book, Integer> totalCopiesColumn;

    @FXML
    private TableColumn<Book, ImageView> imageColumn;
    @FXML
    private AnchorPane availableBooks_form;

//    @FXML
//    private AnchorPane issue_form;
//
//    @FXML
//    private AnchorPane returnBook_form;

    @FXML
    private AnchorPane savedBook_form;

    // Các nút điều hướng
    @FXML
    private Button availableBooks_btn;

    @FXML
    private Button issueBooks_btn;

    @FXML
    private Button returnBooks_btn;

    @FXML
    private Button savedBooks_btn;

    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;

    // Các phương thức xử lý sự kiện
    @FXML
    private void navButtonDesign(ActionEvent event) {
        if (event.getSource() == availableBooks_btn) {
            showForm("availableBooks_form");
        } else if (event.getSource() == issueBooks_btn) {
            showForm("issue_form");
        } else if (event.getSource() == returnBooks_btn) {
            showForm("returnBook_form");
        } else if (event.getSource() == savedBooks_btn) {
            showForm("savedBook_form");
        }
    }

    private double x = 0;
    private double y = 0;

    private void showForm(String formName) {
        availableBooks_form.setVisible(false);
//        issue_form.setVisible(false);
//        returnBook_form.setVisible(false);
        savedBook_form.setVisible(false);

        switch (formName) {
            case "availableBooks_form":
                availableBooks_form.setVisible(true);
                break;
//            case "issue_form":
//                issue_form.setVisible(true);
//                break;
//            case "returnBook_form":
//                returnBook_form.setVisible(true);
//                break;
            case "savedBook_form":
                savedBook_form.setVisible(true);
                break;
        }
    }

    @FXML
    private void minimizeWindow() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private TableView<Book> borrowedBooksTable;

    @FXML
    private ImageView managerAvatar;

    @FXML
    private Label managerName;

    @FXML
    private TableColumn<Book, String> borrowedBookTitleColumn;
    @FXML
    private TableColumn<Book, String> borrowedBookAuthorColumn;
    @FXML
    private TableColumn<Book, LocalDate> borrowedBookBorrowDateColumn;

    @FXML
    public void initialize() {
        managerName.setText("Tên Người Quản Lý");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        totalCopiesColumn.setCellValueFactory(new PropertyValueFactory<>("totalCopies"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imageView"));

        // Cấu hình bảng sách đã mượn
        borrowedBookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        borrowedBookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        borrowedBookBorrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));


        showForm("availableBooks_form");

        // Tải tất cả sách ban đầu
        loadBooks();
        loadBorrowedBooks();

        // Gán sự kiện cho nút tìm kiếm
        searchButton.setOnAction(event -> handleSearchAction());
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            if (event.getSource() == logout) {
                Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));

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
        } catch ( Exception e){
            e.printStackTrace();
        }
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
                int totalCopies = rs.getInt("total_copies");
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
                book.setTotalCopies(totalCopies); // Gán totalCopies
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
            showAlert("Chọn sách Vui lòng chọn sách để mượn.");
            return;
        }

        try (Connection conn = ConnectionJDBCUtils.getConnection()) {
            conn.setAutoCommit(false);
            String personId = GetData.username; // Sử dụng mã người dùng hiện tại

            // Thêm bản ghi mượn vào loans
            String loanQuery = "INSERT INTO loans (person_id, book_id, borrow_date, returned, status) VALUES (?, ?, ?, 0, 'borrowed')";
            try (PreparedStatement stmt = conn.prepareStatement(loanQuery)) {
                stmt.setString(1, personId); // ID của người quản lý
                stmt.setInt(2, selectedBook.getId());
                stmt.setDate(3, Date.valueOf(LocalDate.now()));
                stmt.executeUpdate();
            }

            // Giảm số lượng sách có sẵn
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
            showAlert("Chọn sách vui lòng chọn sách để trả.");
            return;
        }

        try (Connection conn = ConnectionJDBCUtils.getConnection()) {
            conn.setAutoCommit(false);

            // Cập nhật trạng thái mượn
            String returnQuery = "UPDATE loans SET returned = 1, return_date = ?, status = 'returned' WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(returnQuery)) {
                stmt.setDate(1, Date.valueOf(LocalDate.now()));
                stmt.setInt(2, selectedBook.getLoanId());
                stmt.executeUpdate();
            }

            // Tăng số lượng sách có sẵn
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
                int totalCopies = rs.getInt("total_copies");
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
                book.setTotalCopies(totalCopies); // set totalCopies riêng biệt

                bookList.add(book);
            }

            // Đặt dữ liệu vào TableView
            bookTableView.setItems(bookList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<Book> borrowedBooks = FXCollections.observableArrayList();

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

    public void addBook(Book book) {
        String query = "INSERT INTO books (title, author, available, total_copies, image) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setInt(3, book.getAvailable());
            pstmt.setInt(4, book.getTotalCopies());
            // Lấy đường dẫn hình ảnh từ ImageView
            String imagePath = book.getImageView().getImage().getUrl();
            pstmt.setString(5, imagePath);

            pstmt.executeUpdate();

            // Cập nhật TableView
            loadBooks();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBook(Book book) {
        String query = "UPDATE books SET title = ?, author = ?, available = ?, total_copies = ?, image = ? WHERE id = ?";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setInt(3, book.getAvailable());
            pstmt.setInt(4, book.getTotalCopies());
            // Lấy đường dẫn hình ảnh từ ImageView
            String imagePath = book.getImageView().getImage().getUrl();
            pstmt.setString(5, imagePath);
            pstmt.setInt(6, book.getId());

            pstmt.executeUpdate();

            // Cập nhật TableView
            loadBooks();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBook(int bookId) {
        String query = "DELETE FROM books WHERE id = ?";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, bookId);

            pstmt.executeUpdate();

            // Cập nhật TableView
            loadBooks();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddBook() {
        // Mở cửa sổ thêm sách
        showBookForm(null);
    }

    @FXML
    private void handleEditBook() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            showBookForm(selectedBook);
        } else {
            showAlert("Vui lòng chọn sách để chỉnh sửa.");
        }
    }

    @FXML
    private void handleDeleteBook() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText("Bạn có chắc chắn muốn xóa sách này không?");
            alert.setContentText("Sách: " + selectedBook.getTitle());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                deleteBook(selectedBook.getId());
            }
        } else {
            showAlert("Vui lòng chọn sách để xóa.");
        }
    }

    private void showBookForm(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BookForm.fxml"));
            Parent root = loader.load();

            BookFormController controller = loader.getController();
            controller.setBook(book); // Truyền đối tượng Book (null nếu thêm mới)
            controller.setMainController(this); // Thiết lập tham chiếu đến controller chính

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(book == null ? "Thêm Sách" : "Chỉnh Sửa Sách");
            stage.showAndWait();

            // Sau khi đóng form, tải lại danh sách sách
            loadBooks();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}