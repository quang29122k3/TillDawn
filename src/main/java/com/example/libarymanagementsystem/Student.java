package com.example.libarymanagementsystem;

public class Student {
    private String id;           // ID sinh viên
    private String password;      // Mật khẩu
    private String className;     // Lớp học
    private String fullname;      // Tên đầy đủ
    private int status;           // Trạng thái (1: hoạt động, 0: bị chặn)
    private long roleId;          // ID của role (để phân biệt admin và student)

    // Constructor
    public Student(String id, String password, String className, String fullname, int status, long roleId) {
        this.id = id;
        this.password = password;
        this.className = className;
        this.fullname = fullname;
        this.status = status;
        this.roleId = roleId;
    }

    // Getter và Setter cho các thuộc tính
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    // Phương thức kiểm tra nếu sinh viên có quyền hoạt động
    public boolean isActive() {
        return status == 1;
    }

    // Phương thức kiểm tra nếu tài khoản thuộc loại sinh viên
    public boolean isStudent() {
        return roleId == 1; // Giả sử 1 là mã ID của role 'student' trong bảng role
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", fullname='" + fullname + '\'' +
                ", className='" + className + '\'' +
                ", status=" + status +
                '}';
    }

    // Hàm xử lý đăng nhập
//    public void handleLogin(String studentId, String password) {
//        // Giả sử bạn đã kiểm tra tên đăng nhập và mật khẩu hợp lệ
//        String fullName = "John Doe"; // Lấy từ cơ sở dữ liệu
//        String className = "Class A"; // Lấy từ cơ sở dữ liệu
//        int status = 1;  // Trạng thái hoạt động của sinh viên
//
//        // Tạo đối tượng Student với thông tin đăng nhập
//        Student loggedInStudent = new Student(studentId, password, className, fullName, status);
//
//        // Lưu trữ đối tượng Student vào một biến toàn cục hoặc session
//        currentStudent = loggedInStudent; // currentStudent là biến toàn cục hoặc session
//    }

    // Biến toàn cục hoặc session lưu trữ sinh viên đã đăng nhập
    private Student currentStudent;
}
