package com.example.demo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.model.Users;
import com.example.demo.service.EmailService;
import com.example.demo.service.ModelService;
import com.example.demo.service.UserManagementService;


@Controller
@RequestMapping("/changeforgotpassword")
public class ChangeForgotPasswordController {
	
	private final ModelService modelService;
	
	private final UserManagementService userManagementService;
	
	private final EmailService emailService;
	
	ChangeForgotPasswordController(ModelService modelService, UserManagementService userManagementService, EmailService emailService){
		
		this.modelService = modelService;
		
		this.userManagementService = userManagementService;
		
		this.emailService = emailService;
	}
	
	@GetMapping("/")
	public String forgotPassword(Model model ,String token) {
		//ページに遷移した時点でトークンの期限切れ確認をする。有効期限切れの場合はその旨表示させる
		Users user = userManagementService.selectUserToken(token);
		userManagementService.tokenExpirationDateCheck(user);
		
		if(user == null || user.getTokenExpirationDateCheck() == false) {
			modelService.tokenTimeOut(model);
		}else {
			model.addAttribute("user", user);
		}
		
		return "/changeforgotpassword";
	}
	
	//新しいパスワードを入力して変更ボタンを押下時
	@RequestMapping(value = "/changeforgotpassword", params = "change", method = RequestMethod.POST)
	public String sendResetPassword(Model model, Users user, String password) {
		//ボタン押下時にもトークンの期限切れ確認をしてからパスワードの変更を受け付ける
		userManagementService.tokenExpirationDateCheck(user);
		if(user == null || user.getTokenExpirationDateCheck() == false) {
			modelService.tokenTimeOut(model);
		}else {
			userManagementService.passwordChange(password, user);
			modelService.passwordChangeSuccess(model);
		}
		return "changeforgot/changeforgotpassword";
	}
	
}
