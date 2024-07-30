package com.example.demo.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
//		System.out.print("time型どんなのがはいってる?→"+"『"+attendanceFormList.getAttendanceList().get(10).getStartTime()+"』");
		if((attendanceFormList.getAttendanceList().get(i).getStatus() == 12 && "00:00:00".equals(attendanceFormList.getAttendanceList().get(i).getStartTime()) && "00:00:00".equals(attendanceFormList.getAttendanceList().get(i).getEndTime())) || (attendanceFormList.getAttendanceList().get(i).getStatus() == 12 && attendanceFormList.getAttendanceList().get(i).getStartTime() == null && attendanceFormList.getAttendanceList().get(i).getEndTime() == null)) {
			users.setRequestActivityCheck(false);
		}else {
			users.setRequestActivityCheck(true);
		}
	}
  }
  
  //勤怠登録エラーチェック
//  public void errorCheck(AttendanceFormList attendanceFormList, BindingResult result) {
//	  
//	  for(int i = 0 ;i<attendanceFormList.getAttendanceList().size();i++ ) {
//		  //備考欄
//		 if(attendanceFormList.getAttendanceList().get(i).getAttendanceRemarks() == null || attendanceFormList.getAttendanceList().get(i).getAttendanceRemarks().isEmpty()) {
//			  FieldError attendanceRemarks = new FieldError("attendanceFormList", "attendanceList[" + i + "].attendanceRemarks" ,"エラー");
//			  result.addError(attendanceRemarks);
//		  }
//		  
//		  //出勤時間と退勤時間の整合性
//
//			String strStartInputTime = null;
//			String strEndInputTime = null;
//			DateTimeFormatter formatter = null;
//			LocalTime startInputTime = null;
//			LocalTime endInputTime = null;
//			LocalTime firstTime = null;
//			LocalTime lastTime = null;
//			//出勤時間のみの整合性
//		  if(attendanceFormList.getAttendanceList().get(i).getStartTime() != null) {
//			  try {
//					 formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//					 startInputTime = LocalTime.parse(attendanceFormList.getAttendanceList().get(i).getStartTime(), formatter);
//					 
//					 firstTime = LocalTime.of(0, 0);
//					 lastTime = LocalTime.of(23, 59);
//					
//					 strStartInputTime = startInputTime.toString();
//					 
//
//				if(strStartInputTime != "00:00:00") {
//					
//					
//					if((!startInputTime.isBefore(firstTime) && !startInputTime.isAfter(lastTime))) {
//						FieldError startEndTime = new FieldError("attendanceFormList", "attendanceList[" + i + "].startTime","時刻が不正です。00:00～23:59の間で入力してください。");
//						result.addError(startEndTime);
////						System.out.print("作ったやつ→"+startEndTime);
//					}
//				}
//			} catch (DateTimeParseException e) {
//	                FieldError timeFormatError = new FieldError("attendanceFormList", "attendanceList[" + i + "].startTime", "時刻の形式が不正です。HH:mm:ss形式で入力してください。");
//	                result.addError(timeFormatError);
//	            }
//			  
//			  
//			  //退勤時間の整合性
//			  if(attendanceFormList.getAttendanceList().get(i).getEndTime() != null) {
//				  
//				  try {
//					  endInputTime = LocalTime.parse(attendanceFormList.getAttendanceList().get(i).getEndTime(), formatter);
//					  
//					  strEndInputTime = endInputTime.toString();
//
//						if(strEndInputTime != "00:00:00") {
//							
//							
//							if((!endInputTime.isBefore(firstTime) && !endInputTime.isAfter(lastTime))) {
//								FieldError startEndTime = new FieldError("attendanceFormList", "attendanceList[" + i + "].endTime","時刻が不正です。00:00～23:59の間で入力してください。");
//								result.addError(startEndTime);
////								System.out.print("作ったやつ→"+startEndTime);
//							}
//						}
//					} catch (DateTimeParseException e) {
//			                FieldError timeFormatError = new FieldError("attendanceFormList", "attendanceList[" + i + "].endTime", "時刻の形式が不正です。HH:mm:ss形式で入力してください。");
//			                result.addError(timeFormatError);
//			            }
//				  
//				  
//				  //出勤時間と退勤時間を合わせた整合性
//				  if((attendanceFormList.getAttendanceList().get(i).getStartTime() != null) && (attendanceFormList.getAttendanceList().get(i).getEndTime() != null)){
//					  if((startInputTime.isAfter(endInputTime))){
//						  FieldError startEndTime = new FieldError("attendanceFormList", "attendanceList[" + i + "].startEndTime","出勤時間より退勤時間が早くなっています。");
//							result.addError(startEndTime);
//					  }
//				  }
//			  }
//			}
//	  }
//	  
//  }
  
//↑のAI君バージョン
  public void errorCheck(AttendanceFormList attendanceFormList, BindingResult result) {
	    for (int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {
	        // 備考欄
	        if (attendanceFormList.getAttendanceList().get(i).getAttendanceRemarks() == null || attendanceFormList.getAttendanceList().get(i).getAttendanceRemarks().isEmpty()) {
	            FieldError attendanceRemarks = new FieldError("attendanceFormList", "attendanceList[" + i + "].attendanceRemarks", "エラー");
	            result.addError(attendanceRemarks);
	        }

	        // 出勤時間と退勤時間の整合性
	        String startTime = attendanceFormList.getAttendanceList().get(i).getStartTime();
	        String endTime = attendanceFormList.getAttendanceList().get(i).getEndTime();
	        
	        if (startTime != null) {
	            try {
	                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	                LocalTime startInputTime = LocalTime.parse(startTime, formatter);
	                LocalTime firstTime = LocalTime.of(0, 0);
	                LocalTime lastTime = LocalTime.of(23, 59);

	                if (!startInputTime.equals(firstTime) && (startInputTime.isBefore(firstTime) || startInputTime.isAfter(lastTime))) {
	                    FieldError startEndTime = new FieldError("attendanceFormList", "attendanceList[" + i + "].startTime", "時刻が不正です。00:00～23:59の間で入力してください。");
	                    result.addError(startEndTime);
	                }
	            } catch (DateTimeParseException e) {
	                FieldError timeFormatError = new FieldError("attendanceFormList", "attendanceList[" + i + "].startTime", "時刻の形式が不正です。HH:mm:ss形式で入力してください。");
	                result.addError(timeFormatError);
	            }
	        }

	        if (endTime != null) {
	            try {
	                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	                LocalTime endInputTime = LocalTime.parse(endTime, formatter);
	                LocalTime firstTime = LocalTime.of(0, 0);
	                LocalTime lastTime = LocalTime.of(23, 59);

	                if (!endInputTime.equals(firstTime) && (endInputTime.isBefore(firstTime) || endInputTime.isAfter(lastTime))) {
	                    FieldError startEndTime = new FieldError("attendanceFormList", "attendanceList[" + i + "].endTime", "時刻が不正です。00:00～23:59の間で入力してください。");
	                    result.addError(startEndTime);
	                }

	                if (startTime != null) {
	                    LocalTime startInputTime = LocalTime.parse(startTime, formatter);
	                    if (startInputTime.isAfter(endInputTime)) {
	                        FieldError startEndTime = new FieldError("attendanceFormList", "attendanceList[" + i + "].startTime", "出勤時間より退勤時間が早くなっています。");
	                        result.addError(startEndTime);
	                    }
	                }
	            } catch (DateTimeParseException e) {
	                FieldError timeFormatError = new FieldError("attendanceFormList", "attendanceList[" + i + "].endTime", "時刻の形式が不正です。HH:mm:ss形式で入力してください。");
	                result.addError(timeFormatError);
	            }
	        }
	    }
	}


  
  
  
  
//出勤時間と退勤時間の整合性チェック
//	public void timeCheck(AttendanceFormList attendanceFormList) {
//		
//		
//		for(int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {
//			if(attendanceFormList.getAttendanceList().get(i).getStartTime() != null || attendanceFormList.getAttendanceList().get(i).getEndTime() != null) {
//				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//				LocalTime startInputTime = LocalTime.parse(attendanceFormList.getAttendanceList().get(i).getStartTime(), formatter);
//				LocalTime endInputTime = LocalTime.parse(attendanceFormList.getAttendanceList().get(i).getEndTime(), formatter);
//				LocalTime firstTime = LocalTime.of(0, 0);
//				LocalTime lastTime = LocalTime.of(23, 59);
//				
//				if((!startInputTime.isBefore(firstTime) && !startInputTime.isAfter(lastTime)) && (!endInputTime.isBefore(firstTime) && !endInputTime.isAfter(lastTime)) && (startInputTime.isBefore(endInputTime))) {
//					attendanceFormList.getAttendanceList().get(i).setStartEndTimeCheck(true);
//				}else {
//					attendanceFormList.getAttendanceList().get(i).setStartEndTimeCheck(false);
//				}
//			}
//			
//		}
//	}
}
