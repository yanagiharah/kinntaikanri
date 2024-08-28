package com.example.demo.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.demo.mapper.AttendanceSearchMapper;
import com.example.demo.model.Attendance;
import com.example.demo.model.AttendanceFormList;

@Service
public class AttendanceManagementService {

  
  private final AttendanceSearchMapper attendanceSearchMapper;
  
  public AttendanceManagementService(AttendanceSearchMapper attendanceSearchMapper){
	  this.attendanceSearchMapper = attendanceSearchMapper;
  }
  
  	//昨日の勤怠登録状況を取得
	public Boolean checkYesterdayAttendance(Integer userId, LocalDate yesterday) {
		Date yesterdayAttendanceEndtime = attendanceSearchMapper.selectYesterdayCheck(userId, yesterday);
		Boolean checkAttendance;
		if (yesterdayAttendanceEndtime == null) {
			checkAttendance = false;
		}else {
			checkAttendance = true;
		}
		return checkAttendance;
	}
  
  	//勤怠表の取得
	public List<Attendance> attendanceSearchListUp(Integer userId, Integer years, Integer month) {

		//年月から最終月日を算出
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, years);
		calendar.set(Calendar.MONTH, month - 1);
		int lastMonthAndDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		//monthlyAttendanceListは1日～31日までの日付のattendance型の空の勤怠表を作成
        List<Attendance> monthlyAttendanceList = new ArrayList<Attendance>();

		for (int i = 0; i < lastMonthAndDay; i++) {
			Attendance monthlyAttendance = new Attendance();
			monthlyAttendance.setStatus(12);
			monthlyAttendance.setStartTime(null);
			monthlyAttendance.setEndTime(null);
			monthlyAttendance.setAttendanceRemarks(null);//備考
			monthlyAttendance.setDays(i + 1);
			monthlyAttendanceList.add(monthlyAttendance);

			Calendar cal = Calendar.getInstance();
			cal.set(years, month - 1, monthlyAttendance.getDays());

			// 日付から曜日を取得する
			switch (cal.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.SUNDAY: // Calendar.SUNDAY:1 
				//日曜日
				monthlyAttendance.setDayOfWeek("日");
				break;
			case Calendar.MONDAY: // Calendar.MONDAY:2
				//月曜日
				monthlyAttendance.setDayOfWeek("月");
				break;
			case Calendar.TUESDAY: // Calendar.TUESDAY:3
				//火曜日
				monthlyAttendance.setDayOfWeek("火");
				break;
			case Calendar.WEDNESDAY: // Calendar.WEDNESDAY:4
				//水曜日
				monthlyAttendance.setDayOfWeek("水");
				break;
			case Calendar.THURSDAY: // Calendar.THURSDAY:5
				//木曜日
				monthlyAttendance.setDayOfWeek("木");
				break;
			case Calendar.FRIDAY: // Calendar.FRIDAY:6
				//金曜日
				monthlyAttendance.setDayOfWeek("金");
				break;
			case Calendar.SATURDAY: // Calendar.SATURDAY:7
				//土曜日
				monthlyAttendance.setDayOfWeek("土");
				break;
			}
			Date newDate = cal.getTime();
			monthlyAttendance.setAttendanceDate(newDate);
			
			//String型の日付を取得
			String newDateS = new SimpleDateFormat("yyyy/MM/dd").format(newDate);
			monthlyAttendance.setAttendanceDateS(String.valueOf(newDateS));

		}
		
		//DBから勤怠表を取得
		LocalDate targetDate = LocalDate.of(years, month, 1);
		LocalDate endDate = targetDate.with(TemporalAdjusters.lastDayOfMonth());
		List<Attendance> attendance = attendanceSearchMapper.selectByYearMonth(userId, targetDate, endDate);

		
		//DBに存在する勤怠表の日付を日、月、string型日付で取得する
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		for (int j = 0; j < attendance.size(); j++) {
			String str = sdf.format(attendance.get(j).getAttendanceDate());
			String monthStr = str.substring(5, 7);
			String dayStr = str.substring(8, 10);
			attendance.get(j).setMonth(Integer.parseInt(monthStr));
			attendance.get(j).setDays(Integer.parseInt(dayStr));
		}
		
		
		//空の勤怠表とDBから取得した勤怠表を統合する
		for (int i = 0; i < lastMonthAndDay; i++) {
			for (int j = 0; j < attendance.size(); j++) {
				
				//日付で比較
				if (monthlyAttendanceList.get(i).getDays().equals(attendance.get(j).getDays())) {
					
					monthlyAttendanceList.get(i).setStatus(attendance.get(j).getStatus());
					monthlyAttendanceList.get(i).setAttendanceRemarks(attendance.get(j).getAttendanceRemarks());
					
					if(attendance.get(j).getStartTime() != null) {
						monthlyAttendanceList.get(i).setStartTime(attendance.get(j).getStartTime().substring(0, 5));
					}else {
						monthlyAttendanceList.get(i).setStartTime("");
					}
					
					if(attendance.get(j).getEndTime() != null) {
						monthlyAttendanceList.get(i).setEndTime(attendance.get(j).getEndTime().substring(0, 5));
					}else {
						monthlyAttendanceList.get(i).setEndTime("");
					}
				}
			}
		}
		return monthlyAttendanceList;
	}
  
  
	//勤怠テーブルのデータを物理削除
	public void attendanceDelete(AttendanceFormList attendanceFormList) {
		attendanceSearchMapper.deleteByAttendanceOfMonth(attendanceFormList.getAttendanceList().get(0).getUserId(),
				attendanceFormList.getAttendanceList().get(0).getAttendanceDate(),
				attendanceFormList.getAttendanceList().get(attendanceFormList.getAttendanceList().size() - 1).getAttendanceDate());
	}
  
	//勤怠テーブルに登録処理
	public void attendanceCreate(AttendanceFormList attendanceFormList) {
		// List<Attendance> attendanceList = new ArrayList<Attendance>();
		List<Integer> workDay = Arrays.asList(0, 3, 6, 7, 8, 10);
		attendanceFormList.getAttendanceList().stream()
				.filter(attendance -> workDay.contains(attendance.getStatus()))
				.filter(attendance -> !attendance.getStartTime().isEmpty())
				.forEach(attendance -> attendanceSearchMapper.insert(attendance));

		List<Integer> holiday = Arrays.asList(1, 2, 4, 5, 9, 11);
		attendanceFormList.getAttendanceList().stream()
				.filter(attendance -> holiday.contains(attendance.getStatus()))
				.filter(attendance -> attendance.getStartTime().isEmpty())
				.filter(attendance -> attendance.getEndTime().isEmpty())
				.forEach(attendance -> attendanceSearchMapper.insert(attendance));
	}
  
  
	//勤怠登録画面で承認申請ボタンを有効にするかを決める
	public void requestActivityCheck(AttendanceFormList attendanceFormList) {
		
//		List<Integer> activity = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
//		boolean allMatch = attendanceFormList.getAttendanceList().stream()
//			    .allMatch(attendance -> 
//			        activity.contains(attendance.getStatus()) &&
//			        attendance.getStartTime() != null &&
//			        attendance.getEndTime() != null
//			    );
//
//		attendanceFormList.setRequestActivityCheck(allMatch);
		
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
	    for (int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {
	        
	    	// 備考欄
	    	if (attendanceFormList.getAttendanceList().get(i).getAttendanceRemarks().length() > 20) {
	    		    FieldError attendanceRemarks = new FieldError("attendanceFormList", "attendanceList[" + i + "].attendanceRemarks", "20文字以内で入力してください。");
	    		    result.addError(attendanceRemarks);
	    		}

	        if (attendanceFormList.getAttendanceList().get(i).getAttendanceRemarks() != "" && attendanceFormList.getAttendanceList().get(i).getAttendanceRemarks().matches("^[a-zA-Z0-9!-/:-@\\[-`{-~]*$")) {
	        	    FieldError attendanceRemarks = new FieldError("attendanceFormList", "attendanceList[" + i + "].attendanceRemarks", "全角で入力してください。");
	        	    result.addError(attendanceRemarks);
	        	}	        

	        // 出勤時間と退勤時間の整合性
	        String startTime = attendanceFormList.getAttendanceList().get(i).getStartTime();
	        String endTime = attendanceFormList.getAttendanceList().get(i).getEndTime();
	        
	        
	        if (startTime != null) {
	            try {
	            	
	            	if(startTime == "") {
	            		break;
	            	}
	                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
	                LocalTime startInputTime = LocalTime.parse(startTime, formatter);
	                LocalTime firstTime = LocalTime.of(0, 0);
	                LocalTime lastTime = LocalTime.of(23, 59);

	                if (!startInputTime.equals(firstTime) && (startInputTime.isBefore(firstTime) || startInputTime.isAfter(lastTime))) {
	                    FieldError startEndTime = new FieldError("attendanceFormList", "attendanceList[" + i + "].startTime", "時刻が不正です。00:00～23:59の間で入力してください。");
	                    result.addError(startEndTime);
	                }
	            } catch (DateTimeParseException e) {
	                FieldError timeFormatError = new FieldError("attendanceFormList", "attendanceList[" + i + "].startTime", "時刻の形式が不正です。HH:mm形式で入力してください。");
	                result.addError(timeFormatError);
	            }
	        }

	        if (endTime != null) {
	            try {
	            	
	            	if(endTime == "") {
	            		break;
	            	}
	                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
	                LocalTime endInputTime = LocalTime.parse(endTime, formatter);
	                LocalTime firstTime = LocalTime.of(0, 0);
	                LocalTime lastTime = LocalTime.of(23, 59);

	                if (!endInputTime.equals(firstTime) && (endInputTime.isBefore(firstTime) || endInputTime.isAfter(lastTime))) {
	                    FieldError startEndTime = new FieldError("attendanceFormList", "attendanceList[" + i + "].endTime", "時刻が不正です。00:00～23:59の間で入力してください。");
	                    result.addError(startEndTime);
	                }

	                if (startTime != null) {
	                    LocalTime startInputTime = LocalTime.parse(startTime, formatter);
	                    if (endInputTime.isBefore(startInputTime)) {
	                        FieldError startEndTime = new FieldError("attendanceFormList", "attendanceList[" + i + "].endTime", "出勤時間より退勤時間が早くなっています。");
	                        result.addError(startEndTime);
	                    }
	                }
	            } catch (DateTimeParseException e) {
	                FieldError timeFormatError = new FieldError("attendanceFormList", "attendanceList[" + i + "].endTime", "時刻の形式が不正です。HH:mm形式で入力してください。");
	                result.addError(timeFormatError);
	            }
	        }
	    }
	}
}


