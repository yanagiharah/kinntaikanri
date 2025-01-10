package com.example.demo.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class DailyReportForm {
	
	@Valid
	private List<DailyReportDetailForm> dailyReportDetailForm;

	private Integer dailyReportId;
	
	private Integer userId;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate dailyReportDate;
	
//	日付を送りたい場合はこちらに変更
//	private LocalDateTime dailyReportDate;
	
	private Integer status;
	
	private String userName;
}
