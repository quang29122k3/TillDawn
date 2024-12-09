package com.example.libarymanagementsystem.model;

public class Person {
    private String id;
    private String fullname;
    private String className;
    private String status;
    private String email; // Thêm thuộc tính email
    private String role;

    public Person(String id, String fullname, String className, String status) {
        this.id = id;
        this.fullname = fullname;
        this.className = className;
        this.status = status;
    }

    public Person(String id, String fullname, String className, String status, String email, String role) {
        this.id = id;
        this.fullname = fullname;
        this.className = className;
        this.status = status;
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Getters và setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}