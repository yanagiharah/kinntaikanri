package com.example.demo.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.demo.inter.MessageOutput;
import com.example.demo.model.Users;

import jakarta.servlet.http.HttpSession;

@Service
public class CommonActivityService {
	

	private final DailyReportService dailyReportService;

	private final AttendanceManagementService attendanceManagementService;
	
	private final MessageOutput messageOutput;
	
	CommonActivityService(DailyReportService dailyReportService,AttendanceManagementService attendanceManagementService,MessageOutput messageOutput){
		this.dailyReportService = dailyReportService;
		this.attendanceManagementService = attendanceManagementService;
		this.messageOutput = messageOutput;
	}
	
	//Usersセッションに詰める
	public Model usersSession(Model model, HttpSession session) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		return model;
	}
	
	//メニュー画面に戻る挙動(アカウント情報をmodelに詰めてメニュー画面へ遷移)
	public Model backMenu(Model model, HttpSession session) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		if ("Regular".equals(users.getRole()) || "UnitManager".equals(users.getRole())) {
			LocalDate today = LocalDate.now();
			LocalDate yesterday = today.minusDays(1);
			Integer checkDailyReport = dailyReportService.checkYesterdayDailyReport(users.getUserId(), yesterday);
			Integer checkAttendance = attendanceManagementService.checkYesterdayAttendance(users.getUserId(),
					yesterday);
			if (checkDailyReport == 0) {
				model.addAttribute("CheckDailyReport",messageOutput.message("checkDailyReport"));
			}
			if (checkAttendance == 0) {
				model.addAttribute("CheckAttendance", messageOutput.message("checkAttendance"));
			}
		}
		return model;
	}
}
