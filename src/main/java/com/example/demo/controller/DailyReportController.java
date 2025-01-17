package com.example.demo.controller;

import java.time.LocalDate;
import java.util.Locale;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.DailyReportForm;
import com.example.demo.model.Users;
import com.example.demo.service.CommonActivityService;
import com.example.demo.service.DailyReportService;

@Controller
@RequestMapping("/daily")
public class DailyReportController {

	private final DailyReportService dailyReportService;

	private final CommonActivityService commonActivityService;

	public DailyReportController(DailyReportService dailyReportService, 
			CommonActivityService commonActivityService) {
		this.dailyReportService = dailyReportService;
		this.commonActivityService = commonActivityService;
	}

	//日報の初期表示画面（今日時点のものを表示）
	@RequestMapping("/detail")
	public String dailyReportDetail(@RequestParam(value = "date", required = false) String date, HttpSession session, Model model) {
		Users users = commonActivityService.getCommonInfoWithUsers(model,session,null);
		dailyReportService.serviceForDailyReportDetail(model, users, date);
		return "DailyReport/dailyReport";

	}

	//confirmPending表示者を押したときの処理
	@RequestMapping(value = "/detailUpdate", params = "DailyReportSubmitterDisplay", method = RequestMethod.POST)
	public String dailyRepManagement(@RequestParam("dailyReportDate") LocalDate dailyReportDate,@RequestParam("confirmationUserId") Integer confirmationUserId, @RequestParam("confirmationUserName") String confirmationUserName,Model model,HttpSession session) {
		commonActivityService.getCommonInfo(model,session,null);
		dailyReportService.serviceForDailyRepManagement(model,confirmationUserId,confirmationUserName,dailyReportDate);
		return "DailyReport/dailyReport";
	}

	//提出ボタン押下後
	@RequestMapping(value = "/detailUpdate", params = "submission", method = RequestMethod.POST)
	public String updateDailyReportDetail(@ModelAttribute DailyReportForm dailyReportForm,BindingResult result, HttpSession session, Model model, Locale locale) {
		commonActivityService.getCommonInfo(model,session,null);

		if (result.hasErrors()) {
			return "DailyReport/dailyReport";
		}
		String calendarDateS = dailyReportService.serviceForUpdateDailyReportDetail(model ,dailyReportForm,locale);
		return dailyReportDetail(calendarDateS, session, model);
	}
	
	//検索ボタン押下処理
	@RequestMapping(value="/detailUpdate", params ="searchConfirmPending",method = RequestMethod.POST)
	public String searchConfirmPendingStatusOne(Model model,HttpSession session,String date){
		commonActivityService.usersModelSession(model,session);
		dailyReportService.serviceForSearchConfirmPendingStatusOne(model, date);
		return "DailyReport/dailyReport";
	}

	//確認ボタン押下処理
	@RequestMapping(value = "/detailUpdate",params ="confirm",method=RequestMethod.POST)
	public String updateStatusConfirm(@Valid @ModelAttribute("dailyReportForm") DailyReportForm dailyReportForm,BindingResult result, HttpSession session, Model model, Locale locale){
		commonActivityService.getCommonInfo(model,session,null);
		
		if (result.hasErrors()) {
			return "DailyReport/dailyReport";
		}
		
		dailyReportService.serviceForUpdateStatusConfirm(model, dailyReportForm);
		
		return "DailyReport/dailyReport";
	}


	//カレンダーコントロール押下後、検索ボタンで表示されるリンク押下後
	@RequestMapping(value = "/detailUpdate" ,method = RequestMethod.POST)
	public ModelAndView handleDateSubmission(@ModelAttribute("dailyReportDate") String dateS, @RequestParam(value="selectedDate",required = false) String selectedDate, HttpSession session,
			Model model) {
		if(selectedDate != null && !selectedDate.isEmpty()) {
			return new ModelAndView(dailyReportDetail(selectedDate,session,model));
		}
		
		return new ModelAndView(dailyReportDetail(dateS, session, model));

	}
	
	//更新ボタンが押された場合の処理
	@GetMapping(value={"/management","/detailUpdate"})
	public String Reload(Model model,HttpSession session,RedirectAttributes redirectAttributes) {
		return "redirect:/daily/detail";
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
