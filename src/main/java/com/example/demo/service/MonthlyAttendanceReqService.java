package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.helper.DateHelper;
import com.example.demo.model.Attendance;
import com.example.demo.model.AttendanceFormList;
import com.example.demo.model.MonthlyAttendanceReq;
import com.example.demo.model.Users;
import com.google.api.services.calendar.model.Events;

@Service
public class MonthlyAttendanceReqService {

	private final AttendanceManagementService attendanceManagementService;
	
	private final ModelService modelService;
	
	private final DateHelper dateHelper;
	
	private final CommonActivityService commonActivityService;
	
	private final GoogleCalendarService googleCalendarService;

	MonthlyAttendanceReqService(AttendanceManagementService attendanceManagementService,ModelService modelService,DateHelper dateHelper,CommonActivityService commonActivityService,GoogleCalendarService googleCalendarService) {
		this.attendanceManagementService = attendanceManagementService;
		this.modelService = modelService;
		this.dateHelper = dateHelper;
		this.commonActivityService = commonActivityService;
		this.googleCalendarService = googleCalendarService;
	}
	
	//勤怠登録画面遷移時の処理
	public void startAttendanceManagement(Model model,Users users) {
		if (users.getRole().equalsIgnoreCase("Manager")) {
			attendanceManagementService.selectApprovalPending(model);	//ManagerならapprovalPendingデータを取得しモデルに追加
		}else {
			String stringYearsMonth = dateHelper.yearsMonth();
			getSelectedAttendance(model,stringYearsMonth,users.getUserId(),users);	//Managerでないなら現在の年月を取得して、それをもとに自身の勤怠情報を取得する。
		}
			
	}

	//選択した勤怠取得ロジック
	public void getSelectedAttendance(Model model,String targetYearMonth,Integer userId,Users users) {
		int[] yearMonth = dateHelper.parseDate(targetYearMonth);//yearMonth[0]が年、[1]が月
		
		//祝日表示用のカレンダー取得
		Events events = googleCalendarService.getHolidaysEvents(yearMonth[0], yearMonth[1]);
		//勤怠詳細と勤怠データをそれぞれ取得。モデルに追加
		AttendanceFormList attendanceFormList = attendanceManagementService.getAttendanceFormAndAddModel(model,userId,yearMonth[0], yearMonth[1], targetYearMonth, Optional.of(events));
		
//		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, yearMonth[0], yearMonth[1],Optional.of(events));
//		AttendanceFormList attendanceFormList = attendanceManagementService.setInAttendance(attendance, yearMonth[0], yearMonth[1], targetYearMonth);
//		modelService.addAttendanceFormList(model,attendanceFormList);
		
		//月次勤怠テーブルのstatusをユーザー)モデルのstatusに詰める
		attendanceManagementService.submissionStatusCheck(attendanceFormList, userId, model,users);
		//承認申請ボタンのOnOff切り替え設定
		attendanceManagementService.requestActivityCheck(attendanceFormList);
		googleCalendarService.listEvents(model,events);
	}
	
	//登録ボタンのロジック
	public void serviceForUpdateButton(Model model,AttendanceFormList attendanceFormList,BindingResult result,Users users) {
		googleCalendarService.getListHolidays(model,attendanceFormList.getStringYearsMonth());
		//承認申請ボタンのONOffきりかえと書式のエラーチェック
		attendanceManagementService.requestActivityCheck(attendanceFormList);
		attendanceManagementService.errorCheck(attendanceFormList, result);

		if (result.hasErrors()) {
			System.out.println("errors"+ result.getAllErrors());
			return;
		}
		//新しいデータに修正してデータベースに登録
		attendanceManagementService.attendanceUpsert(attendanceFormList,users.getUserId());
		
		modelService.addAttendanceFormList(model,attendanceFormList);
		modelService.addAttendanceMessage(model);
	}
	//承認申請ボタン押下時処理
	public void getForMonthlyAttendanceReqCreate(Model model,AttendanceFormList attendanceFormList,MonthlyAttendanceReq monthlyAttendanceReq,Users users) {
		//月初日、Listから取得したUserId、勤怠データ、勤怠詳細をもとに勤怠詳細の作成・更新処理
		attendanceManagementService.monthlyAttendanceUpdate(monthlyAttendanceReq.getUserId(),monthlyAttendanceReq, attendanceFormList);
		//ステータスの更新処理
		attendanceManagementService.submissionStatusCheck(attendanceFormList, users.getUserId(), model,users);
		//祝日リスト取得
		googleCalendarService.getListHolidays(model,attendanceFormList.getStringYearsMonth());
		
		modelService.addAttendanceFormList(model,attendanceFormList);
		modelService.attendanceSubmit(model);
	}
	
	//マネージャー承認申請者勤怠表示ボタン押下時処理
	public void getForAttendance(Model model,Integer years,Integer month,Integer userId) {
		//勤怠表を取得
		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month, Optional.<Events>empty());
		//勤怠詳細があるときしか動かない approvalPending押下時、選択勤怠取得処理
		if (attendance != null) {
		attendanceManagementService.createAndAddAttendanceFormList(model,attendance,userId);
		}
		//SQLから勤怠申請者一覧表を取得
		attendanceManagementService.selectApprovalPending(model);
		//祝日を取得
		googleCalendarService.getListHolidays(model,years,month);
	}

	//承認ボタン押下処理
	public void getForApproval(RedirectAttributes redirectAttributes, AttendanceFormList attendanceFormList) {
		attendanceManagementService.handleAttendance(redirectAttributes, attendanceFormList, true);
	}
	//却下ボタン押下処理
	public void getForRejected(RedirectAttributes redirectAttributes, AttendanceFormList attendanceFormList) {
		attendanceManagementService.handleAttendance(redirectAttributes, attendanceFormList, false);
	}
	

//*********************************↓↓勤怠訂正↓↓***********************************
	//勤怠訂正開いたときの処理
	public void monthlyAttendanceReqStart(Model model,HttpSession session) {
		Users users = commonActivityService.getCommonInfoWithUsers(model,session,null);
		String stringYearsMonth = (String) model.asMap().get("stringYearsMonth");
		
	    if (stringYearsMonth == null) {
	        stringYearsMonth = dateHelper.lastYearsMonth();
	    }
	    
		if (users.getRole().equalsIgnoreCase("Manager")) {
			attendanceManagementService.getHasChangeReqAndAddModel(model,stringYearsMonth);
			modelService.addStringYearsMonth(model,stringYearsMonth);
		} else {
			serviceAttendanceSearch(model,session,users.getUserId(), stringYearsMonth,users);
		}
				
	}
	
	//表示ボタン処理
	public void serviceAttendanceSearch(Model model,HttpSession session,Integer userId,String stringYearsMonth,Users users) {
		int[] yearMonth = dateHelper.parseDate(stringYearsMonth);//yearMonth[0]が年、[1]が月
		
		if (!users.getRole().equalsIgnoreCase("Manager")) {
			//yearとmonthに該当する勤怠表を探してAttendanceFormListに詰める処理
			AttendanceFormList attendanceFormList = attendanceManagementService.getAttendanceFormAndAddModel(model,userId,yearMonth[0], yearMonth[1],stringYearsMonth, Optional.<Events>empty());
			
//			List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, yearMonth[0], yearMonth[1], Optional.<Events>empty());
//			AttendanceFormList attendanceFormList = attendanceManagementService.setInAttendance(attendance, yearMonth[0], yearMonth[1],stringYearsMonth);
//			modelService.addAttendanceFormList(model,attendanceFormList);
			
			//月次勤怠テーブルのstatusをユーザー)モデルのstatusに詰める
			attendanceManagementService.submissionStatusCheck(attendanceFormList, userId, model, users);
			attendanceManagementService.selectApproval(model,userId);
		} else {
			attendanceManagementService.getHasChangeReqAndAddModel(model,stringYearsMonth);
		}
		//カレンダーを初期化しないために返す
		modelService.addStringYearsMonth(model,stringYearsMonth);
		
		googleCalendarService.getListHolidays(model,yearMonth[0],yearMonth[1]);
	}
	
	//訂正申請ボタン
	public void serviceApproveButton(Model model,HttpSession session,Integer userId,String stringYearsMonth,String changeReason,Users users) {
		
		attendanceManagementService.changeRequestMonthlyAttendanceReq(userId, changeReason, stringYearsMonth);
		//再度同じ月の勤怠表を表示するための処理
		int[] yearMonth = dateHelper.parseDate(stringYearsMonth);//yearMonth[0]が年、[1]が月
		googleCalendarService.getListHolidays(model,yearMonth[0], yearMonth[1]);
		
		AttendanceFormList attendanceFormList = attendanceManagementService.getAttendanceFormAndAddModel(model,userId,yearMonth[0], yearMonth[1],stringYearsMonth, Optional.<Events>empty());
		
//		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, yearMonth[0], yearMonth[1],Optional.<Events>empty());
//		AttendanceFormList attendanceFormList = attendanceManagementService.setInAttendance(attendance, yearMonth[0],  yearMonth[1], stringYearsMonth);
//		modelService.addAttendanceFormList(model,attendanceFormList);
		
		//ステータス変更処理
		attendanceManagementService.submissionStatusCheck(attendanceFormList, userId, model,users);
		
		attendanceManagementService.selectApproval(model,userId);
		
		modelService.sendCorrectionApplication(model);
		
		modelService.addStringYearsMonth(model,stringYearsMonth);
	
	}
	
	//マネージャー用申請者表示ボタン
	public void serviceApprovedAttendance(Model model,HttpSession session,Integer userId,String stringYearsMonth) {
		commonActivityService.getCommonInfo(model,session,null);
		int[] yearMonth = dateHelper.parseDate(stringYearsMonth);//yearMonth[0]が年、[1]が月
		getForAttendance(model, yearMonth[0], yearMonth[1], userId);
		//選択月該当ユーザー
		attendanceManagementService.getHasChangeReqAndAddModel(model,stringYearsMonth);
		//選択した特定ユーザー表記用
		attendanceManagementService.filteringHasChangeReq(model,userId, stringYearsMonth);
		//カレンダー埋める用
		modelService.addStringYearsMonth(model,stringYearsMonth);
	}
	
	
	//訂正承認ボタン
	public void serviceApproval(AttendanceFormList attendanceFormList,RedirectAttributes redirectAttributes,String stringYearsMonth) {
		attendanceManagementService.handleMonthlyAttendanceReq(attendanceFormList,redirectAttributes,stringYearsMonth,null);
	}
	
	//訂正却下ボタン
	public void serviceRejected(AttendanceFormList attendanceFormList,RedirectAttributes redirectAttributes,String rejectionReason,String stringYearsMonth) {
		attendanceManagementService.handleMonthlyAttendanceReq(attendanceFormList,redirectAttributes,stringYearsMonth,rejectionReason);
	}

//	//menuでの却下確認ロジック
	public void changeRejectedMonthlyAttendanceReq(Integer userId) {
		attendanceManagementService.changeRejectedMonthlyAttendanceReq(userId);
	}
}
