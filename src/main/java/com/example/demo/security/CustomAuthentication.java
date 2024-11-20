package com.example.demo.security;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.model.Users;

@Component
public class CustomAuthentication  implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// ログイン成功時にユーザー情報をセッションに保存
		CustomUsersDetails customUserDetails = (CustomUsersDetails) authentication.getPrincipal();
		Users users = customUserDetails.getUsers();
		request.getSession().setAttribute("Users", users);
		// /menu へのリダイレクト
		RequestDispatcher dispatcher = request.getRequestDispatcher("/menu");
		dispatcher.forward(request, response);
//		response.sendRedirect("/menu/");
	}
}


//Authentication authentication2 = SecurityContextHolder.getContext().getAuthentication();
//
//   // ログインしているユーザーを取得
//   Users users = (Users) authentication2.getPrincipal();
//System.out.print(SecurityContextHolder.
//		 getContext
//		 ().getAuthentication());

//Date today = new Date();
//if (users == null || users.getStartDate().compareTo(today) == 1) {
//	RequestDispatcher dispatcher = request.getRequestDispatcher("/dateOut");
//    dispatcher.forward(request, response);
//}