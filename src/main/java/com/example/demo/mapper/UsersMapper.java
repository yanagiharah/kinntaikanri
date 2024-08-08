package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.ManagementForm;
import com.example.demo.model.Users;

@Mapper
public interface UsersMapper {
	

	//検索押下時アカウント取得
	Users selectByAccount(@Param("userName")String userName ,@Param("userId")Integer userId);
	
	//登録押下時アカウント取得
	Users selectByAccountBy(@Param("userId")Integer userId);
	
	
	//登録
	void insert(Users users);
	
	//更新
	void update(Users users);
	
	//削除
	void delete(ManagementForm managementForm);
	
	//ログイン時のアカウント存在チェック
	Users loginCheck(@Param("userId")Integer userId, @Param("password")String password);

}