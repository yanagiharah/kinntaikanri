package com.example.demo.model;

import lombok.Data;

@Data
public class ManagementForm {
	
	private Integer userId;

	private String userName;
	
	private String password;
	
	private String role;
	
	private String startDate;
}