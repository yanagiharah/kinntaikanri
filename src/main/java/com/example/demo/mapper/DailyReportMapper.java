package com.example.demo.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.DailyReportForm;

@Mapper
public interface DailyReportMapper {
	
	//日報取得
	public DailyReportForm getDailyReport(@Param("userId")Integer userId, @Param("dailyReportDate")LocalDate today);
	
	//日報登録
	public void insertDailyReport(DailyReportForm dailyReportForm);
	
	//日報更新
	public void updateDailyReport(DailyReportForm dailyReportForm);
		
	//日報削除
	public void deleteDailyReport(Integer dailyReportId);
	
	//日報確認
	public void updateConfirmDailyReport(DailyReportForm dailyReportForm);
	
	//昨日の日報存在確認
	public List<String> selectYesterdayCheck(Integer userId, LocalDate yesterday,LocalDate oneWeekAgoDate);
	
	//確認待ちユーザー情報の取得
	public List<DailyReportForm> selectConfirmPending(@Param("dailyReportDate")String dailyReportDate);
	
	//マネージャーアラート用日報未確認存在日表示（直近一週間）
	public List<String> selectAlertForConfirm(@Param("yesterday")LocalDate yesterday);
	
	//マネージャー日報画面用最古十件の日報未確認用
	public List<String>  selectComfimPendingStatus1OrderByOlder();
}
