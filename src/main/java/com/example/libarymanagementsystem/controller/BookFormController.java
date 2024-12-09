package com.example.libarymanagementsystem.controller;

import com.example.libarymanagementsystem.model.Book;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class BookFormController {

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private TextField availableField;

    @FXML
    private TextField totalCopiesField;

    @FXML
    private ImageView imageView;

    private Book book;
    private DashBoardControllerManager mainController;

    private String imagePath; // Đường dẫn hình ảnh

    public void setBook(Book book) {
        this.book = book;
        if (book != null) {
            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            availableField.setText(String.valueOf(book.getAvailable()));
            totalCopiesField.setText(String.valueOf(book.getTotalCopies()));

            // Kiểm tra và đặt hình ảnh dựa trên imagePath
            if (book.getImagePath() != null && !book.getImagePath().trim().isEmpty()) {
                try {
                    Image image = new Image(book.getImagePath(), 50, 50, false, true);
                    imageView.setImage(image);
                    imagePath = book.getImagePath();
                } catch (IllegalArgumentException e) {
                    imageView.setImage(getDefaultImageView().getImage());
                    imagePath = null;
                }
            } else {
                imageView.setImage(getDefaultImageView().getImage());
                imagePath = null;
            }
        }
    }

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
        return  new ImageView();
    }

    public void setMainController(DashBoardControllerManager mainController) {
        this.mainController = mainController;
    }

    /**
     * Phuong thuc luu thong tin sach.
     */
    @FXML
    private void handleSave() {
        String title = titleField.getText();
        String author = authorField.getText();
        int available;
        int totalCopies;

        // Kiểm tra và parse số lượng
        try {
            available = Integer.parseInt(availableField.getText());
            totalCopies = Integer.parseInt(totalCopiesField.getText());
        } catch (NumberFormatException e) {
            showAlert("Vui lòng nhập số hợp lệ cho số lượng sách.");
            return;
        }

        if (title.isEmpty() || author.isEmpty() || imagePath == null || imagePath.isEmpty()) {
            showAlert("Vui lòng điền đầy đủ thông tin và chọn hình ảnh.");
            return;
        }

        System.out.println("Saving Book with Image Path: " + imagePath); // Logging

        if (book == null) {
            // Thêm sách mới
            Book newBook = new Book(0, title, author, available, totalCopies, imagePath); // ID = 0 sẽ tự động tăng
            mainController.addBook(newBook);
        } else {
            // Cập nhật sách
            book.setTitle(title);
            book.setAuthor(author);
            book.setAvailable(available);
            book.setTotalCopies(totalCopies);
            book.setImagePath(imagePath);
            mainController.updateBook(book);
        }

        // Đóng cửa sổ sau khi lưu
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    /**
     * Phuong thuc huy.
     */

    @FXML
    private void handleCancel() {
        // Đóng cửa sổ
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    /**
     * Phuong thuc chon hinh anh.
     */

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn Hình Ảnh");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(titleField.getScene().getWindow());
        if (selectedFile != null) {
            imagePath = selectedFile.toURI().toString();
            System.out.println("Selected Image Path: " + imagePath); // Logging
            Image image = new Image(imagePath, 100, 150, false, true);
            imageView.setImage(image);
        }
    }

    /**
     * Phuong thuc tra ra canh bao.
     */

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}