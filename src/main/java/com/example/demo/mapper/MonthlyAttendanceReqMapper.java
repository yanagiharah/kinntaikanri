package com.example.demo.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.MonthlyAttendanceReq;

@Mapper
public interface MonthlyAttendanceReqMapper {
	
	//ステータスが１（承認待ち）指定範囲取得
	List<MonthlyAttendanceReq> selectApprovalPending(@Param("status")Integer status);
	
	//指定された年月とユーザーIDで月次勤怠テーブルからデータを取得（statusチェックのため）
	MonthlyAttendanceReq selectTargetYearMonthStatus(@Param("targetYearMonth")Date targetYearMonth, @Param("userId")Integer userId);
	
//	//登録
	void insertMonthlyAttendanceReq(MonthlyAttendanceReq monthlyAttendanceReq);
//	
//	//日付指定範囲削除
//	int deleteByYearMonth(@Param("userId")Integer userId, @Param("targetDate")LocalDate targetDate, @Param("endDate")LocalDate endDate);
	
	//却下された月次勤怠の申請内容を更新
	void updateMonthlyAttendanceReq(MonthlyAttendanceReq monthlyAttendanceReq);
	
	//ステータス承認
	void approvalStatus(Integer userId);
	
	//ステータス却下
	void rejectedStatus(Integer userId);
}