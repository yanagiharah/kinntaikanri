package com.example.demo.inter;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageOutput {

	private final MessageSource messageSource;

	MessageOutput(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	//メッセージ文の生成
	public String message(String messageName) {
		String out = messageSource.getMessage(messageName, new String[] {}, Locale.getDefault());
		return out;
	}
	
	//メッセージ文の生成
	public String message(String messageName ,String userName) {
		String message= messageSource.getMessage(messageName, new String[] { userName },Locale.getDefault());
		return message;
	}
	
	// メール専用メッセージ文の生成
    public String mailMessage(String messageName) {
        ResourceBundleMessageSource mailMessageSource = new ResourceBundleMessageSource();
        mailMessageSource.setBasename("mail-messages");
        mailMessageSource.setDefaultEncoding("UTF-8");
        return mailMessageSource.getMessage(messageName, null, Locale.getDefault());
    }
}
