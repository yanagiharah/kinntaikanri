package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Attendance;
import com.example.demo.model.AttendanceFormList;
import com.example.demo.model.MonthlyAttendanceReq;
import com.example.demo.model.Users;
import com.example.demo.service.AttendanceManagementService;
import com.example.demo.service.MonthlyAttendanceReqService;

@Controller
@RequestMapping("/attendance")
public class AttendanceManagementController {
	@Autowired
	private AttendanceManagementService attendanceManagementService;
	@Autowired
	private MonthlyAttendanceReqService monthlyAttendanceReqService;

	@RequestMapping("/index")
	public String start(HttpSession session, MonthlyAttendanceReq monthlyAttendanceReq, Model model) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		if (users.getRole().equalsIgnoreCase("Manager")) {
			List<MonthlyAttendanceReq> ApprovalPending = monthlyAttendanceReqService.selectApprovalPending();
			 System.out.println(": " + ApprovalPending);
			model.addAttribute("ApprovalPending",ApprovalPending);
		}
		return "attendance/registration";
	}
	
	//表示ボタンの処理
	@RequestMapping(value = "/management", params = "search", method = RequestMethod.POST)
	public String attendanceSearch(Integer userId, Integer years, Integer month, Model model,
			RedirectAttributes redirectAttributes, HttpSession session) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		if (years == null || month == null) {
			model.addAttribute("check", "年月を入力してください");
			return "attendance/registration";
		}
		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month);
		
			// formに詰めなおす
			AttendanceFormList attendanceFormList = new AttendanceFormList();
			ArrayList<Attendance> attendanceList = new ArrayList<Attendance>();
			attendanceFormList.setAttendanceList(attendanceList);
			attendanceList.addAll(attendance);
			model.addAttribute("attendanceFormList", attendanceFormList);
			
			
			
			//月次勤怠テーブルのstatusをユーザーモデルのstatusに詰める
			MonthlyAttendanceReq statusCheck = monthlyAttendanceReqService.statusCheck(attendance.get(0).getAttendanceDate(), userId);

			if (statusCheck != null) {
			    users.setStatus(statusCheck.getStatus());
			} else {
				users.setStatus(4);
			}
			

			attendanceManagementService.requestActivityCheck(attendanceFormList);
			

			
		return "attendance/registration";
	}
	
	//登録ボタンの処理
	@RequestMapping(value = "/management", params = "insert", method = RequestMethod.POST)
	public String insert(@ModelAttribute AttendanceFormList attendanceFormList, BindingResult result, Model model, HttpSession session) {
		
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		
		attendanceManagementService.errorCheck(attendanceFormList, result);
		
		if(result.hasErrors()) {
			System.out.print("つうか！！！！！！！！");
			return "attendance/registration";
		}
		
		
		 for(int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {
			  attendanceFormList.getAttendanceList().get(i).setAttendanceDate(java.sql.Date.valueOf(attendanceFormList.getAttendanceList().get(i).getAttendanceDateS()));
			  attendanceFormList.getAttendanceList().get(i).setUserId(users.getUserId());
		  }
		attendanceManagementService.attendanceDelete(attendanceFormList);		
		attendanceManagementService.attendanceCreate(attendanceFormList);
		
		
		
		attendanceManagementService.requestActivityCheck(attendanceFormList);
		model.addAttribute("attendanceComplete","勤怠の登録が完了しました。");
		return "attendance/registration";
	}
	
	  //承認申請ボタン押下
	@RequestMapping(value = "/management", params = "approvalApplicationRegistration", method = RequestMethod.POST)
	public String monthlyAttendanceReqCreate(MonthlyAttendanceReq monthlyAttendanceReq,
			AttendanceFormList attendanceFormList, Model model, HttpSession session) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		
		System.out.print(monthlyAttendanceReq);
		for(int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {
			attendanceFormList.getAttendanceList().get(i).setAttendanceDate(java.sql.Date.valueOf(attendanceFormList.getAttendanceList().get(i).getAttendanceDateS()));
		}
		MonthlyAttendanceReq monthlyAttendanceDoubleCheck  = monthlyAttendanceReqService.statusCheck(attendanceFormList.getAttendanceList().get(0).getAttendanceDate(), monthlyAttendanceReq.getUserId());

		  if(monthlyAttendanceDoubleCheck == null) {
			  //中身がnull、すなわち同一ユーザーかつ同一月の月次勤怠申請がテーブルにない時の処理。statusはnull
			  monthlyAttendanceReqService.monthlyAttendanceReqCreate(monthlyAttendanceReq, attendanceFormList);
			  
			  //承認申請が却下された際の処理。statusは3
		  }else if(monthlyAttendanceDoubleCheck != null && monthlyAttendanceDoubleCheck.getStatus() == 3){
			  monthlyAttendanceReq.setTargetYearMonth(attendanceFormList.getAttendanceList().get(0).getAttendanceDate());
			  monthlyAttendanceReqService.updateMonthlyAttendanceReq(monthlyAttendanceReq);
			  
		  } else {
			  //承認申請が承認待ち、もしくは承認された際の処理。statusは1 or 2
			  model.addAttribute("double","既に申請されています。");
		  }
		
		
		
		
		MonthlyAttendanceReq statusCheck = monthlyAttendanceReqService.statusCheck(attendanceFormList.getAttendanceList().get(0).getAttendanceDate(), users.getUserId());
		if (statusCheck != null) {
		    users.setStatus(statusCheck.getStatus());
		} else {
			//4はregistration.thmlのステータスを未申請にする。
			users.setStatus(4);
		}
		return "attendance/registration";
	}
	
	//マネージャー承認申請者勤怠表表示ボタン
	@RequestMapping(value = "/management", params = "ApprovalApplicantDisplay", method = RequestMethod.POST)
	public String attendance(@RequestParam("approvalUserId") Integer userId, @RequestParam("Years") Integer years,
			@RequestParam("Month") Integer month, Model model,
			RedirectAttributes redirectAttributes, HttpSession session) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month);
		if (attendance != null) {
			// formに詰めなおす
			AttendanceFormList attendanceFormList = new AttendanceFormList();
			ArrayList<Attendance> attendanceList = new ArrayList<Attendance>();
			attendanceFormList.setAttendanceList(attendanceList);
			attendanceList.addAll(attendance);
			//★	approvalUserIdをセットするfor文を回す
			for(int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {
				  attendanceFormList.getAttendanceList().get(i).setUserId(userId);
			  }
			model.addAttribute("attendanceFormList", attendanceFormList);
			System.out.print("登録後"+attendanceFormList.getAttendanceList().get(0).getUserId());
			return "attendance/registration";
		}
		return "attendance/registration";
	}
	
	//マネージャー承認ボタン
	@RequestMapping(value = "/management", params = "approval", method = RequestMethod.POST)
	public String approval(AttendanceFormList attendanceFormList, Model model, HttpSession session) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		monthlyAttendanceReqService.approvalStatus(attendanceFormList.getAttendanceList().get(0).getUserId());
		return "redirect:/attendance/index";
	}

	//マネージャー却下ボタン押下
	@RequestMapping(value = "/management", params = "Rejected", method = RequestMethod.POST)
	public String Rejected(AttendanceFormList attendanceFormList, Model model, HttpSession session) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		monthlyAttendanceReqService.rejectedStatus(attendanceFormList.getAttendanceList().get(0).getUserId());
		return "redirect:/attendance/index";
	}
	
}
