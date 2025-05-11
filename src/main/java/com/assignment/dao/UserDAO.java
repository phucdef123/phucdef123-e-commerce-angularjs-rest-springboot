package com.assignment.dao;

import com.assignment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, String>{
}
