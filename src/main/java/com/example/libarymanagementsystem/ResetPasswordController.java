package com.example.libarymanagementsystem;

import org.mindrot.jbcrypt.BCrypt;
import com.example.libarymanagementsystem.utils.ConnectionJDBCUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class ResetPasswordController {
    @FXML
    private TextField codeField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button verifyCodeButton;

    @FXML
    private Button resetButton;

    @FXML
    private Label newPasswordLabel;

    @FXML
    private Label confirmPasswordLabel;

    private String username;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    public void setUsername(String username) {
        this.username = username;
    }
    public void verifyCode() {
        String code = codeField.getText().trim(); // Loại bỏ khoảng trắng đầu và cuối

        Alert alert;

        if (code.isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Vui lòng nhập mã xác nhận.");
            alert.showAndWait();
        } else {
            try {
                connect = ConnectionJDBCUtils.getConnection();

                // Truy vấn lấy mã xác nhận và thời gian hết hạn từ cơ sở dữ liệu
                String sql = "SELECT reset_code, reset_code_expiry FROM person WHERE id = ?";
                prepare = connect.prepareStatement(sql);
                prepare.setString(1, username);
                result = prepare.executeQuery();

                if (result.next()) {
                    String databaseResetCode = result.getString("reset_code");
                    Timestamp expiryTime = result.getTimestamp("reset_code_expiry");

                    // Lấy thời gian hiện tại
                    Timestamp currentTime = new Timestamp(System.currentTimeMillis());

                    // Kiểm tra mã xác nhận
                    if (databaseResetCode != null && databaseResetCode.equals(code)) {
                        // Kiểm tra thời gian hết hạn
                        if (expiryTime != null && expiryTime.after(currentTime)) {
                            // Mã xác nhận hợp lệ và chưa hết hạn
                            alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Mã xác nhận đúng. Vui lòng nhập mật khẩu mới.");
                            alert.showAndWait();

                            // Ẩn trường mã xác nhận và nút xác minh
                            codeField.setVisible(false);
                            verifyCodeButton.setVisible(false);

                            // Hiển thị trường nhập mật khẩu mới và nút đặt lại mật khẩu
                            newPasswordLabel.setVisible(true);
                            newPasswordField.setVisible(true);
                            confirmPasswordLabel.setVisible(true);
                            confirmPasswordField.setVisible(true);
                            resetButton.setVisible(true);

                        } else {
                            // Mã xác nhận đã hết hạn
                            alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Mã xác nhận đã hết hạn.");
                            alert.showAndWait();
                        }
                    } else {
                        // Mã xác nhận không khớp
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Mã xác nhận không chính xác.");
                        alert.showAndWait();
                    }
                } else {
                    // Không tìm thấy người dùng
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Người dùng không tồn tại.");
                    alert.showAndWait();
                }

            } catch (Exception e) {
                e.printStackTrace();
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Đã xảy ra lỗi. Vui lòng thử lại.");
                alert.showAndWait();
            }
        }
    }

    public void resetPassword() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        Alert alert;

        if (newPassword.isEmpty()){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Vui lòng nhập mật khẩu mới.");
            alert.showAndWait();
        }else if (confirmPassword.isEmpty()){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Xác thực mật khẩu mới");
            alert.showAndWait();
        } else if (!newPassword.equals(confirmPassword)){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("gà thế nhập 2 cái mật khẩu cũng sai");
            alert.showAndWait();
        }else {
            try {
                String encryptedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                connect = ConnectionJDBCUtils.getConnection();
                String updatePassword = "UPDATE person SET password = ?, reset_code = NULL, reset_code_expiry = NULL WHERE id = ?";
                prepare = connect.prepareStatement(updatePassword);
                prepare.setString(1, encryptedPassword);
                prepare.setString(2, username);
                prepare.executeUpdate();

                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Mật khẩu đã được đặt lại thành công.");
                alert.showAndWait();

                resetButton.getScene().getWindow().hide();
            }catch (Exception e) {
                e.printStackTrace();
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Đã xảy ra lỗi. Vui lòng thử lại.");
                alert.showAndWait();
            }
        }
    }
}