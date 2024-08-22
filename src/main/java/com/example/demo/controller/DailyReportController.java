package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.model.DailyReportDetailForm;
import com.example.demo.model.DailyReportForm;
import com.example.demo.model.Users;
import com.example.demo.service.DailyReportService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/daily")
public class DailyReportController {

	@Autowired
	private DailyReportService dailyReportService;
	
	//日報取得
	@RequestMapping("")
	public String dailyReport(HttpSession session,Model model) {
		Users users = (Users)session.getAttribute("Users");
		model.addAttribute("Users", users);
		Integer userId = users.getUserId();
		model.addAttribute("Report",
							dailyReportService.getDailyReport(userId));
		
			return "DailyReport/dailyReport";
	}
	
	//日報詳細取得
	@RequestMapping("/detail")
	public String dailyReportDetail(HttpSession session,Model model) {
		Users users = (Users)session.getAttribute("Users");
		model.addAttribute("Users", users);
		
		List<DailyReportDetailForm> list = dailyReportService.getDailyReportDetail(users.getUserId());		
		
		DailyReportForm dailyReportForm = new DailyReportForm();
		ArrayList<DailyReportDetailForm> dailyReportDetailList = new ArrayList<DailyReportDetailForm>();
		dailyReportForm.setDailyReportDetailForm(dailyReportDetailList);
		dailyReportDetailList.addAll(list);
		
		model.addAttribute("dailyReportForm",dailyReportForm);
			return "DailyReport/dailyReport";
	}
	
	//日報内容追加
	@PostMapping("/detailInsert")
	public void insertDailyReportDetail(List<DailyReportDetailForm> list,HttpSession session,Model model) {
		Users users = (Users)session.getAttribute("Users");
		model.addAttribute("Users", users);
		dailyReportService.insertDailyReportDetail(list);
	}
	
	//日報更新
	@RequestMapping( value = "/detailUpdate", params = "submission", method = RequestMethod.POST)
	public String updateDailyReportDetail(@Valid @ModelAttribute("dailyReportForm") DailyReportForm dailyReportForm,HttpSession session,Model model) {
		Users users = (Users)session.getAttribute("Users");
		model.addAttribute("Users", users);
		System.out.print(dailyReportForm.getDailyReportDetailForm());
		dailyReportService.updateDailyReportDetail(dailyReportForm);
		return "DailyReport/dailyReport";
	}
}

