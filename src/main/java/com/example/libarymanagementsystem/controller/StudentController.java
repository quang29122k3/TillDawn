package com.example.libarymanagementsystem.controller;

import com.example.libarymanagementsystem.model.Book;
import com.example.libarymanagementsystem.model.BookItem;
import com.example.libarymanagementsystem.model.GetData;
import com.example.libarymanagementsystem.model.Loan;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
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
    private TableView<Loan> borrowedBooksTable; // đổi từ Book sang Loan
    @FXML
    private TableColumn<Loan, String> loanBookTitleColumn;
    @FXML
    private TableColumn<Loan, String> loanBookAuthorColumn;
    @FXML
    private TableColumn<Loan, LocalDate> loanBorrowDateColumn;
    @FXML
    private TableColumn<Loan, LocalDate> loanDueDateColumn;
    @FXML
    private TableColumn<Loan, String> loanStatusColumn;
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

    @FXML
    private Button savedBooks_btn;

    @FXML
    private Button googleBooksButton;

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
    private Button editButton;

    @FXML
    private AnchorPane userInfoPane;

    @FXML
    private Button userIconButton;
    @FXML
    private TextField emailField;

    private ObservableList<BookItem> googleBooksList = FXCollections.observableArrayList();

    private ObservableList<Book> availableBooks = FXCollections.observableArrayList();
    private ObservableList<Book> borrowedBooks = FXCollections.observableArrayList();

    /**
     * Phuong thuc cau hinh cac bang.
     */

    @FXML
    public void initialize() {
        availableBooks_form.setVisible(true);
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

        // Cấu hình bảng sách đã mượn
        loanBookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        loanBookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));
        loanBorrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        loanDueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        loanStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        // Cấu hình cột cho bảng xếp hạng top 10 sách.
        rankedImageColumn.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        rankedTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        rankedAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        rankedBorrowCountColumn.setCellValueFactory(new PropertyValueFactory<>("borrowCount"));
        rankedAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        // Gán danh sách vào bảng
        bookTableView.setItems(availableBooks);


        // Tải dữ liệu ban đầu
        loadBooks();
        loadBorrowedLoans();

    }

    /**
     * Phuong thuc tim kiem sach tren Google.
     */

    @FXML
    private void handleGoogleBooksSearch() {
        String query = googleBooksSearchField.getText();
        List<BookItem> books = GoogleBooksService.searchBooks(query);

        googleBooksTableView.getItems().clear();
        googleBooksTableView.getItems().addAll(books);
    }

    /**
     * Phuong thuc load sach ra bang sach co san.
     */

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

    /**
     * Load sach da muon.
     */

    private ObservableList<Loan> borrowedLoans = FXCollections.observableArrayList();

    private void loadBorrowedLoans() {
        borrowedLoans.clear();
        String query = "SELECT l.id AS loan_id, l.person_id, l.book_id, l.borrow_date, l.due_date, l.return_date, l.returned, l.status, b.title, b.author " +
                "FROM loans l " +
                "JOIN books b ON l.book_id = b.id " +
                "WHERE l.returned = 0";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            LocalDate now = LocalDate.now();
            while (rs.next()) {
                int loanId = rs.getInt("loan_id");
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

                Loan loan = new Loan(
                        loanId, personId, bookId, borrowDate, dueDate, returnDate,
                        returned, dbStatus, bookTitle, bookAuthor
                );

                // Tính status
                String displayStatus;
                if (returned) {
                    displayStatus = "Đã Trả";
                } else {
                    // Kiểm tra null trước khi so sánh
                    if (dueDate == null) {
                        // Nếu không có due_date, bạn có thể đặt trạng thái mặc định
                        // Ví dụ: "Đang Mượn" hoặc "Chưa xác định"
                        displayStatus = "Đang Mượn";
                    } else {
                        if (now.isAfter(dueDate)) {
                            displayStatus = "Quá Hạn";
                        } else {
                            displayStatus = "Đang Mượn";
                        }
                    }
                }
                loan.setStatus(displayStatus);

                borrowedLoans.add(loan);
            }

            borrowedBooksTable.setItems(borrowedLoans);
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

    /**
     * Phuong thuc tim kiem trong local database.
     */

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

    /**
     * Phuong thuc muon sach.
     */

    @FXML
    private void handleBorrowAction() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Loi","Vui lòng chọn sách để mượn.");
            return;
        }

        if (selectedBook.getAvailable() == 0) {
            showAlert("Loi","Sách \"" + selectedBook.getTitle() + "\" đã hết.");
            return;
        }

        // Mở dialog nhập số ngày mượn
        BorrowDurationDialogController dialogController = showBorrowDurationDialog();
        if (dialogController == null || !dialogController.isConfirmed()) {
            return;
        }

        int days = dialogController.getDays();
        if (days <= 0) {
            showAlert("Loi","Số ngày mượn phải > 0.");
            return;
        }

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(days);

        try (Connection conn = ConnectionJDBCUtils.getConnection()) {
            conn.setAutoCommit(false);
            String personId = GetData.getUsername();

            // Thêm vào loans có due_date
            String loanQuery = "INSERT INTO loans (person_id, book_id, borrow_date, due_date, returned, status) VALUES (?, ?, ?, ?, 0, 'borrowed')";
            try (PreparedStatement stmt = conn.prepareStatement(loanQuery)) {
                stmt.setString(1, personId);
                stmt.setInt(2, selectedBook.getId());
                stmt.setDate(3, Date.valueOf(borrowDate));
                stmt.setDate(4, Date.valueOf(dueDate));
                stmt.executeUpdate();
            }

            String updateBookQuery = "UPDATE books SET available = available - 1 WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateBookQuery)) {
                stmt.setInt(1, selectedBook.getId());
                stmt.executeUpdate();
            }

            conn.commit();
            loadBooks();
            loadBorrowedLoans();// Cập nhật lại trang sách có sẵn
            // Nếu bạn có trang top 10 sách, loadRankedBooks() nếu cần
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Loi","Đã xảy ra lỗi khi mượn sách.");
        }
    }

    /**
     * Phuong thuc tra sach.
     */

    @FXML
    private void handleReturnAction() {
        Loan selectedLoan = borrowedBooksTable.getSelectionModel().getSelectedItem();
        if (selectedLoan == null) {
            showAlert("Loi","Vui lòng chọn lượt mượn sách để trả.");
            return;
        }

        try (Connection conn = ConnectionJDBCUtils.getConnection()) {
            conn.setAutoCommit(false);

            // Cập nhật trạng thái mượn
            String returnQuery = "UPDATE loans SET returned = 1, return_date = ?, status = 'returned' WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(returnQuery)) {
                stmt.setDate(1, Date.valueOf(LocalDate.now()));
                stmt.setInt(2, selectedLoan.getId()); // Sử dụng getId() từ Loan thay cho getLoanId() của Book
                stmt.executeUpdate();
            }

            // Tăng số lượng sách có sẵn
            String updateBookQuery = "UPDATE books SET available = available + 1 WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateBookQuery)) {
                stmt.setInt(1, selectedLoan.getBookId()); // Sử dụng getBookId() từ Loan thay cho selectedBook.getId()
                stmt.executeUpdate();
            }

            conn.commit();
            loadBooks();
            loadBorrowedLoans(); // Gọi loadBorrowedLoans() thay cho loadBorrowedBooks()
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Phuong thuc tra ra canh bao.
     */

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Phuong thuc dieu huong giao dien.
     */
    @FXML
    private Button rankBooksButton;

    @FXML
    private AnchorPane rankedBooks_form;

    @FXML
    private void navButtonDesign(ActionEvent event) {
        availableBooks_form.setVisible(false);
        savedBook_form.setVisible(false);
        googleBooks_form.setVisible(false);
        userInfoPane.setVisible(false);
        rankedBooks_form.setVisible(false);
        if (event.getSource() == availableBooks_btn) {
            availableBooks_form.setVisible(true);
        } else if (event.getSource() == savedBooks_btn) {
            savedBook_form.setVisible(true);
        } else if (event.getSource() == googleBooksButton) {
            googleBooks_form.setVisible(true);
        }
        else if(event.getSource()==userIconButton){
            userInfoPane.setVisible(true);
            loadUserInfo();
        } else if (event.getSource() == rankBooksButton) {
            rankedBooks_form.setVisible(true);
            loadRankedBooks();
        }
    }

    /**
     * Phuong thuc dang xuat.
     */

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

    /**
     * Phuong thuc load thong tin user.
     */

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
                showAlert( "Lỗi", "Không tìm thấy thông tin người dùng.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Đã xảy ra lỗi khi tải thông tin người dùng.");
        }
    }

    /**
     * Phuong thuc luu thong tin.
     */
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
                showAlert( "Cập nhật thành công", "Thông tin đã được cập nhật.");
                fullNameField.setEditable(false);
                classField.setEditable(false);
                emailField.setEditable(false);
                saveButton.setDisable(true);
            } else {
                showAlert("Cập nhật thất bại", "Không thể cập nhật thông tin.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Đã xảy ra lỗi khi cập nhật thông tin.");
        }
    }

    /**
     * Phuong thuc chinh sua tren cac field.
     */

    // Cho phép chỉnh sửa
    @FXML
    public void handleEditAction(ActionEvent event) {
        fullNameField.setEditable(true);
        classField.setEditable(true);
        emailField.setEditable(true);
        saveButton.setDisable(false);
    }

    @FXML
    private Button borrowButton; // nút Mượn

    @FXML
    private void handleBorrowFromRankedOutsideTable(ActionEvent event) {
        Book selectedBook = rankedBooksTableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Loi","Vui lòng chọn sách để mượn.");
            return;
        }

        if (selectedBook.getAvailable() == 0) {
            showAlert("Loi","Sách \"" + selectedBook.getTitle() + "\" đã hết. Không thể mượn sách này.");
            return;
        }

        // Mở dialog nhập số ngày mượn
        BorrowDurationDialogController dialogController = showBorrowDurationDialog();
        if (dialogController == null || !dialogController.isConfirmed()) {
            return; // Người dùng hủy hoặc không nhập, không làm gì thêm
        }

        int days = dialogController.getDays();
        if (days <= 0) {
            showAlert("Loi","Số ngày mượn phải > 0.");
            return;
        }

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(days);

        try (Connection conn = ConnectionJDBCUtils.getConnection()) {
            conn.setAutoCommit(false);
            String personId = GetData.getUsername(); // Lấy ID người dùng hiện tại

            // Thêm bản ghi mượn vào loans với due_date
            String loanQuery = "INSERT INTO loans (person_id, book_id, borrow_date, due_date, returned, status) " +
                    "VALUES (?, ?, ?, ?, 0, 'borrowed')";
            try (PreparedStatement stmt = conn.prepareStatement(loanQuery)) {
                stmt.setString(1, personId);
                stmt.setInt(2, selectedBook.getId());
                stmt.setDate(3, Date.valueOf(borrowDate));
                stmt.setDate(4, Date.valueOf(dueDate));
                stmt.executeUpdate();
            }

            // Giảm số lượng sách có sẵn
            String updateBookQuery = "UPDATE books SET available = available - 1 WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateBookQuery)) {
                stmt.setInt(1, selectedBook.getId());
                stmt.executeUpdate();
            }

            conn.commit();

            // Cập nhật lại danh sách sách và sách đã mượn
            loadBooks();
            loadRankedBooks(); // Cập nhật lại top 10 sách nếu cần
            loadBorrowedLoans();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Loi","Đã xảy ra lỗi khi mượn sách.");
        }
    }
    @FXML
    private TableView<Book> rankedBooksTableView;
    @FXML
    private TableColumn<Book, String> rankedTitleColumn;
    @FXML
    private TableColumn<Book, String> rankedAuthorColumn;
    @FXML
    private TableColumn<Book, Integer> rankedBorrowCountColumn;
    @FXML
    private TableColumn<Book, Integer> rankedAvailableColumn;
    @FXML
    private TableColumn<Book, ImageView> rankedImageColumn;

    private ObservableList<Book> rankedBooksList = FXCollections.observableArrayList();

    private void loadRankedBooks() {
        rankedBooksList.clear();
        String query = "SELECT b.id, b.title, b.author, b.available, b.image, COUNT(l.book_id) AS borrow_count " +
                "FROM books b " +
                "JOIN loans l ON b.id = l.book_id " +
                "GROUP BY l.book_id " +
                "ORDER BY borrow_count DESC " +
                "LIMIT 10";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int available = rs.getInt("available");
                int borrowCount = rs.getInt("borrow_count");
                String imagePath = rs.getString("image");

                ImageView imageView = null;
                if (imagePath != null && !imagePath.trim().isEmpty()) {
                    try {
                        // Tạo Image và ImageView từ đường dẫn ảnh
                        Image image = new Image(imagePath, 50, 50, false, true);
                        imageView = new ImageView(image);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid image URL for book ID " + id + ": " + imagePath);
                        imageView = getDefaultImageView(); // Sử dụng hình mặc định nếu URL không hợp lệ
                    }
                } else {
                    imageView = getDefaultImageView(); // Sử dụng hình mặc định nếu không có imagePath
                }

                // Tạo đối tượng Book với imageView
                Book book = new Book(id, title, author, available, imageView);
                book.setBorrowCount(borrowCount);

                rankedBooksList.add(book);
            }

            rankedBooksTableView.setItems(rankedBooksList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private BorrowDurationDialogController showBorrowDurationDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/libarymanagementsystem/BorrowDurationDialog.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Chọn số ngày mượn");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
