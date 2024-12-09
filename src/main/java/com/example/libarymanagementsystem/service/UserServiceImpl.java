package com.example.libarymanagementsystem.service;

import com.example.libarymanagementsystem.model.Person;
import com.example.libarymanagementsystem.utils.ConnectionJDBCUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    @Override
    public Person getUserInfo(String userId) {
        Person user = null;
        String query = "SELECT p.id, p.fullname, p.class, p.email, r.name AS role " +
                "FROM person p JOIN role r ON p.role_id = r.id " +
                "WHERE p.id = ?";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String fullname = rs.getString("fullname");
                String className = rs.getString("class");
                String email = rs.getString("email");
                String role = rs.getString("role");
                String status = "active"; // Hoặc lấy giá trị từ cơ sở dữ liệu nếu có

                user = new Person(userId, fullname, className, status, email, role);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public void updateUserInfo(Person user) throws Exception {
        String query = "UPDATE person SET fullname = ?, class = ?, email = ? WHERE id = ?";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getFullname());
            pstmt.setString(2, user.getClassName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new Exception("Không tìm thấy người dùng để cập nhật.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Đã xảy ra lỗi khi cập nhật thông tin người dùng.");
        }
    }

    @Override
    public List<Person> getAllMembers() {
        List<Person> members = new ArrayList<>();
        String query = "SELECT id, fullname, class, is_active FROM person";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String fullname = rs.getString("fullname");
                String className = rs.getString("class");
                boolean isActive = rs.getBoolean("is_active");

                String status = isActive ? "Kích Hoạt" : "Bị Chặn";
                Person member = new Person(id, fullname, className, status);
                members.add(member);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }

    @Override
    public void blockMember(String memberId) throws Exception {
        String query = "UPDATE person SET is_active = 0 WHERE id = ?";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, memberId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new Exception("Không tìm thấy thành viên để chặn.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Đã xảy ra lỗi khi chặn thành viên.");
        }
    }

    @Override
    public void unblockMember(String memberId) throws Exception {
        String query = "UPDATE person SET is_active = 1 WHERE id = ?";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, memberId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new Exception("Không tìm thấy thành viên để mở chặn.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Đã xảy ra lỗi khi mở chặn thành viên.");
        }
    }

    @Override
    public List<Person> searchMembers(String keyword) {
        List<Person> members = new ArrayList<>();
        String query = "SELECT id, fullname, class, is_active FROM person WHERE id LIKE ? OR fullname LIKE ? OR class LIKE ?";

        try (Connection conn = ConnectionJDBCUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String likeKeyword = "%" + keyword + "%";
            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String fullname = rs.getString("fullname");
                String className = rs.getString("class");
                boolean isActive = rs.getBoolean("is_active");

                String status = isActive ? "Kích Hoạt" : "Bị Chặn";
                Person member = new Person(id, fullname, className, status);
                members.add(member);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }
}
