package com.example.libarymanagementsystem;

import com.example.libarymanagementsystem.utils.ConnectionJDBCUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class LoginController {
    @FXML
    private Button close;

    @FXML
    private Button loginBtr;

    @FXML
    private Button minimize;

    @FXML
    private AnchorPane main_form;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private double x = 0;
    private double y = 0;

    public void loginAdmin() {
        // Sửa đổi câu lệnh SQL để không so sánh mật khẩu
        String sql = "SELECT person.id, person.password, role.code " +
                "FROM person " +
                "INNER JOIN role ON person.role_id = role.id " +
                "WHERE person.id = ? AND person.is_active = 1";

        try {
            connect = ConnectionJDBCUtils.getConnection();
            Alert alert;

            String enteredUsername = username.getText().trim();
            String enteredPassword = password.getText().trim();

            if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Thông báo lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng điền đầy đủ tên đăng nhập và mật khẩu.");
                alert.showAndWait();
            } else {
                prepare = connect.prepareStatement(sql);
                prepare.setString(1, enteredUsername);

                result = prepare.executeQuery();

                if (result.next()) {
                    String storedHashedPassword = result.getString("password");

                    // Kiểm tra mật khẩu nhập vào với mật khẩu đã mã hóa
                    if (BCrypt.checkpw(enteredPassword, storedHashedPassword)) {
                        // Mật khẩu đúng, tiến hành đăng nhập

                        // Lấy role code (manager/student)
                        String roleCode = result.getString("code");
                        GetData.username = enteredUsername;

                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Thông báo");
                        alert.setHeaderText(null);
                        alert.setContentText("Đăng nhập thành công!");
                        alert.showAndWait();

                        loginBtr.getScene().getWindow().hide();

                        // Phân quyền dựa vào roleCode
                        Parent root = null;
                        if ("manager".equals(roleCode)) {
                            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("dashboardManager.fxml"))); // Quản lý
                        } else if ("student".equals(roleCode)) {
                            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("dashboardStudent.fxml"))); // Sinh viên
                        }

                        if (root != null) {
                            Stage stage = new Stage();
                            Scene scene = new Scene(root);

                            // Xử lý di chuyển cửa sổ
                            root.setOnMousePressed((MouseEvent event) -> {
                                x = event.getSceneX();
                                y = event.getSceneY();
                            });

                            root.setOnMouseDragged((MouseEvent event) -> {
                                stage.setX(event.getScreenX() - x);
                                stage.setY(event.getScreenY() - y);
                            });

                            stage.initStyle(StageStyle.TRANSPARENT);
                            stage.setScene(scene);
                            stage.show();
                        }
                    } else {
                        // Mật khẩu không khớp
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Thông báo lỗi");
                        alert.setHeaderText(null);
                        alert.setContentText("Tên đăng nhập hoặc mật khẩu không đúng.");
                        alert.showAndWait();
                    }
                } else {
                    // Không tìm thấy người dùng hoặc tài khoản không hoạt động
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Thông báo lỗi");
                    alert.setHeaderText(null);
                    alert.setContentText("Tên đăng nhập hoặc mật khẩu không đúng.");
                    alert.showAndWait();
                }

                // Đóng kết quả và câu lệnh
                result.close();
                prepare.close();
            }

            // Đóng kết nối
            connect.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openForgotPasswordForm() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("forgotPassword.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openRegisterForm() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("register.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void minimizeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    // Phương thức này giới hạn đầu vào chỉ cho phép số
    public void numbersOnly(KeyEvent event) {
        if (event.getCharacter().matches("[^\\e\\t\\r\\d+$]")) {
            event.consume();
            username.setStyle("-fx-border-color:#e04040");
        } else {
            username.setStyle("-fx-border-color:#fff");
        }
    }

    public void close() {
        System.exit(0);
    }
}
