package com.example.demo.model;

import java.util.ArrayList;

import lombok.Data;

@Data
public class AttendanceFormList {
	
	private ArrayList<Attendance> attendanceList;
	
	private Integer  status;//承認状況
}
