package com.example.demo.controller;
import java.util.Date;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.LoginForm;
import com.example.demo.model.Users;
import com.example.demo.service.LoginService;


@Controller
@RequestMapping("/")
public class LoginController {
	@Autowired
	private LoginService loginService;
	
	
		@GetMapping("")
		public String login(@ModelAttribute LoginForm loginForm,HttpSession session, Model model) {
			 Users users = (Users) session.getAttribute("Users");
			 model.addAttribute("Users", users);
			 model.addAttribute("loginForm", new LoginForm()); 
			return "Login/index";	
		}
		
		@RequestMapping("/login") 
		public String check(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult ,Model model, RedirectAttributes redirectAttributes,
				HttpSession session) {

		if (bindingResult.hasErrors()) {
			 return "Login/index";
		}
						
			Integer userId = null;
			String userIds = null;
			try {
				userId = Integer.valueOf(loginForm.getUserId());	
			}
			catch(NullPointerException e){
				userId = Integer.valueOf(userIds);	
			}
			catch(NumberFormatException e) {
				redirectAttributes.addFlashAttribute("out", "ユーザーID無効です。");
				return "redirect:/";
			}
			
			userId = Integer.valueOf(loginForm.getUserId());	
			
			Users users = loginService.LoginListUp(userId, loginForm.getPassword());
			
			Date today = new Date();

			if (users == null) {
				redirectAttributes.addFlashAttribute("out", "ユーザーID、パスワードが不正、もしくはユーザーが無効です。");
				return "redirect:/";
			}else if (users.getStartDate().compareTo(today) == 1 ) {
				redirectAttributes.addFlashAttribute("out", "現在は使用できません");
				return "redirect:/";
			}else if (users.getRole().equalsIgnoreCase("Admin")) {
				session.setAttribute("Users", users);
				return "redirect:/user/";
			} 
		
			session.setAttribute("Users", users);
			 System.out.println("セッションに保存されたユーザー: " + session.getAttribute("Users"));

			return "redirect:/attendance/index";
		}

}
