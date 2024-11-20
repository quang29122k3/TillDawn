package com.example.libarymanagementsystem;

import com.example.libarymanagementsystem.utils.ConnectionJDBCUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField, confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private Button registerButton;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    public void registerAccount() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText();

        Alert alert;

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Vui lòng điền đầy đủ thông tin.");
            alert.showAndWait();
        } else if (!password.equals(confirmPassword)) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Mật khẩu xác nhận không khớp.");
            alert.showAndWait();
        } else {
            try {
                connect = ConnectionJDBCUtils.getConnection();

                // Kiểm tra xem tên đăng nhập đã tồn tại chưa
                String checkUser = "SELECT * FROM person WHERE id = ?";
                prepare = connect.prepareStatement(checkUser);
                prepare.setString(1, username);
                result = prepare.executeQuery();

                if (result.next()) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Tên đăng nhập đã tồn tại.");
                    alert.showAndWait();
                } else {
                    // Thêm người dùng mới vào cơ sở dữ liệu
                    String insert = "INSERT INTO person (id, password, email, role_id, is_active) VALUES (?, ?, ?, ?, ?)";
                    prepare = connect.prepareStatement(insert);
                    prepare.setString(1, username);
                    String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                    prepare.setString(2, encryptedPassword);
                    prepare.setString(3, email);
                    prepare.setInt(4, 2); // Giả sử '2' là role_id cho 'student'
                    prepare.setInt(5, 1); // is_active = 1

                    prepare.executeUpdate();

                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Đăng ký thành công!");
                    alert.showAndWait();

                    // Đóng cửa sổ đăng ký và quay lại màn hình đăng nhập
                    registerButton.getScene().getWindow().hide();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
