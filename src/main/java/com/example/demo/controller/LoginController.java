package com.example.demo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/login")
public class LoginController {
		@GetMapping("")
		public String login() {
			return "index";	
		}
		
		@RequestMapping("/check")
			public String check(Integer userId ,Integer password,Model model, RedirectAttributes redirectAttributes ) {

			if(userId ==null|| password ==null) {
				redirectAttributes.addAttribute("error","登録されているデータと一致しません。");
				return "redirect:/";
			}
			
			//ユーザーID,パスワードを引数にserviceクラスでリストの取得
			
			//リストがあった場合
			if(userId !=null && password !=0) {
				model.addAttribute("list", "ここ変える");
				return "user";
			}
			
			return "index";
		}
}
