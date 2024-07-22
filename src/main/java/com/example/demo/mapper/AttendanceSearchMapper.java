package com.example.demo.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.Attendance;

@Mapper
public interface AttendanceSearchMapper {
	
	//日付指定範囲取得
	List<Attendance> selectByYearMonth(@Param("userId")Integer userId, @Param("targetDate")LocalDate targetDate, @Param("endDate")LocalDate endDate);
	
	//登録
	void insert(List<Attendance> attendance);
	
	//日付指定範囲削除
	int deleteByYearMonth(@Param("userId")Integer userId, @Param("targetDate")LocalDate targetDate, @Param("endDate")LocalDate endDate);
}