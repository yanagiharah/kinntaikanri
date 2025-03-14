package com.example.demo.batch;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.service.EmailService;

@Component
public class BatchSchedule {
	private final EmailService emailService;

    public BatchSchedule(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @Scheduled(cron = "0 0 6 3 * ?")
    public void sendEmailOn3rd() {
        emailService.monthlyAttendanceNotApplied();
    }

    @Scheduled(cron = "0 0 6 5 * ?")
    public void sendEmailOn5th() {
        emailService.monthlyAttendanceNotApplied();
    }
    
    @Scheduled(cron = "0 0 17 1-5 * ?", zone = "Asia/Tokyo")
    public void sendEmailMonthlyAttendanceReq() {
    	emailService.monthlyAttendanceReq();
    }

}
