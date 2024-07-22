package com.example.demo.controller;
 
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Attendance;
import com.example.demo.model.AttendanceFormList;
import com.example.demo.model.Users;
import com.example.demo.service.AttendanceManagementService;

import jakarta.servlet.http.HttpSession;
 
@Controller
@RequestMapping("/attendance")
public class AttendanceManagementController {
	@Autowired
	private AttendanceManagementService attendanceManagementService;
	
	
	
	@RequestMapping("/index")
		public String start(HttpSession session, Model model){
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		return "attendance/registration";
	}
	
	
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
		if (attendance != null) {
			//formに詰めなおす
			AttendanceFormList attendanceFormList = new AttendanceFormList();
			ArrayList<Attendance> attendanceList = new  ArrayList<Attendance>();
			attendanceFormList.setAttendanceList(attendanceList);
			attendanceList.addAll(attendance);
			model.addAttribute("attendanceFormList", attendanceFormList);

			return "attendance/registration";
		}

		return "attendance/registration";
	}
	
	
	@RequestMapping(value = "/management", params ="insert" ,method = RequestMethod.POST )
	public String insert(AttendanceFormList attendanceFormList,Model model,HttpSession session) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		System.out.print("登録後"+attendanceFormList.getAttendanceList());
		//System.out.print("登録後"+status);
		return "attendance/registration";
		
	}
}

