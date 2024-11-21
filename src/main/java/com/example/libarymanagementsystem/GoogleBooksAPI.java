//package com.example.libarymanagementsystem;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
//public class GoogleBooksAPI {
//    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
//
//    public static List<Book> searchBooks(String query) {
//        List<Book> books = new ArrayList<>();
//        try {
//            String urlString = API_URL + query.replace(" ", "+") + "&key=AIzaSyCTEdyr1amYApZwpsNRpT-C0wu9UAL6m5c";  // API key của bạn
//            URL url = new URL(urlString);
//
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.connect();
//
//            int responseCode = connection.getResponseCode();
//            if (responseCode != 200) {
//                System.out.println("API Error: " + responseCode);
//                return books;
//            }
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            StringBuilder response = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                response.append(line);
//            }
//            reader.close();
//
//            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
//            JsonArray items = jsonObject.getAsJsonArray("items");
//
//            if (items != null) {
//                for (JsonElement item : items) {
//                    JsonObject bookJson = item.getAsJsonObject().getAsJsonObject("volumeInfo");
//
//                    // Tựa sách
//                    String title = bookJson.has("title") ? bookJson.get("title").getAsString() : "N/A";
//
//                    // Tác giả
//                    String authors = "N/A";
//                    if (bookJson.has("authors")) {
//                        JsonArray authorsArray = bookJson.getAsJsonArray("authors");
//                        List<String> authorList = new ArrayList<>();
//                        for (JsonElement author : authorsArray) {
//                            authorList.add(author.getAsString());
//                        }
//                        authors = String.join(", ", authorList);
//                    }
//
//                    // Nhà xuất bản
//                    String publisher = bookJson.has("publisher") ? bookJson.get("publisher").getAsString() : "N/A";
//
//                    books.add(new Book(title, authors, publisher));
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return books;
//    }
//}