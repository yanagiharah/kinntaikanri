package com.example.demo.model;

import lombok.Data;

@Data
public class Department {
	
	private Integer departmentId;//部署ID
	
	private String departmentName;//部署名
	
	private Integer isActive;//有効フラグ
	
	
}
