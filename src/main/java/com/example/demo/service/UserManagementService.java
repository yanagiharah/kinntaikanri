package com.example.demo.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.demo.mapper.UserSearchMapper;
import com.example.demo.model.ManagementForm;
import com.example.demo.model.Users;

@Service
public class UserManagementService {
  @Autowired
  public UserSearchMapper userSearchMapper;

	//ユーザー管理画面 検索処理
	public Users userSearchListUp(String userName) {
		Users users = userSearchMapper.selectByPrimaryKey(userName);
		return users;
	}

	//ユーザー管理画面 新規登録処理
	public void userCreate(ManagementForm managementForm) {
		String strDate = managementForm.getStartDate();
		Date sqlDate = java.sql.Date.valueOf(strDate);
		Users users = new Users();
		users.setUserId(managementForm.getUserId());
		users.setUserName(managementForm.getUserName());
		users.setPassword(managementForm.getPassword());
		users.setRole(managementForm.getRole());
		users.setStartDate(sqlDate);
		userSearchMapper.insert(users);
	}

	//ユーザー管理画面 更新処理
	public void userUpdate(ManagementForm managementForm) {
		String strDate = managementForm.getStartDate();
		Date sqlDate = java.sql.Date.valueOf(strDate);
		Users users = new Users();
		users.setUserId(managementForm.getUserId());
		users.setPassword(managementForm.getPassword());
		users.setRole(managementForm.getRole());
		users.setStartDate(sqlDate);
		userSearchMapper.update(users);
	}
	
	//ユーザー管理画面 削除処理
	public void userDelete(ManagementForm managementForm) {
		userSearchMapper.delete(managementForm);
	}
	
	//ユーザー管理画面 登録内容エラーチェック
	public void errorCheck(ManagementForm managementForm,BindingResult result) {
		
		if (managementForm.getUserName() == null ||managementForm.getUserName() == "") {
			  FieldError userName = new FieldError(managementForm.getUserName(), "userName", "エラー");
			  result.addError(userName);
		}
		if(managementForm.getPassword() == null || managementForm.getPassword() == "" ) {
			 FieldError password = new FieldError("managementForm", "password", "パスワード必須です");
			  result.addError(password);
		}
		if(managementForm.getPassword().length() >= 16 ) {
			 FieldError password = new FieldError("managementForm", "password", "16未満で入力してください");
			  result.addError(password);
		}
		if(managementForm.getPassword().matches("^[^ -~｡-ﾟ]+$")) {
			 FieldError password = new FieldError("managementForm", "password", "半角文字で入力してください");
			 result.addError(password);
		}
		if(managementForm.getRole() == null||managementForm.getRole() == "") {
			FieldError role = new FieldError("managementForm", "role", "権限は必須です");
			 result.addError(role);
		}
		if (!"9999-99-99".equals(managementForm.getStartDate().trim())) {
			if(!managementForm.getStartDate().matches("^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$")|| managementForm.getStartDate() == null||managementForm.getStartDate() == ""||managementForm.getStartDate().length() != 10) {
				FieldError startDate = new FieldError("managementForm", "startDate", "入力日は必須です");
				 result.addError(startDate);
			}
		}
	}
}
