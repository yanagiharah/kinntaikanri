package com.example.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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

	//日報の初期表示画面（今日時点のものを表示）
	@RequestMapping("/detail")
	public String dailyReportDetail(String day, HttpSession session, Model model) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		
		Integer userId = users.getUserId();
		LocalDate localDateDay ;
		
		if(day == null) {
			 localDateDay = LocalDate.now();
		} else {
			 localDateDay = LocalDate.parse(day);
		}
		

		
		//今日の日報取得　nullなら新しいフォームを作成
		DailyReportForm todayDailyReportForm = dailyReportService.getDailyReport(userId, localDateDay); 
		if (todayDailyReportForm == null) {
			todayDailyReportForm = new DailyReportForm();
			todayDailyReportForm.setUserId(userId);
			todayDailyReportForm.setDailyReportDate(localDateDay);
		}
		
		// 今日の日報詳細を取得し、nullまたは空なら新しいリストを初期化
		List<DailyReportDetailForm> dailyReportDetailForm = dailyReportService.getDailyReportDetail(userId, localDateDay);
	    if (dailyReportDetailForm == null || dailyReportDetailForm.isEmpty()) {
	        dailyReportDetailForm = new ArrayList<>();
	    }
	    
	    //リストを10行まで追加で作成（今日の日付とユーザーIDをセット）
	    
	    for( dailyReportDetailForm.size() ; dailyReportDetailForm.size() < 10 ; ) {
	    	DailyReportDetailForm emptyDetailForm = new DailyReportDetailForm();
	    	emptyDetailForm.setUserId(userId);
	    	emptyDetailForm.setDailyReportDetailDate(localDateDay);
	    	dailyReportDetailForm.add(emptyDetailForm);
	    }
	    
	    todayDailyReportForm.setDailyReportDetailForm(dailyReportDetailForm);
	    todayDailyReportForm.setUserId(userId);
	    
	    model.addAttribute("dailyReportForm", todayDailyReportForm);
	    return "DailyReport/dailyReport";
	    
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
