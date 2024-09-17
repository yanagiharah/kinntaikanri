package com.example.demo.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.demo.mapper.MonthlyAttendanceReqMapper;
import com.example.demo.model.AttendanceFormList;
import com.example.demo.model.MonthlyAttendanceReq;
import com.example.demo.model.Users;

import jakarta.servlet.http.HttpSession;

@Service
public class MonthlyAttendanceReqService {
  
  private final MonthlyAttendanceReqMapper monthlyAttendanceReqMapper;
  
  private final CommonActivityService commonActivityService;
  
  MonthlyAttendanceReqService(MonthlyAttendanceReqMapper monthlyAttendanceReqMapper,CommonActivityService commonActivityService){
	  this.monthlyAttendanceReqMapper = monthlyAttendanceReqMapper;
	  this.commonActivityService = commonActivityService;
  }
  
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
  
  //承認申請ボタン押下(初めての申請の際)
  public void monthlyAttendanceReqCreate(MonthlyAttendanceReq monthlyAttendanceReq, AttendanceFormList attendanceFormList){
	  //申請月の１日をtargetYearMonthにいれる
	  String inputDate = attendanceFormList.getAttendanceList().get(0).getAttendanceDateS();
	  String conversion = inputDate.replace("/","-");
	  monthlyAttendanceReq.setTargetYearMonth(java.sql.Date.valueOf(conversion));
	  //今日の日付をmonthlyAttendanceReqDateに入れる
	  Date date = new Date();
	  monthlyAttendanceReq.setMonthlyAttendanceReqDate(date);
	  //statusに１を入れる(1は承認待ち)
	  monthlyAttendanceReq.setStatus(1);
	  monthlyAttendanceReqMapper.insertMonthlyAttendanceReq(monthlyAttendanceReq);
  }
  
  //承認申請ボタン押下(却下されたやつの更新)
  public void updateMonthlyAttendanceReq(MonthlyAttendanceReq monthlyAttendanceReq) {
	//今日の日付をmonthlyAttendanceReqDateに入れる
	  Date date = new Date();
	  monthlyAttendanceReq.setMonthlyAttendanceReqDate(date);
	  monthlyAttendanceReqMapper.updateMonthlyAttendanceReq(monthlyAttendanceReq);
  }
  
  //月次勤怠申請テーブルのステータスの更新処理の分岐確認メソッド
  public void monthlyAttendanceUpdate(Date targetYearMonth, Integer userId,MonthlyAttendanceReq monthlyAttendanceReq,AttendanceFormList attendanceFormList) {
	  MonthlyAttendanceReq monthlyAttendanceCheck = monthlyAttendanceReqMapper.selectTargetYearMonthStatus(targetYearMonth, userId);
	  if(monthlyAttendanceCheck == null) { //中身がnull、すなわち同一ユーザーかつ同一月の月次勤怠申請がテーブルにない時の処理。statusはnull
		  monthlyAttendanceReqCreate(monthlyAttendanceReq, attendanceFormList); 
	  }else if(monthlyAttendanceCheck != null && monthlyAttendanceCheck.getStatus() == 3){ //承認申請が却下されていた際の処理。statusは3
		  monthlyAttendanceReq.setTargetYearMonth(attendanceFormList.getAttendanceList().get(0).getAttendanceDate());
		  updateMonthlyAttendanceReq(monthlyAttendanceReq);  
	  }
  }
  
  //承認申請のステータス確認
  public Model submissionStatusCheck(Date targetYearMonth, Integer userId,Model model,HttpSession session) {
	  commonActivityService.usersModelSession(model,session);
	  Users users = (Users) model.getAttribute("Users");
	  MonthlyAttendanceReq statusCheck = monthlyAttendanceReqMapper.selectTargetYearMonthStatus(targetYearMonth, userId);  
	  if (statusCheck != null) {
			users.setStatus(statusCheck.getStatus());
		} else {
			users.setStatus(4);
		}
	  model.addAttribute("Users", users);
	  return model;
  }
  
  public void approvalStatus(Integer userId, String targetYearMonth) {
	  monthlyAttendanceReqMapper.approvalStatus(userId, targetYearMonth);
	  }
  
  public void rejectedStatus(Integer userId, String targetYearMonth) {
	  monthlyAttendanceReqMapper.rejectedStatus(userId, targetYearMonth);
	  }
}
