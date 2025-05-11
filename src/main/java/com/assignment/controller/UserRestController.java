package com.assignment.controller;

import com.assignment.entity.User;
import com.assignment.entity.UserProfile;
import com.assignment.service.UserProfileService;
import com.assignment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class UserRestController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> searchUsers(@RequestParam(required = false) String keyword) {
        List<User> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getByUsername(@PathVariable("username") String username) {
        if (username == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!userService.existsByUsername(username)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @PostMapping
    public ResponseEntity<User> post(@RequestBody User user) {
        if (user.getUsername() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (userService.existsByUsername(user.getUsername())){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{username}")
    public ResponseEntity<User> put(@RequestBody User user, @PathVariable("username") String username) {
        if (user.getUsername() == null || !user.getUsername().equals(username)) {
            return ResponseEntity.badRequest().build();
        }
        if (!userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        User updatedUser = userService.updateUser(user);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> delete(@PathVariable("username") String username) {
        if (username == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!userService.existsByUsername(username)) {
            return ResponseEntity.badRequest().build();
        }
        userService.delete(username);
        return ResponseEntity.ok().build();
    }
}
