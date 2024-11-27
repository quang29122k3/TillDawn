package com.example.libarymanagementsystem.controller;

import com.example.libarymanagementsystem.model.Book;
import com.example.libarymanagementsystem.model.BookItem;
import com.example.libarymanagementsystem.model.GetData;
import com.example.libarymanagementsystem.model.Person;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.input.MouseEvent;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

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

    @FXML
    private AnchorPane member_form;

    @FXML
    private TextField searchMemberField;

    @FXML
    private Button searchMemberButton;

    @FXML
    private TableView<Person> memberTableView;

    @FXML
    private TableColumn<Person, String> memberIdColumn;

    @FXML
    private TableColumn<Person, String> memberNameColumn;

    @FXML
    private TableColumn<Person, String> memberClassColumn;

    @FXML
    private TableColumn<Person, String> memberStatusColumn;

    @FXML
    private Button memberButton;

    @FXML
    private Button blockMemberButton;

    @FXML
    private Button unblockMemberButton;

    @FXML
    private Button googleBooksButton;

    @FXML
    private AnchorPane userInfoPane;

    @FXML
    private Button userIconButton;

    // Các phương thức xử lý sự kiện
    @FXML
    private void navButtonDesign(ActionEvent event) {
        availableBooks_form.setVisible(false);
        savedBook_form.setVisible(false);
        member_form.setVisible(false);
        googleBooks_form.setVisible(false);
        userInfoPane.setVisible(false);

        if (event.getSource() == googleBooksButton) {
            googleBooks_form.setVisible(true);
        }
        else if (event.getSource() == availableBooks_btn) {
            availableBooks_form.setVisible(true);
        } else if (event.getSource() == savedBooks_btn) {
            savedBook_form.setVisible(true);
        } else if (event.getSource() == memberButton) { // memberButton là ID của nút "Thành viên"
            member_form.setVisible(true);
            loadMembers(); // Tải danh sách thành viên
        }
        else if(event.getSource()==userIconButton){
            userInfoPane.setVisible(true);
            loadUserInfo();
        }
    }

    private ObservableList<Person> members = FXCollections.observableArrayList();

    private void loadMembers() {
        members.clear();
        String query = "SELECT id, fullname, class, is_active FROM person";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String fullname = rs.getString("fullname");
                String className = rs.getString("class");
                boolean isActive = rs.getBoolean("is_active");

                String status = isActive ? "Kích Hoạt" : "Bị Chặn";
                members.add(new Person(id, fullname, className, status));
            }

            memberTableView.setItems(members);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private double x = 0;
    private double y = 0;

    private void showForm(String formName) {
        availableBooks_form.setVisible(false);
        savedBook_form.setVisible(false);

        switch (formName) {
            case "availableBooks_form":
                availableBooks_form.setVisible(true);
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
    private TableView<Book> borrowedBooksTable;

    @FXML
    private ImageView managerAvatar;

    @FXML
    private Text managerName;

    @FXML
    private TableColumn<Book, String> borrowedBookTitleColumn;
    @FXML
    private TableColumn<Book, String> borrowedBookAuthorColumn;
    @FXML
    private TableColumn<Book, LocalDate> borrowedBookBorrowDateColumn;

    @FXML
    public void initialize() {
        managerName.setText(GetData.getFullName());
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

        // Cấu hình cột cho bảng thành viên
        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        memberClassColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        memberStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        // Cấu hình bảng tra cứu google
        googleBookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        googleBookAuthorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
        googleBookPublisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        googleBookLinkColumn.setCellValueFactory(new PropertyValueFactory<>("infoLink"));

        googleBooksTableView.setItems(googleBooksList);

        // Cấu hình sự kiện tìm kiếm thành viên
        searchMemberButton.setOnAction(event -> handleSearchMemberAction());


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
    private void handleSearchMemberAction() {
        String searchText = searchMemberField.getText().trim();
        members.clear();

        String query = "SELECT id, fullname, class, is_active FROM person WHERE id LIKE ? OR fullname LIKE ? OR class LIKE ?";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchText + "%");
            stmt.setString(2, "%" + searchText + "%");
            stmt.setString(3, "%" + searchText + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String fullname = rs.getString("fullname");
                String className = rs.getString("class");
                boolean isActive = rs.getBoolean("is_active");

                String status = isActive ? "Kích Hoạt" : "Bị Chặn";
                members.add(new Person(id, fullname, className, status));
            }

            memberTableView.setItems(members);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBlockMemberAction() {
        Person selectedMember = memberTableView.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showAlert("Lỗi, vui lòng chọn thành viên để chặn.");
            return;
        }

        String query = "UPDATE person SET is_active = 0 WHERE id = ?";
        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, selectedMember.getId());
            stmt.executeUpdate();

            showAlert("Thành công, thành viên đã bị chặn.");
            loadMembers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUnblockMemberAction() {
        Person selectedMember = memberTableView.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showAlert("Lỗi, vui lòng chọn thành viên để mở chặn.");
            return;
        }

        String query = "UPDATE person SET is_active = 1 WHERE id = ?";
        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, selectedMember.getId());
            stmt.executeUpdate();

            showAlert("Thành công, thành viên đã được mở chặn.");
            loadMembers();
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

        // Kiểm tra nếu số lượng sách hiện có bằng 0
        if (selectedBook.getAvailable() == 0) {
            showAlert("Sách \"" + selectedBook.getTitle() + "\" đã hết. Không thể mượn sách này.");
            return; // Dừng tại đây nếu sách đã hết
        }

        try (Connection conn = ConnectionJDBCUtils.getConnection()) {
            conn.setAutoCommit(false);
            String personId = GetData.getUsername(); // Sử dụng mã người dùng hiện tại

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/libarymanagementsystem/BookForm.fxml"));
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

    private ObservableList<BookItem> googleBooksList = FXCollections.observableArrayList();

    @FXML
    private void handleGoogleBooksSearch() {
        String query = googleBooksSearchField.getText();
        List<BookItem> books = GoogleBooksService.searchBooks(query);

        googleBooksTableView.getItems().clear();
        googleBooksTableView.getItems().addAll(books);
    }

    //INFORPANE
    //test
    @FXML
    private TextField userIdField;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField classField;
    @FXML
    private TextField roleField;
    @FXML
    private Button saveButton;

    @FXML
    private TextField emailField;

    // Xử lý khi nhấn vào nút User
    private void loadUserInfo() {
        String query = "SELECT p.id, p.fullname, p.class, p.email, r.name AS role\n" +
                "FROM person p\n" +
                "JOIN role r ON p.role_id = r.id\n" +
                "WHERE p.id = ?;";
        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String currentUserId = GetData.getUsername(); // Đảm bảo phương thức này trả về ID người dùng hiện tại
            System.out.println("Current User ID: " + currentUserId); // Debug

            pstmt.setString(1, currentUserId);
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                System.out.println("User found: " + resultSet.getString("fullname")); // Debug
                userIdField.setText(resultSet.getString("id"));
                fullNameField.setText(resultSet.getString("fullname"));
                classField.setText(resultSet.getString("class"));
                roleField.setText(resultSet.getString("role"));
                emailField.setText(resultSet.getString("email"));
            } else {
                System.out.println("No user found with ID: " + currentUserId); // Debug
                showAlert( "Không tìm thấy thông tin người dùng.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Đã xảy ra lỗi khi tải thông tin người dùng.");
        }
    }

    // Lưu thông tin sau khi chỉnh sửa
    @FXML
    private void handleSaveAction(ActionEvent event) {
        String updateQuery = "UPDATE person SET fullname = ?, class = ?, email = ? WHERE id = ?";;
        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, fullNameField.getText());
            pstmt.setString(2, classField.getText());
            pstmt.setString(3, emailField.getText());
            pstmt.setString(4, userIdField.getText());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                showAlert( "Thông tin đã được cập nhật.");
                fullNameField.setEditable(false);
                classField.setEditable(false);
                emailField.setEditable(false);
                saveButton.setDisable(true);
            } else {
                showAlert("Không thể cập nhật thông tin.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Đã xảy ra lỗi khi cập nhật thông tin.");
        }
    }

    // Cho phép chỉnh sửa
    @FXML
    public void handleEditAction(ActionEvent event) {
        fullNameField.setEditable(true);
        classField.setEditable(true);
        emailField.setEditable(true);
        saveButton.setDisable(false);
    }
}