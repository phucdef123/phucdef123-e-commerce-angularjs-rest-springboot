package com.assignment.dao;

import com.assignment.entity.User;
import com.assignment.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProfileDAO extends JpaRepository<UserProfile, String> {
    UserProfile findByEmail(String email);
    Boolean existsByEmail(String email);
}
