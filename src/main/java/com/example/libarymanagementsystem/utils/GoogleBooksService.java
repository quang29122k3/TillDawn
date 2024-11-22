package com.example.libarymanagementsystem.utils;

import com.example.libarymanagementsystem.BookItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GoogleBooksService {

    private static final String API_KEY = "AIzaSyCTEdyr1amYApZwpsNRpT-C0wu9UAL6m5c";

    public static List<BookItem> searchBooks(String query) {
        List<BookItem> googleBooksList = new ArrayList<>();
        try {
            // Build the API URL with the key
            String apiUrl = "https://www.googleapis.com/books/v1/volumes?q="
                    + query.replace(" ", "+")
                    + "&key=" + API_KEY;

            URL url = new URL(apiUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray items = jsonResponse.optJSONArray("items");

                if (items != null) {
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject volumeInfo = items.getJSONObject(i).optJSONObject("volumeInfo");

                        if (volumeInfo != null) {
                            String title = volumeInfo.optString("title", "N/A");
                            JSONArray authorsArray = volumeInfo.optJSONArray("authors");
                            String authors = authorsArray != null ? String.join(", ", authorsArray.toList().toArray(new String[0])) : "N/A";
                            String publisher = volumeInfo.optString("publisher", "N/A");
                            String infoLink = volumeInfo.optString("infoLink", "N/A");

                            googleBooksList.add(new BookItem(title, authors, publisher, infoLink));
                        }
                    }
                }
            } else {
                System.err.println("Error: Unable to fetch data from Google Books API. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return googleBooksList;
    }
}