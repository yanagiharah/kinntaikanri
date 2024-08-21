package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Users;
import com.example.demo.service.DailyReportService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/daily")
public class DailyReportController {

	@Autowired
	private DailyReportService dailyReportService;
	
	@RequestMapping("")
	public String dailyReport(HttpSession session,Model model) {
		Users users = (Users)session.getAttribute("Users");
		Integer userId = users.getUserId();
		
		model.addAttribute("Report",dailyReportService.getDailyReport(userId));
		
		return "DailyReport/dailyReport";
		
	}
}
