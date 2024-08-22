package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.model.DailyReportForm;

@Mapper
public interface DailyReportMapper {
	
	//日報取得
	public DailyReportForm getDailyReport(Integer integer) ;
	
}
