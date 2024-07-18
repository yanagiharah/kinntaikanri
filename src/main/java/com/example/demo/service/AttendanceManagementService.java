package com.example.demo.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
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
		
//		System.out.print("『月初と月末変換確認表示』："+ userId + targetDate + endDate);
		
		List<Attendance> attendance = attendanceSearchMapper.selectByYearMonth(userId, targetDate, endDate);
		
		
		for(int j = 0; j < attendance.size(); j++) {
			String str = new SimpleDateFormat("MM-dd").format(attendance.get(j).getAttendanceDate());
			String monthStr = str.substring(0, 2);
			String dayStr = str.substring(3, 5);
			attendance.get(j).setMonth(Integer.valueOf(monthStr));
			attendance.get(j).setDays(Integer.valueOf(dayStr));
			
			
		}
		
		
		//年月から最終月日を算出
		Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, years);
        calendar.set(Calendar.MONTH, month - 1);
        int lastMonthAndDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
        for(int i = 1;i <= lastMonthAndDay ;i++) {
			for(int j = 0; j < attendance.size(); j++) {
				if(i != attendance.get(j).getDays()) {
					
				}
				
			}
		}
        
		return attendance;
	}
	
//	public Users userCreate(String password, String userName, String role, Integer departmentId, Date startDate) {
//		Users users = userSearchMapper.insert(password, userName, role, departmentId, startDate);
//		return users;
//	}
}
