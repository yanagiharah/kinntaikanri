package com.example.demo.controller;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
        System.out.println("Received token: " + token);
        Users user = userManagementService.selectUserToken(token);
        if(user == null) {
        	user = new Users();
        	user.setTokenExpirationDateCheck(false);
        }else {
        	userManagementService.tokenExpirationDateCheck(user);
        }
        
        if (!user.getTokenExpirationDateCheck()) {
            modelService.tokenTimeOut(model);
        } else {
            model.addAttribute("user", user);
        }

        return "changeforgot/changeforgotpassword";
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, "tokenExpiryDate", new CustomDateEditor(dateFormat, true));
    }
    
    @RequestMapping(params = "check", method = RequestMethod.POST)
    public String sendResetPassword(Model model,  @RequestParam Integer userId, Users user, String newPassword, String checkNewPassword) {

        if (!newPassword.equals(checkNewPassword)) {
            modelService.passwordNearMiss(model);
            model.addAttribute("user", user);
            return "changeforgot/changeforgotpassword";
        } else {
            userManagementService.tokenExpirationDateCheck(user);
            //トークンの有効期限しか確認していない。データベースがらuserを再度取得し、トークンの文字列も再評価すべき？
            if (user == null || !user.getTokenExpirationDateCheck()) {
                modelService.tokenTimeOut(model);
            } else {
                userManagementService.passwordChange(newPassword, user);
                modelService.passwordChangeSuccess(model);
            }
            return "changeforgot/changeforgotpassword";
        }
    }
}
