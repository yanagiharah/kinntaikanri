package com.example.demo.mapper;

import java.sql.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CatchForgotMapper {
	
	//勤怠未提出者の名前一覧を取得
	public List<String> getForgotAttendanceName();
	
	//勤怠未提出者のメールアドレス一覧を取得
	public List<String> getForgotAttendanceMail(Date firstDayOfLastMonth);
	
	//ユニットマネージャーのメールアドレス一覧を取得
	public List<String> getUnitManagerMail();
	
	//マネージャーのメールアドレス一覧を取得
	public List<String> getManagerMail();
}
