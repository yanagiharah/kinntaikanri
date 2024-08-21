package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.model.Department;

@Mapper
public interface DepartmentMapper {
	
	//日付指定範囲取得
	List<Department> selectDepartment();
	
	//登録
//	void insert(Attendance attendance);
	
	//日付指定範囲削除
//	int deleteByAttendanceOfMonth(@Param("userId")Integer userId, @Param("targetDate")Date targetDate, @Param("endDate")Date endDate);
}