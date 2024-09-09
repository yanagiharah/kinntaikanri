package com.example.demo.service;

import java.time.LocalDate;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.demo.model.Users;

@Service
public class CommonActivityService {
	
	@Autowired
	private DailyReportService dailyReportService;
	@Autowired
	private AttendanceManagementService attendanceManagementService;
	
	
	//Usersセッションに詰める
	public Users usersSession(Model model, HttpSession session) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		return users;
	}
	
	//メニュー画面に戻る挙動(アカウント情報をmodelに詰めてメニュー画面へ遷移)
	public Model backMenu(Model model, HttpSession session) {

		Users users = usersSession(model, session);

		if ("Regular".equals(users.getRole()) || "UnitManager".equals(users.getRole())) {
			
			LocalDate today = LocalDate.now();
			LocalDate yesterday = today.minusDays(1);
			Integer checkDailyReport = dailyReportService.checkYesterdayDailyReport(users.getUserId(), yesterday);
			Integer checkAttendance = attendanceManagementService.checkYesterdayAttendance(users.getUserId(),
					yesterday);

			if (checkDailyReport == 0) {
				model.addAttribute("CheckDailyReport", "日報未提出");
			}
			if (checkAttendance == 0) {
				model.addAttribute("CheckAttendance", "勤怠未提出");
			}
		}
		return model;
	}
	
	
		
}
