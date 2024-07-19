package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.ManagementForm;
import com.example.demo.model.Users;

@Mapper
public interface UserSearchMapper {
	
	//全件取得
	List<Users> selectAll();
	
	//１件取得
	Users selectByPrimaryKey(@Param("userName")String userName);
	
	//登録
	void insert(Users users);
	
	//更新
	void update(Users users);
	
	//削除
	void delete(ManagementForm managementForm);


}