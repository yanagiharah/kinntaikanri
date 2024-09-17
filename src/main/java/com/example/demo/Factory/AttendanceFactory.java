package com.example.demo.Factory;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.example.demo.model.Attendance;
import com.example.demo.model.AttendanceFormList;

@Component
public class AttendanceFactory {
	
	//検索ボタン時attendanceFormListに詰めなおし
	public AttendanceFormList setInAttendance(List<Attendance> attendance,Integer stringYears,Integer years,String month) {
		AttendanceFormList attendanceFormList = new AttendanceFormList();
		ArrayList<Attendance> attendanceList = new ArrayList<Attendance>();
		attendanceFormList.setAttendanceList(attendanceList);
		attendanceList.addAll(attendance);
		attendanceFormList.setStringYears(String.valueOf(years));
		attendanceFormList.setStringMonth(String.valueOf(years));
		attendanceFormList.setStringYearsMonth(month);
		return attendanceFormList;
	}
	
	//更新用のAttendanceFormの生成メソッド
	public AttendanceFormList  updateAttendanceFormCreate(AttendanceFormList attendanceFormList ,Integer usreId){
		for(int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {  
			String inputDate = attendanceFormList.getAttendanceList().get(i).getAttendanceDateS();
			String conversion = inputDate.replace("/","-");
			attendanceFormList.getAttendanceList().get(i).setAttendanceDate(java.sql.Date.valueOf(conversion));
			attendanceFormList.getAttendanceList().get(i).setUserId(usreId);
		  }
		return attendanceFormList;
	}
	
	//空のmonthlyAttendanceListの生成
	public List<Attendance> emptyAttendanceCreate(int lastMonthAndDay, Integer years, Integer month) {
		Calendar calendar = Calendar.getInstance();
		List<Attendance> emptyAttendanceList = new ArrayList<Attendance>();
		for (int i = 0; i < lastMonthAndDay; i++) {
			Attendance emptyAttendance = new Attendance();
			emptyAttendance.setStatus(12);
			emptyAttendance.setStartTime(null);
			emptyAttendance.setEndTime(null);
			emptyAttendance.setAttendanceRemarks(null);//備考
			emptyAttendance.setDays(i + 1);
			//曜日の設定
			LocalDate englishDayOfweek = LocalDate.of(years, month, i + 1);
			DateTimeFormatter JapaneseFormatter = DateTimeFormatter.ofPattern("E", Locale.JAPANESE);//曜日を日本語に変換
			emptyAttendance.setDayOfWeek(String.valueOf(englishDayOfweek.format(JapaneseFormatter)));
			//日付の設定
			calendar.set(years, month - 1, emptyAttendance.getDays());
			Date newDate = calendar.getTime();
			emptyAttendance.setAttendanceDate(newDate);
			String newDateS = new SimpleDateFormat("yyyy/MM/dd").format(newDate);//日付をString型に変換
			emptyAttendance.setAttendanceDateS(String.valueOf(newDateS));
			emptyAttendanceList.add(emptyAttendance);
		}
		return emptyAttendanceList;
	}
	//dbAttendanceListにString型の月日をセットする
	public List<Attendance> dbAttendanceSetYear(List<Attendance> dbAttendanceList){
		//DBに存在する勤怠表の日付を月、日、string型日付で取得する
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				for (int j = 0; j < dbAttendanceList.size(); j++) {
					String str = sdf.format(dbAttendanceList.get(j).getAttendanceDate());
					String monthStr = str.substring(5, 7);
					String dayStr = str.substring(8, 10);
					dbAttendanceList.get(j).setMonth(Integer.parseInt(monthStr));
					dbAttendanceList.get(j).setDays(Integer.parseInt(dayStr));
				}
		return dbAttendanceList;
	}
	
	public List<Attendance> putTogetherAttendanceList(List<Attendance> emptyAttendanceList,
			List<Attendance> updatedDbAttendanceList, int monthDays) {
		// 空の勤怠表とDBから取得した勤怠表を統合する
		for (int i = 0; i < monthDays; i++) {
			for (Attendance dbAttendance : updatedDbAttendanceList) {
				if (emptyAttendanceList.get(i).getDays().equals(dbAttendance.getDays())) {
					mergeAttendanceData(emptyAttendanceList.get(i), dbAttendance);
				}
			}
		}
		return emptyAttendanceList;
	}

	// 勤怠データの統合処理をメソッド化
	public void mergeAttendanceData(Attendance emptyAttendance, Attendance dbAttendance) {
		emptyAttendance.setStatus(dbAttendance.getStatus());
		emptyAttendance.setAttendanceRemarks(dbAttendance.getAttendanceRemarks());
		setStartAndEndTime(emptyAttendance, dbAttendance);
	}

	// 開始時刻と終了時刻の設定をメソッド化
	public void setStartAndEndTime(Attendance emptyAttendance, Attendance dbAttendance) {
		emptyAttendance.setStartTime(
				dbAttendance.getStartTime() != null ? dbAttendance.getStartTime().substring(0, 5) : "");
		emptyAttendance.setEndTime(
				dbAttendance.getEndTime() != null ? dbAttendance.getEndTime().substring(0, 5) : "");
	}
	
}
	
