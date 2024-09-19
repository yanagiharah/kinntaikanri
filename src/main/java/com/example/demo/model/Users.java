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
	
	private Integer departmentId;
	
	private Date startDate;
	
	private String address;
	
	private String tel;
	
	private Integer  status;//承認状況
	

}