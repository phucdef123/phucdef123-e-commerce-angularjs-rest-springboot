package com.assignment.service;

import com.assignment.entity.UserProfile;

import java.util.List;

public interface UserProfileService {
    List<UserProfile> loadAll();
    UserProfile findByUsername(String username);
    UserProfile findByEmail(String email);
    UserProfile save(UserProfile userProfile);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    void delete(String username);
}
