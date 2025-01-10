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

import com.example.demo.helper.DateHelper;
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
	private final ModelService modelService;
	private final GoogleCalendarService googleCalendarService;
	private final DateHelper dateHelper;

	public MonthlyAttendanceReqController(CommonActivityService commonActivityService,
			MonthlyAttendanceReqService monthlyAttendanceReqService,DateHelper dateHelper,
			AttendanceManagementService attendanceManagementService, ModelService modelService,GoogleCalendarService googleCalendarService) {
		this.commonActivityService = commonActivityService;
		this.monthlyAttendanceReqService = monthlyAttendanceReqService;
		this.attendanceManagementService = attendanceManagementService;
		this.modelService = modelService;
		this.googleCalendarService=googleCalendarService;
		this.dateHelper = dateHelper;
	}

	@RequestMapping("/correction")
	public String start(HttpSession session, MonthlyAttendanceReq monthlyAttendanceReq, Model model) {
		
		Users users = commonActivityService.getCommonInfoAddUsers(model,session,null);
		
		String stringYearsMonth = (String) model.asMap().get("stringYearsMonth");
		
	    if (stringYearsMonth == null) {
	        stringYearsMonth = dateHelper.lastYearsMonth();
	    }
	    
		if (users.getRole().equalsIgnoreCase("Manager")) {
			List<MonthlyAttendanceReq> hasChangeReq = monthlyAttendanceReqService
					.selectHasChangeReq(stringYearsMonth);
			modelService.addHasChangeReq(model,hasChangeReq);
		} else {
			List<MonthlyAttendanceReq> approvedMonths= monthlyAttendanceReqService.selectApproval(users.getUserId());
			modelService.addApprovedMonths(model,approvedMonths);
			attendanceSearch(users.getUserId(), stringYearsMonth, model, session);
		}
		modelService.addStringYearsMonth(model,stringYearsMonth);
		commonActivityService.getForNotMenuPage(model);
		
		return "monthlyAttendanceReq/monthlyAttendance";
	}

	//	//表示ボタンの処理
	@RequestMapping(value = "/management", params = "search", method = RequestMethod.POST)
	public String attendanceSearch(Integer userId, String stringYearsMonth, Model model,
			HttpSession session) {
		Users users = commonActivityService.getCommonInfoAddUsers(model,session,null);
		//yearとmonthの作成
		Integer years = dateHelper.parseDate(stringYearsMonth)[0];
		Integer month = dateHelper.parseDate(stringYearsMonth)[1];

		//yearとmonthに該当する勤怠表を探してAttendanceFormListに詰める処理
		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month, Optional.<Events>empty());
		AttendanceFormList attendanceFormList = attendanceManagementService.setInAttendance(attendance, years, month,
				stringYearsMonth);
		
		if (!users.getRole().equalsIgnoreCase("Manager")) {
			modelService.addAttendanceFormList(model,attendanceFormList);
			//月次勤怠テーブルのstatusをユーザー)モデルのstatusに詰める
			monthlyAttendanceReqService.submissionStatusCheck(attendance.get(0).getAttendanceDate(), userId, model,session);
			attendanceManagementService.requestActivityCheck(attendanceFormList);
			List<MonthlyAttendanceReq> approvedMonths= monthlyAttendanceReqService.selectApproval(users.getUserId());
			modelService.addApprovedMonths(model,approvedMonths);
		} else {
			List<MonthlyAttendanceReq> hasChangeReq = monthlyAttendanceReqService.selectHasChangeReq(stringYearsMonth);
			modelService.addHasChangeReq(model,hasChangeReq);
			//カレンダーを初期化しないために返す
			modelService.addStringYearsMonth(model,stringYearsMonth);
		}
		
		googleCalendarService.getListHolidays(model,years,month);
		return "monthlyAttendanceReq/monthlyAttendance";
	}

	//訂正申請ボタンの処理
	@RequestMapping(value = "/management", params = "willCorrect", method = RequestMethod.POST)
	public String monthlyAttendanceReqCorrect(Model model, HttpSession session,
			@RequestParam("willCorrectReason") String changeReason, @RequestParam("userId") Integer userId,
			@RequestParam("stringYearsMonth") String stringYearsMonth) {
		commonActivityService.getCommonInfo(model,session,null);
		//	has_change_reqを１に変更する処理のためにLocalDateに変更して処理
		LocalDate targetYearMonth = dateHelper.convertStringToLocalDate(stringYearsMonth);
		if (targetYearMonth != null) {
			monthlyAttendanceReqService.changeRequestMonthlyAttendanceReq(userId, targetYearMonth, changeReason);
		}
		//再度同じ月の勤怠表を表示するための処理
		Integer years = dateHelper.parseDate(stringYearsMonth)[0];
		Integer month = dateHelper.parseDate(stringYearsMonth)[1];
		googleCalendarService.getListHolidays(model,stringYearsMonth);
		
		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month,Optional.<Events>empty());
		AttendanceFormList attendanceFormList = attendanceManagementService.setInAttendance(attendance, years, month,
				stringYearsMonth);
		//ステータス変更処理
		Date firstAttendanceDate = attendanceManagementService.getFirstAttendanceDate(attendanceFormList);
		monthlyAttendanceReqService.submissionStatusCheck(firstAttendanceDate, userId, model, session);
		
		modelService.sendCorrectionApplication(model);
		modelService.addAttendanceFormList(model,attendanceFormList);
		modelService.addStringYearsMonth(model,stringYearsMonth);
		return "monthlyAttendanceReq/monthlyAttendance";
	}

	//	//マネージャー用訂正申請者勤怠表表示ボタンの処理
	@RequestMapping(value = "/management", params = "ApprovalApplicantDisplay", method = RequestMethod.POST)
	public String attendance(@RequestParam("approvalUserId") Integer userId, @RequestParam("stringYearsMonth") String stringYearsMonth,  @RequestParam("approvalUserName") String userName, Model model,
			HttpSession session) {
		commonActivityService.getCommonInfo(model,session,null);
		//yearとmonthを"yyyy-MM"の形に再成型
		Integer years = dateHelper.parseDate(stringYearsMonth)[0];
		Integer month = dateHelper.parseDate(stringYearsMonth)[1];
		
		googleCalendarService.getListHolidays(model,years,month);

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
			modelService.addAttendanceFormList(model,attendanceFormList);
		}
		
		List<MonthlyAttendanceReq> currentChangeReq=monthlyAttendanceReqService.filteringHasChangeReq(userId, stringYearsMonth);
		List<MonthlyAttendanceReq> hasChangeReq = monthlyAttendanceReqService.selectHasChangeReq(stringYearsMonth);
		//選択月該当ユーザー
		modelService.addHasChangeReq(model,hasChangeReq);
		//選択した特定ユーザー表記用
		modelService.addCurrentChangeReq(model,currentChangeReq);
		//カレンダー埋める用
		modelService.addStringYearsMonth(model,stringYearsMonth);
		
		return "monthlyAttendanceReq/monthlyAttendance";
	}
	//

		//マネージャー訂正承認ボタン　同上
	@RequestMapping(value = "/management", params = "approval", method = RequestMethod.POST)
	public String approval(AttendanceFormList attendanceFormList, Model model, HttpSession session,
			RedirectAttributes redirectAttributes,@RequestParam("stringYearsMonth")String stringYearsMonth) {
		modelService.stringYearsMonth(redirectAttributes);
		//else句以外ほぼ起きることはない、状況を見て消去可,システム上前半句は起きえないはず
		if (attendanceFormList.getAttendanceList() == null) {
			modelService.choiceUsers(redirectAttributes);
		} else {
			//承認更新文のための処理とその実行
			String inputDate =  monthlyAttendanceReqService.getInputDate(attendanceFormList);
			Integer firstUserId=attendanceManagementService.getFirstAttendanceUserId(attendanceFormList);
			monthlyAttendanceReqService.changeApprovalMonthlyAttendanceReq(firstUserId, inputDate);
			modelService.stringYearsMonth(redirectAttributes);
		}
		return "redirect:/attendanceCorrect/correction";
	}

	//
	//	//マネージャー訂正却下ボタン押下　同上
	@RequestMapping(value = "/management", params = "rejected", method = RequestMethod.POST)
	public String Rejected(AttendanceFormList attendanceFormList, Model model, HttpSession session,
			RedirectAttributes redirectAttributes,@RequestParam("rejectionReason")String rejectionReason,@RequestParam("stringYearsMonth")String stringYearsMonth) {
		modelService.stringYearsMonth(redirectAttributes);
		if (attendanceFormList.getAttendanceList() == null) {
			modelService.choiceUsers(redirectAttributes);
		} else {
			//却下更新分のための処理とその実行
			String inputDate =  monthlyAttendanceReqService.getInputDate(attendanceFormList);
			Integer firstUserId=attendanceManagementService.getFirstAttendanceUserId(attendanceFormList);
			monthlyAttendanceReqService.changeRejectionMonthlyAttendanceReq(firstUserId, inputDate, rejectionReason);
			modelService.changeMonthlyAttendanceReqReject(redirectAttributes);
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
