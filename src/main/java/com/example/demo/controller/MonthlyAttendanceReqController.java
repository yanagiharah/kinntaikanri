package com.example.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import com.example.demo.service.MonthlyAttendanceReqService;

@Controller
@RequestMapping("/attendanceCorrect")
public class MonthlyAttendanceReqController {

	private final CommonActivityService commonActivityService;
	private final MonthlyAttendanceReqService monthlyAttendanceReqService;
	private final AttendanceManagementService attendanceManagementService;
	private final MessageOutput messageOutput;

	public MonthlyAttendanceReqController(CommonActivityService commonActivityService,
			MonthlyAttendanceReqService monthlyAttendanceReqService,
			AttendanceManagementService attendanceManagementService, MessageOutput messageOutput) {
		this.commonActivityService = commonActivityService;
		this.monthlyAttendanceReqService = monthlyAttendanceReqService;
		this.attendanceManagementService = attendanceManagementService;
		this.messageOutput = messageOutput;
	}

	@RequestMapping("/correction")
	public String start(HttpSession session, MonthlyAttendanceReq monthlyAttendanceReq, Model model) {
		
		commonActivityService.usersModelSession(model, session);
		Users users = (Users) model.getAttribute("Users");
		String stringYearsMonth = commonActivityService.yearsMonth();
		
		if (users.getRole().equalsIgnoreCase("Manager")) {
			List<MonthlyAttendanceReq> HasChangeReq = monthlyAttendanceReqService
					.selectHasChangeReq(stringYearsMonth);
			model.addAttribute("HasChangeReq", HasChangeReq);
		} else {
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

		Integer years = Integer.parseInt(stringYearsMonth.substring(0, 4));
		Integer month = Integer.parseInt(stringYearsMonth.substring(5, 7));
			
		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month);
		AttendanceFormList attendanceFormList = attendanceManagementService.setInAttendance(attendance, years, month,
				stringYearsMonth);
		
		if (!users.getRole().equalsIgnoreCase("Manager")) {
			model.addAttribute("attendanceFormList", attendanceFormList);
			//月次勤怠テーブルのstatusをユーザー)モデルのstatusに詰める
			monthlyAttendanceReqService.submissionStatusCheck(attendance.get(0).getAttendanceDate(), userId, model,
					session);
			attendanceManagementService.requestActivityCheck(attendanceFormList);
		} else {
			List<MonthlyAttendanceReq> HasChangeReq = monthlyAttendanceReqService.selectHasChangeReq(stringYearsMonth);
			model.addAttribute("HasChangeReq", HasChangeReq);
			model.addAttribute("stringYearsMonth",stringYearsMonth);
		}
		return "monthlyAttendanceReq/monthlyAttendance";
	}

	//訂正申請ボタンの処理
	@RequestMapping(value = "/management", params = "willCorrect", method = RequestMethod.POST)
	public String monthlyAttendanceReqCorrect(Model model, HttpSession session,
			@RequestParam("willCorrectReason") String changeReason, @RequestParam("userId") Integer userId,
			@RequestParam("stringYearsMonth") String stringYearsMonth) {
		commonActivityService.usersModelSession(model, session);

		LocalDate targetYearMonth = monthlyAttendanceReqService.convertStringToLocalDate(stringYearsMonth);
		if (targetYearMonth != null) {
			monthlyAttendanceReqService.changeRequestMonthlyAttendanceReq(userId, targetYearMonth, changeReason);
		}
		return "monthlyAttendanceReq/monthlyAttendance";
	}

	//	//マネージャー用訂正申請者勤怠表表示ボタンの処理
	@RequestMapping(value = "/management", params = "ApprovalApplicantDisplay", method = RequestMethod.POST)
	public String attendance(@RequestParam("approvalUserId") Integer userId, @RequestParam("Years") Integer years,
			@RequestParam("Month") Integer month, @RequestParam("approvalUserName") String userName, Model model,
			HttpSession session) {
		commonActivityService.usersModelSession(model, session);
		//monthをMMの形に変更
		String monthString = String.format("%02d", month);
		String stringYearsMonth = years + "-" + monthString;

		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month);

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
		List<MonthlyAttendanceReq> HasChangeReq = monthlyAttendanceReqService.selectHasChangeReq(stringYearsMonth);
		model.addAttribute("HasChangeReq", HasChangeReq);
		model.addAttribute("stringYearsMonth",stringYearsMonth);
		
		return "monthlyAttendanceReq/monthlyAttendance";
	}
	//

	//	//マネージャー承認ボタン　同上
	@RequestMapping(value = "/management", params = "approval", method = RequestMethod.POST)
	public String approval(AttendanceFormList attendanceFormList, Model model, HttpSession session,
			RedirectAttributes redirectAttributes) {
		if (attendanceFormList.getAttendanceList() == null) {
			redirectAttributes.addFlashAttribute("choiceUsers", messageOutput.message("choiceUsers"));
		} else {
			String inputDate = attendanceFormList.getAttendanceList().get(0).getAttendanceDateS();
			String conversion = inputDate.replace("/", "-");
			monthlyAttendanceReqService.approvalStatus(attendanceFormList.getAttendanceList().get(0).getUserId(),
					conversion);
		}
		return "redirect:attendanceCorrect/correction";
	}

	//
	//	//マネージャー却下ボタン押下　同上
	@RequestMapping(value = "/management", params = "rejected", method = RequestMethod.POST)
	public String Rejected(AttendanceFormList attendanceFormList, Model model, HttpSession session,
			RedirectAttributes redirectAttributes) {
		if (attendanceFormList.getAttendanceList() == null) {
			redirectAttributes.addFlashAttribute("choiceUsers", messageOutput.message("choiceUsers"));
		} else {
			String inputDate = attendanceFormList.getAttendanceList().get(0).getAttendanceDateS();
			String conversion = inputDate.replace("/", "-");
			monthlyAttendanceReqService.rejectedStatus(attendanceFormList.getAttendanceList().get(0).getUserId(),
					conversion);
		}
		return "redirect:/attendanceCorrect/correction";
	}

	//戻るボタン
	@RequestMapping(value = "/management", params = "back", method = RequestMethod.POST)
	public String back(Model model, HttpSession session) {
		commonActivityService.backMenu(model, session);
		return "menu/processMenu";
	}

}
