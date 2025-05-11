package com.assignment.service;

import com.assignment.entity.User;
import com.assignment.entity.UserProfile;

import java.util.List;

public interface UserService {
    List<User> loadAll();
    User findByUsername(String username);
//    User save(User user);
    Boolean existsByUsername(String username);
    void delete(String username);
    List<User> searchUsers(String keyword);
    User updateUser(User user);
    User createUser(User user);
}
