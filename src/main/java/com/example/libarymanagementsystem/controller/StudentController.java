package com.example.libarymanagementsystem.controller;

import com.example.libarymanagementsystem.game.TicTacToeGame;
import com.example.libarymanagementsystem.model.*;
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
    private TableColumn<Book, Void> pinColumn;

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
    @FXML
    private Button savedBooksButton; // Nút "Sách đã lưu" mới
    @FXML
    private Button requestButton;
    @FXML
    private AnchorPane savedBooks_form_new; // AnchorPane mới cho "Sách đã lưu"
    @FXML
    private TableView<Book> savedBooksTableView; // TableView cho "Sách đã lưu"
    @FXML
    private TableColumn<Book, ImageView> savedImageColumn;
    @FXML
    private TableColumn<Book, String> savedTitleColumn;
    @FXML
    private TableColumn<Book, String> savedAuthorColumn;
    @FXML
    private TableColumn<Book, Integer> savedAvailableColumn;
    @FXML
    private TableColumn<Book, Void> savedUnpinColumn;

    @FXML
    private AnchorPane request_form;

    @FXML
    private TextField requestSubjectField;

    @FXML
    private TextArea requestContentArea;

    @FXML
    private Button sendRequestButton;

    @FXML
    private Button cancelRequestButton;

    @FXML
    private Button gameButton; // Nút trò chơi trên thanh bên

    @FXML
    private AnchorPane game_form; // AnchorPane form game

    @FXML
    private Button startGameButton;

    @FXML
    private AnchorPane gameArea; // Khu vực chứa game XO


    // Dữ liệu cho bảng "Sách đã lưu"
    private ObservableList<Book> savedBooksList = FXCollections.observableArrayList();

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

        // Cấu hình bảng "Sách đã lưu"
        savedImageColumn.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        savedTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        savedAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        savedAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        configurePinColumn();
        // Cấu hình cột Hành Động (Bỏ Ghim)
        configureUnpinColumn();

        savedBooksTableView.setItems(savedBooksList);


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

        requestIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        requestSubjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        requestContentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        requestStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

// Lắng nghe sự kiện double-click trên bảng requestsTableView
        requestsTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !requestsTableView.getSelectionModel().isEmpty()) {
                Request selectedRequest = requestsTableView.getSelectionModel().getSelectedItem();
                showRequestDialog(selectedRequest);
            }
        });

        // Gán danh sách vào bảng
        bookTableView.setItems(availableBooks);

        // Thiết lập các AnchorPane ban đầu
        availableBooks_form.setVisible(true);
        savedBook_form.setVisible(false);
        googleBooks_form.setVisible(false);
        userInfoPane.setVisible(false);
        rankedBooks_form.setVisible(false);
        savedBooks_form_new.setVisible(false);
        request_form.setVisible(false); // Ẩn trang Yêu cầu ban đầu



        // Tải dữ liệu ban đầu
        loadBooks();
        loadBorrowedLoans();
        loadStudentRequests(null);

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
        availableBooks.clear();
        String query = "SELECT b.*, CASE WHEN sb.book_id IS NOT NULL THEN true ELSE false END AS is_pinned " +
                "FROM books b LEFT JOIN saved_books sb ON b.id = sb.book_id AND sb.person_id = ?";
        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, GetData.getUsername()); // Thêm tham số person_id
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int available = rs.getInt("available");
                String imagePath = rs.getString("image"); // Đường dẫn hình ảnh
                boolean isPinned = rs.getBoolean("is_pinned"); // Trạng thái ghim

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
                book.setPinned(isPinned); // Thiết lập trạng thái ghim
                availableBooks.add(book);
            }

            // Sắp xếp sách: ghim trước, chưa ghim sau
            FXCollections.sort(availableBooks, (b1, b2) -> Boolean.compare(b2.isPinned(), b1.isPinned()));

            // Đặt dữ liệu vào TableView
            bookTableView.setItems(availableBooks);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void configureUnpinColumn() {
        savedUnpinColumn.setCellFactory(col -> new TableCell<Book, Void>() {
            private final Button unpinButton = new Button("Bỏ Ghim");

            {
                unpinButton.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white;");
                unpinButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    handleUnpinAction(book);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(unpinButton);
                }
            }
        });
    }

    private void configureSavedUnpinColumn() {
        savedUnpinColumn.setCellFactory(col -> new TableCell<Book, Void>() {
            private final Button unpinButton = new Button("Bỏ Ghim");

            {
                unpinButton.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white;");
                unpinButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    if (book != null && book.isPinned()) {
                        togglePin(book);
                        showAlert("Thông báo", "Đã bỏ ghim sách: " + book.getTitle());
                        // Thêm dòng này để đảm bảo bảng Sách Có Sẵn được cập nhật
                        loadBooks();
                    } else {
                        showAlert("Lỗi", "Không thể bỏ ghim sách này.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(unpinButton);
                }
            }
        });
    }

    private void configurePinColumn() {
        pinColumn.setCellFactory(col -> new TableCell<Book, Void>() {
            private final Button pinButton = new Button();

            {
                // Thiết lập kiểu dáng cho nút
                pinButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black;");
                pinButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    handlePinAction(book);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Book book = getTableView().getItems().get(getIndex());
                    if (book.isPinned()) {
                        pinButton.setText("Đã Ghim");
                    } else {
                        pinButton.setText("Ghim");
                    }
                    setGraphic(pinButton);
                }
            }
        });
    }

    @FXML
    private void handlePinAction(Book book) {
        if (book != null) {
            if (book.isPinned()) {
                showAlert("Thông báo", "Sách đã được ghim trước đó.");
            } else {
                togglePin(book); // Thực hiện ghim sách
                showAlert("Thông báo", "Đã ghim sách: " + book.getTitle());
                // Cập nhật lại bảng sách có sẵn và sách đã lưu
                loadBooks();
                loadSavedBooksNew();
            }
        } else {
            showAlert("Lỗi", "Không tìm thấy sách để ghim.");
        }
    }


    private void handleUnpinAction(Book book) {
        if (book != null && book.isPinned()) {
            togglePin(book); // Sử dụng phương thức togglePin đã có để bỏ ghim
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
                "WHERE l.returned = 0 AND l.person_id = ?"; // Thêm điều kiện lọc theo person_id

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, GetData.getUsername()); // Thêm tham số person_id
            ResultSet rs = pstmt.executeQuery();

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
                    if (dueDate == null) {
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
            query = "SELECT b.*, CASE WHEN sb.book_id IS NOT NULL THEN true ELSE false END AS is_pinned " +
                    "FROM books b LEFT JOIN saved_books sb ON b.id = sb.book_id AND sb.person_id = ?";
        } else {
            query = "SELECT b.*, CASE WHEN sb.book_id IS NOT NULL THEN true ELSE false END AS is_pinned " +
                    "FROM books b LEFT JOIN saved_books sb ON b.id = sb.book_id AND sb.person_id = ? " +
                    "WHERE b.title LIKE ? OR b.author LIKE ?";
        }

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, GetData.getUsername()); // person_id
            if (!searchText.isEmpty()) {
                pstmt.setString(2, "%" + searchText + "%");
                pstmt.setString(3, "%" + searchText + "%");
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int available = rs.getInt("available");
                String imagePath = rs.getString("image");
                boolean isPinned = rs.getBoolean("is_pinned"); // Trạng thái ghim

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
                book.setPinned(isPinned); // Thiết lập trạng thái ghim
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
            showAlert("Lỗi", "Vui lòng chọn lượt mượn sách để trả.");
            return;
        }

        // Kiểm tra xem loan.person_id có khớp với user hiện tại không
        if (!selectedLoan.getPersonId().equals(GetData.getUsername())) {
            showAlert("Lỗi", "Bạn không thể trả sách của người dùng khác.");
            return;
        }

        try (Connection conn = ConnectionJDBCUtils.getConnection()) {
            conn.setAutoCommit(false);

            // Cập nhật trạng thái mượn
            String returnQuery = "UPDATE loans SET returned = 1, return_date = ?, status = 'returned' WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(returnQuery)) {
                stmt.setDate(1, Date.valueOf(LocalDate.now()));
                stmt.setInt(2, selectedLoan.getId());
                stmt.executeUpdate();
            }

            // Tăng số lượng sách có sẵn
            String updateBookQuery = "UPDATE books SET available = available + 1 WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateBookQuery)) {
                stmt.setInt(1, selectedLoan.getBookId());
                stmt.executeUpdate();
            }

            conn.commit();
            loadBooks();
            loadBorrowedLoans();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Đã xảy ra lỗi khi trả sách.");
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
        savedBooks_form_new.setVisible(false);
        request_form.setVisible(false);
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
        else if (event.getSource() == savedBooksButton) { // Nút "Sách đã lưu" mới
            savedBooks_form_new.setVisible(true);
            loadSavedBooksNew(); // Hàm load sách đã lưu mới
        }else if (event.getSource() == requestButton) { // Nút "Yêu cầu"
            request_form.setVisible(true);
        }else if(event.getSource()==gameButton){
            game_form.setVisible(true);
        }
    }

    @FXML
    private ComboBox<String> difficultyComboBox; // Đảm bảo khai báo ComboBox này

    @FXML
    private void handleStartGame() {
        // Lấy độ khó từ ComboBox
        String selectedDifficulty = difficultyComboBox.getValue();
        if (selectedDifficulty == null) {
            showAlert("Chú ý", "Vui lòng chọn độ khó trước khi chơi!");
            return;
        }

        // Clear nội dung cũ trong gameArea
        gameArea.getChildren().clear();

        // Chuyển đổi độ khó từ chuỗi thành enum Difficulty
        TicTacToeGame.Difficulty difficultyEnum;
        switch (selectedDifficulty) {
            case "Dễ":
                difficultyEnum = TicTacToeGame.Difficulty.EASY;
                break;
            case "Trung bình":
                difficultyEnum = TicTacToeGame.Difficulty.MEDIUM;
                break;
            case "Khó":
                difficultyEnum = TicTacToeGame.Difficulty.HARD;
                break;
            default:
                difficultyEnum = TicTacToeGame.Difficulty.EASY;  // Giá trị mặc định nếu không có độ khó hợp lệ
                break;
        }

        // Tạo game mới với độ khó
        TicTacToeGame game = new TicTacToeGame(() -> resetGameUI(), difficultyEnum);
        Parent gameUI = game.createContent();

        // Fit gameUI vào gameArea
        gameUI.setLayoutX(0);
        gameUI.setLayoutY(0);
        gameArea.getChildren().add(gameUI);
    }

    // Hàm này được gọi khi game cần reset giao diện
    // (Nếu muốn tích hợp logic Resume bên ngoài, còn nếu Resume handle bên trong TicTacToeGame thì không cần)
    private void resetGameUI() {
        // Nếu TicTacToeGame có callback gọi khi resume, ta có thể xử lý ở đây.
        // Hiện tại không cần gì đặc biệt, vì TicTacToeGame tự xử lý reset.
    }

    @FXML
    private void handleEasyDifficulty() {
        handleStartGameWithDifficulty("Easy");
    }

    @FXML
    private void handleMediumDifficulty() {
        handleStartGameWithDifficulty("Medium");
    }

    @FXML
    private void handleHardDifficulty() {
        handleStartGameWithDifficulty("Hard");
    }

    private void handleStartGameWithDifficulty(String difficulty) {
        gameArea.getChildren().clear();

        // Chuyển đổi độ khó thành enum Difficulty
        TicTacToeGame.Difficulty difficultyEnum = TicTacToeGame.Difficulty.EASY;  // Giá trị mặc định
        switch (difficulty) {
            case "Dễ":
                difficultyEnum = TicTacToeGame.Difficulty.EASY;
                break;
            case "Trung bình":
                difficultyEnum = TicTacToeGame.Difficulty.MEDIUM;
                break;
            case "Khó":
                difficultyEnum = TicTacToeGame.Difficulty.HARD;
                break;
            default:
                break;
        }

        TicTacToeGame game = new TicTacToeGame(() -> resetGameUI(), difficultyEnum);
        Parent gameUI = game.createContent();

        gameUI.setLayoutX(0);
        gameUI.setLayoutY(0);
        gameArea.getChildren().add(gameUI);
    }

    private void loadSavedBooksNew() {
        savedBooksList.clear();
        String query = "SELECT b.* FROM books b " +
                "JOIN saved_books sb ON b.id = sb.book_id " +
                "WHERE sb.person_id = ?";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, GetData.getUsername());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int available = rs.getInt("available");
                String imagePath = rs.getString("image");

                ImageView imageView = null;
                if (imagePath != null && !imagePath.trim().isEmpty()) {
                    try {
                        Image image = new Image(imagePath, 50, 50, false, true);
                        imageView = new ImageView(image);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid image URL for book ID " + id + ": " + imagePath);
                        imageView = getDefaultImageView();
                    }
                } else {
                    imageView = getDefaultImageView();
                }

                Book book = new Book(id, title, author, available, imageView);
                book.setPinned(true); // Đánh dấu sách đã ghim
                savedBooksList.add(book);
            }

            savedBooksTableView.setItems(savedBooksList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Đã xảy ra lỗi khi tải sách đã lưu.");
        }
    }

    private void togglePin(Book book) {
        boolean newPinnedStatus = !book.isPinned();
        String query = newPinnedStatus ?
                "INSERT INTO saved_books (person_id, book_id) VALUES (?, ?)" :
                "DELETE FROM saved_books WHERE person_id = ? AND book_id = ?";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            if (newPinnedStatus) {
                pstmt.setString(1, GetData.getUsername());
                pstmt.setInt(2, book.getId());
            } else {
                pstmt.setString(1, GetData.getUsername());
                pstmt.setInt(2, book.getId());
            }
            pstmt.executeUpdate();

            book.setPinned(newPinnedStatus);

            // Cập nhật lại danh sách Sách Có Sẵn
            loadBooks(); // Thêm dòng này để tải lại danh sách Sách Có Sẵn

            // Cập nhật bảng Sách đã lưu
            loadSavedBooksNew();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Đã xảy ra lỗi khi ghim/bỏ ghim sách.");
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

    @FXML
    private void handleSendRequest(ActionEvent event) {
        String subject = requestSubjectField.getText().trim();
        String content = requestContentArea.getText().trim();

        if (subject.isEmpty() || content.isEmpty()) {
            showAlert("Lỗi", "Vui lòng điền đầy đủ chủ đề và nội dung yêu cầu.");
            return;
        }

        String personId = GetData.getUsername();
        String insertQuery = "INSERT INTO requests (person_id, subject, content) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, personId);
            pstmt.setString(2, subject);
            pstmt.setString(3, content);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Thành công", "Yêu cầu đã được gửi tới admin.");
                // Làm sạch các trường
                requestSubjectField.clear();
                requestContentArea.clear();
                // Ẩn trang Yêu cầu
                request_form.setVisible(true);
                // Tải lại danh sách yêu cầu để bao gồm yêu cầu mới
                loadStudentRequests(null);
            } else {
                showAlert("Lỗi", "Không thể gửi yêu cầu. Vui lòng thử lại.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Đã xảy ra lỗi khi gửi yêu cầu.");
        }
    }

    @FXML
    private void handleCancelRequest(ActionEvent event) {
        // Làm sạch các trường
        requestSubjectField.clear();
        requestContentArea.clear();
        // Ẩn trang Yêu cầu
        request_form.setVisible(true);
    }

    // Thuộc tính thêm
    @FXML
    private TableView<Request> requestsTableView;
    @FXML
    private TableColumn<Request, Integer> requestIdColumn;
    @FXML
    private TableColumn<Request, String> requestSubjectColumn;
    @FXML
    private TableColumn<Request, String> requestContentColumn;
    @FXML
    private TableColumn<Request, String> requestStatusColumn;
    @FXML
    private TextField searchRequestField;
    @FXML
    private Button searchRequestButton;

    // Danh sách yêu cầu của student
    private ObservableList<Request> studentRequests = FXCollections.observableArrayList();

    private void loadStudentRequests(String keyword) {
        studentRequests.clear();
        String personId = GetData.getUsername();
        String query;
        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty());

        if (hasKeyword) {
            // Tìm kiếm theo ID yêu cầu (nếu keyword là số) hoặc theo chủ đề
            // Kiểm tra keyword có phải số không
            boolean isNumber = keyword.matches("\\d+"); // true nếu là số
            if (isNumber) {
                // Tìm theo ID
                query = "SELECT id, person_id, subject, content, status FROM requests WHERE person_id = ? AND (id = ? OR subject LIKE ?)";
            } else {
                // Tìm theo subject
                query = "SELECT id, person_id, subject, content, status FROM requests WHERE person_id = ? AND (subject LIKE ?)";
            }
        } else {
            // Không có từ khóa, load tất cả
            query = "SELECT id, person_id, subject, content, status FROM requests WHERE person_id = ?";
        }

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, personId);

            if (hasKeyword) {
                boolean isNumber = keyword.matches("\\d+");
                if (isNumber) {
                    pstmt.setInt(2, Integer.parseInt(keyword));
                    pstmt.setString(3, "%" + keyword + "%");
                } else {
                    pstmt.setString(2, "%" + keyword + "%");
                }
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String pId = rs.getString("person_id");
                String subject = rs.getString("subject");
                String content = rs.getString("content");
                String status = rs.getString("status");

                Request request = new Request(id, pId, subject, content, status);
                studentRequests.add(request);
            }

            requestsTableView.setItems(studentRequests);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải danh sách yêu cầu.");
        }
    }

    @FXML
    private void handleSearchRequests(ActionEvent event) {
        String keyword = searchRequestField.getText().trim();
        if (keyword.isEmpty()) {
            // Nếu không gõ gì, vẫn trả ra tất cả yêu cầu
            loadStudentRequests(null);
        } else {
            loadStudentRequests(keyword);
        }
    }

    private void showRequestDialog(Request request) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/libarymanagementsystem/RequestDetailDialog.fxml"));
            Parent root = loader.load();

            RequestDetailDialogController controller = loader.getController();
            controller.setRequestDetails(request.getSubject(), request.getContent());

            Stage stage = new Stage();
            stage.setTitle("Chi Tiết Yêu Cầu");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Đã xảy ra lỗi khi mở chi tiết yêu cầu.");
        }
    }
}
