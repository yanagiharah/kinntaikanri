package com.example.demo.controller;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler  implements AuthenticationFailureHandler {

	 @Override
	    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	                                        AuthenticationException exception) throws IOException, ServletException {
	        String userId = request.getParameter("userId");
	        String password = request.getParameter("password");

	        // 入力の文字数チェック
	        if (userId == null || userId.length() < 3) {
	            request.getSession().setAttribute("error", "ユーザーIDは3文字以上で入力してください。");
	            response.sendRedirect("/login?error");
	            System.out.print("通貨");
	            return;
	        }
	        if (password == null || password.length() < 6) {
	            request.getSession().setAttribute("error", "パスワードは6文字以上で入力してください。");
	            response.sendRedirect("/login?error");
	            System.out.print("通貨22");
	            return;
	        }

	        // デフォルトエラーメッセージ
	        request.getSession().setAttribute("error", "ログインに失敗しました。ユーザーIDとパスワードを確認してください。");
	        response.sendRedirect("/login?error");
	    }
}