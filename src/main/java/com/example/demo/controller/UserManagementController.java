package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.service.UserManagementService;

@Controller
@RequestMapping("/user-management")
public class UserManagementController {
	@Autowired
	private UserManagementService UserManagementService;
	
	@GetMapping("")
	public String userManagement() {
		return "user";	
	}


}
