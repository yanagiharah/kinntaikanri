package com.example.demo.model;

import java.util.Date;

import lombok.Data;

@Data
public class DailyReportForm {

	private Integer id;
	
	private Integer userId;
	
	private Date dailyReportDate;
	
	private Integer status;
	
}
