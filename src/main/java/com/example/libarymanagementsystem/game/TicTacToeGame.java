package com.example.libarymanagementsystem.game;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TicTacToeGame {
    private Button[][] board = new Button[3][3];
    private boolean playerTurn = true; // X's turn
    private Label statusLabel;
    private Button resumeButton;
    private Random random = new Random();
    private Difficulty difficulty; // Default difficulty
    private Runnable onResume;

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    public TicTacToeGame(Runnable onResume) {
        this.onResume = onResume;
    }

    public Parent createContent() {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(400, 300); // giảm kích thước game area

        statusLabel = new Label("Nhấn Bắt đầu trò chơi");
        statusLabel.setFont(Font.font(14));  // giảm font size của label

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);

        // Vẽ bảng với kích thước nhỏ hơn
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button cell = new Button("");
                cell.setPrefSize(80, 80);  // giảm kích thước của các ô
                cell.setFont(Font.font(24));  // giảm font size của các ô
                int r = i, c = j;
                cell.setOnAction(e -> handleClick(r, c));
                board[i][j] = cell;
                grid.add(cell, j, i);
            }
        }

        resumeButton = new Button("Resume");
        resumeButton.setVisible(false);
        resumeButton.setOnAction(e -> resetGame());

        VBox vbox = new VBox(10, statusLabel, grid, resumeButton);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPadding(new Insets(10));

        AnchorPane.setTopAnchor(vbox, 10.0);
        AnchorPane.setLeftAnchor(vbox, 10.0);
        AnchorPane.setRightAnchor(vbox, 10.0);

        root.getChildren().add(vbox);

        statusLabel.setText("Lượt của bạn (X)");
        return root;
    }

    private void handleClick(int row, int col) {
        if (!playerTurn) return;
        if (!board[row][col].getText().isEmpty()) return;

        board[row][col].setText("X");
        playerTurn = false;

        if (checkWin("X")) {
            statusLabel.setText("Bạn (X) thắng!");
            endGame();
            return;
        }

        if (isFull()) {
            statusLabel.setText("Hòa!");
            endGame();
            return;
        }

        statusLabel.setText("Lượt máy (O)");

        makeComputerMove();
    }

    private void makeComputerMove() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].getText().isEmpty()) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }

        if (emptyCells.isEmpty()) {
            statusLabel.setText("Hòa!");
            endGame();
            return;
        }

        int[] cell = emptyCells.get(random.nextInt(emptyCells.size()));

        // AI Logic based on difficulty
        if (difficulty == Difficulty.HARD) {
            makeHardMove();
        } else {
            board[cell[0]][cell[1]].setText("O");
        }

        if (checkWin("O")) {
            statusLabel.setText("Máy (O) thắng!");
            endGame();
            return;
        }

        if (isFull()) {
            statusLabel.setText("Hòa!");
            endGame();
            return;
        }

        statusLabel.setText("Lượt của bạn (X)");
        playerTurn = true;
    }

    private void makeHardMove() {
        int bestVal = Integer.MIN_VALUE;
        int bestMove[] = {-1, -1}; // Chưa có nước đi tối ưu

        // Duyệt qua tất cả các ô và tìm nước đi tối ưu
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].getText().isEmpty()) {
                    // Thử đánh dấu vào ô (i, j)
                    board[i][j].setText("O");
                    int moveVal = minimax(0, false); // Tính giá trị của nước đi này
                    board[i][j].setText(""); // Hoàn tác nước đi

                    // Nếu nước đi này tốt hơn nước đi hiện tại, lưu lại nó
                    if (moveVal > bestVal) {
                        bestMove[0] = i;
                        bestMove[1] = j;
                        bestVal = moveVal;
                    }
                }
            }
        }

        // Đánh dấu vào ô tốt nhất
        board[bestMove[0]][bestMove[1]].setText("O");

        // Kiểm tra nếu máy thắng
        if (checkWin("O")) {
            statusLabel.setText("Máy (O) thắng!");
            endGame();
            return;
        }

        // Kiểm tra nếu bàn cờ đầy
        if (isFull()) {
            statusLabel.setText("Hòa!");
            endGame();
            return;
        }

        statusLabel.setText("Lượt của bạn (X)");
        playerTurn = true;
    }

    private int minimax(int depth, boolean isMax) {
        int score = evaluateBoard();

        // Nếu máy thắng, trả về điểm cao
        if (score == 10) return score;

        // Nếu người chơi thắng, trả về điểm thấp
        if (score == -10) return score;

        // Nếu bàn cờ đầy, trả về 0
        if (isFull()) return 0;

        // Nếu là lượt của máy (Max), chọn nước đi tối ưu
        if (isMax) {
            int best = Integer.MIN_VALUE;
            // Kiểm tra các nước đi có thể và chọn nước đi tối ưu
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].getText().isEmpty()) {
                        board[i][j].setText("O");
                        best = Math.max(best, minimax(depth + 1, false));
                        board[i][j].setText("");
                    }
                }
            }
            return best;
        }
        // Nếu là lượt của người chơi (Min), chọn nước đi tối ưu cho người chơi
        else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].getText().isEmpty()) {
                        board[i][j].setText("X");
                        best = Math.min(best, minimax(depth + 1, true));
                        board[i][j].setText("");
                    }
                }
            }
            return best;
        }
    }

    private int evaluateBoard() {
        if (checkWin("X")) {
            return -10; // Người chơi X thắng => điểm thấp
        }
        if (checkWin("O")) {
            return 10; // Máy O thắng => điểm cao
        }
        return 0; // Hòa => điểm trung bình
    }

    private boolean isFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].getText().isEmpty()) return false;
            }
        }
        return true;
    }

    private boolean checkWin(String player) {
        // check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0].getText().equals(player) &&
                    board[i][1].getText().equals(player) &&
                    board[i][2].getText().equals(player)) return true;
        }

        // check cols
        for (int j = 0; j < 3; j++) {
            if (board[0][j].getText().equals(player) &&
                    board[1][j].getText().equals(player) &&
                    board[2][j].getText().equals(player)) return true;
        }

        // check diagonals
        if (board[0][0].getText().equals(player) &&
                board[1][1].getText().equals(player) &&
                board[2][2].getText().equals(player)) return true;

        if (board[0][2].getText().equals(player) &&
                board[1][1].getText().equals(player) &&
                board[2][0].getText().equals(player)) return true;

        return false;
    }

    private void endGame() {
        resumeButton.setVisible(true);
        playerTurn = false;
    }

    private void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j].setText("");
            }
        }
        playerTurn = true;
        statusLabel.setText("Lượt của bạn (X)");
        resumeButton.setVisible(false);

        // If resume functionality is needed, uncomment the following line
        // if (onResume != null) onResume.run();
    }

    public Button[][] getBoard() {
        return board;
    }

    public void setBoard(Button[][] board) {
        this.board = board;
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(boolean playerTurn) {
        this.playerTurn = playerTurn;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    public Button getResumeButton() {
        return resumeButton;
    }

    public void setResumeButton(Button resumeButton) {
        this.resumeButton = resumeButton;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }


    public Runnable getOnResume() {
        return onResume;
    }

    public void setOnResume(Runnable onResume) {
        this.onResume = onResume;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public TicTacToeGame(Runnable onResume, String selectedDifficulty) {
        this.onResume = onResume;
        this.difficulty = Difficulty.valueOf(selectedDifficulty.toUpperCase()); // Convert string to Difficulty enum
    }

    public TicTacToeGame(Runnable onResume, Difficulty difficulty) {
        this.onResume = onResume;
        this.difficulty = difficulty;  // Store the Difficulty enum
    }
}