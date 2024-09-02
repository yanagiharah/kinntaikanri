package com.example.demo.model;

import java.util.Date;

import org.springframework.stereotype.Component;

import lombok.Data;
@Component
@Data
public class Users {
	
	private Integer userId;
	
	private String userName;
	
	private String password;
	
	private String role;
	
	private Integer departmentId;
	
	private Date startDate;
	
	private Integer  status;//承認状況
	

}