package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Users;
import com.example.demo.service.LoginService;


@Controller
@RequestMapping("/login")
public class LoginController {
	@Autowired
	private LoginService loginService;
	
		@GetMapping("")
		public String login() {
			return "login/index";	
		}
		
		@RequestMapping("/check")
			public String check(Integer userId ,String password,Model model, RedirectAttributes redirectAttributes ){
			
//			try {
//				if(userId ==null|| password == '\u0000' ) {
//					redirectAttributes.addFlashAttribute("out","登録されているデータと一致しません。");
//					return "redirect:/";
//				}
//				
//			}catch(methodArgumentTypeMismatchException e) {
//				redirectAttributes.addFlashAttribute("out","登録されているデータと一致しません。");
//				return "redirect:/";
//			}
			
			
			
			//ユーザーID,パスワードを引数にserviceクラスでリストの取得
			Users users = loginService.LoginListUp(userId, password);
			System.out.print(users);
			//リストがあった場合
			if(users == null) {
				redirectAttributes.addFlashAttribute("out","登録されているデータと一致しません。");
				return "redirect:/";
			}
//			else if(users.getRole() .equalsIgnoreCase("Admin")) {
//				return "User/manegement";
//			}
			model.addAttribute("Users", users);
			return "attendance/registration";
		}
}
