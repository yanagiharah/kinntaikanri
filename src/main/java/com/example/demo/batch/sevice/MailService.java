package com.example.demo.batch.sevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

	@Autowired
	private JavaMailSender mailSender;
	
	public void sendMail(String toMail, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toMail);
		message.setSubject(subject);
		message.setText(body);
		message.setFrom("palholi.boo@gmail.com");	//送信者
		
		mailSender.send(message);
		
		System.out.println("送信OK");
	}
}
