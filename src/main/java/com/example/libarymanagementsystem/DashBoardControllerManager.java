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

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class DashBoardControllerManager {
    @FXML
    private Button minimizeButton;

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

    @FXML
    private AnchorPane issue_form;

    @FXML
    private AnchorPane returnBook_form;

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

    private void showForm(String formName) {
        availableBooks_form.setVisible(false);
        issue_form.setVisible(false);
        returnBook_form.setVisible(false);
        savedBook_form.setVisible(false);

        switch (formName) {
            case "availableBooks_form":
                availableBooks_form.setVisible(true);
                break;
            case "issue_form":
                issue_form.setVisible(true);
                break;
            case "returnBook_form":
                returnBook_form.setVisible(true);
                break;
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
    private ImageView managerAvatar;

    @FXML
    private Label managerName;
    @FXML
    public void initialize() {
        // Cấu hình các cột của TableView

        // Set manager's name
        managerName.setText("Tên Người Quản Lý");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        totalCopiesColumn.setCellValueFactory(new PropertyValueFactory<>("totalCopies"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        showForm("availableBooks_form");
        // Tải dữ liệu vào TableView
        loadBooks();
    }
    @FXML
    private void handleLogout() {
        // Xử lý đăng xuất
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
                Image image = new Image(imagePath, 50, 50, false, true);
                ImageView imageView = new ImageView(image);

                // Tạo đối tượng Book
                Book book = new Book(title, author, available, imageView, totalCopies);
                book.setId(id); // Thiết lập id

                bookList.add(book);
            }

            // Đặt dữ liệu vào TableView
            bookTableView.setItems(bookList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
