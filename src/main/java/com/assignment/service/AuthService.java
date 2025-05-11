package com.assignment.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service("auth")
public class AuthService {
    @Autowired
    HttpServletRequest request;

    public Authentication getAuthentication() {
        return (Authentication) request.getUserPrincipal();
    }

    public UserDetails getUserDetails() {
        Authentication auth = this.getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails)) return null;
        return (UserDetails) auth.getPrincipal();
    }

    public boolean isAuthenticated() {
        return (this.getAuthentication() != null);
    }

    public String getUsername() {
        UserDetails user = this.getUserDetails();
        if (user == null) return null;
        return user.getUsername();
    }

    public List<String> getRoles(){
        UserDetails user = this.getUserDetails();
        return user.getAuthorities().stream()
                .map(au -> au.getAuthority().substring(5))
                .toList();
    }

    public boolean hasRole(String...roles) {
        return this.isAuthenticated() &&
                Stream.of(roles).anyMatch(role -> this.getRoles().contains(role));
    }

    public boolean isAdmin() {
        return this.hasRole("STAFF", "DIRECTOR");
    }


    public boolean isGoogleLogin() {
        Authentication auth = this.getAuthentication();
        if (auth instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) auth;
            OAuth2User oAuth2User = (OAuth2User) oauth2Token.getPrincipal();
            // Kiểm tra nếu thông tin về Google login có tồn tại trong OAuth2User
            return "google".equals(oauth2Token.getAuthorizedClientRegistrationId());
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getUsername() + ": " +  this.getRoles();
    }
}
