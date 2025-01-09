package com.example.demo.controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.service.CommonActivityService;


@Controller
@RequestMapping("/")
public class LoginController {
	
	private final CommonActivityService commonActivityService;
	
	LoginController(CommonActivityService commonActivityService){
		this.commonActivityService = commonActivityService;
	}
	/**
	 * ログイン処理
	 * @param model
	 * @return ログイン画面
	 */
	@GetMapping("/login")
	public String login(Model model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response) {
		//動かなくなったらインスタンスとコンストラクタはずして下記の処理を元に戻す。メソッドを見て中身をそのまま書けば好。
		commonActivityService.usersModelSession(model,session);
		return "Login/index";
	}

	public void sessionOut(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}	
}
//	@RequestMapping("/logOff")
//	public String logout(HttpServletRequest request, HttpServletResponse response) {
//		// セッションを無効にする
//		sessionOut(request, response);
//		return "redirect:/";
//	}

	//		@GetMapping("")
	//		public String login(@ModelAttribute LoginForm loginForm,Model model,HttpSession session,HttpServletRequest request, HttpServletResponse response) {
	//			 Users users = (Users) session.getAttribute("Users");
	//			 model.addAttribute("Users", users);
	//			 model.addAttribute("loginForm", new LoginForm()); 
	////			 sessionOut (request, response);
	//			return "Login/index";	
	//		}

	//		@GetMapping("/login")
	//		public String check(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult, Model model,
	//				RedirectAttributes redirectAttributes, HttpSession session) {
	//
	//			if (bindingResult.hasErrors()) {
	//				System.out.print("get");
	//				return "Login/index";
	//			}
	//			Integer userId;
	//			String userIds = null;
	//			Date today = new Date();
	//			try {
	//				userId = Integer.valueOf(loginForm.getUserId());
	//			} catch (NullPointerException e) {
	//				userId = Integer.valueOf(userIds);
	//			} catch (NumberFormatException e) {
	//				redirectAttributes.addFlashAttribute("out", messageOutput.message("fraud"));
	//				return "redirect:/";
	//			}
	//			userId = Integer.valueOf(loginForm.getUserId());
	//			Users users = loginService.loginCheck(userId, loginForm.getPassword());
	//			if (users == null || users.getStartDate().compareTo(today) == 1) {
	//				redirectAttributes.addFlashAttribute("out", messageOutput.message("fraud"));
	//				return "redirect:/";
	//			}
	//			session.setAttribute("Users", users);
	//			return "redirect:/menu";
	//		}

	//		@PostMapping("/login2")
	//		public String check2(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult, Model model,
	//				RedirectAttributes redirectAttributes, HttpSession session) {
	//			if (bindingResult.hasErrors()) {
	//				System.out.print("チェック済み");
	//				return "Login/index";
	//			}
	//			System.out.print("post");
	//			return "redirect:/menu";
	//		}


