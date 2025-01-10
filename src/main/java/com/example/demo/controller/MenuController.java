package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.CommonActivityService;
import com.example.demo.service.MenuService;
import com.example.demo.service.ModelService;

@Controller
@RequestMapping("/menu")
public class MenuController {

	private final CommonActivityService commonActivityService;
	
	private final ModelService modelService;
	
	private final MenuService menuService;
	
	MenuController(CommonActivityService commonActivityService,ModelService modelService,MenuService menuService) {
		this.commonActivityService = commonActivityService;
		this.menuService = menuService;
		this.modelService = modelService;
		}

//	@RequestMapping("")
//	public String authorityDetermining(HttpSession session, Model model) {
//		commonActivityService.backMenu(model, session);
//		return "menu/processMenu";
//	}
	@RequestMapping("")
	public String authorityDetermining(HttpSession session, Model model) {
		return "redirect:/menu/loaded";
	}
	
	@RequestMapping("loaded")
	public String changeUrl(HttpSession session, Model model) {
		menuService.backMenu(model, session);
		return "menu/processMenu";
	}
	
	
//	@GetMapping("/")
//	public String newPage(HttpServletRequest request,HttpSession session, Model model) {
//	    Users users = (Users) request.getSession().getAttribute("User");
//	    commonActivityService.backMenu(model, session);
//	    model.addAttribute("User", users);
//	    return "menu/processMenu"; 
//	}

	//勤怠登録画面に遷移
	@RequestMapping("/index")
	public String attenddance(HttpSession session, Model model) {
		commonActivityService.usersModelSession(model,session);
		return "redirect:/attendance/index";
	}
	
	//勤怠修正画面に遷移
	@RequestMapping("/correction")
	public String monthlyAttenddance(HttpSession session, Model model) {
		commonActivityService.usersModelSession(model,session);
		return "redirect:/attendanceCorrect/correction";
	}

	//日報登録画面に遷移
	@RequestMapping("/daily/detail")
	public String dailyReport(HttpSession session, Model model) {
		commonActivityService.usersModelSession(model,session);
		return "redirect:/daily/detail";
	}
	
	@RequestMapping(value="/daily/detail",params="date", method = RequestMethod.GET)
	public String dailyReport(HttpSession session, Model model, @RequestParam("dailyReportDate") String date) {
		commonActivityService.usersModelSession(model,session);
		modelService.addDate(model,date);
		return "redirect:/daily/detail?date=" + date;
	}

	//ユーザー情報登録画面に遷移
	@RequestMapping("/usermanagement")
	public String userManagement(HttpSession session, Model model) {
		commonActivityService.usersModelSession(model,session);
		return "redirect:/user/";
	}

	//部署登録画面に遷移 
	@RequestMapping("/department")
	public String department(HttpSession session, Model model) {
		commonActivityService.usersModelSession(model,session);
		return "redirect:/department/";
	}
}

////ログイン成功後アカウント情報をmodelに詰めてメニュー画面へ遷移@AuthenticationPrincipal Users users,
//@RequestMapping("in")
//public String authorityDetermining(HttpSession session, Model model,Authentication authenticatio) {
//	CustomUsersDetails customUserDetails = (CustomUsersDetails) authenticatio.getPrincipal();
//    Users users = customUserDetails.getUsers();
//    Date today = new Date();
//    if (users == null || users.getStartDate().compareTo(today) == 1) {
//    	return "Login/index";
//	}
//    model.addAttribute("Users", users);
//	return "menu/processMenu";
//}
