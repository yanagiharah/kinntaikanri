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
	
	private String itemInaccurate;//時刻が不正です。HH:mm形式の00:00～23:59の間で入力してください。をhtmlに出力するための変数。
}
