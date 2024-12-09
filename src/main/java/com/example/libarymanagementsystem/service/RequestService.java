package com.example.libarymanagementsystem.service;

import com.example.libarymanagementsystem.model.Request;

import java.util.List;

public interface RequestService {
    List<Request> getAllRequests();
    void sendRequest(String personId, String subject, String content) throws Exception;
    void processRequest(int requestId) throws Exception;
    void deleteProcessedRequests() throws Exception;
    List<Request> searchRequests(String keyword);
}
