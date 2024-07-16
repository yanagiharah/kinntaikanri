package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Users;
import com.example.demo.service.UserManagementService;

@Controller
@RequestMapping("/user")
public class UserManagementController {
	@Autowired
	private UserManagementService userManagementService;
	
	
	@RequestMapping("/management")
	public String userSearch(String userName,Model model, RedirectAttributes redirectAttributes ){
		
	
//	if(name == null) {
//		redirectAttributes.addFlashAttribute("check","入力は必須です。");
//		return "redirect:/User/manegement";
//	}
	//名前を引数にserviceクラスでリストの取得
		System.out.print("ここに表示"+userName);
	Users users = userManagementService.UserSearchListUp(userName);
	System.out.print("ここに表示"+users);
	//リストがあった場合
		if(users == null) {
			return "User/manegement";
		}
		
	model.addAttribute("List", users);
	return "User/manegement";
	}
}

