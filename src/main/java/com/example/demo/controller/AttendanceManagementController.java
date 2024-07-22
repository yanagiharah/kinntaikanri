package com.example.demo.controller;
 
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Attendance;
import com.example.demo.model.Users;
import com.example.demo.service.AttendanceManagementService;
 
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
	
	
	@RequestMapping("/management")
	public String attendanceSearch(Integer userId, Integer years, Integer month, Model model, RedirectAttributes redirectAttributes, HttpSession session){
//		System.out.print("『画面から受け取りチェック表示』："+ userId + years + month);
//		System.out.print("『ユーザーID』：" + userId);
//	if(name == null) {
//		redirectAttributes.addFlashAttribute("check","入力は必須です。");
//		return "redirect:/User/manegement";
//	}
	//名前を引数にserviceクラスでリストの取得
//		System.out.print("ここに表示"+userName);
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		
		

			if (years == null || month == null)  {
				model.addAttribute("check", "年月を入力してください");
				return "attendance/registration";
			}
			
		
		
		
		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month);
	if(attendance != null) {
		model.addAttribute("Attendance", attendance);
		
		return "attendance/registration";
	}
	
	return "attendance/registration";
	}
}

