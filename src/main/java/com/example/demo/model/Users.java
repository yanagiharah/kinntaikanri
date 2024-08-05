package com.example.demo.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import lombok.Data;
@Component
@SessionScope
@Data
public class Users implements Serializable {
	
	private Integer userId;
	
	private String userName;
	
	private String password;
	
	private String role;
	
	private Date startDate;
	
	private Integer  status;//承認状況
	
	//練習がｄｇｈ
}