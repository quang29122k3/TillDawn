package com.example.libarymanagementsystem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BorrowDurationDialogController {

    @FXML
    private TextField daysField;

    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @FXML
    private Button closeButton; // Nút Đóng
    @FXML
    private Button minimizeButton; // Nút Thu nhỏ

    private int days = 0;
    private boolean confirmed = false;

    @FXML
    private void handleOk() {
        try {
            days = Integer.parseInt(daysField.getText().trim());
            if (days <= 0) {
                // Hiển thị cảnh báo nếu cần
                return;
            }
            confirmed = true;
            closeWindow();
        } catch (NumberFormatException e) {
            // Hiển thị cảnh báo "Vui lòng nhập số nguyên"
        }
    }

    @FXML
    private void handleCancel() {
        confirmed = false;
        closeWindow();
    }

    @FXML
    private void handleClose() {
        // Đóng cửa sổ
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleMinimize() {
        // Thu nhỏ cửa sổ
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    private void closeWindow() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    public int getDays() {
        return days;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
