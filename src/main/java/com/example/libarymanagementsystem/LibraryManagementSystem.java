package com.example.libarymanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LibraryManagementSystem extends Application {

    private double x = 0;
    private double y = 0;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryManagementSystem.class.getResource("/com/example/libarymanagementsystem/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 450);
        // Đặt tiêu đề cho cửa sổ
        stage.setTitle("Hello!");

        //Áp dụng các hiệu ứng CSS
        stage.setScene(scene);

        // Xử lý sự kiện kéo thả cửa sổ bằng chuột
        scene.getRoot().setOnMousePressed((MouseEvent event) -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });

        scene.getRoot().setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
            stage.setOpacity(0.8); // Giảm độ trong suốt khi kéo
        });

        scene.getRoot().setOnMouseReleased((MouseEvent event) -> {
            stage.setOpacity(1); // Trả lại độ trong suốt khi nhả chuột
        });

        // Bỏ khung cửa sổ (không có thanh tiêu đề và khung điều khiển)
        stage.initStyle(StageStyle.TRANSPARENT);

        // Hiển thị cửa sổ
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}