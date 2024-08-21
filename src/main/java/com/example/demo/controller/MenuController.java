package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Users;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/menu")
public class MenuController {

	//ログイン成功後アカウント情報をmodelに詰めてメニュー画面へ遷移
	@RequestMapping("")
	public String authorityDetermining(HttpSession session, Model model) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
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
	@RequestMapping("/daily")
	public String dailyReport(HttpSession session, Model model) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		return "hoge";
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
		return "department";
	}
}
