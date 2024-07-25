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
  
  //承認申請ボタン押下(登録の方)
  public void monthlyAttendanceReqCreate(MonthlyAttendanceReq monthlyAttendanceReq, AttendanceFormList attendanceFormList){
	  
	  //申請月の１日をtargetYearMonthにいれる
	  monthlyAttendanceReq.setTargetYearMonth(java.sql.Date.valueOf(attendanceFormList.getAttendanceList().get(0).getAttendanceDateS()));
	  //今日の日付をmonthlyAttendanceReqDateに入れる
	  Date date = new Date();
	  monthlyAttendanceReq.setMonthlyAttendanceReqDate(date);
	  //statusに１を入れる(1は承認待ち)
	  monthlyAttendanceReq.setStatus(1);
	  
//		System.out.print("『』→"+monthlyAttendanceReq);
	  monthlyAttendanceReqMapper.insertMonthlyAttendanceReq(monthlyAttendanceReq);
	  
  }
  
//承認申請ボタン押下(却下されたやつの更新)
  public void updateMonthlyAttendanceReq(MonthlyAttendanceReq monthlyAttendanceReq) {
	//今日の日付をmonthlyAttendanceReqDateに入れる
	  Date date = new Date();
	  monthlyAttendanceReq.setMonthlyAttendanceReqDate(date);
//	  System.out.print("『』→"+monthlyAttendanceReq);
	  monthlyAttendanceReqMapper.updateMonthlyAttendanceReq(monthlyAttendanceReq);
  }
  
  
  //承認申請のステータス確認
  public MonthlyAttendanceReq statusCheck(Date targetYearMonth, Integer userId) {
//	  System.out.print("『』→"+targetYearMonth+"&&&"+ userId);
	  MonthlyAttendanceReq statusCheck = monthlyAttendanceReqMapper.selectTargetYearMonthStatus(targetYearMonth, userId);
//	  System.out.print("『』→"+statusCheck);
	  return statusCheck;
  }
  
  
  public void approvalStatus(Integer userId) {
	  monthlyAttendanceReqMapper.approvalStatus(userId);
	  }
  
  public void rejectedStatus(Integer userId) {
	  monthlyAttendanceReqMapper.rejectedStatus(userId);
	  }
}
