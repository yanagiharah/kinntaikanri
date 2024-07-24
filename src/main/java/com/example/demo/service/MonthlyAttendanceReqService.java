package com.example.demo.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.MonthlyAttendanceReqMapper;
import com.example.demo.model.AttendanceFormList;
import com.example.demo.model.MonthlyAttendanceReq;

@Service
public class MonthlyAttendanceReqService {
  @Autowired
  public MonthlyAttendanceReqMapper monthlyAttendanceReqMapper;
  
  public List<MonthlyAttendanceReq> selectApprovalPending() {
	  //1は承認待ち
	  List<MonthlyAttendanceReq> monthlyAttendanceReq = monthlyAttendanceReqMapper.selectApprovalPending(1);
	  for(int i =0 ; i<monthlyAttendanceReq.size() ; i++) {
		  //date型の年と日をinteger型で取得する
		  SimpleDateFormat getYears = new SimpleDateFormat("yyyy");
		  monthlyAttendanceReq.get(i).setYears(Integer.valueOf(getYears.format(monthlyAttendanceReq.get(i).getTargetYearMonth())));
		  SimpleDateFormat getMonth = new SimpleDateFormat("MM");
		  monthlyAttendanceReq.get(i).setMonth(Integer.valueOf(getMonth.format(monthlyAttendanceReq.get(i).getTargetYearMonth())));
	  }
		return monthlyAttendanceReq;
		
	}
  
  public void monthlyAttendanceReqCreate(MonthlyAttendanceReq monthlyAttendanceReq, AttendanceFormList attendanceFormList){
	  
	  //申請月の１日をtargetYearMonthにいれる
	  monthlyAttendanceReq.setTargetYearMonth(java.sql.Date.valueOf(attendanceFormList.getAttendanceList().get(0).getAttendanceDateS()));
	  //今日の日付をmonthlyAttendanceReqDateに入れる
	  Date date = new Date();
	  monthlyAttendanceReq.setMonthlyAttendanceReqDate(date);
	  //statusに１を入れる
	  monthlyAttendanceReq.setStatus(1);
	  
		System.out.print("『』→"+monthlyAttendanceReq);
		
	  monthlyAttendanceReqMapper.insertMonthlyAttendanceReq(monthlyAttendanceReq);
	  
  }
  
  public void approvalStatus(Integer userId) {
	  monthlyAttendanceReqMapper.approvalStatus(userId);
	  }
  
  public void rejectedStatus(Integer userId) {
	  monthlyAttendanceReqMapper.rejectedStatus(userId);
	  }
}
