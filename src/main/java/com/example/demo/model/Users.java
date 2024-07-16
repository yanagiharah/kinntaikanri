package com.example.demo.model;

import java.util.Date;

import lombok.Data;

@Data
public class Users {
	
	private Integer userId;
	
	private String userName;
	
	private String password;
	
	private String role;
	
	private Date startDate;
	
	private Integer departmentName;
	
	
//	private Date status;
}
