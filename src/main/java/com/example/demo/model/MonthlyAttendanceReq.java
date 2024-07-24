package com.example.demo.model;

import java.util.Date;

import lombok.Data;

@Data
public class MonthlyAttendanceReq {
	
	private String userName;//ユーザー名
	
	private Integer monthlyAttendanceReqId;//月次勤怠申請ID
	
	private Integer userId;//ユーザーID
	
	private Date targetYearMonth;//申請対象年月
	
	private Date monthlyAttendanceReqDate;//申請日
	
	private Integer  status;//承認状況
	
	
	

}
