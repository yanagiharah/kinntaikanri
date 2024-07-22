package com.example.demo.model;

import java.util.Date;

import lombok.Data;

@Data
public class MonthlyAttendanceReq {
	
	private Integer monthlyAttendanceReqId;//月次勤怠申請ID
	
	private Integer userId;//ユーザーID
	
	private Date targetYearMonth;//ユーザーの名前
	
	private Date monthlyAttendanceReqDate;
	
	private Integer  status;//勤務状況
	
	
	

}
