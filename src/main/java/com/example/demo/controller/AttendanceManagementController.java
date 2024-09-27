package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
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
@RequestMapping("/attendance")
public class AttendanceManagementController {

	private final AttendanceManagementService attendanceManagementService;
	private final MonthlyAttendanceReqService monthlyAttendanceReqService;
	private final CommonActivityService commonActivityService;
	private final MessageOutput messageOutput;

	public AttendanceManagementController(AttendanceManagementService attendanceManagementService,
			MonthlyAttendanceReqService monthlyAttendanceReqService, CommonActivityService commonActivityService,
			MessageOutput messageOutput) {
		this.attendanceManagementService = attendanceManagementService;
		this.monthlyAttendanceReqService = monthlyAttendanceReqService;
		this.commonActivityService = commonActivityService;
		this.messageOutput = messageOutput;
	}

	@RequestMapping("/index")
	public String start(HttpSession session, MonthlyAttendanceReq monthlyAttendanceReq, Model model) {
		commonActivityService.usersModelSession(model, session);
		Users users = (Users) model.getAttribute("Users");
		String stringYearsMonth = commonActivityService.yearsMonth();
		attendanceSearch(users.getUserId(), stringYearsMonth, model, session);
		if (users.getRole().equalsIgnoreCase("Manager")) {
			List<MonthlyAttendanceReq> ApprovalPending = monthlyAttendanceReqService.selectApprovalPending();
			model.addAttribute("ApprovalPending", ApprovalPending);
		}
		return "attendance/registration";
	}

	//表示ボタンの処理
	@RequestMapping(value = "/management", params = "search", method = RequestMethod.POST)
	public String attendanceSearch(Integer userId, String stringYearsMonth, Model model,
			HttpSession session) {

		Integer years = Integer.parseInt(stringYearsMonth.substring(0, 4));
		Integer month = Integer.parseInt(stringYearsMonth.substring(5, 7));

		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month);
		AttendanceFormList attendanceFormList = attendanceManagementService.setInAttendance(attendance, years, month,
				stringYearsMonth);
		model.addAttribute("attendanceFormList", attendanceFormList);

		//月次勤怠テーブルのstatusをユーザー)モデルのstatusに詰める
		monthlyAttendanceReqService.submissionStatusCheck(attendance.get(0).getAttendanceDate(), userId, model,
				session);
		attendanceManagementService.requestActivityCheck(attendanceFormList);

		return "attendance/registration";
	}

	//登録ボタンの処理
	@RequestMapping(value = "/management", params = "insert", method = RequestMethod.POST)
	public String insert(@ModelAttribute AttendanceFormList attendanceFormList, BindingResult result, Model model,
			HttpSession session) {
		commonActivityService.usersModelSession(model, session);
		Users users = (Users) model.getAttribute("Users");

		attendanceManagementService.requestActivityCheck(attendanceFormList);
		attendanceManagementService.errorCheck(attendanceFormList, result);

		if (result.hasErrors()) {
			return "attendance/registration";
		}

		AttendanceFormList updateAttendanceFormEntity = attendanceManagementService
				.updateAttendanceFormCreate(attendanceFormList, users.getUserId());
		attendanceManagementService.attendanceUpsert(updateAttendanceFormEntity);

		model.addAttribute("attendanceMessage", messageOutput.message("attendanceSuccess"));
		model.addAttribute("attendanceFormList", attendanceFormList);
		return "attendance/registration";
	}

	//承認申請ボタン押下
	@RequestMapping(value = "/management", params = "approvalApplicationRegistration", method = RequestMethod.POST)
	public String monthlyAttendanceReqCreate(MonthlyAttendanceReq monthlyAttendanceReq,
			AttendanceFormList attendanceFormList, Model model, HttpSession session) {
		commonActivityService.usersModelSession(model, session);
		Users users = (Users) model.getAttribute("Users");

		for (int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {
			String inputDate = attendanceFormList.getAttendanceList().get(i).getAttendanceDateS();
			String conversion = inputDate.replace("/", "-");
			attendanceFormList.getAttendanceList().get(i).setAttendanceDate(java.sql.Date.valueOf(conversion));
		}
		monthlyAttendanceReqService.monthlyAttendanceUpdate(
				attendanceFormList.getAttendanceList().get(0).getAttendanceDate(), monthlyAttendanceReq.getUserId(),
				monthlyAttendanceReq, attendanceFormList);
		monthlyAttendanceReqService.submissionStatusCheck(
				attendanceFormList.getAttendanceList().get(0).getAttendanceDate(), users.getUserId(), model, session);
		model.addAttribute("attendanceFormList", attendanceFormList);
		return "attendance/registration";
	}

	//マネージャー承認申請者勤怠表表示ボタン
	@RequestMapping(value = "/management", params = "ApprovalApplicantDisplay", method = RequestMethod.POST)
	public String attendance(@RequestParam("approvalUserId") Integer userId, @RequestParam("Years") Integer years,
			@RequestParam("Month") Integer month, Model model,
			RedirectAttributes redirectAttributes, HttpSession session) {
		commonActivityService.usersModelSession(model, session);

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
		List<MonthlyAttendanceReq> ApprovalPending = monthlyAttendanceReqService.selectApprovalPending();
		model.addAttribute("ApprovalPending", ApprovalPending);

		return "attendance/registration";
	}

	//マネージャー承認ボタン
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
		return "redirect:/attendance/index";
	}

	//マネージャー却下ボタン押下
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
		return "redirect:/attendance/index";
	}

	//戻るボタン
	@RequestMapping(value = "/management", params = "back", method = RequestMethod.POST)
	public String back(Model model, HttpSession session) {
		commonActivityService.backMenu(model, session);
		return "menu/processMenu";
	}

}
