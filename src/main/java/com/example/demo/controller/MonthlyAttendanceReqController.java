package com.example.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.inter.MessageOutput;
import com.example.demo.model.Attendance;
import com.example.demo.model.AttendanceFormList;
import com.example.demo.model.MonthlyAttendanceReq;
import com.example.demo.model.Users;
import com.example.demo.service.AttendanceManagementService;
import com.example.demo.service.CommonActivityService;
import com.example.demo.service.GoogleCalendarService;
import com.example.demo.service.ModelService;
import com.example.demo.service.MonthlyAttendanceReqService;
import com.google.api.services.calendar.model.Events;


@Controller
@RequestMapping("/attendanceCorrect")
public class MonthlyAttendanceReqController {

	private final CommonActivityService commonActivityService;
	private final MonthlyAttendanceReqService monthlyAttendanceReqService;
	private final AttendanceManagementService attendanceManagementService;
	private final MessageOutput messageOutput;
	private final ModelService modelService;
	private final GoogleCalendarService googleCalendarService;

	public MonthlyAttendanceReqController(CommonActivityService commonActivityService,
			MonthlyAttendanceReqService monthlyAttendanceReqService,
			AttendanceManagementService attendanceManagementService, MessageOutput messageOutput,ModelService modelService,GoogleCalendarService googleCalendarService) {
		this.commonActivityService = commonActivityService;
		this.monthlyAttendanceReqService = monthlyAttendanceReqService;
		this.attendanceManagementService = attendanceManagementService;
		this.messageOutput = messageOutput;
		this.modelService = modelService;
		this.googleCalendarService=googleCalendarService;
	}

	@RequestMapping("/correction")
	public String start(HttpSession session, MonthlyAttendanceReq monthlyAttendanceReq, Model model) {
		
		commonActivityService.usersModelSession(model, session);
		Users users = (Users) model.getAttribute("Users");
		
		String stringYearsMonth;
		stringYearsMonth = (String) model.asMap().get("stringYearsMonth");
	    if (stringYearsMonth == null) {
	        stringYearsMonth = commonActivityService.lastYearsMonth();
	    }
	    
		if (users.getRole().equalsIgnoreCase("Manager")) {
			List<MonthlyAttendanceReq> HasChangeReq = monthlyAttendanceReqService
					.selectHasChangeReq(stringYearsMonth);
			model.addAttribute("HasChangeReq", HasChangeReq);
		} else {
			List<MonthlyAttendanceReq> approvedMonths= monthlyAttendanceReqService.selectApproval(users.getUserId());
			model.addAttribute("approvedMonths",approvedMonths);
			attendanceSearch(users.getUserId(), stringYearsMonth, model, session);
		}
		model.addAttribute("stringYearsMonth",stringYearsMonth);
		
		return "monthlyAttendanceReq/monthlyAttendance";
	}

	//	//表示ボタンの処理
	@RequestMapping(value = "/management", params = "search", method = RequestMethod.POST)
	public String attendanceSearch(Integer userId, String stringYearsMonth, Model model,
			HttpSession session) {
		commonActivityService.usersModelSession(model, session);
		Users users = (Users) model.getAttribute("Users");
		//yearとmonthの作成
		Integer years = Integer.parseInt(stringYearsMonth.substring(0, 4));
		Integer month = Integer.parseInt(stringYearsMonth.substring(5, 7));

		//yearとmonthに該当する勤怠表を探してAttendanceFormListに詰める処理
		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month, Optional.<Events>empty());
		AttendanceFormList attendanceFormList = attendanceManagementService.setInAttendance(attendance, years, month,
				stringYearsMonth);
		
		if (!users.getRole().equalsIgnoreCase("Manager")) {
			model.addAttribute("attendanceFormList", attendanceFormList);
			//月次勤怠テーブルのstatusをユーザー)モデルのstatusに詰める
			monthlyAttendanceReqService.submissionStatusCheck(attendance.get(0).getAttendanceDate(), userId, model,
					session);
			attendanceManagementService.requestActivityCheck(attendanceFormList);
			List<MonthlyAttendanceReq> approvedMonths= monthlyAttendanceReqService.selectApproval(users.getUserId());
			model.addAttribute("approvedMonths",approvedMonths);
		} else {
			List<MonthlyAttendanceReq> HasChangeReq = monthlyAttendanceReqService.selectHasChangeReq(stringYearsMonth);
			model.addAttribute("HasChangeReq", HasChangeReq);
			//カレンダーを初期化しないために返す
			model.addAttribute("stringYearsMonth",stringYearsMonth);
		}
		
		List<String> holidays = googleCalendarService. getListHolidays(years,month);
		model.addAttribute("holidays",holidays);
		return "monthlyAttendanceReq/monthlyAttendance";
	}

	//訂正申請ボタンの処理
	@RequestMapping(value = "/management", params = "willCorrect", method = RequestMethod.POST)
	public String monthlyAttendanceReqCorrect(Model model, HttpSession session,
			@RequestParam("willCorrectReason") String changeReason, @RequestParam("userId") Integer userId,
			@RequestParam("stringYearsMonth") String stringYearsMonth) {
		commonActivityService.usersModelSession(model, session);
		//	has_change_reqを１に変更する処理のためにLocalDateに変更して処理
		LocalDate targetYearMonth = monthlyAttendanceReqService.convertStringToLocalDate(stringYearsMonth);
		if (targetYearMonth != null) {
			monthlyAttendanceReqService.changeRequestMonthlyAttendanceReq(userId, targetYearMonth, changeReason);
		}
		//再度同じ月の勤怠表を表示するための処理
		Integer years = Integer.parseInt(stringYearsMonth.substring(0, 4));
		Integer month = Integer.parseInt(stringYearsMonth.substring(5, 7));
		List<String> holidays = googleCalendarService.getListHolidays(years,month);
		model.addAttribute("holidays",holidays);
		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month,Optional.<Events>empty());
		AttendanceFormList attendanceFormList = attendanceManagementService.setInAttendance(attendance, years, month,
				stringYearsMonth);
		//ステータス変更処理
		Date firstAttendanceDate = attendanceManagementService.getFirstAttendanceDate(attendanceFormList);
		monthlyAttendanceReqService.submissionStatusCheck(firstAttendanceDate, userId, model, session);
		
		model.addAttribute("sendCorrectionApplication", messageOutput.message("sendCorrectionApplication"));
		model.addAttribute("attendanceFormList", attendanceFormList);
		model.addAttribute("stringYearsMonth",stringYearsMonth);
		return "monthlyAttendanceReq/monthlyAttendance";
	}

	//	//マネージャー用訂正申請者勤怠表表示ボタンの処理
	@RequestMapping(value = "/management", params = "ApprovalApplicantDisplay", method = RequestMethod.POST)
	public String attendance(@RequestParam("approvalUserId") Integer userId, @RequestParam("Years") Integer years,
			@RequestParam("Month") Integer month, @RequestParam("approvalUserName") String userName, Model model,
			HttpSession session) {
		commonActivityService.usersModelSession(model, session);
		//monthをMMの形に再成型
		String monthString = String.format("%02d", month);
		String stringYearsMonth = years + "-" + monthString;
		
		List<String> holidays = googleCalendarService.getListHolidays(years,month);
		model.addAttribute("holidays",holidays);

		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month,Optional.<Events>empty());

		if (attendance != null) {
			// formに詰めなおす
			AttendanceFormList attendanceFormList = new AttendanceFormList();
			ArrayList<Attendance> attendanceList = new ArrayList<Attendance>();
			attendanceFormList.setAttendanceList(attendanceList);
			attendanceList.addAll(attendance);
			
			//★	approvalUserIdをセットするfor文を回す
			for (int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {
				attendanceFormList.getAttendanceList().get(i).setUserId(userId);
			}
			model.addAttribute("attendanceFormList", attendanceFormList);
		}
		
		List<MonthlyAttendanceReq> currentChangeReq=monthlyAttendanceReqService.filteringHasChangeReq(userId, stringYearsMonth);
		List<MonthlyAttendanceReq> hasChangeReq = monthlyAttendanceReqService.selectHasChangeReq(stringYearsMonth);
		//選択月該当ユーザー
		model.addAttribute("HasChangeReq", hasChangeReq);
		//選択した特定ユーザー表記用
		model.addAttribute("CurrentChangeReq",currentChangeReq);
		//カレンダー埋める用
		model.addAttribute("stringYearsMonth",stringYearsMonth);
		
		return "monthlyAttendanceReq/monthlyAttendance";
	}
	//

		//マネージャー訂正承認ボタン　同上
	@RequestMapping(value = "/management", params = "approval", method = RequestMethod.POST)
	public String approval(AttendanceFormList attendanceFormList, Model model, HttpSession session,
			RedirectAttributes redirectAttributes,@RequestParam("stringYearsMonth")String stringYearsMonth) {
		redirectAttributes.addFlashAttribute("stringYearsMonth",stringYearsMonth);
		//else句以外ほぼ起きることはない、状況を見て消去可,システム上前半句は起きえないはず
		if (attendanceFormList.getAttendanceList() == null) {
			redirectAttributes.addFlashAttribute("choiceUsers", messageOutput.message("choiceUsers"));
		} else {
			//承認更新文のための処理とその実行
			String inputDate =  monthlyAttendanceReqService.getInputDate(attendanceFormList);
			Integer firstUserId=attendanceManagementService.getFirstAttendanceUserId(attendanceFormList);
			monthlyAttendanceReqService.changeApprovalMonthlyAttendanceReq(firstUserId, inputDate);
			modelService.changeMonthlyAttendanceReqApproval(model);
		}
		return "redirect:/attendanceCorrect/correction";
	}

	//
	//	//マネージャー訂正却下ボタン押下　同上
	@RequestMapping(value = "/management", params = "rejected", method = RequestMethod.POST)
	public String Rejected(AttendanceFormList attendanceFormList, Model model, HttpSession session,
			RedirectAttributes redirectAttributes,@RequestParam("rejectionReason")String rejectionReason,@RequestParam("stringYearsMonth")String stringYearsMonth) {
		redirectAttributes.addFlashAttribute("stringYearsMonth",stringYearsMonth);
		if (attendanceFormList.getAttendanceList() == null) {
			redirectAttributes.addFlashAttribute("choiceUsers", messageOutput.message("choiceUsers"));
		} else {
			//却下更新分のための処理とその実行
			String inputDate =  monthlyAttendanceReqService.getInputDate(attendanceFormList);
			Integer firstUserId=attendanceManagementService.getFirstAttendanceUserId(attendanceFormList);
			monthlyAttendanceReqService.changeRejectionMonthlyAttendanceReq(firstUserId, inputDate, rejectionReason);
			modelService.changeMonthlyAttendanceReqReject(model);
		}
		return "redirect:/attendanceCorrect/correction";
	}
	
	//更新ボタンが押された場合の処理
	@RequestMapping(value = "/management",method = RequestMethod.GET)
	public String reload(Model model,HttpSession session,RedirectAttributes redirectAttributes) {
		return "redirect:/attendanceCorrect/correction";
	}
	
	@RequestMapping(value = "/updateData", params = "confirmReason", method = RequestMethod.POST)
	public String ConfirmRejectedReason(Integer userId) {
		monthlyAttendanceReqService.changeRejectedMonthlyAttendanceReq(userId);
		return "redirect:/menu/loaded";
	}
	
}
