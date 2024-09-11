package com.example.demo.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.DailyReportDetailMapper;
import com.example.demo.mapper.DailyReportMapper;
import com.example.demo.model.DailyReportDetailForm;
import com.example.demo.model.DailyReportForm;
import com.example.demo.model.Users;

@Service
public class DailyReportService {

	private final DailyReportMapper dailyReportMapper;
	private final DailyReportDetailMapper dailyReportDetailMapper;
	
	/**
	 * コンストラクタ。日報管理のためのマッパーを初期化します。
	 *
	 * @param dailyReportMapper 日報を管理するためのデータベース操作を行うマッパー
	 * @param dailyReportDetailMapper 日報の詳細情報を管理するためのデータベース操作を行うマッパー
	 */
	DailyReportService(DailyReportMapper dailyReportMapper, DailyReportDetailMapper dailyReportDetailMapper){
		this.dailyReportMapper = dailyReportMapper;
		this.dailyReportDetailMapper = dailyReportDetailMapper;
	}
	
	/**
	 * 日報の初期表示設定を行います。
	 * 
	 * @param users ログイン中のユーザー情報
	 * @param date 対象の日付（初期画面は本日分）
	 * @return 空or登録済みの日報フォームオブジェクト
	 */
	public DailyReportForm initialSet(Users users, String date) {
	    Integer userId = users.getUserId();
		LocalDate calendarDate = (date == null)
				? LocalDate.now()
				: LocalDate.parse(date);
		
		//日報取得し代入、nullならnewして代入
		DailyReportForm dailyReportForm = getDailyReport(userId, calendarDate); 
		
		//日報詳細を代入 nullまたは空ならnewして代入
		List<DailyReportDetailForm> dailyReportDetailForm = getDailyReportDetail(userId, calendarDate);
	    
		//日報フォームに日報詳細とユーザーIDをセット
	    dailyReportForm.setDailyReportDetailForm(dailyReportDetailForm);
	    dailyReportForm.setUserId(userId);
	    return dailyReportForm;
    }
	
	/**
	 * 昨日の日報のステータスを取得します。
	 *
	 * @param userId ユーザーID
	 * @param yesterday 昨日の日付	 *
	 * @return 昨日の日報のステータス
	 */
	public Integer checkYesterdayDailyReport(Integer userId, LocalDate yesterday) {
		Integer checkDailyReport = dailyReportMapper.selectYesterdayCheck(userId, yesterday);
		return checkDailyReport;
	}
	
	/**
	 * 日報を取得します。日報が存在しない場合は新しいフォームを返します。
	 *
	 * @param userId ユーザーID
	 * @param calendarDate 取得する日付
	 * @return 日報フォームオブジェクト
	 */
	public DailyReportForm getDailyReport(Integer userId, LocalDate calendarDate) {
		DailyReportForm dailyReportForm = dailyReportMapper.getDailyReport(userId, calendarDate);
		if (dailyReportForm == null) {
			dailyReportForm = new DailyReportForm();
			dailyReportForm.setUserId(userId);
			dailyReportForm.setDailyReportDate(calendarDate);
		}
		return dailyReportForm;
	}

	/**
	 * 日報の詳細を取得します。日報が存在しない場合は新しいリストを返します。
	 *
	 * @param userId ユーザーID
	 * @param calendarDate 取得する日付
	 * @return 日報詳細フォームのリスト
	 */
	public List<DailyReportDetailForm> getDailyReportDetail(Integer userId, LocalDate calendarDate) {
		List<DailyReportDetailForm> dailyReportDetailForm =
				dailyReportDetailMapper.getDailyReportDetail(userId, calendarDate);
		if (dailyReportDetailForm == null || dailyReportDetailForm.isEmpty()) {
	        dailyReportDetailForm = new ArrayList<>(10);
	    }
		//空のリストを10行まで追加で作成し今日の日付とユーザーIDをセット）
		while (dailyReportDetailForm.size() < 10) {
	    	DailyReportDetailForm emptyDetailForm = new DailyReportDetailForm();
	    	emptyDetailForm.setUserId(userId);
	    	emptyDetailForm.setDailyReportDetailDate(calendarDate);
	    	dailyReportDetailForm.add(emptyDetailForm);
	    }
		return dailyReportDetailForm;
	}

//	/**
//	 * 日報の詳細を更新します。
//	 *
//	 * @param dailyReportForm 日報フォームオブジェクト
//	 */
//	public void updateDailyReportDetail(DailyReportForm dailyReportForm) {
//		try {
//			//日報の存在確認、無ければinsert
//			if (dailyReportForm.getStatus() == null) {
//				dailyReportMapper.insertDailyReport(dailyReportForm);
//			}
//			//日報詳細リストの更新処理
//			for (DailyReportDetailForm dailyReportDetailForm : dailyReportForm.getDailyReportDetailForm()) {
//
//				//日報の削除または更新
//				if (dailyReportDetailForm.getDailyReportDetailId() == null
//						&& dailyReportDetailForm.getDailyReportDetailTime() != null
//						&& dailyReportDetailForm.getContent() != null) {
//
//					dailyReportForm.setUserId(dailyReportDetailForm.getUserId());
//
//					dailyReportDetailMapper.insertDailyReportDetail(dailyReportDetailForm);
//
//					//UPDATE処理
//				} else if (dailyReportDetailForm.getDailyReportDetailId() != null
//						&& !dailyReportDetailForm.getContent().isEmpty()
//						&& dailyReportDetailForm.getDailyReportDetailDate() != null) {
//
//					dailyReportForm.setUserId(dailyReportDetailForm.getUserId());
//
//					dailyReportDetailMapper.updateDailyReportDetail(dailyReportDetailForm);
//
//					//DELETE処理
//				} else if (dailyReportDetailForm.getDailyReportDetailId() != null
//						&& dailyReportDetailForm.getContent().isEmpty()
//						&& dailyReportDetailForm.getDailyReportDetailTime() == null) {
//
//					dailyReportDetailMapper.deleteDailyReportDetail(dailyReportDetailForm.getDailyReportDetailId());
//				}
//			}
//
//			//最後に日報詳細を取得し空なら日報を削除。それ以外の場合は更新（内容に変更はない）。
//			if (getDailyReportDetail(dailyReportForm.getUserId(), dailyReportForm.getDailyReportDate()).isEmpty()) {
//
//				dailyReportMapper.deleteDailyReport(dailyReportForm.getDailyReportId());
//			} else {
//				dailyReportMapper.updateDailyReport(dailyReportForm);
//			}
//
//			//更新用（マネージャーが承認済みにするときに使う）
//			//dailyReportMapper.updateDailyReport(dailyReportForm);
//			
//			//日報登録をしてからf5を押したときに出るエクセプション
//		} catch (DuplicateKeyException e) {
//
//		}
//	}
//	
	/////////////////////////////////
	
	/**
	 * 日報の更新を行います。日報が存在しない場合は INSERT し、
	 * 各日報詳細の内容に応じて、INSERT・UPDATE・DELETEを行います。
	 * 最後に、日報を削除するか更新するかを判断します。
	 *
	 * @param dailyReportForm 日報フォームオブジェクト。
	 */
	public void updateDailyReportDetail(DailyReportForm dailyReportForm) {
	    try {
	        // 日報の存在確認、無ければ挿入
	        insertIfNotData(dailyReportForm);

	        // 日報詳細リストの更新処理
	        for (DailyReportDetailForm dailyReportDetailForm : dailyReportForm.getDailyReportDetailForm()) {
	            if(isCondition(dailyReportDetailForm)) {
	            	dailyReportDetailMapper.upsert(dailyReportDetailForm);
	            }
	            if(isDeleteCondition(dailyReportDetailForm)) {
	            	dailyReportDetailMapper.deleteDailyReportDetail(dailyReportDetailForm.getDailyReportDetailId());
	            }
	        }
	        // 最後に日報を削除または更新
	        List<DailyReportDetailForm> a = dailyReportForm.getDailyReportDetailForm();
	        
	        DailyReportDetailForm b = a.get(0);
	       deleteCheck(dailyReportForm, b);
	    } catch (DuplicateKeyException e) {
	    }
	}

	/**
	 * UPSERTの条件を満たすかどうかをチェックします。
	 */
	private boolean isCondition(DailyReportDetailForm dailyReportdetailForm) {
	    return dailyReportdetailForm.getDailyReportDetailTime() != null
	    		&& !dailyReportdetailForm.getContent().isEmpty(); 
	}
	
	/**
	 * 日報の存在確認を行い、無ければ INSERT します。
	 *
	 * @param dailyReportForm 日報フォームオブジェクト。
	 */
	private void insertIfNotData(DailyReportForm dailyReportForm) {
	    if (dailyReportForm.getStatus() == null) {
	        dailyReportMapper.insertDailyReport(dailyReportForm);
	    }
	}

	/**
	 * DELETEの条件を満たすかどうかをチェックします。
	 */
	private boolean isDeleteCondition(DailyReportDetailForm dailyReportDetailForm) {
	    return dailyReportDetailForm.getDailyReportDetailId() != null
	    		&& dailyReportDetailForm.getContent().isEmpty()
	    		&& dailyReportDetailForm.getDailyReportDetailTime() == null;
	}

	/**
	 * 更新処理の終了後に日報が日報詳細を所持しているか確認します。
	 * 所持していなければ、日報を削除します。
	 *
	 * @param dailyReportForm 日報フォームオブジェクト。
	 */
	private void deleteCheck(DailyReportForm dailyReportForm, DailyReportDetailForm dailyReportDetailForm) {

		if (0 == dailyReportMapper.check(dailyReportDetailForm.getUserId(), dailyReportDetailForm.getDailyReportDetailDate())){
			dailyReportMapper.deleteDailyReport(dailyReportForm.getDailyReportId());
		} else {
			dailyReportMapper.updateDailyReport(dailyReportForm);
		}
	}
//
//	getDailyReportDetail(dailyReportForm.getUserId(), dailyReportForm.getDailyReportDate()).isEmpty()) {
//			dailyReportMapper.deleteDailyReport(dailyReportForm.getDailyReportId());
	
//	if (getDailyReportDetail(dailyReportForm.getUserId(), dailyReportForm.getDailyReportDate()).isEmpty()) {
//		
//						dailyReportMapper.deleteDailyReport(dailyReportForm.getDailyReportId());
//					} else {
//						dailyReportMapper.updateDailyReport(dailyReportForm);
//					}
	
	
	/**
	 * 日報詳細の処理を行います（INSERT、UPDATE、DELETE）。
	 *
	 * @param detailForm 日報詳細フォームオブジェクト。
	 */
//	private void processDailyReportDetail(DailyReportDetailForm detailForm) {
//	    if (isInsertCondition(detailForm)) {
//	        dailyReportDetailMapper.insertDailyReportDetail(detailForm);
//	    } else if (isUpdateCondition(detailForm)) {
//	        dailyReportDetailMapper.updateDailyReportDetail(detailForm);
//	    } else if (isDeleteCondition(detailForm)) {
//	        dailyReportDetailMapper.deleteDailyReportDetail(detailForm.getDailyReportDetailId());
//	    }
//	}

	

	/**
	 * UPDATEの条件を満たすかどうかをチェックします。
	 */
//	private boolean isUpdateCondition(DailyReportDetailForm detailForm) {
//	    return detailForm.getDailyReportDetailId() != null
//	    		&& !detailForm.getContent().isEmpty()
//	    		&& detailForm.getDailyReportDetailDate() != null;
//	}
	
	
	
	/**
     * 日付を指定されたフォーマットで文字列に変換するメソッド
     *
     * @param date フォーマットする日付
     * @param pattern フォーマットのパターン（例："yyyy-MM-dd"）
     * @return フォーマット済みの日付文字列
     */
    public String formatLocalDate(LocalDate date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }    
}
