package com.example.demo.model;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DailyReportDetailForm {

	private Integer dailyReportDetailId;
	
	private Integer userId;
	
//	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate dailyReportDetailDate;
	
	private Integer dailyReportDetailTime;
	
	private String content;
}
