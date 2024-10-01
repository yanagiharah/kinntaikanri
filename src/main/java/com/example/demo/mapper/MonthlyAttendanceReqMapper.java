package com.example.demo.mapper;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.MonthlyAttendanceReq;

@Mapper
public interface MonthlyAttendanceReqMapper {
	
	//ステータスが１（承認待ち）指定範囲取得
	List<MonthlyAttendanceReq> selectApprovalPending(@Param("status")Integer status);
	
	//特定のユーザーの承認済み月次勤怠をリストで取得(一般ユーザーのカレンダーで活性化させるやつを選択して制御するために必要)
	List<MonthlyAttendanceReq> selectApproved(@Param("userId") Integer userId);
	
	//変更依頼ステータスが1の月次勤怠をリストで取得
	List<MonthlyAttendanceReq> selectHasChangeReq(LocalDate targetYearMonth);
	
	//指定された年月とユーザーIDで月次勤怠テーブルからデータを取得（statusチェックのため）
	MonthlyAttendanceReq selectTargetYearMonthStatus(@Param("targetYearMonth")Date targetYearMonth, @Param("userId")Integer userId);
	
	//先月の月次勤怠テーブルで承認待ち(status=1)の有無を取得
	Integer selectMonthlyAttendanceReq(Date lastMonth); 
	
	//登録
	void insertMonthlyAttendanceReq(MonthlyAttendanceReq monthlyAttendanceReq);
	
	//却下された月次勤怠の申請内容を更新
	void updateMonthlyAttendanceReq(MonthlyAttendanceReq monthlyAttendanceReq);
	
	//ステータス承認
	void approvalStatus(Integer userId, String targetYearMonth);
	
	//ステータス却下
	void rejectedStatus(Integer userId, String targetYearMonth);
	
	//月次勤怠訂正依頼の更新文
	void changeRequestMonthlyAttendanceReq(Integer userId, String targetYearMonth, String changeReason);
	
	//月次勤怠訂正の承認更新文
	void changeApprovalMonthlyAttendanceReq(Integer userId, String targetYearMonth);
	
	//月次勤怠訂正の却下更新文
	void changeRejectionMonthlyAttendanceReq(Integer userId, String targetYearMonth, String rejectionReason);
}