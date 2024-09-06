package com.example.demo.model;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class DepartmentForm {
	
	private List<Department> activeDepartment;
	
	private List<Department> deactiveDepartment;
	
	private String oldDepartmentName;
	
	private String newDepartmentName;
	
	private String deletedDepartmentName;
}