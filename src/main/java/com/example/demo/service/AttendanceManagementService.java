package com.example.demo.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Factory.AttendanceFactory;
import com.example.demo.helper.DateHelper;
import com.example.demo.mapper.AttendanceSearchMapper;
import com.example.demo.mapper.MonthlyAttendanceReqMapper;
import com.example.demo.model.Attendance;
import com.example.demo.model.AttendanceFormList;
import com.example.demo.model.MonthlyAttendanceReq;
import com.example.demo.model.Users;
import com.example.demo.validation.AttendanceValidation;
import com.google.api.services.calendar.model.Events;

@Service
public class AttendanceManagementService {

  private final AttendanceSearchMapper attendanceSearchMapper;
  private final AttendanceValidation attendanceValidation; 
  private final AttendanceFactory attendanceFactory;
  private final ModelService modelService;
  private final DateHelper dateHelper;
  private final MonthlyAttendanceReqMapper monthlyAttendanceReqMapper;
  
  private final List<Integer> workDay = Arrays.asList(0, 3, 6, 7, 8, 10);
  private final List<Integer> holiday = Arrays.asList(1, 2, 4, 5, 9, 11);
  
  public AttendanceManagementService(AttendanceSearchMapper attendanceSearchMapper,AttendanceValidation attendanceValidation,
		  AttendanceFactory attendanceFactory,ModelService modelService,DateHelper dateHelper,MonthlyAttendanceReqMapper monthlyAttendanceReqMapper){
	  this.attendanceSearchMapper = attendanceSearchMapper;
	  this.attendanceValidation = attendanceValidation;
	  this.attendanceFactory = attendanceFactory;
	  this.modelService = modelService;
	  this.dateHelper = dateHelper;
	  this.monthlyAttendanceReqMapper = monthlyAttendanceReqMapper;
  }
	//勤怠表の取得 
	public List<Attendance> attendanceSearchListUp(Integer userId, Integer years, Integer month,Optional<Events> events) {
		
		//年月から最終月日を算出
		int monthDays = dateHelper.getMonthDays(years,month);
		List<Attendance> emptyAttendanceList = null;

		//空の勤怠表を生成
		if(events.isPresent()) {
		emptyAttendanceList =attendanceFactory.emptyAttendanceCreate(monthDays, years, month,events.get());
		} else{
		emptyAttendanceList =attendanceFactory.emptyAttendanceCreate(monthDays, years, month);
		}
		
		//DBから勤怠表を取得
		LocalDate firstDayMonth = LocalDate.of(years, month, 1);
		LocalDate lastDayMonth = firstDayMonth.with(TemporalAdjusters.lastDayOfMonth());
		
		List<Attendance> dbAttendanceList = attendanceSearchMapper.selectByYearMonth(userId, firstDayMonth, lastDayMonth);
		List<Attendance> UpdatedDbAttendanceList = attendanceFactory.dbAttendanceSetYear(dbAttendanceList);
		
		//空の勤怠表とDBの勤怠表を統合
		List<Attendance> mergeAttendanceList = attendanceFactory.putTogetherAttendanceList(emptyAttendanceList, UpdatedDbAttendanceList, monthDays);
		
		return mergeAttendanceList;
	}
	
	//勤怠詳細と勤怠データをそれぞれ取得。モデルに追加
	public AttendanceFormList getAttendanceFormAndAddModel(Model model,Integer userId,Integer year,Integer month,String stringYearsMonth,Optional<Events> events) {
		List<Attendance> attendance = attendanceSearchListUp(userId, year, month, events);
		//検索ボタン時attendanceFormListに詰めなおし
		AttendanceFormList attendanceFormList = attendanceFactory.setInAttendance(attendance, year, month,stringYearsMonth);
		modelService.addAttendanceFormList(model,attendanceFormList);
		return attendanceFormList;
	}
	
	//特定の年月の勤怠取得(勤怠訂正用)
	public List<MonthlyAttendanceReq> selectHasChangeReq(String targetYearMonth) {
		//LocalDate型に変更
		LocalDate dateTargetYearMonth = dateHelper.convertStringToLocalDate(targetYearMonth);
		//変更依頼ステータスが１の勤怠表呼び出し
		List<MonthlyAttendanceReq> monthlyAttendanceReq = monthlyAttendanceReqMapper.selectHasChangeReq(dateTargetYearMonth);
		//yearとmonthがそれぞれ必要になるので作成
		YearMonth yearMonth = YearMonth.parse(targetYearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
		 // 年と月を取得
	    int year = yearMonth.getYear();
	    int month = yearMonth.getMonthValue();
		
	    java.sql.Date sqlDate = java.sql.Date.valueOf(dateTargetYearMonth);
		
		//以下for文でstringYearsMonthとyear,monthの値をそれぞれMonthlyAttenanceReqの型にセット
		for (MonthlyAttendanceReq req : monthlyAttendanceReq) {
			// targetYearMonthを設定
			req.setTargetYearMonth(sqlDate);
			// 年と月を設定
			req.setYears(year);
			req.setMonth(month);//整数型で月を取得
		}
		return monthlyAttendanceReq;
	}
	
	//勤怠取得後モデルに詰める
	public void getHasChangeReqAndAddModel(Model model,String stringYearsMonth) {
		List<MonthlyAttendanceReq> hasChangeReq = selectHasChangeReq(stringYearsMonth);
		modelService.addHasChangeReq(model,hasChangeReq);
	}
	
	//特定ユーザーだけ表示するフィルター
	public void filteringHasChangeReq(Model model,Integer userId, String stringYearsMonth) {
		List<MonthlyAttendanceReq> hasChangeReq = selectHasChangeReq(stringYearsMonth);
		//hasChangeReqは複数の要素を持つので、一つに絞るためのフィルターをかける
		List<MonthlyAttendanceReq> currentChangeReq = hasChangeReq.stream()
				.filter(req -> userId.equals(req.getUserId()))
				.collect(Collectors.toList());
		modelService.addCurrentChangeReq(model,currentChangeReq);
	}
	
	//勤怠承認申請者取得ロジック
	public void selectApprovalPending(Model model) {
		//1は承認待ち
		List<MonthlyAttendanceReq> monthlyAttendanceReq = monthlyAttendanceReqMapper.selectApprovalPending(1);
		
		for (int i = 0; i < monthlyAttendanceReq.size(); i++) {
			Date targetYearMonth = monthlyAttendanceReq.get(i).getTargetYearMonth();
			LocalDate localDate = dateHelper.convertToLocalDate(targetYearMonth);
			Integer intYear = localDate.getYear();
			Integer intMonth = localDate.getMonthValue();
			
			// date型の年と月をinteger型で取得する
			monthlyAttendanceReq.get(i).setYears(intYear);
			monthlyAttendanceReq.get(i).setMonth(intMonth);
		}
		modelService.addApprovalPending(model,monthlyAttendanceReq);
	}
	
	//approvalPending押下時、選択勤怠取得処理
	public void createAndAddAttendanceFormList(Model model,List<Attendance> attendance,Integer userId) {
		//初期化
		AttendanceFormList attendanceFormList = new AttendanceFormList();
		ArrayList<Attendance> attendanceList = new ArrayList<Attendance>();
		//勤怠データ(AttendanceList)と勤怠詳細(attendance)をそれぞれに追加する
		attendanceFormList.setAttendanceList(attendanceList);
		attendanceList.addAll(attendance);

		//★	approvalUserIdをセットするfor文を回す
		for (int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {
			attendanceFormList.getAttendanceList().get(i).setUserId(userId);
		}
		modelService.addAttendanceFormList(model,attendanceFormList);
	
	}
	
//承認申請のステータス確認
	public Model submissionStatusCheck(AttendanceFormList attendanceFormList, Integer userId, Model model,Users users) {
		Date targetYearMonth = getFirstAttendanceDate(attendanceFormList);
		//ステータスチェックを呼び出し、ステータスの記録があればそれを、なければ０を割り当てる処理
		MonthlyAttendanceReq statusCheck = monthlyAttendanceReqMapper.selectTargetYearMonthStatus(targetYearMonth,userId);
		if (statusCheck != null) {
			Integer nowStatus = statusCheck.getStatus();
			users.setStatus(nowStatus);
		} else {
			users.setStatus(0);
		}
		modelService.addUsers(model,users);
		return model;
	}
	
	
	
	//承認　却下分岐処理
	public void handleMonthlyAttendanceReq(AttendanceFormList attendanceFormList,RedirectAttributes redirectAttributes,String stringYearsMonth,String rejectionReason) {
		modelService.stringYearsMonth(redirectAttributes,stringYearsMonth);
		//1つめのelse句以外ほぼ起きることはない、状況を見て消去可,システム上前半句は起きえないはず。二個目のifとは話が別。
		if (attendanceFormList.getAttendanceList() == null) {
			modelService.choiceUsers(redirectAttributes);
		} else {
			if(rejectionReason == null||rejectionReason.isEmpty()) {	
				//承認更新文のための処理とその実行
				changeApprovalMonthlyAttendanceReq(attendanceFormList);
				modelService.changeMonthlyAttendanceReqApproval(redirectAttributes);
			} else {
				//却下更新分のための処理とその実行
				changeRejectionMonthlyAttendanceReq(attendanceFormList, rejectionReason);
				modelService.changeMonthlyAttendanceReqReject(redirectAttributes);
			}	
		}
	}
	
	//特定のユーザーの承認申請で承認済みを取得 カレンダー（一般）用
	public void selectApproval(Model model,Integer userId) {
	  // MonthlyAttendanceReqのリストを取得
	List<MonthlyAttendanceReq> targetList = monthlyAttendanceReqMapper.selectApproved(userId);       
	String lastMonth = dateHelper.lastYearsMonth();
	
	// 先月の月のMonthlyAttendanceReqオブジェクトを作成
	MonthlyAttendanceReq lastMonthReq = new MonthlyAttendanceReq();
	lastMonthReq.setStringTargetYearMonth(lastMonth);
	
	// リストに追加
	targetList.add(lastMonthReq);
	
	modelService.addApprovedMonths(model,targetList);
	}

	
	//承認申請ボタン押下(初めての申請の際)内部処理
	public void serviceForMonthlyAttendanceReqCreate(MonthlyAttendanceReq monthlyAttendanceReq, AttendanceFormList attendanceFormList) {
		//申請月の１日をtargetYearMonthにいれる
		String inputDate =  dateHelper.getInputDate(attendanceFormList);
		monthlyAttendanceReq.setTargetYearMonth(java.sql.Date.valueOf(inputDate));
		//今日の日付をmonthlyAttendanceReqDateに入れる
		Date date = new Date();
		monthlyAttendanceReq.setMonthlyAttendanceReqDate(date);
		//statusに１を入れる(1は承認待ち)
		monthlyAttendanceReq.setStatus(1);
		monthlyAttendanceReqMapper.insertMonthlyAttendanceReq(monthlyAttendanceReq);
	}
	
	//承認申請ボタン押下(却下されたやつの更新)内部処理
	public void updateMonthlyAttendanceReq(MonthlyAttendanceReq monthlyAttendanceReq) {
		//今日の日付をmonthlyAttendanceReqDateに入れる
		Date date = new Date();
		monthlyAttendanceReq.setMonthlyAttendanceReqDate(date);
		monthlyAttendanceReqMapper.updateMonthlyAttendanceReq(monthlyAttendanceReq);
	}
	
	//月次勤怠申請テーブルのステータスの更新処理の分岐確認メソッド
	public void monthlyAttendanceUpdate(Integer userId, MonthlyAttendanceReq monthlyAttendanceReq,AttendanceFormList attendanceFormList) {
		String inputDate = dateHelper.getInputDate(attendanceFormList);
		attendanceFormList.getAttendanceList().get(0).setAttendanceDate(java.sql.Date.valueOf(inputDate));
		Date targetYearMonth = getFirstAttendanceDate(attendanceFormList);
		MonthlyAttendanceReq monthlyAttendanceCheck = monthlyAttendanceReqMapper.selectTargetYearMonthStatus(targetYearMonth, userId);
		//中身がnull、すなわち同一ユーザーかつ同一月の月次勤怠申請がテーブルにない時の処理。statusはnull
		if (monthlyAttendanceCheck == null) {
			serviceForMonthlyAttendanceReqCreate(monthlyAttendanceReq, attendanceFormList);
			//承認申請が却下または訂正承認されていた際の処理。statusは0か3
		} else if (monthlyAttendanceCheck != null && (monthlyAttendanceCheck.getStatus() == 3 || monthlyAttendanceCheck.getStatus() == 0)) {
			Date firstAttendanceDate = getFirstAttendanceDate(attendanceFormList);
			monthlyAttendanceReq.setTargetYearMonth(firstAttendanceDate);
			updateMonthlyAttendanceReq(monthlyAttendanceReq);
		}
	}	
	  
//勤怠テーブルに登録処理
	@Transactional
	public void attendanceUpsert(AttendanceFormList attendanceFormList,Integer userId) {
		AttendanceFormList updateAttendanceFormEntity = attendanceFactory.updateAttendanceFormCreate(attendanceFormList, userId);

		updateAttendanceFormEntity.getAttendanceList().stream()
				.filter(attendance -> workDay.contains(attendance.getStatus()))
				.filter(attendance -> !attendance.getStartTime().isEmpty())
				.forEach(attendance -> attendanceSearchMapper.upsert(attendance));

		updateAttendanceFormEntity.getAttendanceList().stream()
				.filter(attendance -> holiday.contains(attendance.getStatus()))
				.filter(attendance -> attendance.getStartTime().isEmpty())
				.filter(attendance -> attendance.getEndTime().isEmpty())
				.forEach(attendance -> attendanceSearchMapper.upsert(attendance));
	}

	//勤怠登録画面で承認申請ボタンを有効にするかを決める
	public void requestActivityCheck(AttendanceFormList attendanceFormList) {   

	    for (int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {
	        Attendance attendance = attendanceFormList.getAttendanceList().get(i);
	        String startTime = attendance.getStartTime();
	        String endTime = attendance.getEndTime();
	        Integer status = attendance.getStatus();

	        // 条件1 ステータスがworkDayに含まれており、出勤退勤がnullまたは空文字でない場合
	        if (workDay.contains(status)
	                && startTime != null && !startTime.isEmpty()
	                && endTime != null && !endTime.isEmpty()) {
	            attendanceFormList.setRequestActivityCheck(true);
	        } 
	        // 条件2 ステータスがholidayに含まれており、出勤退勤がnullまたは空文字の場合
	        else if (holiday.contains(status)
	                && (startTime == null || startTime.isEmpty())
	                && (endTime == null || endTime.isEmpty())) {
	            attendanceFormList.setRequestActivityCheck(true);
	        } 
	        // それ以外の場合
	        else {
	            attendanceFormList.setRequestActivityCheck(false);
	            break;
	        }
	    }
	}

	//勤怠登録のエラーチェック
	public void errorCheck(AttendanceFormList attendanceFormList, BindingResult result) {
		attendanceValidation.errorCheck(attendanceFormList, result);
	}
	
	//AttendanceFormList内の要素、List<attendance>の中で最初のattendanceの申請日を取るためのメソッド オブジェ句t
	public Date getFirstAttendanceDate(AttendanceFormList attendanceFormList) {
	    if (attendanceFormList != null && !attendanceFormList.getAttendanceList().isEmpty()) {
	        return attendanceFormList.getAttendanceList().get(0).getAttendanceDate();
	    }
	    return null; // 存在しない場合nullを返す
	}
	
	//上記メソッド同様にuserIdを取るためのメソッド オブジェクト
	public Integer getFirstAttendanceUserId(AttendanceFormList attendanceFormList) {
		if (attendanceFormList != null && !attendanceFormList.getAttendanceList().isEmpty()) {
			return attendanceFormList.getAttendanceList().get(0).getUserId();
		}
		return null;
	}
	
	//承認申請承認・却下分岐ロジック
	public void handleAttendance(RedirectAttributes redirectAttributes, AttendanceFormList attendanceFormList, boolean isApproval) {
	    if (attendanceFormList.getAttendanceList() == null) {
	        modelService.choiceUsers(redirectAttributes);
	    } else {
	        String inputDate =  dateHelper.getInputDate(attendanceFormList);
	        Integer firstUserId =getFirstAttendanceUserId(attendanceFormList);
	        if (isApproval) {
	        	//マネージャ勤怠申請承認文
	            monthlyAttendanceReqMapper.approvalStatus(firstUserId, inputDate);
	            modelService.attendanceApplove(redirectAttributes);
	        } else {
	        	//マネージャ勤怠申請却下文
	        	monthlyAttendanceReqMapper.rejectedStatus(firstUserId, inputDate);
	            modelService.attendanceReject(redirectAttributes);
	        }
	    }
	}

	//月次勤怠訂正依頼の更新文
	public void changeRequestMonthlyAttendanceReq(Integer userId,String changeReason,String stringYearsMonth) {
    //has_change_reqを１に変更する処理のためにLocalDateに変更して処理
		LocalDate targetYearMonth = dateHelper.convertStringToLocalDate(stringYearsMonth);
		if (targetYearMonth != null) {
			monthlyAttendanceReqMapper.changeRequestMonthlyAttendanceReq(userId, targetYearMonth, changeReason);
		}
	}
	
	//月次勤怠訂正の承認更新文
	public void changeApprovalMonthlyAttendanceReq(AttendanceFormList attendanceFormList) {
		String inputDate = dateHelper.getInputDate(attendanceFormList);
		Integer firstUserId=getFirstAttendanceUserId(attendanceFormList);
		monthlyAttendanceReqMapper.changeApprovalMonthlyAttendanceReq(firstUserId, inputDate);
	}
	
	//月次勤怠訂正の却下更新文
	public void changeRejectionMonthlyAttendanceReq(AttendanceFormList attendanceFormList,String rejectionReason) {
		String inputDate =   dateHelper.getInputDate(attendanceFormList);
		Integer firstUserId=getFirstAttendanceUserId(attendanceFormList);
		monthlyAttendanceReqMapper.changeRejectionMonthlyAttendanceReq(firstUserId, inputDate, rejectionReason);
	}
	//*********************************アラート*************************
	//menuでの却下確認ロジック
		public void changeRejectedMonthlyAttendanceReq(Integer userId) {
			monthlyAttendanceReqMapper.changeRejectedMonthlyAttendanceReq(userId);
		}
		
	//一般ユーザー用勤怠修正系アラート表示機能
	public Model checkMonthlyAttendanceReqStatus(Integer userId,Model model) {
		List<Integer> checkGeneralUsersHasChangeReq = new ArrayList<>();
		
		//訂正申請がされていた（hasChangeReq=0）のステータスを入手
		checkGeneralUsersHasChangeReq = monthlyAttendanceReqMapper.selectMonthlyAttendanceReqHasChangeReq(userId);
		//もし上記があれば
		if(!checkGeneralUsersHasChangeReq.isEmpty()) {
			if(checkGeneralUsersHasChangeReq.contains(0)) {
				//勤怠修正申請許可
				modelService.monthlyAttendanceReqApproved(model);
			}
			if(checkGeneralUsersHasChangeReq.contains(2)) {
				//勤怠修正申請却下
				String rejectionReason = monthlyAttendanceReqMapper.selectRejectionReason(userId);
				modelService.combinedMessageAndReason(model,rejectionReason);
			}
			return model;
		}
		return null;
	}
	
	//マネージャー勤怠訂正申請受領アラート
	public Model selectMonthlyAttendanceReqAnyHasChangeReq(Model model) {
		Integer existHasChangeReq = monthlyAttendanceReqMapper.selectMonthlyAttendanceReqAnyHasChangeReq();
		if(existHasChangeReq == 1) {
			return modelService.monthlyAttendanceReqArrival(model);
		}
		return null;
	}
	
	
	//昨日の勤怠登録状況を取得・アラート用
	public Model checkYesterdayAttendance(Integer userId, LocalDate yesterday,Model model) {
		Integer checkAttendance = attendanceSearchMapper.selectYesterdayCheck(userId, yesterday);
		if(checkAttendance == 0) {
			return modelService.CheckAttendance(model);
		}
		return null;
	}
	//勤怠申請却下アラート確認処理
	public Model checkForAlertStatusThree(Integer userId,Date date,Model model) {
		 MonthlyAttendanceReq monthlyAttendanceReq = monthlyAttendanceReqMapper.selectTargetYearMonthStatus(date, userId);
		 if (monthlyAttendanceReq != null && monthlyAttendanceReq.getStatus() == 3) {
				return modelService.monthlyAttendanceStatusIsThree(model);
			}
		return null;
	}
	//勤怠承認申請受領アラート確認処理
	public Model checkForAlertMonthlyAttendanceReq(Date date, Model model) {
		 Integer alertFlag = monthlyAttendanceReqMapper.selectMonthlyAttendanceReq(date);
		 if(alertFlag == 1) {
			 return modelService.monthlyAttendanceIsSentInsertModel(model);
		 }
		 return null;
	}
//	public List<Attendance> getSelectedAttendance(Model model,String targetYearMonth,Integer userId) {
//		Integer years = dateHelper.parseDate(targetYearMonth)[0];
//		Integer month = dateHelper.parseDate(targetYearMonth)[1];
//		
//		//祝日表示用のカレンダー取得
//		Events events = googleCalendarService.getHolidaysEvents(years, month);
//		//勤怠詳細と勤怠データをそれぞれ取得。モデルに追加
//		List<Attendance> attendance = attendanceSearchListUp(userId, years, month,Optional.of(events));
//		AttendanceFormList attendanceFormList = setInAttendance(attendance, years, month,
//				targetYearMonth);
//		modelService.addAttendanceFormList(model,attendanceFormList);
//		//承認申請ボタンのOnOff切り替え設定
//		requestActivityCheck(attendanceFormList);
//		googleCalendarService.listEvents(model,events);
//		return attendance;
//	}
	
//	public Boolean serviceForUpdateButton(Model model,AttendanceFormList attendanceFormList,BindingResult result,Users users) {
//		//承認申請ボタンのONOffきりかえと書式のエラーチェック
//		requestActivityCheck(attendanceFormList);
//		errorCheck(attendanceFormList, result);
//
//		if (result.hasErrors()) {
//			System.out.println("errors"+ result.getAllErrors());
//			return true;
//		}
//		//新しいデータに修正してデータベースに登録
//		AttendanceFormList updateAttendanceFormEntity = updateAttendanceFormCreate(attendanceFormList, users.getUserId());
//		attendanceUpsert(updateAttendanceFormEntity);
//		
//		googleCalendarService.getListHolidays(model,attendanceFormList.getStringYearsMonth());
//		
//		modelService.addAttendanceFormList(model,attendanceFormList);
//		modelService.addAttendanceMessage(model);
//		return false;
//	}
}



//勤怠テーブルのデータを物理削除
//public void attendanceDelete(AttendanceFormList attendanceFormList) {
//	attendanceSearchMapper.deleteByAttendanceOfMonth(attendanceFormList.getAttendanceList().get(0).getUserId(),
//			attendanceFormList.getAttendanceList().get(0).getAttendanceDate(),
//			attendanceFormList.getAttendanceList().get(attendanceFormList.getAttendanceList().size() - 1).getAttendanceDate());
//}