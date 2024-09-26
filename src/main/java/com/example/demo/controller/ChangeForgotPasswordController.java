package com.example.demo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Users;
import com.example.demo.service.ModelService;
import com.example.demo.service.UserManagementService;


@Controller
@RequestMapping("/changeforgotpassword")
public class ChangeForgotPasswordController {
	
	private final ModelService modelService;
	
	private final UserManagementService userManagementService;
	
	ChangeForgotPasswordController(ModelService modelService, UserManagementService userManagementService){
		
		this.modelService = modelService;
		
		this.userManagementService = userManagementService;
	}
	
	@GetMapping
	public String forgotPassword(Model model, @RequestParam("token") String token) {
		System.out.println("Received token: " + token);  // ログを追加
	    Users user = userManagementService.selectUserToken(token);
	    userManagementService.tokenExpirationDateCheck(user);

	    if (user == null || user.getTokenExpirationDateCheck() == false) {
	        modelService.tokenTimeOut(model);
	    } else {
	        model.addAttribute("user", user);
	    }

	    return "changeforgot/changeforgotpassword";
	}
	
	//新しいパスワードを入力して変更ボタンを押下時
	@RequestMapping(params = "check")
	public String sendResetPassword(Model model, Users user, String newPassword, String checkNewPassword) {

		if (!newPassword.equals(checkNewPassword)) {
			//入力されたパスワードが合致しない旨を記述
			
			model.addAttribute("user", user);
			return "changeforgot/changeforgotpassword";
		} else {

			//ボタン押下時にもトークンの期限切れ確認をしてからパスワードの変更を受け付ける
			userManagementService.tokenExpirationDateCheck(user);
			if (user == null || user.getTokenExpirationDateCheck() == false) {
				modelService.tokenTimeOut(model);
			} else {
				userManagementService.passwordChange(newPassword, user);
				modelService.passwordChangeSuccess(model);
			}
			return "changeforgot/changeforgotpassword";
		}
	}
	
}
