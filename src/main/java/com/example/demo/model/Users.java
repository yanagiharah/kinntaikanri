package com.example.demo.model;

import java.util.Date;

import lombok.Data;

@Data
public class Users {
	
	private Integer id;
	
	private String name;
	
	private String password;
	
	private String role;
	
	private Date startDate;
	
	
	

}
