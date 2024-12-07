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
