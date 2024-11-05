package com.example.demo.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	//昨日の日報のステータス取得
	public Integer checkYesterdayDailyReport(Integer userId, LocalDate yesterday) {
		Integer checkDailyReport = dailyReportMapper.selectYesterdayCheck(userId, yesterday);
		return checkDailyReport;
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

			//更新用（マネージャーが承認済みにするときに使う）
			//dailyReportMapper.updateDailyReport(dailyReportForm);
			
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
	
	//日報確認
	public void updateConfirmDailyReport(DailyReportForm dailyReportForm) {
		dailyReportMapper.updateConfirmDailyReport(dailyReportForm);
	}
}
