package com.example.demo.model;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class DailyReportForm {
	
	private List< DailyReportDetailForm> dailyReportDetailForm;

	private Integer id;
	
	private Integer userId;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private Date dailyReportDate;
	
//	日付を送りたい場合はこちらに変更
//	private LocalDateTime dailyReportDate;
	
	private Integer status;
}
