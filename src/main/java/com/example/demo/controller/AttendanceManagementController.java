package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.helper.DateHelper;
import com.example.demo.model.AttendanceFormList;
import com.example.demo.model.MonthlyAttendanceReq;
import com.example.demo.model.Users;
import com.example.demo.service.AttendanceManagementService;
import com.example.demo.service.CommonActivityService;
import com.example.demo.service.GoogleCalendarService;
import com.example.demo.service.ModelService;
import com.example.demo.service.MonthlyAttendanceReqService;

@Controller
@RequestMapping("/attendance")
public class AttendanceManagementController {

	private final AttendanceManagementService attendanceManagementService;
	private final MonthlyAttendanceReqService monthlyAttendanceReqService;
	private final CommonActivityService commonActivityService;
	private final GoogleCalendarService googleCalendarService;
	private final ModelService modelService;
	private final DateHelper dateHelper;

	public AttendanceManagementController(AttendanceManagementService attendanceManagementService,
			MonthlyAttendanceReqService monthlyAttendanceReqService, CommonActivityService commonActivityService,
			GoogleCalendarService googleCalendarService,ModelService modelService,DateHelper dateHelper) {
		this.attendanceManagementService = attendanceManagementService;
		this.monthlyAttendanceReqService = monthlyAttendanceReqService;
		this.commonActivityService = commonActivityService;
		this.googleCalendarService=googleCalendarService;
		this.modelService = modelService;
		this.dateHelper = dateHelper;
	}

	@RequestMapping("/index") 
	public String start(HttpSession session, Model model) {
		
		Users users = commonActivityService.getCommonInfoAddUsers(model, session,null);
		
		if (users.getRole().equalsIgnoreCase("Manager")) {
			monthlyAttendanceReqService.selectApprovalPending(model);//ManagerならapprovalPendingデータを取得しモデルに追加
		}else {
			attendanceSearch(users.getUserId(), dateHelper.yearsMonth(), model, session);	//Managerでないなら現在の年月を取得して、それをもとに自身の勤怠情報を取得する。
		}
		return "attendance/registration";
	}

	//表示ボタンの処理
	@RequestMapping(value = "/management", params = "search", method = RequestMethod.POST)
	public String attendanceSearch(Integer userId, String stringYearsMonth, Model model,
			HttpSession session) {
		
		monthlyAttendanceReqService.getSelectedAttendance(model,stringYearsMonth,userId,session);
		
		return "attendance/registration";
	}

//		Integer years = Integer.parseInt(stringYearsMonth.substring(0, 4));
//		Integer month = Integer.parseInt(stringYearsMonth.substring(5, 7));
		
		//上記置換予定
//		Integer years = dateHelper.parseDate(stringYearsMonth)[0];
//		Integer month = dateHelper.parseDate(stringYearsMonth)[1];
//		
//		//祝日表示用のカレンダー取得
//		Events events = googleCalendarService.getHolidaysEvents(years, month);
//		//勤怠詳細と勤怠データをそれぞれ取得。モデルに追加
//		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month,Optional.of(events));
//		AttendanceFormList attendanceFormList = attendanceManagementService.setInAttendance(attendance, years, month,
//				stringYearsMonth);
//		modelService.addAttendanceFormList(model,attendanceFormList);
	

	//登録ボタンの処理
	@RequestMapping(value = "/management", params = "insert", method = RequestMethod.POST)
	public String insert(@ModelAttribute AttendanceFormList attendanceFormList, BindingResult result, Model model,
			HttpSession session) {
		Users users = commonActivityService.getCommonInfoAddUsers(model,session,null);
		monthlyAttendanceReqService.serviceForUpdateButton(model, attendanceFormList, result, users);
		
		return "attendance/registration";
		}
	
		//承認申請ボタンのONOffきりかえと書式のエラーチェック
//		attendanceManagementService.requestActivityCheck(attendanceFormList);
//		attendanceManagementService.errorCheck(attendanceFormList, result);
//
//		if (result.hasErrors()) {
//			return "attendance/registration";
//		}
//		//新しいデータに修正してデータベースに登録
//		AttendanceFormList updateAttendanceFormEntity = attendanceManagementService.updateAttendanceFormCreate(attendanceFormList, users.getUserId());
//		attendanceManagementService.attendanceUpsert(updateAttendanceFormEntity);
//		
//		googleCalendarService.getListHolidays(model,attendanceFormList.getStringYearsMonth());
//		
//		modelService.addAttendanceFormList(model,attendanceFormList);
//		modelService.addAttendanceMessage(model);
		
	

	//承認申請ボタン押下
	@RequestMapping(value = "/management", params = "approvalApplicationRegistration", method = RequestMethod.POST)
	public String monthlyAttendanceReqCreate(MonthlyAttendanceReq monthlyAttendanceReq,
			AttendanceFormList attendanceFormList, Model model, HttpSession session) {
		monthlyAttendanceReqService.getForMonthlyAttendanceReqCreate(model,attendanceFormList,monthlyAttendanceReq,session);
		return "attendance/registration";
	}

	//マネージャー承認申請者の勤怠表表示ボタン
	@RequestMapping(value = "/management", params = "ApprovalApplicantDisplay", method = RequestMethod.POST)
	public String attendance(@RequestParam("approvalUserId") Integer userId, @RequestParam("Years") Integer years,
		@RequestParam("Month") Integer month, Model model,RedirectAttributes redirectAttributes, HttpSession session) {
		
		commonActivityService.getCommonInfo(model,session,null);
		monthlyAttendanceReqService.getForAttendance(model, years, month, userId);
		
		return "attendance/registration";
	}

	//マネージャー承認ボタン
	@RequestMapping(value = "/management", params = "approval", method = RequestMethod.POST)
	public String approval(AttendanceFormList attendanceFormList,RedirectAttributes redirectAttributes) {
		monthlyAttendanceReqService.getForApproval(redirectAttributes,attendanceFormList);
		return "redirect:/attendance/index";
	}

	//マネージャー却下ボタン押下
	@RequestMapping(value = "/management", params = "rejected", method = RequestMethod.POST)
	public String Rejected(AttendanceFormList attendanceFormList, Model model, HttpSession session,
			RedirectAttributes redirectAttributes) {
		monthlyAttendanceReqService.getForRejected(redirectAttributes, attendanceFormList);
		return "redirect:/attendance/index";
	}

	//更新ボタンが押された場合の処理
	@RequestMapping(value = "/management",method = RequestMethod.GET)
	public String reload(Model model,HttpSession session,RedirectAttributes redirectAttributes) {
		return "redirect:/attendance/index";
	}
	


}
