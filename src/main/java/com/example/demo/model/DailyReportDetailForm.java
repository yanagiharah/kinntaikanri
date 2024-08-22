package com.example.demo.model;

import java.util.Date;

import lombok.Data;

@Data
public class DailyReportDetailForm {

	private Integer dailyReportDetailId;
	
	private Integer userId;
	
	private Date dailyReportDetailDate;
	
	private Integer dailyReportDetailTime;
	
	private String content;
		
}
