package com.example.demo.service;

import java.time.LocalDate;
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
	public DailyReportForm getDailyReport(Integer userId, LocalDate today) {
		DailyReportForm dailyReportform = dailyReportMapper.getDailyReport(userId, today);
		return dailyReportform;

	}

	//日報詳細取得
	public List<DailyReportDetailForm> getDailyReportDetail(Integer userId, LocalDate today) {
		List<DailyReportDetailForm> dailyReportDetailForm = dailyReportDetailMapper.getDailyReportDetail(userId, today);
		return dailyReportDetailForm;
	}

	//日報内容追加
	public void insertDailyReportDetail(DailyReportDetailForm dailyReportDetailForm) {
		dailyReportDetailMapper.insertDailyReportDetail(dailyReportDetailForm);
	}

	//日報更新
	public void updateDailyReportDetail(DailyReportForm dailyReportForm) {

		//日報フォームの中にある日報詳細リストを取り出す。それを元にDBを更新
		for (DailyReportDetailForm dailyReportDetailForm : dailyReportForm.getDailyReportDetailForm()) {
			
			//INSERT処理
			if (dailyReportDetailForm.getDailyReportDetailId() == null &&
				dailyReportDetailForm.getDailyReportDetailTime() != null &&
				dailyReportDetailForm.getContent() != null) {
				
				dailyReportDetailMapper.insertDailyReportDetail(dailyReportDetailForm);
				
			//UPDATE処理
			} else if (dailyReportDetailForm.getDailyReportDetailId() != null &&
					   dailyReportDetailForm.getContent() != null &&
					   dailyReportDetailForm.getDailyReportDetailDate() != null) {
				
				dailyReportDetailMapper.updateDailyReportDetail(dailyReportDetailForm);
			}
		}
	}
}
