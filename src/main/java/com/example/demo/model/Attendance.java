package com.example.demo.model;

import java.util.Date;

import lombok.Data;

@Data
public class Attendance {
	
	private Integer attendanceId;
	
	private Integer userId;
	
	private Integer  status;
	
	private Date attendance_date;
	
	private String start_time;
	
	private String end_time;
	
	private String attendance_remarks;
	
	private Integer years;
	
	private Integer month;
	
	private Date startDate;
	
	private Date endDate;
	
	
//	private Date status;
}
