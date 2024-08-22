package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.DailyReportDetailMapper;
import com.example.demo.mapper.DailyReportMapper;
import com.example.demo.model.DailyReportDetailForm;
import com.example.demo.model.DailyReportForm;

@Service
public class DailyReportService {
	
	@Autowired
	private DailyReportMapper dailyReportMapper;
	
	@Autowired
	private DailyReportDetailMapper dailyReportDetailMapper;
		
	//日報取得
	public DailyReportForm getDailyReport(Integer integer) {
		DailyReportForm dailyReportform = dailyReportMapper.getDailyReport(integer);
			return dailyReportform;
		
	}
	
	//日報詳細取得
	public List<DailyReportDetailForm> getDailyReportDetail(Integer integer){
		List<DailyReportDetailForm> dailyReportDetailForm = dailyReportDetailMapper.getDailyReportDetail(integer);
			return dailyReportDetailForm;
	}
	
	//日報内容追加
	public void insertDailyReportDetail(List<DailyReportDetailForm> list) {
		dailyReportDetailMapper.insertDailyReportDetail(list);
	}
	
	//日報更新
	public void updateDailyReportDetail(List<DailyReportDetailForm> list) {
		dailyReportDetailMapper.updateDailyReportDetail(list);
	}

}
