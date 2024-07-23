package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Users;
import com.example.demo.service.LoginService;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/login")
public class LoginController {
	@Autowired
	private LoginService loginService;
	
		@GetMapping("")
		public String login(HttpSession session, Model model) {
			 Users users = (Users) session.getAttribute("Users");
			 model.addAttribute("Users", users);
			return "login/index";	
		}
		
		@RequestMapping("/check")
		public String check(Integer userId, String password, Model model, RedirectAttributes redirectAttributes, HttpSession session) {

			Users users = loginService.LoginListUp(userId, password);

			if (users == null) {
				redirectAttributes.addFlashAttribute("out", "ユーザーID、パスワードが不正、もしくはユーザーが無効です。");
				return "redirect:/";
			} else if (users.getRole().equalsIgnoreCase("Admin")) {
				model.addAttribute("Users", users);
				return "User/manegement";
			}
		
			session.setAttribute("Users", users);
			 System.out.println("セッションに保存されたユーザー: " + session.getAttribute("Users"));

			return "redirect:/attendance/index";
		}

}
