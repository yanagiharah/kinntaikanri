package com.example.demo.batch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.demo.batch.sevice.MailService;

@ControllerAdvice
public class CatchException {

	@Autowired
	private MailService mailService;
	
	@ExceptionHandler(Exception.class)
	public void chachAllExceptions(Exception ex, WebRequest request) {
		
		String errorMessage = "エラーが発生したよ：" + ex.getMessage();
		String stackTrace = org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(ex);
		mailService.sendMail(
			"hasegawam@analix.co.jp", //受信者,
			"エラー通知",
			errorMessage + "n/n/" + stackTrace
		);
	}
}
