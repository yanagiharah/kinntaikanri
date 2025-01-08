package com.example.demo.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.example.demo.mapper.DailyReportDetailMapper;
import com.example.demo.mapper.DailyReportMapper;
import com.example.demo.model.DailyReportDetailForm;
import com.example.demo.model.DailyReportForm;

@Service
public class DailyReportService {

	private DailyReportMapper dailyReportMapper;

	private DailyReportDetailMapper dailyReportDetailMapper;
	
	private ModelService modelService;
	
	DailyReportService(DailyReportMapper dailyReportMapper,DailyReportDetailMapper dailyReportDetailMapper,ModelService modelService){
		this.dailyReportMapper = dailyReportMapper;
		this.dailyReportDetailMapper = dailyReportDetailMapper;
		this.modelService = modelService;
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
	
	//日報取得
	public DailyReportForm getDailyReport(Integer userId, LocalDate localDateDay) {
		DailyReportForm dailyReportForm = dailyReportMapper.getDailyReport(userId, localDateDay);
		//nullなら新しいフォームを作成
		if (dailyReportForm == null) {
			dailyReportForm = new DailyReportForm();
			dailyReportForm.setUserId(userId);
			dailyReportForm.setDailyReportDate(localDateDay);
		}
		return dailyReportForm;

	}
	//空のリストを10行まで追加で作成
	public List<DailyReportDetailForm> populateEmptyDailyReportDetails(List<DailyReportDetailForm> dailyReportDetailForm, 
			Integer userId, LocalDate calendarDate) {
	    while (dailyReportDetailForm.size() < 10) {
	        DailyReportDetailForm emptyDetailForm = new DailyReportDetailForm();
	        emptyDetailForm.setUserId(userId);
	        emptyDetailForm.setDailyReportDetailDate(calendarDate);
	        dailyReportDetailForm.add(emptyDetailForm);
	    }
	    return dailyReportDetailForm;
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

	//日報更新
	@Transactional
	public void updateDailyReportDetail(DailyReportForm dailyReportForm) {
		try {
			//dailyReportの存在確認ののち、無ければinsert
			if (dailyReportForm.getStatus() == null) {
				dailyReportMapper.insertDailyReport(dailyReportForm);
			}

			//日報フォームの中にある日報詳細リストを取り出す。それを元にDBを更新
			for (DailyReportDetailForm dailyReportDetailForm : dailyReportForm.getDailyReportDetailForm()) {

				//INSERT処理
				if (dailyReportDetailForm.getDailyReportDetailId() == null &&
						dailyReportDetailForm.getDailyReportDetailTime() != null &&
						dailyReportDetailForm.getContent() != null) {

					dailyReportForm.setUserId(dailyReportDetailForm.getUserId());

					dailyReportDetailMapper.insertDailyReportDetail(dailyReportDetailForm);

					//UPDATE処理
				} else if (dailyReportDetailForm.getDailyReportDetailId() != null &&
						!dailyReportDetailForm.getContent().isEmpty() &&
						dailyReportDetailForm.getDailyReportDetailDate() != null) {

					dailyReportForm.setUserId(dailyReportDetailForm.getUserId());

					dailyReportDetailMapper.updateDailyReportDetail(dailyReportDetailForm);

					//DELETE処理
				} else if (dailyReportDetailForm.getDailyReportDetailId() != null &&
						dailyReportDetailForm.getContent().isEmpty() &&
						dailyReportDetailForm.getDailyReportDetailTime() == null) {

					dailyReportDetailMapper.deleteDailyReportDetail(dailyReportDetailForm.getDailyReportDetailId());
				}
			}

			//最後に日報詳細を取得し空なら日報を削除。それ以外の場合は更新（内容に変更はない）。
			if (getDailyReportDetail(dailyReportForm.getUserId(), dailyReportForm.getDailyReportDate()).isEmpty()) {

				dailyReportMapper.deleteDailyReport(dailyReportForm.getDailyReportId());
			} else {
				dailyReportMapper.updateDailyReport(dailyReportForm);
			}
			
			//日報登録をしてからf5を押したときに出るエクセプション
		} catch (DuplicateKeyException e) {

		}
	}
	//マネージャー用 確認待ち取得
	public List<DailyReportForm> selectConfirmPending(LocalDate today) {
		// LocalDateをStringに変換
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	     String dailyReportDate = today.format(formatter);;
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
	//日報確認
	public void updateConfirmDailyReport(DailyReportForm dailyReportForm) {
		dailyReportMapper.updateConfirmDailyReport(dailyReportForm);
	}
}
