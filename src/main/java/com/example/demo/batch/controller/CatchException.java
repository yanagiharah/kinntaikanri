package com.example.demo.batch.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.demo.batch.sevice.MailService;
import com.example.demo.inter.MessageOutput;

/**
 * このクラスはエラー発生時にメールを送るためトリガーとなります
 * 
 */
@ControllerAdvice
public class CatchException {

	
	private final MailService mailService;
		
	CatchException(MailService mailService, MessageOutput messageOutput){
		this.mailService = mailService;	
	}
	
	/**
	 * このメソッドは、ホワイトラベル全般をキャッチしてメール送信メソッドを呼び出します。
	 * エラー内容をこのメソッド内で取得し、メール送信メソッドへ引数として渡しています。
	 * 
	 */
	@ExceptionHandler(Exception.class)
	public void chachAllExceptions(Exception ex, WebRequest request) {
		
		String stackTrace =
				org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(ex);
		
		mailService.exceptionNotificationMail(stackTrace);
	}
}
