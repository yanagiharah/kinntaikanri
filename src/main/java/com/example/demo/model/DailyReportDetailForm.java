package com.example.demo.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class DailyReportDetailForm {

	private Integer dailyReportDetailId;
	
	private Integer userId;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDateTime dailyReportDetailDate;
	
	private Integer dailyReportDetailTime;
	
	private String content;
}
