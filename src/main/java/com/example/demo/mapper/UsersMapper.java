package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.Users;

@Mapper
public interface UsersMapper {
	
	//１件取得
	Users selectByPrimaryKey(@Param("userId")Integer userId, @Param("password")String password);
	
}