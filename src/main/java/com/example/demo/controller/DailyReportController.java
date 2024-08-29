package com.example.demo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.model.DailyReportDetailForm;
import com.example.demo.model.DailyReportForm;
import com.example.demo.model.Users;
import com.example.demo.service.AttendanceManagementService;
import com.example.demo.service.DailyReportService;

@Controller
@RequestMapping("/daily")
public class DailyReportController {

	@Autowired
	private DailyReportService dailyReportService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private AttendanceManagementService attendanceManagementService; 

	//日報の初期表示画面（今日時点のものを表示）
	@RequestMapping("/detail")
	public String dailyReportDetail(String date, HttpSession session, Model model) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);

		Integer userId = users.getUserId();
		LocalDate calendarDate ;
		
		if(date == null) {
			calendarDate = LocalDate.now();
		} else {
			calendarDate = LocalDate.parse(date);
		}
		
		
		//日報取得 nullなら新しいフォームを作成
		DailyReportForm dailyReportForm = dailyReportService.getDailyReport(userId, calendarDate); 
		if (dailyReportForm== null) {
			dailyReportForm= new DailyReportForm();
			dailyReportForm.setUserId(userId);
			dailyReportForm.setDailyReportDate(calendarDate);
		}
		
		
		// 日報詳細を取得 nullまたは空なら新しいリストを初期化
		List<DailyReportDetailForm> dailyReportDetailForm = dailyReportService.getDailyReportDetail(userId, calendarDate);
	    if (dailyReportDetailForm == null || dailyReportDetailForm.isEmpty()) {
	        dailyReportDetailForm = new ArrayList<>();
	    }
	    
	    //空のリストを10行まで追加で作成（今日の日付とユーザーIDをセット）
	    
	    for( dailyReportDetailForm.size() ; dailyReportDetailForm.size() < 10 ; ) {
	    	DailyReportDetailForm emptyDetailForm = new DailyReportDetailForm();
	    	emptyDetailForm.setUserId(userId);
	    	emptyDetailForm.setDailyReportDetailDate(calendarDate);
	    	dailyReportDetailForm.add(emptyDetailForm);
	    }
	    
	    dailyReportForm.setDailyReportDetailForm(dailyReportDetailForm);
	    dailyReportForm.setUserId(userId);
	    
//	    System.out.print("提出はここです！！！！！！！！"+dailyReportForm);
	    
	    model.addAttribute("dailyReportForm", dailyReportForm);
	    return "DailyReport/dailyReport";
	    
	}

	//提出ボタン押下後
	@RequestMapping(value = "/detailUpdate", params = "submission", method = RequestMethod.POST)
	public String updateDailyReportDetail(@Valid @ModelAttribute("dailyReportForm") DailyReportForm dailyReportForm,
			BindingResult result, HttpSession session, Model model, Locale locale) {

		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);

		if (result.hasErrors()) {
			return "DailyReport/dailyReport";
		}

		dailyReportService.updateDailyReportDetail(dailyReportForm);

		String successMessage = messageSource.getMessage("dailyReport.update.success", null, locale);
		model.addAttribute("message", successMessage);
		LocalDate calendarDate = dailyReportForm.getDailyReportDetailForm().get(0).getDailyReportDetailDate();
		String calendarDateS = calendarDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		return dailyReportDetail(calendarDateS, session, model);
	}
	
	//戻るボタン
	@RequestMapping(value = "/detailUpdate", params = "back", method = RequestMethod.POST)
	public String back(Model model, HttpSession session) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);
		Integer checkDailyReport = dailyReportService.checkYesterdayDailyReport(users.getUserId(),yesterday);
		Boolean checkAttendance = attendanceManagementService.checkYesterdayAttendance(users.getUserId(),yesterday);
		if(checkDailyReport == 0) {
			model.addAttribute("CheckDailyReport", "日報未提出");
		}
		if(checkAttendance == false) {
			model.addAttribute("CheckAttendance", "勤怠未提出");
		}
		return "menu/processMenu";
	}
	
	//カレンダーコントロール押下後
	@RequestMapping(value = "/date", params = "dailyReportDate", method = RequestMethod.POST)
    public ModelAndView handleDateSubmission(@ModelAttribute("dailyReportDate") String dateS,HttpSession session, Model model) {
        return new ModelAndView(dailyReportDetail(dateS,session,model));
    }
	

	
///----------★一時的にとっておきたい(javaScript連携の為)------------------
	
	//カレンダーコントロール押下後
//	@ResponseBody
//	@PostMapping("/calendarDate")
//	public  Map<String, Object> receiveDate(@RequestParam("date") String date, HttpSession session, Model model) {
//		Users users = (Users) session.getAttribute("Users");
//		
//		Integer userId = users.getUserId();
//		LocalDate calendarDate ;
//		
//		if(date == null) {
//			calendarDate = LocalDate.now();
//		} else {
//			calendarDate = LocalDate.parse(date);
//		}
//		
//		DailyReportForm dailyReportForm = dailyReportService.getDailyReport(userId, calendarDate); 
//		if (dailyReportForm== null) {
//			dailyReportForm= new DailyReportForm();
//			dailyReportForm.setUserId(userId);
//			dailyReportForm.setDailyReportDate(calendarDate);
//		}
//		
//		
//		// 日報詳細を取得 nullまたは空なら新しいリストを初期化
//		List<DailyReportDetailForm> dailyReportDetailForm = dailyReportService.getDailyReportDetail(userId, calendarDate);
//	    if (dailyReportDetailForm == null || dailyReportDetailForm.isEmpty()) {
//	        dailyReportDetailForm = new ArrayList<>();
//	    }
//	    
//	    //空のリストを10行まで追加で作成（今日の日付とユーザーIDをセット）
//	    
//	    for( dailyReportDetailForm.size() ; dailyReportDetailForm.size() < 10 ; ) {
//	    	DailyReportDetailForm emptyDetailForm = new DailyReportDetailForm();
//	    	emptyDetailForm.setUserId(userId);
//	    	emptyDetailForm.setDailyReportDetailDate(calendarDate);
//	    	dailyReportDetailForm.add(emptyDetailForm);
//	    }
//	    
//	    dailyReportForm.setDailyReportDetailForm(dailyReportDetailForm);
//	    dailyReportForm.setUserId(userId);
//		
//		Map<String, Object> response = new HashMap<>();
//	    response.put("dailyReportForm", dailyReportForm);
//	    response.put("Users", users);
//		return response;
//	    //return "redirect:/daily/detail?date=" + date;
//	}
	


}
