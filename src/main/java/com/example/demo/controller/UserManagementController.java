package com.example.demo.controller;

import java.text.SimpleDateFormat;
import java.util.Random;

import jakarta.servlet.http.HttpSession;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.inter.MessageOutput;
import com.example.demo.model.ManagementForm;
import com.example.demo.model.Users;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.UserManagementService;

@Controller
@RequestMapping("/user")
public class UserManagementController {

	private final UserManagementService userManagementService;
	
	private final DepartmentService departmentService;

	private final MessageOutput messageOutput;

	private final ManagementForm managementForm;

	UserManagementController(UserManagementService userManagementService, MessageSource messageSource,
			ManagementForm managementForm, MessageOutput messageOutput, DepartmentService departmentService) {
		this.userManagementService = userManagementService;
		this.managementForm = managementForm;
		this.messageOutput = messageOutput;
		this.departmentService = departmentService;
	}

	@RequestMapping("/")
	public String user(HttpSession session, Model model) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		managementForm.setDepartment(departmentService.departmentSearchListUp());
		model.addAttribute("managementForm", managementForm);
		return "User/manegement";
	}

	@RequestMapping(value = "/management", params = "search", method = RequestMethod.POST)
	public String userSearch(String userName, Model model, RedirectAttributes redirectAttributes) {

		if (userName == null || userName == "") {
			redirectAttributes.addFlashAttribute("check", messageOutput.message("userName"));
			return "redirect:/user/";
		} else if (userName.length() >= 20) {
			redirectAttributes.addFlashAttribute("check", messageOutput.message("userId"));
			return "redirect:/user/";
		}

		//★★エラーチェック正しく動いているがDBのユーザー名全て半角なので入れなくなる
		else if (!userName.matches("^[^ -~｡-ﾟ]+$")) {
			redirectAttributes.addFlashAttribute("check", messageOutput.message("zennkaku"));
			return "redirect:/user/";
		}

		//名前を引数にserviceクラスでリストの取得
		Users users = userManagementService.selectByAccount(userName, null);
		//リストがあった場合
		if (users != null) {
			ManagementForm managementForm = new ManagementForm();
			managementForm.setUserId(users.getUserId());
			managementForm.setUserName(users.getUserName());
			managementForm.setPassword(users.getPassword());
			managementForm.setRole(users.getRole());
			managementForm.setDepartmentId(users.getDepartmentId());
			managementForm.setDepartment(departmentService.departmentSearchListUp());
			String str = new SimpleDateFormat("yyyy-MM-dd").format(users.getStartDate());
			managementForm.setStartDate(str);
			model.addAttribute("managementForm", managementForm);
			return "User/manegement";
		}

		Random rand = new Random();
		ManagementForm managementForm2 = new ManagementForm();
		Integer randomNumber = rand.nextInt(2147483647);
		//RandomNumberがdbにあるか確認する処理を行う
		//dbに存在した場合再度RandomNumberの生成forぶんで繰り返す、dbに存在しない数字がでるまで
		managementForm2.setUserId(randomNumber);
		managementForm2.setUserName(userName);
		managementForm2.setDepartment(departmentService.departmentSearchListUp());
		model.addAttribute("managementForm", managementForm2);

		return "User/manegement";
	}

	@RequestMapping(value = "/management", params = "insert", method = RequestMethod.POST)
	public String userCreate(@ModelAttribute ManagementForm managementForm, BindingResult result, Model model) {

		//serviceのエラーメソッド
		userManagementService.errorCheck(managementForm, result);

		if (result.hasErrors()) {
			managementForm.setDepartment(departmentService.departmentSearchListUp());
			return "User/manegement";
		}

		Users users = userManagementService.selectByAccount(null, managementForm.getUserId());

		if ("9999/99/99".equals(managementForm.getStartDate().trim())) {
			if (users != null) {
				managementForm.setStartDate("9999-12-31");
				userManagementService.userUpdate(managementForm);
				model.addAttribute("check", messageOutput.message("update", managementForm.getUserName()));
			} else {
				model.addAttribute("check", messageOutput.message("missTake", managementForm.getUserName()));
			}
		} else {
			if (users != null) {
				userManagementService.userUpdate(managementForm);
				model.addAttribute("check", messageOutput.message("update", managementForm.getUserName()));
			} else {
				userManagementService.userCreate(managementForm);
				model.addAttribute("check", messageOutput.message("insert", managementForm.getUserName()));
			}
		}
		managementForm.setDepartment(departmentService.departmentSearchListUp());
		return "User/manegement";
	}

	//戻るボタン
	@RequestMapping(value = "/management", params = "back", method = RequestMethod.POST)
	public String back(Model model, HttpSession session) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		return "menu/processMenu";
	}

}


