package com.assignment.service.impl;

import com.assignment.dao.AuthorityDAO;
import com.assignment.entity.Authority;
import com.assignment.entity.User;
import com.assignment.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorityServiceImpl implements AuthorityService {
    @Autowired
    private AuthorityDAO authorityDAO;

    @Override
    public List<Authority> findAll() {
        return authorityDAO.findAll();
    }

    @Override
    public Authority findById(Long id) {
        return authorityDAO.findById(id).orElse(null);
    }

    @Override
//    @Transactional
    public Authority save(Authority authority) {
        return authorityDAO.save(authority);
    }

    @Override
    public Boolean existsById(Long id) {
        return authorityDAO.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        authorityDAO.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteByUser(User user) {
        authorityDAO.deleteByUser(user);
    }
}
