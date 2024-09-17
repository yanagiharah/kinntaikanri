package com.example.demo.service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.example.demo.Factory.AttendanceFactory;
import com.example.demo.mapper.AttendanceSearchMapper;
import com.example.demo.model.Attendance;
import com.example.demo.model.AttendanceFormList;
import com.example.demo.validation.AttendanceValidation;

@Service
public class AttendanceManagementService {

  private final AttendanceSearchMapper attendanceSearchMapper;
  private final AttendanceValidation attendanceValidation; 
  private final AttendanceFactory attendanceFactory;
  
  public AttendanceManagementService(AttendanceSearchMapper attendanceSearchMapper,AttendanceValidation attendanceValidation,AttendanceFactory attendanceFactory){
	  this.attendanceSearchMapper = attendanceSearchMapper;
	  this.attendanceValidation = attendanceValidation;
	  this.attendanceFactory = attendanceFactory;
  }
  
  	//昨日の勤怠登録状況を取得
	public Integer checkYesterdayAttendance(Integer userId, LocalDate yesterday) {
		Integer checkAttendance = attendanceSearchMapper.selectYesterdayCheck(userId, yesterday);
		return checkAttendance;
	}
	
  	//勤怠表の取得 
	public List<Attendance> attendanceSearchListUp(Integer userId, Integer years, Integer month) {
		
		//年月から最終月日を算出
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, years);
		calendar.set(Calendar.MONTH, month - 1);
		int monthDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		//空の勤怠表を生成
		 List<Attendance> emptyAttendanceList =attendanceFactory.emptyAttendanceCreate(monthDays, years, month);
		
		//DBから勤怠表を取得
		LocalDate firsrtDayMonth = LocalDate.of(years, month, 1);
		LocalDate lastDayMonth = firsrtDayMonth.with(TemporalAdjusters.lastDayOfMonth());
		List<Attendance> dbAttendanceList = attendanceSearchMapper.selectByYearMonth(userId, firsrtDayMonth, lastDayMonth);
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

	//更新用のAttendanceFormの生成メソッド
	public AttendanceFormList updateAttendanceFormCreate(AttendanceFormList attendanceFormList, Integer usreId) {
		AttendanceFormList updateAttendanceFormEntity = attendanceFactory
				.updateAttendanceFormCreate(attendanceFormList, usreId);
		return updateAttendanceFormEntity;
	}

	//勤怠テーブルに登録処理
	public void attendanceUpsert(AttendanceFormList attendanceFormList) {

		List<Integer> workDay = Arrays.asList(0, 3, 6, 7, 8, 10);
		attendanceFormList.getAttendanceList().stream()
				.filter(attendance -> workDay.contains(attendance.getStatus()))
				.filter(attendance -> !attendance.getStartTime().isEmpty())
				.forEach(attendance -> attendanceSearchMapper.upsert(attendance));

		List<Integer> holiday = Arrays.asList(1, 2, 4, 5, 9, 11, 12);
		attendanceFormList.getAttendanceList().stream()
				.filter(attendance -> holiday.contains(attendance.getStatus()))
				.filter(attendance -> attendance.getStartTime().isEmpty())
				.filter(attendance -> attendance.getEndTime().isEmpty())
				.forEach(attendance -> attendanceSearchMapper.upsert(attendance));
	}
  
	//勤怠登録画面で承認申請ボタンを有効にするかを決める
	public void requestActivityCheck(AttendanceFormList attendanceFormList) {
		for (int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {
			
			//条件1 ステータスに記入があり、出勤退勤がnullじゃない場合
			if (attendanceFormList.getAttendanceList().get(i).getStatus() != 12
					&& attendanceFormList.getAttendanceList().get(i).getStartTime() != null
					&& attendanceFormList.getAttendanceList().get(i).getEndTime() != null) {
				attendanceFormList.setRequestActivityCheck(true);
			} else {
				attendanceFormList.setRequestActivityCheck(false);
				break;
			}
			
			//条件2 ステータスに記入があり、出勤のみ記入がある場合
			if (attendanceFormList.getAttendanceList().get(i).getStatus() != 12
					&& attendanceFormList.getAttendanceList().get(i).getStartTime() != ""
					&& attendanceFormList.getAttendanceList().get(i).getEndTime() == "") {
				attendanceFormList.setRequestActivityCheck(false);
				break;
			}
		}
	}
	//勤怠登録のエラーチェック
	public void errorCheck(AttendanceFormList attendanceFormList, BindingResult result) {
		attendanceValidation.errorCheck(attendanceFormList, result);
	}
}



//勤怠テーブルのデータを物理削除
//public void attendanceDelete(AttendanceFormList attendanceFormList) {
//	attendanceSearchMapper.deleteByAttendanceOfMonth(attendanceFormList.getAttendanceList().get(0).getUserId(),
//			attendanceFormList.getAttendanceList().get(0).getAttendanceDate(),
//			attendanceFormList.getAttendanceList().get(attendanceFormList.getAttendanceList().size() - 1).getAttendanceDate());
//}