package com.example.demo.mapper;

import java.time.LocalDate;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.DailyReportForm;

@Mapper
public interface DailyReportMapper {
	
	//日報取得c
	public DailyReportForm getDailyReport(@Param("userId")Integer userId, @Param("dailyReportDate")LocalDate today);
	
	//日報登録
	public void insertDailyReport(DailyReportForm dailyReportForm);
	
	//日報更新
	public void updateDailyReport(DailyReportForm dailyReportForm);
		
	//日報削除
	public void deleteDailyReport(Integer dailyReportId);
	
	//昨日の日報存在確認
	public Integer selectYesterdayCheck(Integer userId, LocalDate yesterday);
	
	//
	public Integer check(Integer userId, LocalDate calendarDate);
	
}
