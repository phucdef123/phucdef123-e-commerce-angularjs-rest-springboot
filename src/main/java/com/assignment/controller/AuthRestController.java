package com.assignment.controller;

import com.assignment.dao.UserDAO;
import com.assignment.entity.Authority;
import com.assignment.entity.User;
import com.assignment.service.AuthorityService;
import com.assignment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/authorities")
public class AuthRestController {
    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private UserService userService;
    @Autowired
    private UserDAO userDAO;

    @GetMapping
    public ResponseEntity<List<Authority>> get() {
        return ResponseEntity.ok(authorityService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Authority> getById(@PathVariable("id") Long id) {
        if (id == null || id <= 0 ) {
            return ResponseEntity.badRequest().build();
        }
        if (!authorityService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(authorityService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Authority> post(@RequestBody Authority authority) {
        if (authority == null || authority.getAuthority() == null
                || authority.getAuthority().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (authority.getId() != null && authorityService.existsById(authority.getId())) {
            return ResponseEntity.badRequest().build();
        }
        if (authority.getUser() == null || authority.getUser().getUsername() == null) {
            return ResponseEntity.badRequest().build();
        }
        User user = userDAO.findById(authority.getUser().getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        authority.setId(null);
        System.out.println(authority.getUser().getUsername());
        return ResponseEntity.ok(authorityService.save(authority));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        if (id == null || id <= 0 ) {
            return ResponseEntity.badRequest().build();
        }
        if (!authorityService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        authorityService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteAuthorities(@PathVariable String username) {
        User user =  userService.findByUsername(username);
        if (user == null) return ResponseEntity.notFound().build();

        authorityService.deleteByUser(user);
        return ResponseEntity.ok().build();
    }

}
