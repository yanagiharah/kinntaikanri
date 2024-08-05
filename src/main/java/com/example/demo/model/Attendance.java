package com.example.demo.model;

import java.util.Date;

import lombok.Data;

@Data
public class Attendance {
	
	private Integer attendanceId;//勤怠ID
	
	private Integer userId;//ユーザーID
	
	private String userName;//ユーザーの名前
	
	private Integer  status;//勤務状況
	
	private Date attendanceDate;//勤務日
	
	private String attendanceDateS;//画面表示用勤務日
	
	private String startTime;//勤務開始時刻
	
	private String endTime;//勤務終了時刻
	
	private String attendanceRemarks;//備考
	
	private Integer years;//年
	
	private Integer month;//月
	
	private Integer days;//日
	
	private String dayOfWeek;//曜日
	
	//月末が何日かを判断するためのに使用してる変数
	private Date startDate;	
	private Date endDate;
	
	
	
	
//	private Date status;
}
