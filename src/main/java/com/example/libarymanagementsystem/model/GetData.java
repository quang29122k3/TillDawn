package com.example.libarymanagementsystem.model;

public class GetData {
    private static String username;

    private static String fullName;

    public static String getFullName() {
        return fullName;
    }

    public static void setFullName(String fullName) {
        GetData.fullName = fullName;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        GetData.username = username;
    }
}
