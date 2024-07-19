package com.example.demo.model;

import java.util.Date;

import lombok.Data;

@Data
public class Attendance {
	
	private Integer attendanceId;
	
	private Integer userId;
	
	private String userName;
	
	private Integer  status;
	
	private Date attendanceDate;
	
	private String startTime;
	
	private String endTime;
	
	private String attendanceRemarks;
	
	private Integer years;
	
	private Integer month;
	
	private Integer days;
	
	private String dayOfWeek;
	
	//月末が何日かを判断するためのに使用してる変数
	private Date startDate;	
	private Date endDate;
	
	
//	private Date status;
}
