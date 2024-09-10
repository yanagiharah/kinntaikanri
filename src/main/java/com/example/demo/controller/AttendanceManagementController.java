package com.example.demo.controller;

import java.time.DateTimeException;
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
	
	
	public AttendanceManagementController(AttendanceManagementService attendanceManagementService,MonthlyAttendanceReqService monthlyAttendanceReqService, CommonActivityService commonActivityService, MessageOutput messageOutput){
		this.attendanceManagementService = attendanceManagementService;
		this.monthlyAttendanceReqService = monthlyAttendanceReqService;
		this.commonActivityService = commonActivityService;
		this.messageOutput = messageOutput;
	}

	@RequestMapping("/index")
	public String start(HttpSession session, MonthlyAttendanceReq monthlyAttendanceReq, Model model) {
		
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		
		if (users.getRole().equalsIgnoreCase("Manager")) {
			List<MonthlyAttendanceReq> ApprovalPending = monthlyAttendanceReqService.selectApprovalPending();
			model.addAttribute("ApprovalPending",ApprovalPending);
		}
		return "attendance/registration";
	}
	
	//表示ボタンの処理
	@RequestMapping(value = "/management", params = "search", method = RequestMethod.POST)
	public String attendanceSearch(Integer userId, String stringYears, String stringMonth, Model model,
			RedirectAttributes redirectAttributes, HttpSession session) {
		
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		
		try {
			Integer years = Integer.parseInt(stringYears);
			Integer month = Integer.parseInt(stringMonth);
		
			if(stringYears != null && stringMonth != null) {
				if(stringYears.matches("^[0-9]+$") && stringMonth.matches("^[0-9]+$")) {
					
					if(years < 1800 || years > 3000) {
						users.setStatus(null);
						model.addAttribute("check", messageOutput.message("yearsRangeOut"));
						return "attendance/registration";
					}
					
					if(month <= 12) {
						List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month);
						
						// formに詰めなおす
						AttendanceFormList attendanceFormList = new AttendanceFormList();
						ArrayList<Attendance> attendanceList = new ArrayList<Attendance>();
						attendanceFormList.setAttendanceList(attendanceList);
						attendanceList.addAll(attendance);
						attendanceFormList.setStringYears(stringYears);
						attendanceFormList.setStringMonth(stringMonth);
						model.addAttribute("attendanceFormList", attendanceFormList);
						
						//月次勤怠テーブルのstatusをユーザーモデルのstatusに詰める
						MonthlyAttendanceReq statusCheck = monthlyAttendanceReqService.statusCheck(attendance.get(0).getAttendanceDate(), userId);
			
						if (statusCheck != null) {
						    users.setStatus(statusCheck.getStatus());
						} else {
							users.setStatus(4);
						}
			
						attendanceManagementService.requestActivityCheck(attendanceFormList);
						
					} else {
						users.setStatus(null);
						model.addAttribute("check", messageOutput.message("yearsMonthInaccurate"));
						return "attendance/registration";
					}
				}	
			}
		}catch (NumberFormatException e) {
			users.setStatus(null);
			model.addAttribute("check", messageOutput.message("hannkakuCheck"));
			return "attendance/registration";
		}catch(DateTimeException e) {
			users.setStatus(null);
			model.addAttribute("check", messageOutput.message("yearsMonthInaccurate"));
			return "attendance/registration";
		}
		
		
		return "attendance/registration";
	}
	
	//登録ボタンの処理
	@RequestMapping(value = "/management", params = "insert", method = RequestMethod.POST)
	public String insert(@ModelAttribute AttendanceFormList attendanceFormList, BindingResult result, Model model, HttpSession session) {
		
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		
		attendanceManagementService.requestActivityCheck(attendanceFormList);
		
		attendanceManagementService.errorCheck(attendanceFormList, result);
		
		if(result.hasErrors()) {
			return "attendance/registration";
		}
		
		
		for(int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {  
			String inputDate = attendanceFormList.getAttendanceList().get(i).getAttendanceDateS();
			String conversion = inputDate.replace("/","-");
			attendanceFormList.getAttendanceList().get(i).setAttendanceDate(java.sql.Date.valueOf(conversion));
			attendanceFormList.getAttendanceList().get(i).setUserId(users.getUserId());
		  }
		
		attendanceManagementService.attendanceUpsert(attendanceFormList);
//		attendanceManagementService.attendanceDelete(attendanceFormList);		
//		attendanceManagementService.attendanceCreate(attendanceFormList);
		
		model.addAttribute("attendanceMessage",messageOutput.message("attendanceSuccess"));
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
			String inputDate = attendanceFormList.getAttendanceList().get(i).getAttendanceDateS();
			String conversion = inputDate.replace("/","-");
			attendanceFormList.getAttendanceList().get(i).setAttendanceDate(java.sql.Date.valueOf(conversion));
		}
		MonthlyAttendanceReq monthlyAttendanceDoubleCheck  = monthlyAttendanceReqService.statusCheck(attendanceFormList.getAttendanceList().get(0).getAttendanceDate(), monthlyAttendanceReq.getUserId());

		  if(monthlyAttendanceDoubleCheck == null) {
			  //中身がnull、すなわち同一ユーザーかつ同一月の月次勤怠申請がテーブルにない時の処理。statusはnull
			  monthlyAttendanceReqService.monthlyAttendanceReqCreate(monthlyAttendanceReq, attendanceFormList);
			  
			  //承認申請が却下されていた際の処理。statusは3
		  }else if(monthlyAttendanceDoubleCheck != null && monthlyAttendanceDoubleCheck.getStatus() == 3){
			  monthlyAttendanceReq.setTargetYearMonth(attendanceFormList.getAttendanceList().get(0).getAttendanceDate());
			  monthlyAttendanceReqService.updateMonthlyAttendanceReq(monthlyAttendanceReq);
			  
		  } else {
			  //承認申請が承認待ち、もしくは承認されていた際の処理。statusは1 or 2。←（承認申請ボタンが非活性になっているため不要と判断してコメントアウトしました。削除する際はmessages.propertiesのrequestAppliedも消してください。2024/09/10柳原）
//			  model.addAttribute("double",messageOutput.message("requestApplied"));
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
		}
		
		List<MonthlyAttendanceReq> ApprovalPending = monthlyAttendanceReqService.selectApprovalPending();
		model.addAttribute("ApprovalPending",ApprovalPending);
		
		return "attendance/registration";
	}
	
	//マネージャー承認ボタン
	@RequestMapping(value = "/management", params = "approval", method = RequestMethod.POST)
	public String approval(AttendanceFormList attendanceFormList, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
//		Users users = (Users) session.getAttribute("Users");
//		model.addAttribute("Users", users);
		if(attendanceFormList.getAttendanceList() == null) {
			redirectAttributes.addFlashAttribute("madahayai", messageOutput.message("choiceUsers"));
		} else {
			String inputDate = attendanceFormList.getAttendanceList().get(0).getAttendanceDateS();
			String conversion = inputDate.replace("/","-");
			monthlyAttendanceReqService.approvalStatus(attendanceFormList.getAttendanceList().get(0).getUserId(), conversion);
		}
		return "redirect:/attendance/index";
	}

	//マネージャー却下ボタン押下
	@RequestMapping(value = "/management", params = "rejected", method = RequestMethod.POST)
	public String Rejected(AttendanceFormList attendanceFormList, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
//		Users users = (Users) session.getAttribute("Users");
//		model.addAttribute("Users", users);
		if(attendanceFormList.getAttendanceList() == null) {
			redirectAttributes.addFlashAttribute("madahayai", messageOutput.message("choiceUsers"));
		} else {
			String inputDate = attendanceFormList.getAttendanceList().get(0).getAttendanceDateS();
			String conversion = inputDate.replace("/","-");
			monthlyAttendanceReqService.rejectedStatus(attendanceFormList.getAttendanceList().get(0).getUserId(), conversion);
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


