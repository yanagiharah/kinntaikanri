package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.DailyReportDetailForm;
import com.example.demo.model.Users;
import com.example.demo.service.DailyReportService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/daily")
public class DailyReportController {

	@Autowired
	private DailyReportService dailyReportService;
	
	//日報取得
	@RequestMapping("")
	public String dailyReport(HttpSession session,Model model) {
		Users users = (Users)session.getAttribute("Users");
		Integer userId = users.getUserId();
		model.addAttribute("Report",
							dailyReportService.getDailyReport(userId));
		
			return "DailyReport/dailyReport";
	}
	
	//日報詳細取得
	@RequestMapping("/detail")
	public String dailyReportDetail(HttpSession session,Model model) {
		Users users = (Users)session.getAttribute("Users");
		Integer userId = users.getUserId();
		model.addAttribute("ReportDailyDetail",
							dailyReportService.getDailyReportDetail(userId));
			
			return "DailyReport/dailyReport";
	}
	
	//日報内容追加
	@PostMapping("/detail/insert")
	public void insertDailyReportDetail(@ModelAttribute 
										List<DailyReportDetailForm> list) {
		dailyReportService.insertDailyReportDetail(list);
	}
	
	//日報更新
	@PostMapping("/detail/update")
	public void updateDailyReportDetail(@ModelAttribute 
										List<DailyReportDetailForm> list) {
		dailyReportService.updateDailyReportDetail(list);
	}
}
