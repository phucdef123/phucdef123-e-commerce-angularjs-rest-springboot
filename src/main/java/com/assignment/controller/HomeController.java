package com.assignment.controller;

import com.assignment.dao.*;
import com.assignment.entity.*;
import com.assignment.service.CookieService;
import com.assignment.service.SessionService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;


@Controller
public class HomeController {

	@RequestMapping("/shop/cart")
	public String cart() {
		return "cart";
	}

    @RequestMapping({"/", "/home/index"})
	public String index() {
		return "index";
	}

	@RequestMapping({"/admin", "/admin/home/index"})
	public String admin() {
		return "redirect:/assets/admin/admin.html";
	}

	@RequestMapping( "/shop")
	public String shop() {
		return "shop";
	}

	@RequestMapping("/shop/detail/{id}")
	public String shopDetail(@PathVariable Integer id) {
		return "shop-detail";
	}

	@RequestMapping("/register")
	public String register() {
		return "register";
	}

	@RequestMapping("/forgot-password")
	public String forgotPassword() {
		return "forgot-password";
	}

	@RequestMapping("/auth/profile")
	public String authProfile() {
		return "account-detail";
	}
}
