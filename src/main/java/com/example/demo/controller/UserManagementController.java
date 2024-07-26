package com.example.demo.controller;

import java.text.SimpleDateFormat;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.ManagementForm;
import com.example.demo.model.Users;
import com.example.demo.service.UserManagementService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserManagementController {
	@Autowired
	private UserManagementService userManagementService;
//	@Autowired
//	private MessageSource messageSource;
	
	@RequestMapping("/")
		public String user(@ModelAttribute ManagementForm managementForm,HttpSession session, Model model) {
		Users users = (Users) session.getAttribute("Users");
		 model.addAttribute("Users", users);
		 model.addAttribute("managementForm", new ManagementForm());
			return "User/manegement";
		}
	
	
	@RequestMapping(value = "/management", params ="search" ,method = RequestMethod.POST)
	public String userSearch(String userName,Model model, RedirectAttributes redirectAttributes ){
		
	
	if(userName == null || userName == "") {
		redirectAttributes.addFlashAttribute("check", "ユーザー名は必須です");
		return "redirect:/user/";
	}else if(userName.length() >=20) {
		redirectAttributes.addFlashAttribute("check", "ユーザーID無効です。");
		return "redirect:/user/";
	}
	
	//★エラーチェック正しく動いているがDBのユーザー名全て半角なので入れなくなる
	
//	else if( !userName.matches("^[^ -~｡-ﾟ]+$")) {
//		redirectAttributes.addFlashAttribute("check", "全角文字以外入力できません");
//		return "redirect:/user/";
//	}
	
	//名前を引数にserviceクラスでリストの取得
	Users users = userManagementService.userSearchListUp(userName);	
	//リストがあった場合
	if(users != null) {
		ManagementForm managementForm = new ManagementForm();
		managementForm.setUserId(users.getUserId());
		managementForm.setUserName(users.getUserName());
		managementForm.setPassword(users.getPassword());
		managementForm.setRole(users.getRole());
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
    
    model.addAttribute("managementForm", managementForm2);
    return "User/manegement";
	}
	
	
	@RequestMapping(value = "/management", params = "insert", method = RequestMethod.POST)
	public String userCreate(@ModelAttribute ManagementForm managementForm, BindingResult result, Model model) {
		
		//serviceのエラーメソッド
		 userManagementService.errorCheck(managementForm,result);
		
		if(result.hasErrors()) {
			//フィールドにエラー入るからmodelに入れない
		    return "User/manegement";
		  }
		System.out.print("つうか！！！！");
//		if (managementForm.getUserName() == null ||managementForm.getUserName() == ""
//				|| managementForm.getPassword() == "" || managementForm.getRole() == ""
//				|| managementForm.getStartDate() == "") {
//			model.addAttribute("check", "入力してください");
//			return "redirect:/user/";
//		}
		
		if ("9999-99-99".equals(managementForm.getStartDate().trim())) {
			//userManagementService.userDelete(managementForm);
			System.out.print("つうか！！！！");
		} else {
			Users users = userManagementService.userSearchListUp(managementForm.getUserName());
			if (users != null) {
				userManagementService.userUpdate(managementForm);
//				String agree = messageSource.getMessage("agree", null, Locale.JAPAN);
//				model.addAttribute("check","agree");
			} else {
				userManagementService.userCreate(managementForm);
				model.addAttribute("check","登録を完了しました");
			}
		}
		return "User/manegement";
	}
}
//@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)Date startDate

