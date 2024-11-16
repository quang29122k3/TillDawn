package com.example.libarymanagementsystem;

import com.example.libarymanagementsystem.utils.ConnectionJDBCUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// Import các lớp từ javax.mail
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

// Import java.sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.Random;

public class ForgotPasswordController {
    @FXML
    private TextField usernameField;

    @FXML
    private Button submitButton;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    // Thêm thuộc tính để lưu trữ mã xác nhận
    private String resetCode;

    public void sendResetPassword() {
        String username = usernameField.getText();

        Alert alert;

        if (username.isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Vui lòng nhập tên đăng nhập hoặc email.");
            alert.showAndWait();
        } else {
            try {
                connect = ConnectionJDBCUtils.getConnection();

                // Kiểm tra xem người dùng có tồn tại không
                String checkUser = "SELECT * FROM person WHERE id = ?";
                prepare = connect.prepareStatement(checkUser);
                prepare.setString(1, username);
                result = prepare.executeQuery();

                if (result.next()) {
                    // Lấy email của người dùng
                    String email = result.getString("email");
                    resetCode = generateResetCode();
                    sendResetPasswordEmail(email, resetCode);
                    storeResetCode(username, resetCode);

                    // Mở giao diện đặt lại mật khẩu
                    openResetPasswordForm(username);

                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Người dùng không tồn tại.");
                    alert.showAndWait();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendResetPasswordEmail(String email, String resetCode) {
        // Thông tin tài khoản email gửi đi
        final String fromEmail = "salon462003@gmail.com";
        final String password = "bucp txjz rhvx mzpf";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP Host
        props.put("mail.smtp.port", "587"); // TLS Port
        props.put("mail.smtp.auth", "true"); // Enable authentication
        props.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS

        // Tạo đối tượng Session
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });
        try {
            // Sử dụng tên đầy đủ cho MimeMessage để tránh xung đột
            javax.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Đặt lại mật khẩu");
            message.setText("Mã xác nhận đặt lại mật khẩu của bạn là: " + resetCode);

            // Gửi email
            Transport.send(message);

            System.out.println("Email đã được gửi thành công!");
        } catch (MessagingException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Đã xảy ra lỗi khi gửi email. Vui lòng thử lại.");
            alert.showAndWait();
        }
    }

    private String generateResetCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void storeResetCode(String username, String resetCode) {
        try {
            String sql = "UPDATE person SET reset_code = ?, reset_code_expiry = ? WHERE id = ?";
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, resetCode);
            // Thời gian hết hạn là 15 phút kể từ hiện tại
            Timestamp expiryTime = new Timestamp(System.currentTimeMillis() + 15 * 60 * 1000);
            prepare.setTimestamp(2, expiryTime);
            prepare.setString(3, username);
            prepare.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void openResetPasswordForm(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resetPassword.fxml"));
            Parent root = loader.load();

            // Truyền tên đăng nhập sang controller của resetPassword
            ResetPasswordController controller = loader.getController();
            controller.setUsername(username);

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();

            // Đóng cửa sổ hiện tại nếu cần
            submitButton.getScene().getWindow().hide();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
