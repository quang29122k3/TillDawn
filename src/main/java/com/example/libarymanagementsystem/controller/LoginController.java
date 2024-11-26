package com.example.libarymanagementsystem.controller;

import com.example.libarymanagementsystem.model.GetData;
import com.example.libarymanagementsystem.utils.ConnectionJDBCUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.input.KeyEvent;
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
    private AnchorPane main_form;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    @FXML
    private Button minimize;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private double x = 0;
    private double y = 0;

    public void loginAdmin() {
        // Cập nhật query để lấy role từ bảng `role`
        String sql = "SELECT person.id, person.password, role.code, person.is_active " +
                "FROM person " +
                "INNER JOIN role ON person.role_id = role.id " +
                "WHERE person.id = ? ";

        try {
            connect = ConnectionJDBCUtils.getConnection();
            Alert alert;
            String enteredUsername = username.getText().trim();
            String enteredPassword = password.getText().trim();

            if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
            } else {
                prepare = connect.prepareStatement(sql);
                prepare.setString(1, enteredUsername);
                result = prepare.executeQuery();
                if (result.next()) {
                    String storedHashedPassword = result.getString("password");
                    if (BCrypt.checkpw(enteredPassword, storedHashedPassword)) {
                        // Lấy role code (manager/student)
                        String roleCode = result.getString("code");
                        GetData.setUsername(enteredUsername);
//                     Kiểm tra trạng thái tài khoản
                        boolean isActive = result.getBoolean("is_active");

                        if (!isActive) {
                            alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Thông báo");
                            alert.setHeaderText(null);
                            alert.setContentText("Tài khoản của bạn đã bị chặn. Vui lòng liên hệ quản trị viên!");
                            alert.showAndWait();
                            return; // Kết thúc nếu tài khoản bị chặn
                        }
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information Message");
                        alert.setHeaderText(null);
                        alert.setContentText("Successfully Login!");
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
                    }

                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Wrong Username/Password");
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void numbersOnly(KeyEvent event) {

        if (event.getCharacter().matches("[^\\e\t\r\\d+$]")) {
            event.consume();

            username.setStyle("-fx-border-color:#e04040");
        } else {
            username.setStyle("-fx-border-color:#fff");
        }

    }


    public void close() {
        System.exit(0);
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


}