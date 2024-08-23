package com.example.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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

@Controller
@RequestMapping("/daily")
public class DailyReportController {

	@Autowired
	private DailyReportService dailyReportService;

	//日報の初期表示画面（今日時点のものを表示）
	@RequestMapping("/detail")
	public String dailyReportDetail(HttpSession session, Model model) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		
		Integer userId = users.getUserId();
		LocalDate today = LocalDate.now();

		//今日の日報取得　nullなら新しいフォームを初期表示
		DailyReportForm todayDailyReportForm = dailyReportService.getDailyReport(userId, today); 
		if (todayDailyReportForm == null) {
			todayDailyReportForm = new DailyReportForm();
		}
		
		// 今日の日報詳細を取得し、nullまたは空なら新しいリストを初期化
		List<DailyReportDetailForm> list = dailyReportService.getDailyReportDetail(userId, today);
	    if (list == null || list.isEmpty()) {
	        list = new ArrayList<>();
	    }
	    
	    //リストを24行(24時間分)まで追加で作成
	    while(list.size() < 24) {
	    	list.add(new DailyReportDetailForm());
	    }
	    
	    todayDailyReportForm.setDailyReportDetailForm(list);

	    model.addAttribute("dailyReportForm", todayDailyReportForm);
	    return "DailyReport/dailyReport";
	    
	}

	//日報内容追加
	@PostMapping("/detailInsert")
	public void insertDailyReportDetail(List<DailyReportDetailForm> list, HttpSession session, Model model) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		dailyReportService.insertDailyReportDetail(list);
	}

	//日報更新
	@RequestMapping(value = "/detailUpdate", params = "submission", method = RequestMethod.POST)
	public String updateDailyReportDetail(@Valid @ModelAttribute("dailyReportForm") DailyReportForm dailyReportForm,
			HttpSession session, Model model) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		System.out.print(dailyReportForm.getDailyReportDetailForm());
		dailyReportService.updateDailyReportDetail(dailyReportForm);
		return "DailyReport/dailyReport";
	}
	
	//戻るボタン
	@RequestMapping(value = "/detailUpdate", params = "back", method = RequestMethod.POST)
	public String back(Model model, HttpSession session) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		return "menu/processMenu";
	}
}
