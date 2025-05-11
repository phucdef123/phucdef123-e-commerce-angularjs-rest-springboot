package com.assignment.controller;

import com.assignment.entity.Order;
import com.assignment.entity.User;
import com.assignment.service.AuthService;
import com.assignment.service.impl.AuthorityServiceImpl;
import com.assignment.service.impl.OrderServiceImpl;
import com.assignment.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class ProfileRestController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private OrderServiceImpl orderService;

    @GetMapping("/details")
    public ResponseEntity<User> getProfile() {
        if (!authService.isAuthenticated() ) {
            return ResponseEntity.noContent().build();
        }
        if (authService.isGoogleLogin()) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authService.getAuthentication();
            OAuth2User oAuth2User = (OAuth2User) oauth2Token.getPrincipal();

            String email = oAuth2User.getAttribute("email");
            System.out.println("gg login");
            User user = userService.findByUsername(email);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.noContent().build();
            }
        }
        System.out.println("not gg login");
        String username = authService.getUsername();
        User user = userService.findByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrders() {
        return ResponseEntity.ok(orderService.findByUsername(authService.getUsername()));
    }

    @PutMapping("/update")
    public ResponseEntity<User> putProfile(@RequestBody User user) {
        User user1 = userService.findByUsername(user.getUsername());
        if (user1 == null) {
            return ResponseEntity.notFound().build();
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(user1.getPassword());
        }
        user.setEnabled(true);
        return ResponseEntity.ok(userService.updateUser(user));
    }
}
