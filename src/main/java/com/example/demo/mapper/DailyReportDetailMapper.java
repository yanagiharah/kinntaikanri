package com.example.demo.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.DailyReportDetailForm;

@Mapper
public interface DailyReportDetailMapper {

	//日報詳細取得
	public List<DailyReportDetailForm> getDailyReportDetail(@Param("userId")Integer userId, @Param("dailyReportDetailDate")LocalDate dailyReportDetailDate);
	
	//日報内容追加
	public void insertDailyReportDetail(List<DailyReportDetailForm> dailyReportDetailForm);
	
	//日報更新
	public void updateDailyReportDetail(DailyReportDetailForm dailyReportDetailFrom);
}
