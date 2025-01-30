package com.example.demo.helper;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.example.demo.model.Users;

@Component
public class SessionHelper {
	
	public Users usersGetBySession(Model model, HttpSession session) {
		return  (Users) session.getAttribute("Users");
		//commonActivityService.usersModelSession(model,session);
	}

}
