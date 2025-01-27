package com.example.demo.Factory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.demo.mapper.DailyReportDetailMapper;
import com.example.demo.mapper.DailyReportMapper;
import com.example.demo.model.DailyReportDetailForm;
import com.example.demo.model.DailyReportForm;

@Component
public class DailyReportFactory {
	
	private DailyReportMapper dailyReportMapper;
	
	private DailyReportDetailMapper dailyReportDetailMapper;
	
	DailyReportFactory(DailyReportMapper dailyReportMapper,DailyReportDetailMapper dailyReportDetailMapper){
		this.dailyReportMapper = dailyReportMapper;
		this.dailyReportDetailMapper = dailyReportDetailMapper;
	}
	
	public DailyReportForm createNewDailyReport(Integer userId,LocalDate localDateDay) {
		DailyReportForm dailyReportForm = new DailyReportForm();
		dailyReportForm.setUserId(userId);
		dailyReportForm.setDailyReportDate(localDateDay);
		return dailyReportForm;
	}
	
	public DailyReportForm createDailyReportForm(Integer userId,LocalDate dailyReportDate) {
		//日報取得 
		DailyReportForm dailyReportForm = getDailyReport(userId, dailyReportDate);
		// 日報詳細を取得
		List<DailyReportDetailForm> dailyReportDetailForm = getDailyReportDetail(userId,dailyReportDate);
		//空のリストを10行まで追加で作成
		populateEmptyDailyReportDetails(dailyReportDetailForm,userId,dailyReportDate);
		//（今日の日付とユーザーIDをセット）
		dailyReportForm.setDailyReportDetailForm(dailyReportDetailForm);
		dailyReportForm.setUserId(userId);
		return dailyReportForm;
				
	}
	
	//日報取得
		public DailyReportForm getDailyReport(Integer userId, LocalDate localDateDay) {
			DailyReportForm dailyReportForm = dailyReportMapper.getDailyReport(userId, localDateDay);
			//nullなら新しいフォームを作成
			if (dailyReportForm == null) {
				dailyReportForm = createNewDailyReport(userId, localDateDay);
			}
			return dailyReportForm;
		}
	
		//日報詳細取得
		public List<DailyReportDetailForm> getDailyReportDetail(Integer userId, LocalDate localDateDay) {
			List<DailyReportDetailForm> dailyReportDetailForm = dailyReportDetailMapper.getDailyReportDetail(userId, localDateDay);
			// nullまたは空なら新しいリストを初期化
			if (dailyReportDetailForm == null || dailyReportDetailForm.isEmpty()) {
				dailyReportDetailForm = new ArrayList<>();
			}
			return dailyReportDetailForm;
		}
	
	//空のリストを10行まで追加で作成
	public List<DailyReportDetailForm> populateEmptyDailyReportDetails(List<DailyReportDetailForm> dailyReportDetailForm, Integer userId, LocalDate calendarDate) {
	    while (dailyReportDetailForm.size() < 10) {
	        DailyReportDetailForm emptyDetailForm = new DailyReportDetailForm();
	        emptyDetailForm.setUserId(userId);
	        emptyDetailForm.setDailyReportDetailDate(calendarDate);
	        dailyReportDetailForm.add(emptyDetailForm);
	    }
	    return dailyReportDetailForm;
	}
}
