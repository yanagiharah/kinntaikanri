package com.example.demo.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.Users;

@Mapper
public interface UserSearchMapper {
	
	//全件取得
	List<Users> selectAll();
	
	//１件取得
	Users selectByPrimaryKey(@Param("userName")String userName);
	
	//登録
	Users insert(@Param("password")String password, @Param("userName")String userName, @Param("role")String role, @Param("departmentId")Integer departmentId, @Param("startDate")Date startDate);
	
	//更新
	int updateByPrimaryKey(Users user);
	
	//削除
	int deleteByPrimaryKey(Long id);
}