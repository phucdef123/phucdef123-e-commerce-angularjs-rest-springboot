package com.assignment.dao;

import com.assignment.entity.Authority;
import com.assignment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AuthorityDAO extends JpaRepository<Authority, Long> {
    @Query("SELECT DISTINCT a.authority FROM Authority a")
    List<String> findDistinctAuthorities();

    @Modifying
    @Transactional  // Đảm bảo transaction được mở
    @Query("DELETE FROM Authority a WHERE a.user = :user")
    void deleteByUser(@Param("user") User user);
}
