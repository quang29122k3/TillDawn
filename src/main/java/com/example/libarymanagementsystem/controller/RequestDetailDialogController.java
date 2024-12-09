package com.example.libarymanagementsystem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class RequestDetailDialogController {

    @FXML
    private TextArea subjectTextArea;

    @FXML
    private TextArea contentTextArea;

    /**
     * Thiết lập dữ liệu cho dialog.
     *
     * @param subject  Chủ đề của yêu cầu
     * @param content  Nội dung của yêu cầu
     */
    public void setRequestDetails(String subject, String content) {
        subjectTextArea.setText(subject);
        contentTextArea.setText(content);
    }

    /**
     * Xử lý sự kiện đóng dialog.
     */
    @FXML
    private void handleClose() {
        // Đóng cửa sổ hiện tại
        Stage stage = (Stage) subjectTextArea.getScene().getWindow();
        stage.close();
    }
}