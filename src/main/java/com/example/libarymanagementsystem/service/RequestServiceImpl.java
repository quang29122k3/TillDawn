package com.example.libarymanagementsystem.service;

import com.example.libarymanagementsystem.model.Request;
import com.example.libarymanagementsystem.utils.ConnectionJDBCUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestServiceImpl implements RequestService {

    @Override
    public List<Request> getAllRequests() {
        List<Request> requestsList = new ArrayList<>();
        String query = "SELECT r.*, p.fullname, p.class FROM requests r JOIN person p ON r.person_id = p.id";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String personId = rs.getString("person_id");
                String senderName = rs.getString("fullname");
                String senderClass = rs.getString("class");
                String subject = rs.getString("subject");
                String content = rs.getString("content");
                String status = rs.getString("status");

                Request request = new Request(id, personId, senderName, senderClass, subject, content, status);
                requestsList.add(request);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return requestsList;
    }

    @Override
    public void sendRequest(String personId, String subject, String content) throws Exception {
        String insertQuery = "INSERT INTO requests (person_id, subject, content) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, personId);
            pstmt.setString(2, subject);
            pstmt.setString(3, content);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Đã xảy ra lỗi khi gửi yêu cầu.");
        }
    }

    @Override
    public void processRequest(int requestId) throws Exception {
        String updateQuery = "UPDATE requests SET status = 'Đã xử lý' WHERE id = ?";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setInt(1, requestId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new Exception("Không tìm thấy yêu cầu để xử lý.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Đã xảy ra lỗi khi xử lý yêu cầu.");
        }
    }

    @Override
    public void deleteProcessedRequests() throws Exception {
        String deleteQuery = "DELETE FROM requests WHERE status = 'Đã xử lý'";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Đã xảy ra lỗi khi xóa yêu cầu đã xử lý.");
        }
    }

    @Override
    public List<Request> searchRequests(String keyword) {
        List<Request> requestsList = new ArrayList<>();
        String query;
        boolean isNumber = keyword.matches("\\d+");

        if (isNumber) {
            query = "SELECT r.*, p.fullname, p.class FROM requests r JOIN person p ON r.person_id = p.id " +
                    "WHERE r.id = ? OR r.subject LIKE ?";
        } else {
            query = "SELECT r.*, p.fullname, p.class FROM requests r JOIN person p ON r.person_id = p.id " +
                    "WHERE r.subject LIKE ?";
        }

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            if (isNumber) {
                pstmt.setInt(1, Integer.parseInt(keyword));
                pstmt.setString(2, "%" + keyword + "%");
            } else {
                pstmt.setString(1, "%" + keyword + "%");
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String personId = rs.getString("person_id");
                String senderName = rs.getString("fullname");
                String senderClass = rs.getString("class");
                String subject = rs.getString("subject");
                String content = rs.getString("content");
                String status = rs.getString("status");

                Request request = new Request(id, personId, senderName, senderClass, subject, content, status);
                requestsList.add(request);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return requestsList;
    }
}