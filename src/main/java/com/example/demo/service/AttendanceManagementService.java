package com.example.demo.service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
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
		
		
		return attendance;
	}
	
//	public Users userCreate(String password, String userName, String role, Integer departmentId, Date startDate) {
//		Users users = userSearchMapper.insert(password, userName, role, departmentId, startDate);
//		return users;
//	}
}
