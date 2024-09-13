package com.example.demo.batch.controller;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.batch.sevice.MailService;


/**
 * このクラスは、決められた時間になったらメールを送信するトリガーを定義しています。
 */
@Component
public class MailScheduler {
	
	private final MailService mailService;
	
	MailScheduler(MailService mailService) {
		this.mailService = mailService;
	}
	
	/**
     * このメソッドは、先月の勤怠が未提出の人へメールを送信するトリガーです。
     * 
     * 実行日 毎月1日 9:00
     */
	@Scheduled(cron = "0 0 9 1 * *")
	public void sendAttendanceRemindCaution() {

        mailService.sendAttendanceRemindCaution(); 
	}
	
	/**
     * このメソッドは、先月の勤怠が未提出の人へメールを送信するトリガーです。
     * 
     * 実行日 毎月3日 9:00
     */
	@Scheduled(cron = "0 0 9 3 * *")
	public void sendAttendanceRemindWarning() {

        mailService.sendAttendanceRemindWarning(); 
	}
	
	/**
     * このメソッドは、先月の勤怠が未提出の人へメールを送信するトリガーです。
     * 
     * 実行日 毎月5日 9:00
     */
	@Scheduled(cron = "0 0 9 5 * *")
	public void sendAttendanceRemindDanger() {

        mailService.sendAttendanceRemindDanger(); 
	}
	
	/**
     * このメソッドは、先月の勤怠未提出者一覧をメール送信するトリガーです。
     * 
     * 実行日 毎月1日 9:00
     */
	@Scheduled(cron = "0 * * * * *")//(cron = "0 0 9 1 * *")
	public void sendAttendanceFogotNameCaution() {

        mailService.sendAttendanceFogotNameCaution(); 
	}
	
	
	/**
     * このメソッドは、先月の勤怠未提出者一覧をメール送信するトリガーです。
     * 
     * 実行日 毎月3日 9:00 
     */
	@Scheduled(cron = "0 0 9 3 * *")
	public void sendAttendanceFogotNameWarning() {

        mailService.sendAttendanceFogotNameWarning(); 
	}
	
	/**
     * このメソッドは、先月の勤怠未提出者一覧をメール送信するトリガーです。
     * 
     * 実行日 毎月5日 9:00
     */
	@Scheduled(cron = "0 0 9 5 * *")
	public void sendAttendanceFogotNameDanger() {

        mailService.sendAttendanceFogotNameDanger(); 
	}
	
	/**
     * このメソッドは、先月の勤怠について承認処理を促すメールを送信するトリガーです。
     * 
     * 実行日 毎月3日 9:00
     */
	@Scheduled(cron = "0 0 3 9 * *")
	public void sendMonthlyAttendanceApprovalReminder() {

        mailService.sendAttendanceApprovalReminder() ; 
	}
	
	/**
     * このメソッドは、先月の勤怠について未提出者の名前一覧をメール送信するトリガーです。
     * 
     * 実行日 毎月5日 9:00
     */
	@Scheduled(cron = "0 0 9 5 * *")
	public void sendUnsubmittedAttendanceReminder() {

        mailService.sendUnsubmittedAttendanceReminder() ; 
	}
}
