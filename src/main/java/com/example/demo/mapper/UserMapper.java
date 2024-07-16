package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.Users;

@Mapper
public interface UserMapper {
	
	//全件取得
	List<Users> selectAll();
	
	//１件取得
	Users selectByPrimaryKey(@Param("id")Integer id, @Param("password")String password);
	
	//登録
	int insert(Users user);
	
	//更新
	int updateByPrimaryKey(Users user);
	
	//削除
	int deleteByPrimaryKey(Long id);
}