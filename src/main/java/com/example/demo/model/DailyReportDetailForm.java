package com.example.demo.model;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class DailyReportDetailForm {

	private Integer dailyReportDetailId;
	
	private Integer userId;
	
//	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate dailyReportDetailDate;
	
	@Min(value = 0, message = "{DailyReportDetailForm.time}")
	@Max(value = 99, message = "{DailyReportDetailForm.time}")
	private Integer dailyReportDetailTime;
	
	@Size(max = 20, message = "{DailyReportDetailForm.content}")
//	@Pattern(regexp = "^[\\p{IsHiragana}\\p{IsKatakana}\\p{IsIdeographic}ー]*$", message = "{DailyReportDetailForm.wide}")
	private String content;
}
