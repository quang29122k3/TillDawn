package com.example.libarymanagementsystem.service;

import com.example.libarymanagementsystem.model.Person;

import java.util.List;

public interface UserService {
    Person getUserInfo(String userId);
    void updateUserInfo(Person user) throws Exception;
    List<Person> getAllMembers();
    void blockMember(String memberId) throws Exception;
    void unblockMember(String memberId) throws Exception;
    List<Person> searchMembers(String keyword);
}
