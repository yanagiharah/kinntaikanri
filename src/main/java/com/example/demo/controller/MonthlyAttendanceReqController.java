package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.AttendanceFormList;
import com.example.demo.model.Users;
import com.example.demo.service.CommonActivityService;
import com.example.demo.service.MonthlyAttendanceReqService;


@Controller
@RequestMapping("/attendanceCorrect")
public class MonthlyAttendanceReqController {

	private final CommonActivityService commonActivityService;
	private final MonthlyAttendanceReqService monthlyAttendanceReqService;

	public MonthlyAttendanceReqController(CommonActivityService commonActivityService,
			MonthlyAttendanceReqService monthlyAttendanceReqService) {
		this.commonActivityService = commonActivityService;
		this.monthlyAttendanceReqService = monthlyAttendanceReqService;
	}

	@RequestMapping("/correction")
	public String start(HttpSession session, Model model) {
		monthlyAttendanceReqService.monthlyAttendanceReqStart(model, session);
		return "monthlyAttendanceReq/monthlyAttendance";
	}

	//	//表示ボタンの処理
	@RequestMapping(value = "/management", params = "search", method = RequestMethod.POST)
	public String attendanceSearch(Integer userId, String stringYearsMonth, Model model,
			HttpSession session) {
		Users users = commonActivityService.getCommonInfoWithUsers(model, session, null);
		monthlyAttendanceReqService.serviceAttendanceSearch(model,session,userId,stringYearsMonth,users);
		return "monthlyAttendanceReq/monthlyAttendance";
	}

	//訂正申請ボタンの処理
	@RequestMapping(value = "/management", params = "willCorrect", method = RequestMethod.POST)
	public String monthlyAttendanceReqCorrect(Model model, HttpSession session,
			@RequestParam("willCorrectReason") String changeReason, @RequestParam("userId") Integer userId,
			@RequestParam("stringYearsMonth") String stringYearsMonth) {
		Users users = commonActivityService.getCommonInfoWithUsers(model,session,null);
		monthlyAttendanceReqService.serviceApproveButton(model, session, userId, stringYearsMonth, changeReason,users);
		return "monthlyAttendanceReq/monthlyAttendance";
	}

	//マネージャー用訂正申請者勤怠表表示ボタンの処理
	@RequestMapping(value = "/management", params = "ApprovalApplicantDisplay", method = RequestMethod.POST)
	public String attendance(@RequestParam("approvalUserId") Integer userId, @RequestParam("stringYearsMonth") String stringYearsMonth,Model model,
			HttpSession session) {
		monthlyAttendanceReqService.serviceApprovedAttendance(model, session, userId, stringYearsMonth);
		
		return "monthlyAttendanceReq/monthlyAttendance";
	}
	//

		//マネージャー訂正承認ボタン　同上
	@RequestMapping(value = "/management", params = "approval", method = RequestMethod.POST)
	public String approval(AttendanceFormList attendanceFormList, RedirectAttributes redirectAttributes,@RequestParam("stringYearsMonth") String stringYearsMonth) {
		monthlyAttendanceReqService.serviceApproval(attendanceFormList, redirectAttributes,stringYearsMonth);
		return "redirect:/attendanceCorrect/correction";
	}

	//
	//	//マネージャー訂正却下ボタン押下　同上
	@RequestMapping(value = "/management", params = "rejected", method = RequestMethod.POST)
	public String Rejected(AttendanceFormList attendanceFormList,RedirectAttributes redirectAttributes,@RequestParam("stringYearsMonth") String stringYearsMonth,@RequestParam("rejectionReason")String rejectionReason) {
		monthlyAttendanceReqService.serviceRejected(attendanceFormList, redirectAttributes, rejectionReason,stringYearsMonth);
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
