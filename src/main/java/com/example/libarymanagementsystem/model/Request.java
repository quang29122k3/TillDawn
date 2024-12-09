package com.example.libarymanagementsystem.model;

public class Request {
    private int id;
    private String personId;
    private String senderName;
    private String senderClass;
    private String subject;
    private String content;
    private String status;

    // Constructor với 7 tham số
    public Request(int id, String personId, String senderName, String senderClass, String subject, String content, String status) {
        this.id = id;
        this.personId = personId;
        this.senderName = senderName;
        this.senderClass = senderClass;
        this.subject = subject;
        this.content = content;
        this.status = status;
    }

    // Constructor với 5 tham số (cho Student)
    public Request(int id, String personId, String subject, String content, String status) {
        this.id = id;
        this.personId = personId;
        this.subject = subject;
        this.content = content;
        this.status = status;
        // Các trường senderName và senderClass có thể được thiết lập sau hoặc để mặc định (null)
        this.senderName = null;
        this.senderClass = null;
    }

    // Getters và Setters

    public int getId() {
        return id;
    }

    public String getPersonId() {
        return personId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderClass() {
        return senderClass;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}