package com.example.demo.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.demo.mapper.UsersMapper;
import com.example.demo.model.Users;
import com.example.demo.service.EmailService;

@ControllerAdvice
public class GlobalExceptionHandler {
	private final EmailService emailService; // EmailServiceに変更
    private final UsersMapper usersMapper;   // ユーザーリストを取得するためのMapper

    public GlobalExceptionHandler(EmailService emailService, UsersMapper usersMapper) {
        this.emailService = emailService;
        this.usersMapper = usersMapper;
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleAllExceptions(Exception ex) {
        // 例外の詳細をログに出力
        ex.printStackTrace();

        // メール本文に使用するエラーログ
        StringBuilder errorText = new StringBuilder();
        errorText.append("An error occurred: ").append(ex.getMessage()).append("\n\n");
        for (StackTraceElement element : ex.getStackTrace()) {
            errorText.append(element.toString()).append("\n");
        }

        // すべてのユーザーを取得
        List<Users> usersList = usersMapper.selectAdmin(); 

        // ユーザーリストに対してメール送信
        emailService.sendErrorLogToUsers(usersList, errorText.toString());
    }

}
