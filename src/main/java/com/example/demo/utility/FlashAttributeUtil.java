//package com.example.demo.utility;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import com.example.demo.inter.MessageOutput;
//
//@Component
//public class FlashAttributeUtil {
//	
//	 private final MessageOutput messageOutput;
//
//	    @Autowired
//	    public FlashAttributeUtil(MessageOutput messageOutput) {
//	        this.messageOutput = messageOutput;
//	    }
//	
//	    public void addFlashMessage(RedirectAttributes redirectAttributes, String messageKey, String message) {
//	        String mainMessage = messageOutput.message(message);
//	        redirectAttributes.addFlashAttribute(messageKey, mainMessage);
//	    }
//}