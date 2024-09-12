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
	 * 日報画面の初期表示設定を行います。
	 *  1.DBから日報取得し、nullならインスタンスを新規作成して代入します。
	 *  2.取得したor新規作成した日報から、日報詳細を取得or新規作成します。
	 *  3.日報に日報詳細をセット
	 *  4.日報にログインユーザーのIDをセット
	 *  5.日報をリターン
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
		
		DailyReportForm dailyReportForm = getDailyReport(userId, calendarDate); 
		
		List<DailyReportDetailForm> dailyReportDetailForm = getDailyReportDetail(userId, calendarDate);
	    
	    dailyReportForm.setDailyReportDetailForm(dailyReportDetailForm);
	    dailyReportForm.setUserId(userId);
	    return dailyReportForm;
    }
	
	/**
	 * 日報を取得するメソッドです。
	 * 日報が存在しない場合は、新しい日報を作成して返します。
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
	 * 日報から日報詳細を取得するメソッドです。
	 *  1. 日報が存在しない場合は新しい日報詳細を作成して返します。
	 *  2. 日報入力欄を合計10行分になるまで作成します。
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
		while (dailyReportDetailForm.size() < 10) {
	    	DailyReportDetailForm emptyDetailForm = new DailyReportDetailForm();
	    	emptyDetailForm.setUserId(userId);
	    	emptyDetailForm.setDailyReportDetailDate(calendarDate);
	    	dailyReportDetailForm.add(emptyDetailForm);
	    }
		return dailyReportDetailForm;
	}
	
	/**
	 * 日報の更新処理を行うメソッドです。
	 * 
	 *  1.DBの日報の存在をチェックします。無ければDBにINSETします。
	 *  2.日報から日報詳細を1行ずつ取り出し、追加or更新or削除を行います。
	 *  2.日報詳細を追加or更新できる条件にあれば、追加or更新します。
	 *  3.日報詳細を削除する条件にあれば、削除します。
	 *  4.日報の中の日報詳細に入力データが無い場合、日報を削除します。
	 *  
	 * @param dailyReportForm 日報フォームオブジェクト。
	 */
	public void updateDailyReportDetail(DailyReportForm dailyReportForm) {
	    try {
	         //日報の存在確認
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
	        
	        List<DailyReportDetailForm> detailList = dailyReportForm.getDailyReportDetailForm();
	        
	        //日報詳細リストの１番目の内容取り出して、削除or更新メソッドへ渡す
	        DailyReportDetailForm firstDetail = detailList.get(0);
	        deletingUpdate(dailyReportForm, firstDetail);
	       
	    } catch (DuplicateKeyException e) {
	    }
	}

	/**
	 * DBの日報の存在確認を行い、無ければ INSERT します。
	 *
	 * @param dailyReportForm 日報フォームオブジェクト。
	 */
	private void insertIfNotData(DailyReportForm dailyReportForm) {
		
	    if (dailyReportForm.getStatus() == null) {
	        dailyReportMapper.insertDailyReport(dailyReportForm);
	    }
	}
	
	/**
	 * 追加or更新の条件を満たすかどうかをチェックします。
	 * 
	 * 次の2つを満たせば「true」を返します
	 *  1.日報詳細に作業時間が入力されている
	 *  2.日報詳細に作業内容が入力されている
	 */
	private boolean isCondition(DailyReportDetailForm dailyReportdetailForm) {
		
	    return dailyReportdetailForm.getDailyReportDetailTime() != null
	    		&& !dailyReportdetailForm.getContent().isEmpty(); 
	}
	
	/**
	 * DELETEの条件を満たすかチェックします。
	 * 次の3つを満たせば「true」を返します
	 *  1.日報詳細が既にDBに存在している（前に登録したことがある）
	 *  2.日報詳細の作業内容が入力されていない
	 *  3.日報詳細の作業時間が入力されていない
	 */
	private boolean isDeleteCondition(DailyReportDetailForm dailyReportDetailForm) {
		
	    return dailyReportDetailForm.getDailyReportDetailId() != null
	    		&& dailyReportDetailForm.getContent().isEmpty()
	    		&& dailyReportDetailForm.getDailyReportDetailTime() == null;
	}

	/**
	 * 更新処理の終了後に日報が持っている日報詳細の入力内容を確認します。
	 * データが無ければ、日報を削除します。
	 * 「check」メソッドの結果が「0」なら日報が持つ日報詳細に何もデータがないので削除します
	 * 
	 * @param dailyReportForm 日報フォームオブジェクト。
	 */
	private void deletingUpdate(DailyReportForm dailyReportForm, DailyReportDetailForm dailyReportDetailForm) {
		
		if (0 == dailyReportMapper.check(dailyReportDetailForm.getUserId(), dailyReportDetailForm.getDailyReportDetailDate())){
			dailyReportMapper.deleteDailyReport(dailyReportForm.getDailyReportId());
		} else {
			dailyReportMapper.updateDailyReport(dailyReportForm);
		}
	}

	/**
     * 日付を指定したフォーマットの文字列に変換するメソッドです。
     *
     * @param date フォーマットする日付
     * @param pattern フォーマットのパターン（例："yyyy-MM-dd"）
     * @return フォーマット済みの日付文字列
     */
    public String formatLocalDate(LocalDate date, String pattern) {
    	
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        
        return date.format(formatter);
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
}


///**
// * 日報の詳細を更新します。
// *
// * @param dailyReportForm 日報フォームオブジェクト
// */
//public void updateDailyReportDetail(DailyReportForm dailyReportForm) {
//	try {
//		//日報の存在確認、無ければinsert
//		if (dailyReportForm.getStatus() == null) {
//			dailyReportMapper.insertDailyReport(dailyReportForm);
//		}
//		//日報詳細リストの更新処理
//		for (DailyReportDetailForm dailyReportDetailForm : dailyReportForm.getDailyReportDetailForm()) {
//
//			//日報の削除または更新
//			if (dailyReportDetailForm.getDailyReportDetailId() == null
//					&& dailyReportDetailForm.getDailyReportDetailTime() != null
//					&& dailyReportDetailForm.getContent() != null) {
//
//				dailyReportForm.setUserId(dailyReportDetailForm.getUserId());
//
//				dailyReportDetailMapper.insertDailyReportDetail(dailyReportDetailForm);
//
//				//UPDATE処理
//			} else if (dailyReportDetailForm.getDailyReportDetailId() != null
//					&& !dailyReportDetailForm.getContent().isEmpty()
//					&& dailyReportDetailForm.getDailyReportDetailDate() != null) {
//
//				dailyReportForm.setUserId(dailyReportDetailForm.getUserId());
//
//				dailyReportDetailMapper.updateDailyReportDetail(dailyReportDetailForm);
//
//				//DELETE処理
//			} else if (dailyReportDetailForm.getDailyReportDetailId() != null
//					&& dailyReportDetailForm.getContent().isEmpty()
//					&& dailyReportDetailForm.getDailyReportDetailTime() == null) {
//
//				dailyReportDetailMapper.deleteDailyReportDetail(dailyReportDetailForm.getDailyReportDetailId());
//			}
//		}
//
//		//最後に日報詳細を取得し空なら日報を削除。それ以外の場合は更新（内容に変更はない）。
//		if (getDailyReportDetail(dailyReportForm.getUserId(), dailyReportForm.getDailyReportDate()).isEmpty()) {
//
//			dailyReportMapper.deleteDailyReport(dailyReportForm.getDailyReportId());
//		} else {
//			dailyReportMapper.updateDailyReport(dailyReportForm);
//		}
//
//		//更新用（マネージャーが承認済みにするときに使う）
//		//dailyReportMapper.updateDailyReport(dailyReportForm);
//		
//		//日報登録をしてからf5を押したときに出るエクセプション
//	} catch (DuplicateKeyException e) {
//
//	}
//}
//
/////////////////////////////////

//
//getDailyReportDetail(dailyReportForm.getUserId(), dailyReportForm.getDailyReportDate()).isEmpty()) {
//		dailyReportMapper.deleteDailyReport(dailyReportForm.getDailyReportId());

//if (getDailyReportDetail(dailyReportForm.getUserId(), dailyReportForm.getDailyReportDate()).isEmpty()) {
//	
//					dailyReportMapper.deleteDailyReport(dailyReportForm.getDailyReportId());
//				} else {
//					dailyReportMapper.updateDailyReport(dailyReportForm);
//				}


/**
 * 日報詳細の処理を行います（INSERT、UPDATE、DELETE）。
 *
 * @param detailForm 日報詳細フォームオブジェクト。
 */
//private void processDailyReportDetail(DailyReportDetailForm detailForm) {
//    if (isInsertCondition(detailForm)) {
//        dailyReportDetailMapper.insertDailyReportDetail(detailForm);
//    } else if (isUpdateCondition(detailForm)) {
//        dailyReportDetailMapper.updateDailyReportDetail(detailForm);
//    } else if (isDeleteCondition(detailForm)) {
//        dailyReportDetailMapper.deleteDailyReportDetail(detailForm.getDailyReportDetailId());
//    }
//}

/**
 * UPDATEの条件を満たすかどうかをチェックします。
 */
//private boolean isUpdateCondition(DailyReportDetailForm detailForm) {
//    return detailForm.getDailyReportDetailId() != null
//    		&& !detailForm.getContent().isEmpty()
//    		&& detailForm.getDailyReportDetailDate() != null;
//}



