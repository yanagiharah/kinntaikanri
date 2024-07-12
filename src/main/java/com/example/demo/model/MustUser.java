package com.example.demo.model;

import java.util.Date;

import lombok.Data;

@Data
public class MustUser {
	
	private Integer id;
	
	private String name;
	
	private char password;
	
	private String role;
	
	private Date startDate;
	
	
	

}
