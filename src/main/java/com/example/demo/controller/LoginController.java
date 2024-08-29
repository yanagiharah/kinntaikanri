package com.example.demo.controller;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.inter.MessageOutput;
import com.example.demo.model.LoginForm;
import com.example.demo.model.Users;
import com.example.demo.service.LoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/")
public class LoginController {

	private  final LoginService loginService;
	private final MessageOutput messageOutput;
	
	LoginController(LoginService loginService ,MessageOutput messageOutput){
		this.loginService =loginService;
		this.messageOutput =messageOutput;
	}
	
		@GetMapping("")
		public String login(@ModelAttribute LoginForm loginForm,Model model,HttpSession session,HttpServletRequest request, HttpServletResponse response) {
			 Users users = (Users) session.getAttribute("Users");
			 model.addAttribute("Users", users);
			 model.addAttribute("loginForm", new LoginForm()); 
			 sessionOut (request, response);
			return "Login/index";	
		}
		
		@RequestMapping("/login")
		public String check(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult, Model model,
				RedirectAttributes redirectAttributes, HttpSession session) {

			if (bindingResult.hasErrors()) {
				return "Login/index";
			}
			Integer userId;
			String userIds = null;
			Date today = new Date();
			try {
				userId = Integer.valueOf(loginForm.getUserId());
			} catch (NullPointerException e) {
				userId = Integer.valueOf(userIds);
			} catch (NumberFormatException e) {
				redirectAttributes.addFlashAttribute("out", messageOutput.message("fraud"));
				return "redirect:/";
			}
			userId = Integer.valueOf(loginForm.getUserId());
			Users users = loginService.loginCheck(userId, loginForm.getPassword());
			if (users == null || users.getStartDate().compareTo(today) == 1) {
				redirectAttributes.addFlashAttribute("out", messageOutput.message("fraud"));
				return "redirect:/";
			}
			session.setAttribute("Users", users);
			return "redirect:/menu";
		}
		
		@RequestMapping("/logOff")
		public String logout(HttpServletRequest request, HttpServletResponse response) {
	        // セッションを無効にする
			sessionOut(request,response);
	        return "redirect:/";
		}

		public void sessionOut(HttpServletRequest request, HttpServletResponse response) {
			HttpSession session = request.getSession(false);
	        if (session != null) {
	            session.invalidate();
	        }
		}
		
		
}
