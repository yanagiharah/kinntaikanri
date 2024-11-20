package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

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
import com.example.demo.service.CommonActivityService;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.UserManagementService;

@Controller
@RequestMapping("/user")
public class UserManagementController {

	private final UserManagementService userManagementService;
	
	private final DepartmentService departmentService;

	private final ManagementForm managementForm;
	
	private final CommonActivityService commonActivityService;


	UserManagementController(UserManagementService userManagementService,
			ManagementForm managementForm, MessageOutput messageOutput, DepartmentService departmentService,
			CommonActivityService commonActivityService) {
		this.userManagementService = userManagementService;
		this.managementForm = managementForm;
		this.departmentService = departmentService;
		this.commonActivityService = commonActivityService;
	}
	
	/**
	 * ユーザー管理画面の初期表示を行います。
	 * 
	 * <p>セッションから {@link Users} 情報を取得し、それをモデルに追加します。また、部門情報を取得して
	 * フォームに設定し、ユーザー管理画面を表示します。</p>
	 * 
	 * @param session 現在のセッション
	 * @param model 画面に渡すデータを格納するためのモデル
	 * @return ユーザー管理画面のビュー名
	 */
	@RequestMapping("/")
	public String user(HttpSession session, Model model) {
		commonActivityService.usersModelSession(model,session);
		managementForm.setDepartment(departmentService.departmentSearchListUp());
		model.addAttribute("managementForm", managementForm);
		return "User/manegement";
	}
	/**
	 * 検索ボタン押下時に呼び出され、ユーザー情報の検索を行います。
	 * 
	 * <p>フォームで入力されたユーザー名を基に、データベースからユーザー情報を検索します。エラーがある場合は、
	 * 再度ユーザー管理画面を表示します。ユーザーが存在する場合はその情報を、存在しない場合は新しいユーザー情報を
	 * モデルに追加して、ユーザー管理画面を表示します。</p>
	 * 
	 * @param managementForm ユーザー管理画面のフォームデータ
	 * @param model 画面に渡すデータを格納するためのモデル
	 * @param redirectAttributes リダイレクト時に渡すメッセージなどを格納するためのオブジェクト
	 * @param result バリデーション結果
	 * @return ユーザー管理画面のビュー名
	 */
	@RequestMapping(value = "/management", params = "search", method = RequestMethod.POST)
	public String userSearch(@ModelAttribute ManagementForm managementForm, Model model, RedirectAttributes redirectAttributes,BindingResult result) {
		userManagementService.errorCheck(managementForm.getUserName(),result);
		if (result.hasErrors()) {
            return "User/manegement"; 
        }
		model.addAttribute("managementForm", userManagementService.useAccountChoice(managementForm));
		return "User/manegement";
	}
	/**
	 * 更新ボタン押下時に呼び出され、ユーザー情報の更新を行います。
	 * 
	 * <p>フォームのデータをバリデーションし、エラーがある場合は再度ユーザー管理画面を表示します。
	 * エラーがなければ、データベースに新しいユーザーを作成または既存のユーザー情報を更新し、
	 * 処理結果を画面に反映します。</p>
	 * 
	 * @param managementForm ユーザー管理画面のフォームデータ
	 * @param result バリデーション結果
	 * @param model 画面に渡すデータを格納するためのモデル
	 * @return ユーザー管理画面のビュー名
	 */
	@RequestMapping(value = "/management", params = "insert", method = RequestMethod.POST)
	public String userCreate(@ModelAttribute ManagementForm managementForm, BindingResult result, Model model) {
		userManagementService.errorCheck(managementForm, result);
		if (result.hasErrors()) {
			managementForm.setDepartment(departmentService.departmentSearchListUp());
			return "User/manegement";
		}
		userManagementService.dbActionchoice(managementForm,model);
		managementForm.setDepartment(departmentService.departmentSearchListUp());
		return "User/manegement";
	}
	/**
	 * 戻るボタン押下時メニュー画面に遷移
	 * @param managementForm ユーザー管理画面のフォームデータ
	 * @param result バリデーション結果
	 * @param model 画面に渡すデータを格納するためのモデル
	 * @return メニュー画面
	 */
	@RequestMapping(value = "/management", params = "back", method = RequestMethod.POST)
	public String back(Model model, HttpSession session,RedirectAttributes redirectAttributes) {
		return "redirect:/menu/loaded";
	}
	
	@RequestMapping(value = "/management",method = RequestMethod.GET)
	public String reload(Model model,HttpSession session,RedirectAttributes redirectAttributes) {
		return "redirect:/user/";
	}

}


