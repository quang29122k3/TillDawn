package com.example.libarymanagementsystem.controller;

import com.example.libarymanagementsystem.model.*;
import com.example.libarymanagementsystem.service.BookService;
import com.example.libarymanagementsystem.service.ServiceFactory;
import com.example.libarymanagementsystem.utils.BookExists;
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
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class DashBoardControllerManager {

    @FXML
    private Button logout;


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
    private TableColumn<Book, String> imageColumn;
    @FXML
    private AnchorPane availableBooks_form;

    @FXML
    private AnchorPane savedBook_form;

    // Các nút điều hướng
    @FXML
    private Button availableBooks_btn;


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
    private Button googleBooksButton;

    @FXML
    private AnchorPane userInfoPane;

    @FXML
    private Button userIconButton;

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

    @FXML
    private TableView<Loan> borrowedBooksTable; // đổi từ Book sang Loan

    @FXML
    private TableColumn<Loan, ImageView> bookImageColumn;
    @FXML
    private TableColumn<Loan, Integer> bookIdColumn;
    @FXML
    private TableColumn<Loan, String> borrowerIdColumn;
    @FXML
    private TableColumn<Loan, String> borrowerNameColumn;
    @FXML
    private TableColumn<Loan, String> borrowerClassColumn;
    @FXML
    private TableColumn<Loan, String> borrowerRoleColumn;
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
    private Button rankBooksButton;

    @FXML
    private AnchorPane rankedBooks_form;

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

    @FXML
    private Text managerName;

    @FXML
    private TableColumn<Loan, Void> extendLoanColumn;

    @FXML
    private Button manageRequestsButton;

    // AnchorPane quản lý yêu cầu
    @FXML
    private AnchorPane requests_form;

    // TableView và các cột cho yêu cầu
    @FXML
    private TableView<Request> requestsTableView;

    @FXML
    private TableColumn<Request, Integer> requestIdColumn;

    @FXML
    private TableColumn<Request, String> requestSenderIdColumn;

    @FXML
    private TableColumn<Request, String> requestSenderNameColumn;

    @FXML
    private TableColumn<Request, String> requestSenderClassColumn;

    @FXML
    private TableColumn<Request, String> requestSubjectColumn;

    @FXML
    private TableColumn<Request, String> requestContentColumn;

    @FXML
    private TableColumn<Request, String> requestStatusColumn;

    private ObservableList<Request> requestsList = FXCollections.observableArrayList();

    // Khởi tạo Service Layer
    private BookService bookService;
    private ObservableList<Book> bookList;



// Có thể thêm một TableColumn chứa Button mượn, bạn dùng cell factory để tạo nút



    /**
     * Phuong thuc dieu huong giao dien.
     */
    @FXML
    private void navButtonDesign(ActionEvent event) {
        availableBooks_form.setVisible(false);
        savedBook_form.setVisible(false);
        member_form.setVisible(false);
        googleBooks_form.setVisible(false);
        userInfoPane.setVisible(false);
        rankedBooks_form.setVisible(false);
        requests_form.setVisible(false);

        if (event.getSource() == googleBooksButton) {
            googleBooks_form.setVisible(true);
        } else if (event.getSource() == availableBooks_btn) {
            availableBooks_form.setVisible(true);
        } else if (event.getSource() == savedBooks_btn) {
            savedBook_form.setVisible(true);
        } else if (event.getSource() == memberButton) { // memberButton là ID của nút "Thành viên"
            member_form.setVisible(true);
            loadMembers(); // Tải danh sách thành viên
        } else if (event.getSource() == userIconButton) {
            userInfoPane.setVisible(true);
            loadUserInfo();
        } else if (event.getSource() == rankBooksButton) {
            rankedBooks_form.setVisible(true);
            loadRankedBooks();
        } else if (event.getSource() == manageRequestsButton) { // Nút quản lý yêu cầu
            requests_form.setVisible(true);
            loadRequests();
        }
    }

    @FXML
    public void loadRequests() {
        requestsList.clear();
        String query = "SELECT r.id, r.person_id, p.fullname, p.class, r.subject, r.content, r.status " +
                "FROM requests r " +
                "JOIN person p ON r.person_id = p.id";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Request request = new Request(
                        rs.getInt("id"),
                        rs.getString("person_id"),
                        rs.getString("fullname"),
                        rs.getString("class"),
                        rs.getString("subject"),
                        rs.getString("content"),
                        rs.getString("status")
                );
                requestsList.add(request);
            }

            requestsTableView.setItems(requestsList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Không thể tải dữ liệu yêu cầu.");
        }
    }

    /**
     * Phương thức xử lý yêu cầu (cập nhật trạng thái).
     */
    @FXML
    private void handleProcessRequest(ActionEvent event) {
        Request selectedRequest = requestsTableView.getSelectionModel().getSelectedItem();
        if (selectedRequest == null) {
            showAlert("Vui lòng chọn yêu cầu để xử lý.");
            return;
        }

        // Hiển thị xác nhận trước khi xử lý
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Xác Nhận Xử Lý");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Bạn có chắc chắn muốn xử lý yêu cầu ID: " + selectedRequest.getId() + "?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String updateQuery = "UPDATE requests SET status = 'Đã xử lý' WHERE id = ?";

            try (Connection conn = ConnectionJDBCUtils.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

                pstmt.setInt(1, selectedRequest.getId());
                int rowsUpdated = pstmt.executeUpdate();

                if (rowsUpdated > 0) {
                    showAlert("Yêu cầu đã được xử lý.");
                    loadRequests();
                } else {
                    showAlert("Không thể xử lý yêu cầu.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert( "Đã xảy ra lỗi khi xử lý yêu cầu.");
            }
        }
    }

    /**
     * Phương thức xóa các yêu cầu đã được xử lý.
     */
    @FXML
    private void handleDeleteProcessedRequests(ActionEvent event) {
        // Xác nhận trước khi xóa
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Xác Nhận Xóa");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Bạn có chắc chắn muốn xóa tất cả các yêu cầu đã xử lý?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String deleteQuery = "DELETE FROM requests WHERE status = 'Đã xử lý'";

            try (Connection conn = ConnectionJDBCUtils.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

                int rowsDeleted = pstmt.executeUpdate();
                showAlert("Đã xóa " + rowsDeleted + " yêu cầu đã xử lý.");
                loadRequests();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert( "Không thể xóa yêu cầu đã xử lý.");
            }
        }
    }

    private ObservableList<Person> members = FXCollections.observableArrayList();

    /**
     * Phuong thuc load data cua Person.
     */

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

    /**
     * Cau hinh cac bang.
     */

    @FXML
    public void initialize() {
        // Khởi tạo Service
        bookService = ServiceFactory.getBookService();
        // Khởi tạo ObservableList
        bookList = FXCollections.observableArrayList();

        managerName.setText(GetData.getFullName());
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        totalCopiesColumn.setCellValueFactory(new PropertyValueFactory<>("totalCopies"));
//        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imageView"));

        // Cấu hình bảng sách đã mượn với các cột mới
        bookImageColumn.setCellValueFactory(new PropertyValueFactory<>("bookImage"));
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        loanBookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        loanBookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));
        borrowerIdColumn.setCellValueFactory(new PropertyValueFactory<>("personId"));
        borrowerNameColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        borrowerClassColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerClass"));
        borrowerRoleColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerRole"));
        loanBorrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        loanDueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        loanStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

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

        rankedImageColumn.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        rankedTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        rankedAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        rankedBorrowCountColumn.setCellValueFactory(new PropertyValueFactory<>("borrowCount"));
        rankedAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        // Cấu hình các cột quản lý yêu cầu
        requestIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        requestSenderIdColumn.setCellValueFactory(new PropertyValueFactory<>("personId"));
        requestSenderNameColumn.setCellValueFactory(new PropertyValueFactory<>("senderName"));
        requestSenderClassColumn.setCellValueFactory(new PropertyValueFactory<>("senderClass"));
        requestSubjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        requestContentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        requestStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));

// Thiết lập cellFactory để hiển thị hình ảnh dựa trên imagePath
        imageColumn.setCellFactory(new Callback<TableColumn<Book, String>, TableCell<Book, String>>() {
            @Override
            public TableCell<Book, String> call(TableColumn<Book, String> param) {
                return new TableCell<Book, String>() {
                    private final ImageView imageView = new ImageView();

                    @Override
                    protected void updateItem(String imagePath, boolean empty) {
                        super.updateItem(imagePath, empty);
                        if (empty || imagePath == null || imagePath.trim().isEmpty()) {
                            imageView.setImage(null);
                            setGraphic(null);
                        } else {
                            try {
                                Image image = new Image(imagePath, 50, 50, false, true);
                                imageView.setImage(image);
                            } catch (IllegalArgumentException e) {
                                imageView.setImage(getDefaultImageView().getImage());
                            }
                            setGraphic(imageView);
                        }
                    }
                };
            }
        });

        // Thêm Listener cho TableView yêu cầu để mở dialog khi double-click
        requestsTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !requestsTableView.getSelectionModel().isEmpty()) {
                Request selectedRequest = requestsTableView.getSelectionModel().getSelectedItem();
                showRequestDialog(selectedRequest);
            }
        });

        googleBooksTableView.setItems(googleBooksList);
        borrowedBooksTable.setItems(borrowedLoans);

        // Cấu hình sự kiện tìm kiếm thành viên
        searchMemberButton.setOnAction(event -> handleSearchMemberAction());


        showForm("availableBooks_form");
        bookTableView.setItems(bookList);

        // Tải tất cả sách ban đầu
        loadRequests();
        loadBooks();
        loadBorrowedLoans();

        // Gán sự kiện cho nút tìm kiếm
        searchButton.setOnAction(event -> handleSearchAction());

        extendLoanColumn.setCellFactory(new Callback<TableColumn<Loan, Void>, TableCell<Loan, Void>>() {
            @Override
            public TableCell<Loan, Void> call(final TableColumn<Loan, Void> param) {
                final TableCell<Loan, Void> cell = new TableCell<Loan, Void>() {

                    private final Button btn = new Button("Gia Hạn");

                    {
                        btn.setStyle("-fx-background-color: #0288D1; -fx-text-fill: white;");
                        btn.setOnAction((ActionEvent event) -> {
                            Loan loan = getTableView().getItems().get(getIndex());
                            handleExtendLoan(loan);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });

    }

    @FXML
    private void handleExtendLoan(Loan loan) {
        // Tạo dialog để nhập số ngày gia hạn
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Gia Hạn Sách");
        dialog.setHeaderText("Gia hạn sách: " + loan.getBookTitle());
        dialog.setContentText("Nhập số ngày gia hạn:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String input = result.get().trim();
            if (input.isEmpty()) {
                showAlert("Số ngày gia hạn không được để trống.");
                return;
            }
            int days;
            try {
                days = Integer.parseInt(input);
                if (days <= 0) {
                    showAlert("Số ngày gia hạn phải là số dương.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Vui lòng nhập số ngày hợp lệ.");
                return;
            }

            // Tính ngày gia hạn mới
            LocalDate today = LocalDate.now();
            LocalDate newDueDate;
            if (loan.getDueDate() != null && loan.getDueDate().isAfter(today)) {
                newDueDate = loan.getDueDate().plusDays(days);
            } else {
                newDueDate = today.plusDays(days);
            }

            // Cập nhật cơ sở dữ liệu
            String updateQuery = "UPDATE loans SET due_date = ?, status = 'Đang Mượn' WHERE id = ?";
            try (Connection conn = ConnectionJDBCUtils.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

                pstmt.setDate(1, Date.valueOf(newDueDate));
                pstmt.setInt(2, loan.getId());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Gia hạn thành công. Hạn trả mới: " + newDueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    loadBorrowedLoans(); // Cập nhật lại bảng
                } else {
                    showAlert("Gia hạn thất bại. Vui lòng thử lại.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Đã xảy ra lỗi khi gia hạn sách.");
            }
        }
    }

    /**
     * Su dung event de chuyen ve trang hellơview.
     */

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
     * Phuong thuc tim kiem sach, author.
     */

    @FXML
    private void handleSearchAction() {
        String searchText = searchField.getText().trim();
        List<Book> bookList;
        if (searchText.isEmpty()) {
            bookList = bookService.getAllBooks();
        } else {
            bookList = bookService.searchBooks(searchText);
        }
        ObservableList<Book> observableBooks = FXCollections.observableArrayList(bookList);
        bookTableView.setItems(observableBooks);

        // Xử lý ImageView cho từng sách dựa trên imagePath
        for (Book book : observableBooks) {
            String imagePath = book.getImagePath();
            ImageView imageView = null;
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                try {
                    Image image = new Image(imagePath, 50, 50, false, true);
                    imageView = new ImageView(image);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid image URL for book ID " + book.getId() + ": " + imagePath);
                    imageView = getDefaultImageView();
                }
            } else {
                imageView = getDefaultImageView();
            }

            // Cập nhật imagePath thành ImageView
            book.setImagePath(imagePath); // Đảm bảo imagePath đã được set
            // Bạn có thể thêm ImageView vào một cột khác hoặc xử lý tùy ý
        }
    }

    /**
     * Phuong thuc load person.
     */

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

    /**
     * Phuong thuc chan thanh vien.
     */

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

    /**
     * Phuong thuc mo chan.
     */

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

    /**
     * Phuong thuc muon sach.
     */

//    @FXML
//    private void handleBorrowAction() {
//        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
//        if (selectedBook == null) {
//            showAlert("Vui lòng chọn sách để mượn.");
//            return;
//        }
//
//        if (selectedBook.getAvailable() == 0) {
//            showAlert("Sách \"" + selectedBook.getTitle() + "\" đã hết.");
//            return;
//        }
//
//        // Mở dialog nhập số ngày mượn
//        BorrowDurationDialogController dialogController = showBorrowDurationDialog();
//        if (dialogController == null || !dialogController.isConfirmed()) {
//            return;
//        }
//
//        int days = dialogController.getDays();
//        if (days <= 0) {
//            showAlert("Số ngày mượn phải > 0.");
//            return;
//        }
//
//        LocalDate borrowDate = LocalDate.now();
//        LocalDate dueDate = borrowDate.plusDays(days);
//
//        try (Connection conn = ConnectionJDBCUtils.getConnection()) {
//            conn.setAutoCommit(false);
//            String personId = GetData.getUsername();
//
//            // Thêm vào loans có due_date
//            String loanQuery = "INSERT INTO loans (person_id, book_id, borrow_date, due_date, returned, status) VALUES (?, ?, ?, ?, 0, 'borrowed')";
//            try (PreparedStatement stmt = conn.prepareStatement(loanQuery)) {
//                stmt.setString(1, personId);
//                stmt.setInt(2, selectedBook.getId());
//                stmt.setDate(3, Date.valueOf(borrowDate));
//                stmt.setDate(4, Date.valueOf(dueDate));
//                stmt.executeUpdate();
//            }
//
//            String updateBookQuery = "UPDATE books SET available = available - 1 WHERE id = ?";
//            try (PreparedStatement stmt = conn.prepareStatement(updateBookQuery)) {
//                stmt.setInt(1, selectedBook.getId());
//                stmt.executeUpdate();
//            }
//
//            conn.commit();
//            loadBooks();
//            loadBorrowedLoans();// Cập nhật lại trang sách có sẵn
//            // Nếu bạn có trang top 10 sách, loadRankedBooks() nếu cần
//        } catch (SQLException e) {
//            e.printStackTrace();
//            showAlert("Đã xảy ra lỗi khi mượn sách.");
//        }
//    }

    /**
     * Phuong thuc tra sach.
     */

//    @FXML
//    private void handleReturnAction() {
//        Loan selectedLoan = borrowedBooksTable.getSelectionModel().getSelectedItem();
//        if (selectedLoan == null) {
//            showAlert("Vui lòng chọn lượt mượn sách để trả.");
//            return;
//        }
//
//        try (Connection conn = ConnectionJDBCUtils.getConnection()) {
//            conn.setAutoCommit(false);
//
//            // Cập nhật trạng thái mượn
//            String returnQuery = "UPDATE loans SET returned = 1, return_date = ?, status = 'returned' WHERE id = ?";
//            try (PreparedStatement stmt = conn.prepareStatement(returnQuery)) {
//                stmt.setDate(1, Date.valueOf(LocalDate.now()));
//                stmt.setInt(2, selectedLoan.getId()); // Sử dụng getId() từ Loan thay cho getLoanId() của Book
//                stmt.executeUpdate();
//            }
//
//            // Tăng số lượng sách có sẵn
//            String updateBookQuery = "UPDATE books SET available = available + 1 WHERE id = ?";
//            try (PreparedStatement stmt = conn.prepareStatement(updateBookQuery)) {
//                stmt.setInt(1, selectedLoan.getBookId()); // Sử dụng getBookId() từ Loan thay cho selectedBook.getId()
//                stmt.executeUpdate();
//            }
//
//            conn.commit();
//            loadBooks();
//            loadBorrowedLoans(); // Gọi loadBorrowedLoans() thay cho loadBorrowedBooks()
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Load sach.
     */

    private void loadBooks() {
        List<Book> bookList = bookService.getAllBooks();
        ObservableList<Book> observableBooks = FXCollections.observableArrayList(bookList);
        bookTableView.setItems(observableBooks);

        // Xử lý ImageView cho từng sách dựa trên imagePath
        for (Book book : observableBooks) {
            String imagePath = book.getImagePath();
            ImageView imageView = null;
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                try {
                    Image image = new Image(imagePath, 50, 50, false, true);
                    imageView = new ImageView(image);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid image URL for book ID " + book.getId() + ": " + imagePath);
                    imageView = getDefaultImageView();
                }
            } else {
                imageView = getDefaultImageView();
            }
            book.setImagePath(imagePath); // Đảm bảo imagePath đã được set
        }
    }

    private ObservableList<Loan> borrowedLoans = FXCollections.observableArrayList();

    private void loadBorrowedLoans() {
        borrowedLoans.clear();
        String query = "SELECT l.id AS loan_id, l.person_id, p.fullname, p.class, r.name AS role, " +
                "l.book_id, b.title, b.author, b.image, l.borrow_date, l.due_date, l.return_date, l.returned, l.status " +
                "FROM loans l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN person p ON l.person_id = p.id " +
                "JOIN role r ON p.role_id = r.id " +
                "WHERE l.returned = 0";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            LocalDate now = LocalDate.now();
            while (rs.next()) {
                int loanId = rs.getInt("loan_id");
                String personId = rs.getString("person_id");
                String borrowerName = rs.getString("fullname");
                String borrowerClass = rs.getString("class");
                String borrowerRole = rs.getString("role");
                int bookId = rs.getInt("book_id");
                String bookTitle = rs.getString("title");
                String bookAuthor = rs.getString("author");
                String imagePath = rs.getString("image");
                LocalDate borrowDate = rs.getDate("borrow_date").toLocalDate();
                Date dueDateSql = rs.getDate("due_date");
                LocalDate dueDate = (dueDateSql != null) ? dueDateSql.toLocalDate() : null;
                Date returnDateSql = rs.getDate("return_date");
                LocalDate returnDate = (returnDateSql != null) ? returnDateSql.toLocalDate() : null;
                boolean returned = rs.getBoolean("returned");
                String dbStatus = rs.getString("status");

                // Tính trạng thái hiển thị
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

                // Tạo ImageView từ imagePath
                ImageView imageView = null;
                if (imagePath != null && !imagePath.trim().isEmpty()) {
                    try {
                        Image image = new Image(imagePath, 50, 50, false, true);
                        imageView = new ImageView(image);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid image URL for book ID " + bookId + ": " + imagePath);
                        imageView = getDefaultImageView();
                    }
                } else {
                    imageView = getDefaultImageView();
                }

                // Tạo đối tượng Loan với thông tin đầy đủ
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
     * Phuong thuc them sach.
     */

    public void addBook(Book book) {
        BookExists bookChecker = new BookExists(book.getTitle());

        // Kiểm tra nếu sách đã tồn tại
        if (bookChecker.exists()) {
            showAlert("Sách với tiêu đề \"" + book.getTitle() + "\" đã tồn tại trong hệ thống.");
            return;
        }
        try {
            bookService.addBook(book);
            showAlert("Thêm sách thành công.");
            loadBooks();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Đã xảy ra lỗi khi thêm sách.");
        }
    }

    /**
     * Phuong thuc cap nhat sach.
     */

    public void updateBook(Book book) {
        BookExists bookChecker = new BookExists(book.getTitle(), book.getId());

        // Kiểm tra nếu sách đã tồn tại
        if (bookChecker.exists()) {
            showAlert("Sách với tiêu đề \"" + book.getTitle() + "\" đã tồn tại trong hệ thống.");
            return;
        }
        try {
            bookService.updateBook(book);
            showAlert("Cập nhật sách thành công.");
            loadBooks();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Đã xảy ra lỗi khi cập nhật sách.");
        }
    }

    /**
     * Phuong thuc xoa sach.
     */

    public void deleteBook(int bookId) {
        try {
            bookService.deleteBook(bookId);
            showAlert("Xóa sách thành công.");
            loadBooks();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Đã xảy ra lỗi khi xóa sách.");
        }
    }


    /**
     * Phuong thuc open.
     */

    @FXML
    private void handleAddBook() {
        // Mở cửa sổ thêm sách
        showBookForm(null);
    }

    /**
     * Phuong thuc open.
     */

    @FXML
    private void handleEditBook() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            showBookForm(selectedBook);
        } else {
            showAlert("Vui lòng chọn sách để chỉnh sửa.");
        }
    }

    /**
     * Phuong thuc open.
     */

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

    /**
     * Phuong thuc dieu huong toi BookForm.
     */

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

    /**
     * Phuong thuc tra ra thong bao.
     */

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private ObservableList<BookItem> googleBooksList = FXCollections.observableArrayList();

    /**
     * Phuong thuc tim kiem sach tren GoogleAPI.
     */

    @FXML
    private void handleGoogleBooksSearch() {
        String query = googleBooksSearchField.getText();
        List<BookItem> books = GoogleBooksService.searchBooks(query);

        googleBooksTableView.getItems().clear();
        googleBooksTableView.getItems().addAll(books);
    }

    /**
     * Phuong thuc load thong tin user.
     */

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
                showAlert("Không tìm thấy thông tin người dùng.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Đã xảy ra lỗi khi tải thông tin người dùng.");
        }
    }

    /**
     * Phuong thuc luu.
     */

    @FXML
    private void handleSaveAction(ActionEvent event) {
        String updateQuery = "UPDATE person SET fullname = ?, class = ?, email = ? WHERE id = ?";
        ;
        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, fullNameField.getText());
            pstmt.setString(2, classField.getText());
            pstmt.setString(3, emailField.getText());
            pstmt.setString(4, userIdField.getText());
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                showAlert("Thông tin đã được cập nhật.");
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

    /**
     * Phuong thuc chinh sua tren cac field.
     */

    @FXML
    public void handleEditAction(ActionEvent event) {
        fullNameField.setEditable(true);
        classField.setEditable(true);
        emailField.setEditable(true);
        saveButton.setDisable(false);
    }

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

//    @FXML
//    private void handleBorrowFromRankedOutsideTable(ActionEvent event) {
//        Book selectedBook = rankedBooksTableView.getSelectionModel().getSelectedItem();
//        if (selectedBook == null) {
//            showAlert("Vui lòng chọn sách để mượn.");
//            return;
//        }
//
//        if (selectedBook.getAvailable() == 0) {
//            showAlert("Sách \"" + selectedBook.getTitle() + "\" đã hết. Không thể mượn sách này.");
//            return;
//        }
//
//        // Mở dialog nhập số ngày mượn
//        BorrowDurationDialogController dialogController = showBorrowDurationDialog();
//        if (dialogController == null || !dialogController.isConfirmed()) {
//            return; // Người dùng hủy hoặc không nhập, không làm gì thêm
//        }
//
//        int days = dialogController.getDays();
//        if (days <= 0) {
//            showAlert("Số ngày mượn phải > 0.");
//            return;
//        }
//
//        LocalDate borrowDate = LocalDate.now();
//        LocalDate dueDate = borrowDate.plusDays(days);
//
//        try (Connection conn = ConnectionJDBCUtils.getConnection()) {
//            conn.setAutoCommit(false);
//            String personId = GetData.getUsername(); // Lấy ID người dùng hiện tại
//
//            // Thêm bản ghi mượn vào loans với due_date
//            String loanQuery = "INSERT INTO loans (person_id, book_id, borrow_date, due_date, returned, status) " +
//                    "VALUES (?, ?, ?, ?, 0, 'borrowed')";
//            try (PreparedStatement stmt = conn.prepareStatement(loanQuery)) {
//                stmt.setString(1, personId);
//                stmt.setInt(2, selectedBook.getId());
//                stmt.setDate(3, Date.valueOf(borrowDate));
//                stmt.setDate(4, Date.valueOf(dueDate));
//                stmt.executeUpdate();
//            }
//
//            // Giảm số lượng sách có sẵn
//            String updateBookQuery = "UPDATE books SET available = available - 1 WHERE id = ?";
//            try (PreparedStatement stmt = conn.prepareStatement(updateBookQuery)) {
//                stmt.setInt(1, selectedBook.getId());
//                stmt.executeUpdate();
//            }
//
//            conn.commit();
//
//            // Cập nhật lại danh sách sách và sách đã mượn
//            loadBooks();
//            loadRankedBooks(); // Cập nhật lại top 10 sách nếu cần
//            loadBorrowedLoans();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            showAlert("Đã xảy ra lỗi khi mượn sách.");
//        }
//    }

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

    /**
     * Hiển thị dialog chi tiết yêu cầu.
     *
     * @param request Yêu cầu được chọn
     */
    private void showRequestDialog(Request request) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/libarymanagementsystem/RequestDetailDialog.fxml"));
            Parent root = loader.load();

            // Lấy controller của dialog
            RequestDetailDialogController controller = loader.getController();
            controller.setRequestDetails(request.getSubject(), request.getContent());

            // Tạo và thiết lập Stage cho dialog
            Stage stage = new Stage();
            stage.setTitle("Chi Tiết Yêu Cầu");
            stage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác với cửa sổ khác
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Đã xảy ra lỗi khi mở chi tiết yêu cầu.");
        }
    }
}