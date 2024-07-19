package com.example.demo.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.AttendanceSearchMapper;
import com.example.demo.model.Attendance;

@Service
public class AttendanceManagementService {
  @Autowired
  public AttendanceSearchMapper attendanceSearchMapper;
	public List<Attendance> attendanceSearchListUp(Integer userId, Integer years, Integer month) {
		
		
		
		LocalDate targetDate = LocalDate.of(years, month, 1);
		LocalDate endDate = targetDate.with(TemporalAdjusters.lastDayOfMonth());
		
		
		//年月から最終月日を算出
		Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, years);
        calendar.set(Calendar.MONTH, month - 1);
        int lastMonthAndDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
        
		
//		Attendance attendanceDate = new Attendance();
//		List<Attendance> attendanceDate1 = new ArrayList<Attendance>();
//		
//		for(int i = 1; i<= lastMonthAndDay;i++) {
//			attendanceDate1.add(attendanceDate);
//			attendanceDate1.get(i).setStatus(i);
//			attendanceDate1.get(i).setStartTime("");
//			attendanceDate1.get(i).setEndTime("");
//			attendanceDate1.get(i).setAttendanceRemarks("");
        
        
        List<Attendance> attendanceDate1 = new ArrayList<Attendance>();

        for (int i = 0; i < lastMonthAndDay; i++) {
            Attendance attendanceDate = new Attendance();
            attendanceDate.setStatus(1);
            attendanceDate.setStartTime("");
            attendanceDate.setEndTime("");
            attendanceDate.setAttendanceRemarks("");
            attendanceDate.setDays(i + 1);
            attendanceDate1.add(attendanceDate);
            
            
            
            
			Calendar cal = Calendar.getInstance();
			cal.set(years, month - 1, attendanceDate.getDays());

			// 日付から曜日を取得する
			 switch (cal.get(Calendar.DAY_OF_WEEK)) { 
			    case Calendar.SUNDAY:     // Calendar.SUNDAY:1 
			    	//日曜日
			    	attendanceDate.setDayOfWeek("日曜日");
				    break;
				case Calendar.MONDAY:     // Calendar.MONDAY:2
				    //月曜日
					attendanceDate.setDayOfWeek("月曜日");
				    break;
				case Calendar.TUESDAY:    // Calendar.TUESDAY:3
				    //火曜日
					attendanceDate.setDayOfWeek("火曜日");
				    break;
				case Calendar.WEDNESDAY:  // Calendar.WEDNESDAY:4
				    //水曜日
					attendanceDate.setDayOfWeek("水曜日");
				    break;
				case Calendar.THURSDAY:   // Calendar.THURSDAY:5
				    //木曜日
					attendanceDate.setDayOfWeek("木曜日");
				    break;
				case Calendar.FRIDAY:     // Calendar.FRIDAY:6
				    //金曜日
					attendanceDate.setDayOfWeek("金曜日");
				    break;
				case Calendar.SATURDAY:   // Calendar.SATURDAY:7
				    //土曜日
					attendanceDate.setDayOfWeek("土曜日");
				    break;
			  }
        

        
                
        
        
		}
		
//		System.out.print("『月初と月末変換確認表示』："+ userId + targetDate + endDate);
		
		
		

		
		
		List<Attendance> attendance = attendanceSearchMapper.selectByYearMonth(userId, targetDate, endDate);
		
		
		
		
//		for(int j = 0; j < attendance.size(); j++) {
//			String str = new SimpleDateFormat("MM-dd").format(attendance.get(j).getAttendanceDate());
//			String monthStr = str.substring(0, 2);
//			String dayStr = str.substring(3, 5);
//			attendance.get(j).setMonth(Integer.valueOf(monthStr));
//			attendance.get(j).setDays(Integer.valueOf(dayStr));
//
//		}
		
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		

		for (int j = 0; j < attendance.size(); j++) {
		    String str = sdf.format(attendance.get(j).getAttendanceDate());
		    String monthStr = str.substring(5, 7);
		    String dayStr = str.substring(8, 10);
		    attendance.get(j).setMonth(Integer.parseInt(monthStr));
		    attendance.get(j).setDays(Integer.parseInt(dayStr));
		}
		
		
		
		
		
		
        for(int i = 0;i < lastMonthAndDay ;i++) {
			for(int j = 0; j < attendance.size(); j++) {
				if(attendanceDate1.get(i).getDays() .equals(attendance.get(j).getDays()) ) {
					attendanceDate1.get(i).setStatus(attendance.get(j).getStatus());
					attendanceDate1.get(i).setStartTime(attendance.get(j).getStartTime());
					attendanceDate1.get(i).setEndTime(attendance.get(j).getEndTime());
					attendanceDate1.get(i).setAttendanceRemarks(attendance.get(j).getAttendanceRemarks());
					
					
				}
				
			}
		}
        System.out.print("『』："+ attendanceDate1);
		return attendanceDate1;
	}
	
//	public Users userCreate(String password, String userName, String role, Integer departmentId, Date startDate) {
//		Users users = userSearchMapper.insert(password, userName, role, departmentId, startDate);
//		return users;
//	}
}
