package com.example.demo.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class DailyReportForm {
	
	private List<DailyReportDetailForm> dailyReportDetailForm;

	private Integer id;
	
	private Integer userId;
	
	private Date dailyReportDate;
	
	private Integer status;
}
