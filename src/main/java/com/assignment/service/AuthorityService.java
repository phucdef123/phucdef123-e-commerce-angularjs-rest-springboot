package com.assignment.service;

import com.assignment.entity.Authority;
import com.assignment.entity.Category;
import com.assignment.entity.User;

import java.util.List;

public interface AuthorityService {
    List<Authority> findAll();
    Authority findById(Long id);
    Authority save(Authority authority);
    Boolean existsById(Long id);
    void deleteById(Long id);
    void deleteByUser(User user);
}
