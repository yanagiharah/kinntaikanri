package com.example.demo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.context.MessageSource;
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

import com.example.demo.model.DailyReportDetailForm;
import com.example.demo.model.DailyReportForm;
import com.example.demo.model.Users;
import com.example.demo.service.CommonActivityService;
import com.example.demo.service.DailyReportService;

@Controller
@RequestMapping("/daily")
public class DailyReportController {

	private final DailyReportService dailyReportService;

	private final MessageSource messageSource;

	private final CommonActivityService commonActivityService;

	public DailyReportController(DailyReportService dailyReportService, MessageSource messageSource,
			CommonActivityService commonActivityService) {
		this.dailyReportService = dailyReportService;
		this.messageSource = messageSource;
		this.commonActivityService = commonActivityService;
	}

	//日報の初期表示画面（今日時点のものを表示）
	@RequestMapping("/detail")
	public String dailyReportDetail(String date, HttpSession session, Model model) {
		commonActivityService.usersModelSession(model, session);
		Users users = (Users) model.getAttribute("Users");

		LocalDate calendarDate;
		
		if (date == null) {
			calendarDate = LocalDate.now();
		} else {
			calendarDate = LocalDate.parse(date);
		}
		if (!users.getRole().equalsIgnoreCase("Manager")) {
			Integer userId = users.getUserId();
			
			//日報取得 
			DailyReportForm dailyReportForm = dailyReportService.getDailyReport(userId, calendarDate);
			// 日報詳細を取得
			List<DailyReportDetailForm> dailyReportDetailForm = dailyReportService.getDailyReportDetail(userId,
					calendarDate);
			//空のリストを10行まで追加で作成
			dailyReportService.populateEmptyDailyReportDetails(dailyReportDetailForm,userId,calendarDate);
			//（今日の日付とユーザーIDをセット）
			dailyReportForm.setDailyReportDetailForm(dailyReportDetailForm);
			dailyReportForm.setUserId(userId);

			model.addAttribute("dailyReportForm", dailyReportForm);

		} else {
			List<DailyReportForm> confirmPending = dailyReportService.selectConfirmPending(calendarDate);
			model.addAttribute("ConfirmPending", confirmPending);
			model.addAttribute("calendarDate",calendarDate);

		}
		return "DailyReport/dailyReport";

	}

	//confirmPending表示者を押したときの処理
	@RequestMapping(value = "/detailUpdate", params = "DailyReportSubmitterDisplay", method = RequestMethod.POST)
	public String dailyRepManagement(@RequestParam("dailyReportDate") LocalDate dailyReportDate,
			@RequestParam("confirmationUserId") Integer confirmaitionUserId, @RequestParam("confirmationUserName") String confirmaitionUserName,Model model,HttpSession session) {
		commonActivityService.usersModelSession(model, session);
		
		//日報取得 
		DailyReportForm dailyReportForm = dailyReportService.getDailyReport(confirmaitionUserId, dailyReportDate);
		// 日報詳細を取得
		List<DailyReportDetailForm> dailyReportDetailForm = dailyReportService.getDailyReportDetail(confirmaitionUserId,
				dailyReportDate);
		//空のリストを10行まで追加で作成
		dailyReportService.populateEmptyDailyReportDetails(dailyReportDetailForm,confirmaitionUserId,dailyReportDate);
		//（今日の日付とユーザーIDをセット）
		dailyReportForm.setDailyReportDetailForm(dailyReportDetailForm);
		dailyReportForm.setUserId(confirmaitionUserId);
		dailyReportForm.setUserName(confirmaitionUserName);
		model.addAttribute("dailyReportForm", dailyReportForm);
		
		List<DailyReportForm> confirmPending= dailyReportService.selectConfirmPending(dailyReportDate);
		model.addAttribute("ConfirmPending", confirmPending);
		return "DailyReport/dailyReport";
	}

	//提出ボタン押下後
	@RequestMapping(value = "/detailUpdate", params = "submission", method = RequestMethod.POST)
	public String updateDailyReportDetail(@ModelAttribute DailyReportForm dailyReportForm,
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

	//確認ボタン押下処理
	@RequestMapping(value = "/detailUpdate",params ="confirm",method=RequestMethod.POST)
	public String updateStatusConfirm(@Valid @ModelAttribute("dailyReportForm") DailyReportForm dailyReportForm,
			BindingResult result, HttpSession session, Model model, Locale locale){
		commonActivityService.usersModelSession(model, session);
		
		if (result.hasErrors()) {
			return "DailyReport/dailyReport";
		}
		
		dailyReportService.updateConfirmDailyReport(dailyReportForm);
		
		LocalDate calendarDate = dailyReportForm.getDailyReportDate();
		List<DailyReportForm> confirmPending = dailyReportService.selectConfirmPending(calendarDate);
		model.addAttribute("ConfirmPending", confirmPending);
		model.addAttribute("calendarDate",dailyReportForm.getDailyReportDate());
		
		 dailyReportForm = null;
		 model.addAttribute("dailyReportForm", dailyReportForm);
		
		return "DailyReport/dailyReport";
	}


	//カレンダーコントロール押下後
	@RequestMapping(value = "/detailUpdate" ,params="date",method = RequestMethod.POST)
	public ModelAndView handleDateSubmission(@ModelAttribute("dailyReportDate") String dateS, HttpSession session,
			Model model) {
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
