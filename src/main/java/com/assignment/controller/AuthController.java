package com.assignment.controller;

import com.assignment.entity.User;
import com.assignment.entity.UserProfile;
import com.assignment.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {
    @Autowired
    HttpServletRequest request;

    @Autowired
    private UserServiceImpl userService;

    @RequestMapping(path = "/auth/login/form")
    public String form(Model model) {
        return "/login";
    }

    @RequestMapping(path = "/auth/login/success")
    public String success(Model model) {
        return "forward:/";
    }

    @RequestMapping(path = "/auth/login/error")
    public String error(Model model) {
        model.addAttribute("message", "Sai thông tin đăng nhập");
        return "forward:/auth/login/form";
    }

    @RequestMapping(path = "/auth/logout/success")
    public String logout(Model model) {
        return "forward:/";
    }

    @RequestMapping(path = "/auth/access/denied")
    public String denied(Model model) {
        model.addAttribute("message", "Không có quyền truy cập");
        return "forward:/auth/login/form";
    }

    @RequestMapping(path = "/oauth2/login/success")
    public String ggSuccess(Model model, OAuth2AuthenticationToken token) {
        String email = token.getPrincipal().getAttribute("email");
        String fullName = token.getPrincipal().getAttribute("name");
        String picture = token.getPrincipal().getAttribute("picture");
        User existingUser = userService.findByUsername(email);
        if (existingUser == null) {
            User newUser = new User();

            UserProfile userProfile = new UserProfile();
            userProfile.setUsername(email); // Khớp với username của User
            userProfile.setEmail(email);
            userProfile.setFullname(fullName);
            userProfile.setPhoto(picture);

            newUser.setUsername(email);
            newUser.setPassword("");

            newUser.setUserProfile(userProfile);
            userProfile.setUser(newUser);

            System.out.println(newUser);
            userService.createUser(newUser);
        }
//        System.out.println(existingUser);
//        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        return "forward:/auth/login/success";
    }
}
