package com.example.demo.mapper;

import java.util.Date;
import java.util.List;

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
	
	/**
	 * 月次勤怠の未提出者を取得
	 */
	List<Users> selectMonthlyAttendanceNotSubmittedUsers(Date lastMonth);
	
	/**
	 * システム障害発生時にAdminのリストを取得
	 */
	List<Users> selectAdmin();
	
	/**
	 * 月次勤怠の提出があった場合（1～5日の間のみ）にManagerのリストを取得
	 */
	List<Users> selectManager();
	
	/**
	 * パスワードを忘れた時にユーザーIDとメールアドレスでユーザーを探す
	 */
	Integer selectUserIdAddressCheck(Integer userId, String address);
	
	/**
	 * パスワードを忘れてユーザーIDとメールアドレスでユーザーを探して存在した際に、トークンと有効期限を更新
	 */
	void tokenUpdate (Users users);
	
	/**
	 * パスワードを忘れてメールのリンク押下後にトークンに一致するユーザーデータを取得
	 */
	Users selectToken(String token);
	
	/**
	 * パスワードを忘れてパスワード変更
	 */
	void passwordUpdate(Users users);

}