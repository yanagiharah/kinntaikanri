//package com.example.demo.batch.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import com.example.demo.batch.sevice.MailService;
//
//@Component
//public class MailScheduler {
//
//	@Autowired
//	private MailService mailService;
//	
//	@Scheduled(cron = "0 * * * * ?")
//	public void sendScheduledEmail() {
//		String tomail = "hasegawam@analix.co.jp"; //受信者
//        String subject = "確認用";
//        String body = "テストメール！？";
//        
//        mailService.sendMail(tomail, subject, body); 
//        System.out.println("メール完了！");
//	}
//}