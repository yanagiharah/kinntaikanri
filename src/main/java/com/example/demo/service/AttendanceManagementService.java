package com.example.demo.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.demo.mapper.AttendanceSearchMapper;
import com.example.demo.model.Attendance;
import com.example.demo.model.AttendanceFormList;
import com.example.demo.model.Users;

@Service
public class AttendanceManagementService {
  @Autowired
  public AttendanceSearchMapper attendanceSearchMapper;
  
  //勤怠表の取得
  public List<Attendance> attendanceSearchListUp(Integer userId, Integer years, Integer month) {
		
		LocalDate targetDate = LocalDate.of(years, month, 1);
		LocalDate endDate = targetDate.with(TemporalAdjusters.lastDayOfMonth());
		
		
		//年月から最終月日を算出
		Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, years);
        calendar.set(Calendar.MONTH, month - 1);
        int lastMonthAndDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

       
               
        //attendanceDate1は１日～３１日までの日付のattendance型が入ったList
        List<Attendance> attendanceDate1 = new ArrayList<Attendance>();

		for (int i = 0; i < lastMonthAndDay; i++) {
			Attendance attendanceDate = new Attendance();
			attendanceDate.setStatus(12);
			attendanceDate.setStartTime(null);
			attendanceDate.setEndTime(null);
			attendanceDate.setAttendanceRemarks(null);//備考
			attendanceDate.setDays(i + 1);
			attendanceDate1.add(attendanceDate);

			Calendar cal = Calendar.getInstance();
			cal.set(years, month - 1, attendanceDate.getDays());

			// 日付から曜日を取得する
			switch (cal.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.SUNDAY: // Calendar.SUNDAY:1 
				//日曜日
				attendanceDate.setDayOfWeek("日");
				break;
			case Calendar.MONDAY: // Calendar.MONDAY:2
				//月曜日
				attendanceDate.setDayOfWeek("月");
				break;
			case Calendar.TUESDAY: // Calendar.TUESDAY:3
				//火曜日
				attendanceDate.setDayOfWeek("火");
				break;
			case Calendar.WEDNESDAY: // Calendar.WEDNESDAY:4
				//水曜日
				attendanceDate.setDayOfWeek("水");
				break;
			case Calendar.THURSDAY: // Calendar.THURSDAY:5
				//木曜日
				attendanceDate.setDayOfWeek("木");
				break;
			case Calendar.FRIDAY: // Calendar.FRIDAY:6
				//金曜日
				attendanceDate.setDayOfWeek("金");
				break;
			case Calendar.SATURDAY: // Calendar.SATURDAY:7
				//土曜日
				attendanceDate.setDayOfWeek("土");
				break;
			}
			Date newDate = cal.getTime();
			attendanceDate.setAttendanceDate(newDate);
			//String型の日付を取得
			String newDateS = new SimpleDateFormat("yyyy-MM-dd").format(newDate);
			attendanceDate.setAttendanceDateS(String.valueOf(newDateS));

		}
		//DBから勤怠表を取得
		List<Attendance> attendance = attendanceSearchMapper.selectByYearMonth(userId, targetDate, endDate);

		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//DBに存在する勤怠表の日付を日、月、string型日付で取得する
		for (int j = 0; j < attendance.size(); j++) {
			String str = sdf.format(attendance.get(j).getAttendanceDate());
			String monthStr = str.substring(5, 7);
			String dayStr = str.substring(8, 10);
			//attendance.get(j).setAttendanceDateS(str);
			attendance.get(j).setMonth(Integer.parseInt(monthStr));
			attendance.get(j).setDays(Integer.parseInt(dayStr));
		}

		for (int i = 0; i < lastMonthAndDay; i++) {
			for (int j = 0; j < attendance.size(); j++) {
				if (attendanceDate1.get(i).getDays().equals(attendance.get(j).getDays())) {
					attendanceDate1.get(i).setStatus(attendance.get(j).getStatus());
					attendanceDate1.get(i).setStartTime(attendance.get(j).getStartTime());
					attendanceDate1.get(i).setEndTime(attendance.get(j).getEndTime());
					attendanceDate1.get(i).setAttendanceRemarks(attendance.get(j).getAttendanceRemarks());
				}
			}
		}
		return attendanceDate1;
	}
	

  
  //勤怠テーブルのデータを物理削除
	public void attendanceDelete(AttendanceFormList attendanceFormList) {
		attendanceSearchMapper.deleteByAttendanceOfMonth(attendanceFormList.getAttendanceList().get(0).getUserId(),
				attendanceFormList.getAttendanceList().get(0).getAttendanceDate(),
				attendanceFormList.getAttendanceList().get(attendanceFormList.getAttendanceList().size() - 1)
						.getAttendanceDate());
	}
  
  //勤怠テーブルに登録処理
  public void attendanceCreate(AttendanceFormList attendanceFormList) {
	  for(int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {
	  attendanceSearchMapper.insert(attendanceFormList.getAttendanceList().get(i));
	  }
  }
  
  
//勤怠登録画面で承認申請ボタンを有効にするかを決める
  public void requestActivityCheck(AttendanceFormList attendanceFormList, Users users) {
	for(int i = 0;i < attendanceFormList.getAttendanceList().size();i++) {
		System.out.print("time型どんなのがはいってる?→"+"『"+attendanceFormList.getAttendanceList().get(10).getStartTime()+"』");
		if((attendanceFormList.getAttendanceList().get(i).getStatus() == 12 && "00:00:00".equals(attendanceFormList.getAttendanceList().get(i).getStartTime()) && "00:00:00".equals(attendanceFormList.getAttendanceList().get(i).getEndTime())) || (attendanceFormList.getAttendanceList().get(i).getStatus() == 12 && attendanceFormList.getAttendanceList().get(i).getStartTime() == null && attendanceFormList.getAttendanceList().get(i).getEndTime() == null)) {
			users.setRequestActivityCheck(false);
		}else {
			users.setRequestActivityCheck(true);
		}
	}
  }
  
  //勤怠登録エラーチェック
  public void errorCheck(AttendanceFormList attendanceFormList, BindingResult result) {
	  
	  for(int i = 0 ;i<attendanceFormList.getAttendanceList().size();i++ ) {
		  if(attendanceFormList.getAttendanceList().get(i).getAttendanceRemarks() != null || attendanceFormList.getAttendanceList().get(i).getAttendanceRemarks() == "") {
			  FieldError attendanceRemarks = new FieldError("attendanceFormList", "attendanceList.["+i+"].attendanceRemarks", "エラー");
			  result.addError(attendanceRemarks);
		  }
	  }
	  
  }
}
