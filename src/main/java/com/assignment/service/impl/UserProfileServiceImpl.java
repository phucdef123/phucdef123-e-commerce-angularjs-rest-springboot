package com.assignment.service.impl;

import com.assignment.dao.UserProfileDAO;
import com.assignment.entity.UserProfile;
import com.assignment.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    @Autowired
    UserProfileDAO userProfileDAO;
    @Override
    public List<UserProfile> loadAll() {
        return userProfileDAO.findAll();
    }

    @Override
    public UserProfile findByUsername(String username) {
        return userProfileDAO.findById(username).orElse(null);
    }

    @Override
    public UserProfile findByEmail(String email) {
        return userProfileDAO.findByEmail(email);
    }

    @Override
//    @Transactional
    public UserProfile save(UserProfile userProfile) {
        return userProfileDAO.save(userProfile);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userProfileDAO.existsById(username);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userProfileDAO.existsByEmail(email);
    }

    @Override
    public void delete(String username) {
        userProfileDAO.deleteById(username);
    }
}
