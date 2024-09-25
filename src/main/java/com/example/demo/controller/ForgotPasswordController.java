package com.example.demo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.EmailService;
import com.example.demo.service.ModelService;
import com.example.demo.service.UserManagementService;


@Controller
@RequestMapping("/forgotpassword")
public class ForgotPasswordController {
    
    private final ModelService modelService;
    private final UserManagementService userManagementService;
    private final EmailService emailService;
    
    public ForgotPasswordController(ModelService modelService, UserManagementService userManagementService, EmailService emailService){
        this.modelService = modelService;
        this.userManagementService = userManagementService;
        this.emailService = emailService;
    }
    
    @GetMapping
    public String forgotPassword() {
        return "forgot/forgotpassword";
    }
    
    @PostMapping(params = "send")
    public String sendResetPassword(Model model, @RequestParam("userId") Integer userId, @RequestParam("address") String address) {
        Integer userIdAddressCheck = userManagementService.selectUserIdAddressCheck(userId, address);
        emailService.resetPassword(userIdAddressCheck, userId, address);
        modelService.emailSent(model);
        return "forgot/forgotpassword";
    }
}
