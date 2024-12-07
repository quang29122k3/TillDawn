package com.example.libarymanagementsystem.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Lớp BookExists được sử dụng để kiểm tra sự tồn tại của sách trong cơ sở dữ liệu.
 */
public class BookExists {

    private String title;
    private Integer excludeBookId; // ID của cuốn sách để loại trừ (sử dụng khi cập nhật)

    /**
     * Constructor để khởi tạo đối tượng BookExists với tiêu đề sách cần kiểm tra.
     *
     * @param title Tiêu đề của sách cần kiểm tra.
     */
    public BookExists(String title) {
        this.title = title;
        this.excludeBookId = null;
    }

    /**
     * Constructor để khởi tạo đối tượng BookExists với tiêu đề sách cần kiểm tra và ID cuốn sách để loại trừ.
     *
     * @param title         Tiêu đề của sách cần kiểm tra.
     * @param excludeBookId ID của cuốn sách để loại trừ khỏi kiểm tra (sử dụng khi cập nhật).
     */
    public BookExists(String title, Integer excludeBookId) {
        this.title = title;
        this.excludeBookId = excludeBookId;
    }

    /**
     * Phương thức kiểm tra sự tồn tại của sách trong cơ sở dữ liệu.
     *
     * @return true nếu sách tồn tại, false nếu không.
     */
    public boolean exists() {
        String query;
        if (excludeBookId == null) {
            query = "SELECT COUNT(*) FROM books WHERE title = ?";
        } else {
            query = "SELECT COUNT(*) FROM books WHERE title = ? AND id != ?";
        }

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, this.title);
            if (excludeBookId != null) {
                pstmt.setInt(2, excludeBookId);
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Bạn có thể xử lý ngoại lệ theo cách khác nếu cần
        }
        return false;
    }

    // Getter và Setter (nếu cần)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getExcludeBookId() {
        return excludeBookId;
    }

    public void setExcludeBookId(Integer excludeBookId) {
        this.excludeBookId = excludeBookId;
    }
}
