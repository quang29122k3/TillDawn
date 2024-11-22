package com.example.libarymanagementsystem;

public class Person {
    private String id;
    private String fullname;
    private String className;
    private String status;

    public Person(String id, String fullname, String className, String status) {
        this.id = id;
        this.fullname = fullname;
        this.className = className;
        this.status = status;
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