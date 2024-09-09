package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Users;
import com.example.demo.service.CommonActivityService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/menu")
public class MenuController {

	private final CommonActivityService commonActivityService;

	MenuController(CommonActivityService commonActivityService) {
		this.commonActivityService = commonActivityService;
	}

	@RequestMapping("")
	public String authorityDetermining(HttpSession session, Model model) {
		commonActivityService.backMenu(model, session);
		return "menu/processMenu";
	}

	//勤怠登録画面に遷移
	@RequestMapping("/index")
	public String attenddance(HttpSession session, Model model) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		return "redirect:/attendance/index";
	}

	//日報登録画面に遷移
	@RequestMapping("/daily/detail")
	public String dailyReport(HttpSession session, Model model) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		return "redirect:/daily/detail";
	}

	//ユーザー情報登録画面に遷移
	@RequestMapping("/usermanagement")
	public String userManagement(HttpSession session, Model model) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		return "redirect:/user/";
	}

	//部署登録画面に遷移 
	@RequestMapping("/department")
	public String department(HttpSession session, Model model) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		return "redirect:/department/";
	}
}

////ログイン成功後アカウント情報をmodelに詰めてメニュー画面へ遷移@AuthenticationPrincipal Users users,
//@RequestMapping("in")
//public String authorityDetermining(HttpSession session, Model model,Authentication authenticatio) {
//	CustomUsersDetails customUserDetails = (CustomUsersDetails) authenticatio.getPrincipal();
//    Users users = customUserDetails.getUsers();
//    Date today = new Date();
//    if (users == null || users.getStartDate().compareTo(today) == 1) {
//    	return "Login/index";
//	}
//    model.addAttribute("Users", users);
//	return "menu/processMenu";
//}
