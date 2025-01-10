package com.example.demo.service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.example.demo.Factory.AttendanceFactory;
import com.example.demo.helper.DateHelper;
import com.example.demo.mapper.AttendanceSearchMapper;
import com.example.demo.model.Attendance;
import com.example.demo.model.AttendanceFormList;
import com.example.demo.validation.AttendanceValidation;
import com.google.api.services.calendar.model.Events;

@Service
public class AttendanceManagementService {

  private final AttendanceSearchMapper attendanceSearchMapper;
  private final AttendanceValidation attendanceValidation; 
  private final AttendanceFactory attendanceFactory;
  private final ModelService modelService;
  private final DateHelper dateHelper;
  private final GoogleCalendarService googleCalendarService;
  
  public AttendanceManagementService(AttendanceSearchMapper attendanceSearchMapper,AttendanceValidation attendanceValidation,
		  AttendanceFactory attendanceFactory,ModelService modelService,DateHelper dateHelper,GoogleCalendarService googleCalendarService){
	  this.attendanceSearchMapper = attendanceSearchMapper;
	  this.attendanceValidation = attendanceValidation;
	  this.attendanceFactory = attendanceFactory;
	  this.modelService = modelService;
	  this.dateHelper = dateHelper;
	  this.googleCalendarService =googleCalendarService;
  }
  
  	//昨日の勤怠登録状況を取得
	public Model checkYesterdayAttendance(Integer userId, LocalDate yesterday,Model model) {
		Integer checkAttendance = attendanceSearchMapper.selectYesterdayCheck(userId, yesterday);
		if(checkAttendance == 0) {
			return modelService.CheckAttendance(model);
		}
		return null;
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
  
	//検索ボタン時attendanceFormListに詰めなおし
	public AttendanceFormList setInAttendance(List<Attendance> attendance, Integer years, Integer month,
			String stringYearsMonth) {
		AttendanceFormList attendanceFormList = attendanceFactory.setInAttendance(attendance, years, month,
				stringYearsMonth);
		return attendanceFormList;
	}

	//更新用のAttendanceFormの生成メソッド     ・9/24、70,73行目usreIdをuserIdに修正
	public AttendanceFormList updateAttendanceFormCreate(AttendanceFormList attendanceFormList, Integer userId) {
		AttendanceFormList updateAttendanceFormEntity = attendanceFactory
				.updateAttendanceFormCreate(attendanceFormList, userId);
		return updateAttendanceFormEntity;
	}

	//勤怠テーブルに登録処理
	@Transactional
	public void attendanceUpsert(AttendanceFormList attendanceFormList) {

		List<Integer> workDay = Arrays.asList(0, 3, 6, 7, 8, 10);
		attendanceFormList.getAttendanceList().stream()
				.filter(attendance -> workDay.contains(attendance.getStatus()))
				.filter(attendance -> !attendance.getStartTime().isEmpty())
				.forEach(attendance -> attendanceSearchMapper.upsert(attendance));

		List<Integer> holiday = Arrays.asList(1, 2, 4, 5, 9, 11);
		attendanceFormList.getAttendanceList().stream()
				.filter(attendance -> holiday.contains(attendance.getStatus()))
				.filter(attendance -> attendance.getStartTime().isEmpty())
				.filter(attendance -> attendance.getEndTime().isEmpty())
				.forEach(attendance -> attendanceSearchMapper.upsert(attendance));
	}
  
	//勤怠登録画面で承認申請ボタンを有効にするかを決める
	public void requestActivityCheck(AttendanceFormList attendanceFormList) {
	    List<Integer> workDay = Arrays.asList(0, 3, 6, 7, 8, 10);
	    List<Integer> holiday = Arrays.asList(1, 2, 4, 5, 9, 11);

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
	
	//AttendanceFormList内の要素、List<attendance>の中で最初のattendanceの申請日を取るためのメソッド
	public Date getFirstAttendanceDate(AttendanceFormList attendanceFormList) {
	    if (attendanceFormList != null && !attendanceFormList.getAttendanceList().isEmpty()) {
	        return attendanceFormList.getAttendanceList().get(0).getAttendanceDate();
	    }
	    return null; // 存在しない場合nullを返す
	}
	
	//上記メソッド同様にuserIdを取るためのメソッド
	public Integer getFirstAttendanceUserId(AttendanceFormList attendanceFormList) {
		if (attendanceFormList != null && !attendanceFormList.getAttendanceList().isEmpty()) {
			return attendanceFormList.getAttendanceList().get(0).getUserId();
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