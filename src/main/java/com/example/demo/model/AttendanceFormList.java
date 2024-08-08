package com.example.demo.model;

import java.util.ArrayList;

import lombok.Data;

@Data
public class AttendanceFormList {
	
	private ArrayList<Attendance> attendanceList;
	
	private Integer  status;//承認状況
	
	private Boolean RequestActivityCheck;//承認申請ボタンの活性と非活性
	
	private Boolean registrationActivityCheck;//登録ボタンの活性と非活性
	
	private String stringYears;//年
	
	private String stringMonth;//月
}
