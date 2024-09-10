package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.Users;

@Mapper
public interface UsersMapper {
	
	/**
	 * 検索押下時アカウント取得
	 */
	Users selectByAccount(@Param("userName")String userName ,@Param("userId")Integer userId);
	
	/**
	 * 登録押下時アカウント取得
	 */
	Users selectByAccountBy(@Param("userId")Integer userId);
	
	/**
	 * 更新処理
	 */
	void userCreate(Users users);
	
	/**
	 * ログイン時のアカウント存在チェック
	 */
	Users loginCheck(@Param("userId")Integer userId);

}