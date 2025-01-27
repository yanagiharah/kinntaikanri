package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.example.demo.Factory.DailyReportFactory;
import com.example.demo.helper.DateHelper;
import com.example.demo.mapper.DailyReportDetailMapper;
import com.example.demo.mapper.DailyReportMapper;
import com.example.demo.model.DailyReportDetailForm;
import com.example.demo.model.DailyReportForm;
import com.example.demo.model.Users;

@Service
public class DailyReportService {

	private DailyReportMapper dailyReportMapper;

	private DailyReportDetailMapper dailyReportDetailMapper;
	
	private ModelService modelService;
	
	private DateHelper dateHelper;
	
	private DailyReportFactory dailyReportFactory;
	
	DailyReportService(DailyReportMapper dailyReportMapper,DailyReportDetailMapper dailyReportDetailMapper,ModelService modelService,DateHelper dateHelper,DailyReportFactory dailyReportFactory){
		this.dailyReportMapper = dailyReportMapper;
		this.dailyReportDetailMapper = dailyReportDetailMapper;
		this.modelService = modelService;
		this.dateHelper = dateHelper;
		this.dailyReportFactory = dailyReportFactory;
	}
	//日報の初期表示処理（今日時点のものを表示）
	public void serviceForDailyReportDetail(Model model,Users users,String date) {
		//日付を取得
		LocalDate calendarDate=dateHelper.getInputCalendarDate(date);
		if (!users.getRole().equalsIgnoreCase("Manager")) {
			Integer userId = users.getUserId();
			//日報取得 
			DailyReportForm dailyReportForm = dailyReportFactory.createDailyReportForm(userId,calendarDate);
			modelService.addDailyReportForm(model,dailyReportForm);
		} else {
			modelService.addConfirmPending(model,selectConfirmPending(calendarDate));
			modelService.addCalendarDate(model,calendarDate);
		}
	}
	//confirmPending表示者を押したときの処理
	public void serviceForDailyRepManagement(Model model, Integer userId,String userName,LocalDate dailyReportDate) {
		//日報取得 
		DailyReportForm dailyReportForm = dailyReportFactory.createDailyReportForm(userId,dailyReportDate);
		//上記に加えて名前表示もあるため以下で追加
		dailyReportForm.setUserName(userName);
		modelService.addDailyReportForm(model,dailyReportForm);
		
		List<DailyReportForm> confirmPending= selectConfirmPending(dailyReportDate);
		modelService.addConfirmPending(model,confirmPending);
	}
	//提出ボタン押下時処理
	public String serviceForUpdateDailyReportDetail(Model model,DailyReportForm dailyReportForm,Locale locale) {
		updateDailyReportDetail(dailyReportForm);
		modelService.addMessage(model);

		LocalDate calendarDate = dailyReportForm.getDailyReportDetailForm().get(0).getDailyReportDetailDate();
		return dateHelper.getYearMonthDay(calendarDate);
	}
	
	//検索ボタン押下時処理
	public void serviceForSearchConfirmPendingStatusOne(Model model,String date) {
		LocalDate calendarDate=dateHelper.getInputCalendarDate(date);
		
		List<String> confirmPendingStatus1 = selectConfirmPendingStatus1();
		if(!confirmPendingStatus1.isEmpty()) {
			modelService.addConfirmPendingStatus1(model,confirmPendingStatus1);
		} else {
			modelService.dailyReportAllSubmitted(model);
		}
		//もしデータがない場合はそのメッセージも送らないといけないかも
	    
		modelService.addCalendarDate(model,calendarDate);
	}
	//確認ボタン押下時処理
	public void serviceForUpdateStatusConfirm(Model model,DailyReportForm dailyReportForm) {
		dailyReportMapper.updateConfirmDailyReport(dailyReportForm);
		
		LocalDate calendarDate = dailyReportForm.getDailyReportDate();
		List<DailyReportForm> confirmPending = selectConfirmPending(calendarDate);
		modelService.addConfirmPending(model,confirmPending);
		modelService.addCalendarDate(model,dailyReportForm.getDailyReportDate());
		
		dailyReportForm = null;
		modelService.addDailyReportForm(model,dailyReportForm);
	}

	//昨日から一週間前までの日報のステータス取得
	public Model checkYesterdayDailyReport(Integer userId, LocalDate yesterday,Model model) {
		LocalDate oneWeekAgoDate = yesterday.minusDays(7);
		List<String> listCheckDailyReport = dailyReportMapper.selectYesterdayCheck(userId, yesterday,oneWeekAgoDate);
		if (listCheckDailyReport != null && !listCheckDailyReport.isEmpty()) {
			//出勤日でありながら日報を提出していない場合IFに入る
			return modelService.CheckDailyReport(model,listCheckDailyReport);
		}
		return null;
	}
	
	

	//日報更新
	@Transactional
	public void updateDailyReportDetail(DailyReportForm dailyReportForm) {
			//dailyReportの存在確認ののち、無ければinsert
			if (dailyReportForm.getStatus() == null) {
				dailyReportMapper.insertDailyReport(dailyReportForm);
			}
			//日報フォームの中にある日報詳細リストを取り出す。それを元にDBを更新
			for (DailyReportDetailForm reportDetail : dailyReportForm.getDailyReportDetailForm()) {
				//INSERT処理
				if (reportDetail.getDailyReportDetailId() == null &&reportDetail.getDailyReportDetailTime() != null &&reportDetail.getContent() != null) {
					dailyReportForm.setUserId(reportDetail.getUserId());
					dailyReportDetailMapper.insertDailyReportDetail(reportDetail);
					//UPDATE処理
				} else if (reportDetail.getDailyReportDetailId() != null &&!reportDetail.getContent().isEmpty() && reportDetail.getDailyReportDetailDate() != null) {
					dailyReportForm.setUserId(reportDetail.getUserId());
					dailyReportDetailMapper.updateDailyReportDetail(reportDetail);
					//DELETE処理
				} else if (reportDetail.getDailyReportDetailId() != null && reportDetail.getContent().isEmpty() && reportDetail.getDailyReportDetailTime() == null) {
					dailyReportDetailMapper.deleteDailyReportDetail(reportDetail.getDailyReportDetailId());
				}
			}
			//最後に日報詳細を取得し空なら日報を削除。それ以外の場合は更新（内容に変更はない）。
			if (dailyReportFactory.getDailyReportDetail(dailyReportForm.getUserId(), dailyReportForm.getDailyReportDate()).isEmpty()) {
				dailyReportMapper.deleteDailyReport(dailyReportForm.getDailyReportId());
			} else {
				dailyReportMapper.updateDailyReport(dailyReportForm);
			}

	}
	//マネージャー用 確認待ち取得
	public List<DailyReportForm> selectConfirmPending(LocalDate today) {
		// LocalDateをStringに変換
		String dailyReportDate = dateHelper.getYearMonthDay(today);
		return dailyReportMapper.selectConfirmPending(dailyReportDate);
	}
	
	//マネージャーアラート用確認待ち存在確認
	public Model selectAlertForConfirm(LocalDate yesterday,Model model) {
		List<String> existsForAlert= dailyReportMapper.selectAlertForConfirm(yesterday);
		if(existsForAlert == null || existsForAlert.isEmpty()) {
			return null;
		}
		return  modelService.dailyReportArrival(model, existsForAlert);
	};
	
	//未確認検索ボタン押したとき用
	public List<String> selectConfirmPendingStatus1(){
		List<String> confirmPendingIsStatus1 = dailyReportMapper.selectComfimPendingStatus1OrderByOlder();
		return confirmPendingIsStatus1;
	}
}
