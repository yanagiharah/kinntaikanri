package com.example.demo.model;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class ManagementForm {
	
	private List<Department> department;
	
	private Integer userId;

	private String userName;
	
	private String password;
	
	private String role;
	
	private Integer departmentId;
	
	private String startDate;
	
	private String tel;
	
	private String address;
}