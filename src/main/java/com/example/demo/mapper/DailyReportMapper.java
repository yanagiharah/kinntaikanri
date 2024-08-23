package com.example.demo.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.DailyReportForm;

@Mapper
public interface DailyReportMapper {
	
	//日報取得
	public DailyReportForm getDailyReport(@Param("userId")Integer userId, @Param("dailyReportDate")Date dailyReportDate);
	
}
