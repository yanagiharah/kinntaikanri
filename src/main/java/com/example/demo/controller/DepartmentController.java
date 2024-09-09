package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.DepartmentForm;
import com.example.demo.model.Users;
import com.example.demo.service.CommonActivityService;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.ModelService;

@Controller
@RequestMapping("/department")
public class DepartmentController {

	
	private final DepartmentService departmentService;

	private final DepartmentForm departmentForm;
	
	private final ModelService modelService;
	
	private final CommonActivityService commonActivityService;

	DepartmentController(MessageSource messageSource,
			DepartmentForm departmentForm, DepartmentService departmentService, ModelService modelService, CommonActivityService commonActivityService) {
		this.departmentForm = departmentForm;
		this.departmentService = departmentService;
		this.modelService = modelService;
		this.commonActivityService = commonActivityService;
	}

	@RequestMapping("/")
	public String department(HttpSession session, Model model) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
		
		//有効部署一覧取得
		departmentForm.setActiveDepartment(departmentService.departmentSearchListUp());
		
		//無効（削除済み）部署一覧取得。
		departmentForm.setDeactiveDepartment(departmentService.deleteDepartmentSearchListUp());
		
		model.addAttribute("departmentForm", departmentForm);
		return "department/department";
	}
	
	//登録ボタン押下
	@RequestMapping(value = "/action", params = "registration", method = RequestMethod.POST)
	public String departmentCreate(@ModelAttribute DepartmentForm departmentForm, RedirectAttributes redirectAttributes) {
		Integer overlappingDepartmentCheck = departmentService.departmentCheckInsert(departmentForm);
		modelService.departmentInsertModel(overlappingDepartmentCheck, redirectAttributes);
		return "redirect:/department/";
	}
	
	//変更ボタン押下
	@RequestMapping(value = "/action", params = "change", method = RequestMethod.POST)
	public String departmenNameUpdate(DepartmentForm departmentForm, RedirectAttributes redirectAttributes) {
		Integer departmentNameEqualCheck = departmentService.departmentNameUpdate(departmentForm);
		modelService.departmentNameUpdateModel(departmentNameEqualCheck, redirectAttributes);
		return "redirect:/department/";
	}
	
	//削除ボタン押下
	@RequestMapping(value = "/action", params = "delete", method = RequestMethod.POST)
	public String departmentDeactiveUpdate(DepartmentForm departmentForm, RedirectAttributes redirectAttributes) {
		departmentService.departmentDeactiveUpdate(departmentForm);
		modelService.departmentDeactiveUpdateModel(redirectAttributes);
		return "redirect:/department/";
	}
	
	//復元ボタン押下。
	@RequestMapping(value = "/action", params = "restore", method = RequestMethod.POST)
	public String departmentActiveUpdate(DepartmentForm departmentForm, RedirectAttributes redirectAttributes) {
		departmentService.departmentActiveUpdate(departmentForm);
		modelService.departmentActiveUpdateModel(redirectAttributes);
		return "redirect:/department/";
	}
	
	//戻るボタン押下
	@RequestMapping(value = "/action", params = "back", method = RequestMethod.POST)
	public String back(Model model, HttpSession session) {
		commonActivityService.backMenu(model, session);
		return "menu/processMenu";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

//	@RequestMapping(value = "/management", params = "search", method = RequestMethod.POST)
//	public String userSearch(String userName, Model model, RedirectAttributes redirectAttributes) {
//
//		if (userName == null || userName == "") {
//			redirectAttributes.addFlashAttribute("check", messageOutput.message("userName"));
//			return "redirect:/user/";
//		} else if (userName.length() >= 20) {
//			redirectAttributes.addFlashAttribute("check", messageOutput.message("userId"));
//			return "redirect:/user/";
//		}
//
//		//★★エラーチェック正しく動いているがDBのユーザー名全て半角なので入れなくなる
//		else if (!userName.matches("^[^ -~｡-ﾟ]+$")) {
//			redirectAttributes.addFlashAttribute("check", messageOutput.message("zennkaku"));
//			return "redirect:/user/";
//		}
//
//		//名前を引数にserviceクラスでリストの取得
//		Users users = userManagementService.selectByAccount(userName, null);
//		//リストがあった場合
//		if (users != null) {
//			ManagementForm managementForm = new ManagementForm();
//			managementForm.setUserId(users.getUserId());
//			managementForm.setUserName(users.getUserName());
//			managementForm.setPassword(users.getPassword());
//			managementForm.setRole(users.getRole());
//			managementForm.setDepartmentId(users.getDepartmentId());
//			managementForm.setDepartment(departmentService.departmentSearchListUp());
//			String str = new SimpleDateFormat("yyyy-MM-dd").format(users.getStartDate());
//			managementForm.setStartDate(str);
//			model.addAttribute("managementForm", managementForm);
//			return "User/manegement";
//		}
//
//		Random rand = new Random();
//		ManagementForm managementForm2 = new ManagementForm();
//		Integer randomNumber = rand.nextInt(2147483647);
//		//RandomNumberがdbにあるか確認する処理を行う
//		//dbに存在した場合再度RandomNumberの生成forぶんで繰り返す、dbに存在しない数字がでるまで
//		managementForm2.setUserId(randomNumber);
//		managementForm2.setUserName(userName);
//		managementForm2.setDepartment(departmentService.departmentSearchListUp());
//		model.addAttribute("managementForm", managementForm2);
//
//		return "User/manegement";
//	}
//
//	@RequestMapping(value = "/management", params = "insert", method = RequestMethod.POST)
//	public String userCreate(@ModelAttribute ManagementForm managementForm, BindingResult result, Model model) {
//
//		//serviceのエラーメソッド
//		userManagementService.errorCheck(managementForm, result);
//
//		if (result.hasErrors()) {
//			managementForm.setDepartment(departmentService.departmentSearchListUp());
//			return "User/manegement";
//		}
//
//		Users users = userManagementService.selectByAccount(null, managementForm.getUserId());
//
//		if ("9999/99/99".equals(managementForm.getStartDate().trim())) {
//			if (users != null) {
//				managementForm.setStartDate("9999-12-31");
//				userManagementService.userUpdate(managementForm);
//				model.addAttribute("check", messageOutput.message("update", managementForm.getUserName()));
//			} else {
//				model.addAttribute("check", messageOutput.message("missTake", managementForm.getUserName()));
//			}
//		} else {
//			if (users != null) {
//				userManagementService.userUpdate(managementForm);
//				model.addAttribute("check", messageOutput.message("update", managementForm.getUserName()));
//			} else {
//				userManagementService.userCreate(managementForm);
//				model.addAttribute("check", messageOutput.message("insert", managementForm.getUserName()));
//			}
//		}
//		managementForm.setDepartment(departmentService.departmentSearchListUp());
//		return "User/manegement";
//	}
//
//	//戻るボタン
//	@RequestMapping(value = "/management", params = "back", method = RequestMethod.POST)
//	public String back(Model model, HttpSession session) {
//		Users users = (Users) session.getAttribute("Users");
//		model.addAttribute("Users", users);
//		return "menu/processMenu";
//	}

}


