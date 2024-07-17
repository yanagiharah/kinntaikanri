package com.example.demo.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.Attendance;
import com.example.demo.model.Users;

@Mapper
public interface AttendanceSearchMapper {
	
	//全件取得
	List<Users> selectAll();
	
	//１件取得
	Attendance selectByPrimaryKey(@Param("userId")Integer userId, @Param("startDate")Date startDate, @Param("endDate")Date endDate);
	
	//登録
	int insert(Users user);
	
	//更新
	int updateByPrimaryKey(Users user);
	
	//削除
	int deleteByPrimaryKey(Long id);
}