package com.example.demo.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.stereotype.Component;

import lombok.Data;
@Component
@Data
public class Users implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer userId;
	
	private String userName;
	
	private String password;
	
	private String role;
	
	//部署ID
	private Integer departmentId;
	
	//利用開始可能日
	private Date startDate;
	
	//メールアドレス
	private String address;
	
	//電話番号
	private String tel;
	
	//承認状況
	private Integer  status;
	
	//トークン
	private String resetToken;
	
	//トークンの有効期限
	private Date tokenExpiryDate;
	
	//トークンの有効期限がOKかどうか
	private Boolean tokenExpirationDateCheck;
	

}