package com.example.demo.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Users;
import com.example.demo.service.AttendanceManagementService;
import com.example.demo.service.DailyReportService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/menu")
public class MenuController {

	@Autowired
	private DailyReportService dailyReportService;
	
	@Autowired
	private AttendanceManagementService attendanceManagementService; 
	
	//ログイン成功後アカウント情報をmodelに詰めてメニュー画面へ遷移
	@RequestMapping("")
	public String authorityDetermining(HttpSession session, Model model) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);
		Integer checkDailyReport = dailyReportService.checkYesterdayDailyReport(users.getUserId(),yesterday);
		Integer checkAttendance = attendanceManagementService.checkYesterdayAttendance(users.getUserId(),yesterday);
		if(checkDailyReport == 0) {
			model.addAttribute("CheckDailyReport", "日報未提出");
			model.addAttribute("userRole", users.getRole());
		}
		if(checkAttendance == 0) {
			model.addAttribute("CheckAttendance", "勤怠未提出");
			model.addAttribute("userRole", users.getRole());
		}
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
