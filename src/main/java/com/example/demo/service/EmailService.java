package com.example.demo.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.UsersMapper;
import com.example.demo.model.Users;

@Service
public class EmailService {
	private final JavaMailSender mailSender;
    private final UsersMapper usersMapper;
    private final MessageSource messageSource;
    private final CommonActivityService commonActivityService;

    public EmailService(JavaMailSender mailSender, UsersMapper usersMapper, MessageSource messageSource, CommonActivityService commonActivityService) {
        this.mailSender = mailSender;
        this.usersMapper = usersMapper;
        this.messageSource = messageSource;
        this.commonActivityService = commonActivityService;
    }
    
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("koishi3rd.eye@gmail.com");
        mailSender.send(message);
    }

    //月初の3日と5日に月次勤怠申請がされていないユーザーへメール送信
    public void monthlyAttendanceNotApplied() {
    	Date firstDayOfLastMonthDate = commonActivityService.oneDayLastMonth();
        List<Users> usersList = usersMapper.selectMonthlyAttendanceNotSubmittedUsers(firstDayOfLastMonthDate);
        String regularAndUnitManagerUserNames = concatenateRegularAndUnitManagerUserNames(usersList);
        // 現在の日付を取得
     		LocalDate now = LocalDate.now();
     	// Date型に変換
    		Date nowDate = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
        for (Users user : usersList) {
            if ("Admin".equals(user.getRole())) {
                continue; // Adminロールのユーザーにはメールを送信しない
            }
            if(nowDate.before(user.getStartDate())) {
            	continue;// 利用開始日が未来の人にはメールを送信しない
            }
            String subjectKey = user.getRole().equals("Manager") ? "managerSubject" : "regularSubject";
            String bodyKey = user.getRole().equals("Manager") ? "managerBody" : "regularBody";
            String subject = messageSource.getMessage(subjectKey, null, Locale.getDefault());
            String text = messageSource.getMessage(bodyKey, new Object[]{regularAndUnitManagerUserNames}, Locale.getDefault());
            sendEmail(user.getAddress(), subject, text);
        }
    }

    //マネージャーに送信するRegularとUnitManagerの名前リストを作成（利用開始日を過ぎているユーザーのみ）
	private String concatenateRegularAndUnitManagerUserNames(List<Users> usersList) {
		// 現在の日付を取得
		LocalDate now = LocalDate.now();
		// Date型に変換
		Date nowDate = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return usersList.stream()
				.filter(user -> ("Regular".equals(user.getRole()) && nowDate.after(user.getStartDate()))
						|| ("UnitManager".equals(user.getRole()) && nowDate.after(user.getStartDate())))
				.map(Users::getUserName)
				.collect(Collectors.joining(", "));
	}

}
