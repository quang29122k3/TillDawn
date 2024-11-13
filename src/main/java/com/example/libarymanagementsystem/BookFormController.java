package com.example.libarymanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

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
            // Điền thông tin sách vào các trường
            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            availableField.setText(String.valueOf(book.getAvailable()));
            totalCopiesField.setText(String.valueOf(book.getTotalCopies()));
            imageView.setImage(book.getImageView().getImage());
            imagePath = book.getImageView().getImage().getUrl();
        }
    }

    public void setMainController(DashBoardControllerManager mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleSave() {
        String title = titleField.getText();
        String author = authorField.getText();
        int available = Integer.parseInt(availableField.getText());
        int totalCopies = Integer.parseInt(totalCopiesField.getText());

        if (title.isEmpty() || author.isEmpty() || imagePath == null || imagePath.isEmpty()) {
            showAlert("Vui lòng điền đầy đủ thông tin và chọn hình ảnh.");
            return;
        }

        ImageView bookImageView = new ImageView(new Image(imagePath, 50, 50, false, true));

        if (book == null) {
            // Thêm sách mới
            Book newBook = new Book(title, author, available, bookImageView, totalCopies);
            mainController.addBook(newBook);
        } else {
            // Cập nhật sách
            book.setTitle(title);
            book.setAuthor(author);
            book.setAvailable(available);
            book.setTotalCopies(totalCopies);
            book.setImageView(bookImageView);
            mainController.updateBook(book);
        }

        // Đóng cửa sổ sau khi lưu
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        // Đóng cửa sổ
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

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
            Image image = new Image(imagePath, 100, 150, false, true);
            imageView.setImage(image);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
