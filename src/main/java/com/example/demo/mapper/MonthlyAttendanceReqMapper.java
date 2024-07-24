package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.MonthlyAttendanceReq;

@Mapper
public interface MonthlyAttendanceReqMapper {
	
	//ステータスが１（承認待ち）指定範囲取得
	List<MonthlyAttendanceReq> selectApprovalPending(@Param("status")Integer status);
	
//	//登録
	void insertMonthlyAttendanceReq(MonthlyAttendanceReq monthlyAttendanceReq);
//	
//	//日付指定範囲削除
//	int deleteByYearMonth(@Param("userId")Integer userId, @Param("targetDate")LocalDate targetDate, @Param("endDate")LocalDate endDate);
}