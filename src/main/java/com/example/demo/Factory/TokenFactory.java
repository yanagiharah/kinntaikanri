package com.example.demo.Factory;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class TokenFactory {
	
	// トークンの生成
    public String generateResetToken() {
        return UUID.randomUUID().toString();  // 一意のトークンを生成
    }

    // トークンの有効期限を設定する（1時間）
    public Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return cal.getTime();
    }
}
