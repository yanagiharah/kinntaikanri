package com.example.demo.exception;

import java.io.IOException;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import jakarta.servlet.ServletException;

public class SessionTimeOut implements SessionInformationExpiredStrategy {

	@Override
	public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
		// TODO 自動生成されたメソッド・スタブ
		  DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	        redirectStrategy.sendRedirect(event.getRequest(), event.getResponse(), "/login");
	}

}
