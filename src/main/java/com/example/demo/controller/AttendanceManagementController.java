package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Attendance;
import com.example.demo.service.AttendanceManagementService;

@Controller
@RequestMapping("/attendance")
public class AttendanceManagementController {
	@Autowired
	private AttendanceManagementService attendanceManagementService;
	
	
	@RequestMapping("/management")
	public String attendanceSearch(Integer userId, Integer years, Integer month, Model model, RedirectAttributes redirectAttributes ){
		System.out.print("『画面から受け取りチェック表示』："+ userId + years + month);
	
//	if(name == null) {
//		redirectAttributes.addFlashAttribute("check","入力は必須です。");
//		return "redirect:/User/manegement";
//	}
	//名前を引数にserviceクラスでリストの取得
//		System.out.print("ここに表示"+userName);
		Attendance attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month);
//	System.out.print("ここに表示"+users);
	//リストがあった場合
		System.out.print("『最終チェック表示』："+attendance);
	if(attendance != null) {
		model.addAttribute("List", attendance);
		
		return "attendance/registration";
	}
	
//	Random rand = new Random();
//	Users user =new Users();
//    Integer randomNumber = rand.nextInt(2147483647);
//    user.setUserId(randomNumber);
//    
//    model.addAttribute("List", user);
	return "attendance/registration";
	}
}

