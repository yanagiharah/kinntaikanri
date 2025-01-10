package com.example.demo.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class DateHelper {
	
	public LocalDate getYesterday() {
		return LocalDate.now().minusDays(1);
	}
	
	private LocalDate getFirstDayLastMonthByLocalDate(){
		// 現在の日付を取得
		LocalDate now = LocalDate.now();
		// 先月の1日を返す
		return now.minusMonths(1).withDayOfMonth(1);
	}
	
	//先月一日をdate型で取得
	public Date firstDayLastMonth() {
		LocalDate targetDate = getFirstDayLastMonthByLocalDate();
		// Date型に変換
		return Date.from(targetDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	//LocalDate型変数を"yyyy-MM"型に変更するフォーマッターメソッド
	private String formatLocalDate(LocalDate localDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");//年月の形式に変換
		return localDate.format(formatter);//String型に変換;
	}
	
	//現在の年・月を"yyyy-MM"の型で取得
	public String yearsMonth() {
		return formatLocalDate(LocalDate.now());
	}
	
	//先月の年・月を"yyyy-MM"型で取得
	public String lastYearsMonth() {
		LocalDate lastYearsMonth = getFirstDayLastMonthByLocalDate();
		return formatLocalDate(lastYearsMonth);
	}
	
	//年月日を"yyyy-MM-dd"のString型で取得
	public String getYearMonthDay(LocalDate date) {
		return  date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
	
	//String型の年月をint型の年と月に分けて取得
	public int[] parseDate(String yearMonth) {
		Integer year = Integer.parseInt(yearMonth.substring(0, 4));
		Integer month = Integer.parseInt(yearMonth.substring(5, 7));
		return new int[] {year,month};
	}
	
	//カレンダーで選んだ年月を"yyyy-MM-01"の形に変更するメソッド
	public LocalDate convertStringToLocalDate(String stringYearsMonth) {
		try {
			// 元の形式
			DateTimeFormatter monthlyAttendanceReqFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate targetDate = LocalDate.parse(stringYearsMonth + "-01", monthlyAttendanceReqFormat);
			// 日付を1日に設定
			return targetDate.withDayOfMonth(1);
		} catch (DateTimeParseException e) {
			//ほぼあり得ないがエラー時用の処理
			System.err.println("Error parsing date: " + stringYearsMonth);
			e.printStackTrace();
			return null;
		}
	}
	
	public int getMonthDays(Integer year,Integer month) {
		//年月から最終月日を算出
		Calendar calendar = getNewCalendar(year,month);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	public Calendar getNewCalendar(Integer year,Integer month) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		return calendar;
	}
	
	public String convertionInputDate(String date) {
		return date.replace("/", "-");
	}
	
	public LocalDate getInputCalendarDate(String date) {
		LocalDate calendarDate;
		if (date == null) {
			calendarDate = LocalDate.now();
		} else {
			calendarDate = LocalDate.parse(date);
		}
		return calendarDate;
	}
	
	public Date convertToDate(int year, int month){
	    String dateString = year + "-" + String.format("%02d", month) + "-01";
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    Date dateYearMonth = null;
		try {
			dateYearMonth = sdf.parse(dateString);
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	    return dateYearMonth;
    }

}
