package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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

	//昨日の日報のステータス取得
	public Integer checkYesterdayDailyReport(Integer userId, LocalDate yesterday) {
		Integer checkDailyReport = dailyReportMapper.selectYesterdayCheck(userId, yesterday);
		return checkDailyReport;
	}
	
	//日報取得
	public DailyReportForm getDailyReport(Integer userId, LocalDate localDateDay) {
		DailyReportForm dailyReportForm = dailyReportMapper.getDailyReport(userId, localDateDay);
		return dailyReportForm;

	}

	//日報詳細取得
	public List<DailyReportDetailForm> getDailyReportDetail(Integer userId, LocalDate localDateDay) {
		List<DailyReportDetailForm> dailyReportDetailForm = dailyReportDetailMapper.getDailyReportDetail(userId, localDateDay);
		return dailyReportDetailForm;
	}

	//日報更新
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
}
