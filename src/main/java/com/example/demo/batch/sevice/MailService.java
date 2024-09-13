package com.example.demo.batch.sevice;
import java.sql.Date;
import java.time.YearMonth;
import java.util.List;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.demo.inter.MessageOutput;
import com.example.demo.mapper.CatchForgotMapper;


/**
 * このクラスにはメール送信に関する処理をまとめています。
 * タイトルや本文などは[mail-prperties]にを参照しています。 
 */
@Service
public class MailService{
	
	private final CatchForgotMapper catchForgotMapper;
	private final MessageOutput messageOutput;
	private final JavaMailSender mailSender;
	
	MailService(
			CatchForgotMapper catchForgotMapper, 
			MessageOutput messageOutput,
			JavaMailSender mailSender
	){
		this.catchForgotMapper = catchForgotMapper;
		this.messageOutput = messageOutput;
		this.mailSender = mailSender;
	}
	
	/**
	 * このメソッドは先月の勤怠が未提出であることをメールでお知らせするメソッドです。
	 * 「setTo」で宛先の一覧（メールアドレスの配列）を指定しています。このメソッドは先月の勤怠が未提出であることをメールでお知らせするメソッドです
	 * 
	 * 宛先 レギュラー権限かつ勤怠未提出者
	 * 内容 未提出であることを知らせる
	 */
	public void sendAttendanceRemindCaution() {
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		String subject = messageOutput.mailMessage("remindAttendance.subject");
	    String text = messageOutput.mailMessage("remindAttendanceCaution.text");
	    String from = messageOutput.mailMessage("systemManager.from");
	    
	    message.setTo(forgotAttendanceArray());
	    message.setSubject(subject);
	    message.setText(text);
	    message.setFrom(from);
		
		mailSender.send(message);
		
		System.out.println("レギュラー宛　１日  送信OK");
	}
		
	/**
	 * このメソッドは先月の勤怠が未提出であることをメールでお知らせするメソッドです。
	 * 「setTo」で宛先の一覧（メールアドレスの配列）を指定しています。
	 * 
	 * 宛先 レギュラー権限かつ勤怠未提出者
	 * 内容 本日が〆切であることを知らせる
	 */
	public void sendAttendanceRemindWarning() {
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		String subject = messageOutput.mailMessage("remindAttendance.subject");
	    String text = messageOutput.mailMessage("remindAttendanceWarning.text");
	    String from = messageOutput.mailMessage("systemManager.from");
	    
	    message.setTo(forgotAttendanceArray());
	    message.setSubject(subject);
	    message.setText(text);
	    message.setFrom(from);
		
		mailSender.send(message);
		
		System.out.println("レギュラー宛　２日　送信OK");
	}
	
	/**
	 * このメソッドは先月の勤怠が未提出であることをメールでお知らせするメソッドです。
	 * 「setTo」で宛先の一覧（メールアドレスの配列）を指定しています。
	 * 宛先 レギュラー権限かつ勤怠未提出者
	 * 内容 〆切を過ぎていることを知らせる
	 */
	public void sendAttendanceRemindDanger() {
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		String subject = messageOutput.mailMessage("remindAttendance.subject");
	    String text = messageOutput.mailMessage("remindAttendanceDanger.text");
	    String from = messageOutput.mailMessage("systemManager.from");
	    
	    message.setTo(forgotAttendanceArray());
	    message.setSubject(subject);
	    message.setText(text);
	    message.setFrom(from);
		
		mailSender.send(message);
		
		System.out.println("レギュラー宛　３日　送信OK");
	}
	
	/**
	 * このメソッドは先月の勤怠が未提出であることをメールでお知らせするメソッドです。
	 * 「setTo」で宛先の一覧（メールアドレスの配列）を指定しています。
	 * 
	 * 宛先 ユニットマネージャー
	 * 内容 未提出者一覧
	 */
	public void sendAttendanceFogotNameCaution() {
		
		SimpleMailMessage message = new SimpleMailMessage();
		String[] unsubmittedAttendanceNameArray = getForgotAttendanceNameArray();
		
		String subject = messageOutput.mailMessage("fogotAttendanceNameList.subject");
	    String text = messageOutput.mailMessage("fogotAttendanceNameListCaution.text");
	    String from = messageOutput.mailMessage("systemManager.from");
	    String unsubmittedAttendanceName = unsubmittedAttendanceNameArray.toString();
	    
	    message.setTo(forgotAttendanceArray());
	    message.setSubject(subject);
	    message.setText(text + unsubmittedAttendanceName);
	    message.setFrom(from);
		
		mailSender.send(message);
		
		System.out.println("UM宛　１日　勤怠送信OK");
	}
	
	/**
	 * このメソッドは先月の勤怠が未提出であることをメールでお知らせするメソッドです。
	 * 「setTo」で宛先の一覧（メールアドレスの配列）を指定しています。
	 * 
	 * 宛先 ユニットマネージャー
	 * 内容 未提出者一覧
	 */
	public void sendAttendanceFogotNameWarning() {
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		String subject = messageOutput.mailMessage("fogotAttendanceNameList.subject");
	    String text = messageOutput.mailMessage("fogotAttendanceNameListWarning.text");
	    String from = messageOutput.mailMessage("systemManager.from");
	    
	    message.setTo(forgotAttendanceArray());
	    message.setSubject(subject);
	    message.setText(text);
	    message.setFrom(from);
		
		mailSender.send(message);
		
		System.out.println("UM宛　２日　勤怠送信OK");
	}
	
	/**
	 * このメソッドは先月の勤怠が未提出であることをメールでお知らせするメソッドです。
	 * 「setTo」で宛先の一覧（メールアドレスの配列）を指定しています。
	 * 
	 * 宛先 ユニットマネージャー
	 * 内容 未提出者一覧
	 */
	public void sendAttendanceFogotNameDanger() {
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		String subject = messageOutput.mailMessage("fogotAttendanceNameList.subject");
	    String text = messageOutput.mailMessage("fogotAttendanceNameListDanger.text");
	    String from = messageOutput.mailMessage("systemManager.from");
	    
	    message.setTo(forgotAttendanceArray());
	    message.setSubject(subject);
	    message.setText(text);
	    message.setFrom(from);
		
		mailSender.send(message);
		
		System.out.println("UM宛　３日　勤怠送信OK");
	}
	
	/**
	 * このメソッドは先月分の勤怠を承認する時期になったことをメール送信するメソッドです。
	 * 「setTo」で未提出者の一覧（名前の配列）を指定しています。
	 * 
	 * 宛先 マネージャー
	 * 内容 提出された勤怠処理を促す内容
	 */
	public void sendAttendanceApprovalReminder() {
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		String subject = messageOutput.mailMessage("attendance.subject");
	    String text = messageOutput.mailMessage("attendance.text");
	    String from = messageOutput.mailMessage("systemManager.from");
	    
	    message.setTo(getForgotAttendanceNameArray());
	    message.setSubject(subject);
	    message.setText(text);
	    message.setFrom(from);
		
		mailSender.send(message);
		
		System.out.println("マネージャー宛　３日　勤怠送信OK");		
	}
	
	/**
	 * このメソッドは先月分の勤怠が未提出者名をメール送信するメソッドです。
	 * 「setTo」で未提出者の一覧（名前の配列）を指定しています。
	 * 
	 * 宛先 マネージャー
	 * 内容 未提出者一覧
	 */
	public void sendUnsubmittedAttendanceReminder() {
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		String subject = messageOutput.mailMessage("attendance.subject");
	    String text = messageOutput.mailMessage("fogotAttendanceNameList.text");
	    String from = messageOutput.mailMessage("systemManager.from");
	    
	    message.setTo(getForgotAttendanceNameArray());
	    message.setSubject(subject);
	    message.setText(text);
	    message.setFrom(from);
		
		mailSender.send(message);
		
		System.out.println("マネージャー宛　５日　勤怠送信OK");		
	}
	
	
	/**
	 * このメソッドは先月分の勤怠未提出者の名前を取得します。
	 *  1. 未提出者の名前を「List」で取得します。
	 *  2. 「List」を配列に変換します。
	 *  
	 *  @return ユニットマネージャーのアドレス配列
	 */
	public String[] getForgotAttendanceNameArray(){
				
		List<String> forgotAttendanceNameList =
				catchForgotMapper.getUnitManagerMail();
		
		return forgotAttendanceNameList.toArray(new String[0]);
	}
	
	/**
	 * このメソッドは先月の勤怠が未提出の人のメールアドレスを取得します。
	 *  1. 未提出者とマネージャーのアドレスを「List」で取得します。
	 *  2. 「List」を配列に変換します。
	 *  
	 *  @return メール送信対象者のアドレス配列
	 */
	public String[] forgotAttendanceArray(){
		
		Date firstDayOfLastMonth =
				Date.valueOf(YearMonth.now().minusMonths(1).atDay(1));
		
		List<String> forgotAttendanceMailList =
				catchForgotMapper.getForgotAttendanceMail(firstDayOfLastMonth);
		
		return forgotAttendanceMailList.toArray(new String[0]);
	}	
	
	/**
	 * このメソッドはユニットマネージャーのメールアドレスを取得します。
	 *  1. ユニットマネージャーのアドレスを「List」で取得します。
	 *  2. 「List」を配列に変換します。
	 *  
	 *  @return ユニットマネージャーのアドレス配列
	 */
	public String[] getUnitmanagerMailArray(){
				
		List<String> unitmanagerMailList =
				catchForgotMapper.getUnitManagerMail();
		
		return unitmanagerMailList.toArray(new String[0]);
	}
	
	/**
	 * このメソッドはマネージャーのメールアドレスを取得します。
	 *  1. マネージャーのアドレスを「List」で取得します。
	 *  2. 「List」を配列に変換します。
	 *  
	 *  @return ユニットマネージャーのアドレス配列
	 */
	public String[] getManagerMailArray(){
				
		List<String> unitmanagerMailList =
				catchForgotMapper.getManagerMail();
		
		return unitmanagerMailList.toArray(new String[0]);
	}
	
	/**
	 * このメソッドはエラーが発生したことメールするメソッドです
	 *  
	 */
	public void exceptionNotificationMail(String stackTrace) {
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		String subject = messageOutput.mailMessage("exception.message.subject");
	    String text = stackTrace;
	    String from = messageOutput.mailMessage("exception.message.from");
		
		message.setTo("hasegawam@analix.co.jp"); 
	    message.setSubject(subject);
	    message.setText(text);
	    message.setFrom(from); 
		
		mailSender.send(message);
	}
}
