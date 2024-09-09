package com.example.demo.service;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.demo.inter.MessageOutput;
import com.example.demo.mapper.UsersMapper;
import com.example.demo.model.ManagementForm;
import com.example.demo.model.Users;

@Service
public class UserManagementService {
	
	public final UsersMapper userSearchMapper;
	public final MessageOutput messageOutput;
	
	UserManagementService(UsersMapper userSearchMapper, MessageOutput messageOutput){
		this.userSearchMapper = userSearchMapper;
		this.messageOutput = messageOutput;
	}
	

	//ユーザー管理画面 検索処理
	public Users selectByAccount(String userName, Integer userId) {
		
		Users users = new Users();
		
		if(userName != null) {
			users = userSearchMapper.selectByAccount(userName, userId);
		} else {
			users = userSearchMapper.selectByAccountBy(userId);
		}
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
		users.setDepartmentId(managementForm.getDepartmentId());
		users.setStartDate(sqlDate);
		userSearchMapper.insert(users);
	}

	//ユーザー管理画面 更新処理
	public void userUpdate(ManagementForm managementForm) {
		String strDate = managementForm.getStartDate();
		Date sqlDate = java.sql.Date.valueOf(strDate);
		Users users = new Users();
		users.setUserId(managementForm.getUserId());
		users.setUserName(managementForm.getUserName());
		users.setPassword(managementForm.getPassword());
		users.setRole(managementForm.getRole());
		users.setDepartmentId(managementForm.getDepartmentId());
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
			  FieldError userName = new FieldError(managementForm.getUserName(), "userName", messageOutput.message("requiredUserName"));
			  result.addError(userName);
		}
		
		if (!managementForm.getUserName().matches("^[^ -~｡-ﾟ]+$")) {
			FieldError userName = new FieldError(managementForm.getUserName(), "userName", messageOutput.message("requiredZennkaku"));
			result.addError(userName);
		}
		
		if(managementForm.getPassword() == null || managementForm.getPassword() == "" ) {
			 FieldError password = new FieldError("managementForm", "password", messageOutput.message("requiredPassword"));
			  result.addError(password);
		}
		if(managementForm.getPassword().length() >= 16 ) {
			 FieldError password = new FieldError("managementForm", "password", messageOutput.message("CharacterLimit"));
			  result.addError(password);
		}
		if(managementForm.getPassword().matches(".*[^ -~｡-ﾟ]+.*")) {
			 FieldError password = new FieldError("managementForm", "password", messageOutput.message("requiredHannkaku"));
			 result.addError(password);
		}
		if(managementForm.getRole() == null||managementForm.getRole() == "") {
			FieldError role = new FieldError("managementForm", "role", messageOutput.message("requiredRole"));
			 result.addError(role);
		}
		if(managementForm.getDepartmentId() == 0) {
			FieldError department = new FieldError("managementForm", "department", messageOutput.message("requiredDepartment"));
			 result.addError(department);
		}
		if(managementForm.getUserId() == null||managementForm.getUserId() == 0) {
			FieldError userId = new FieldError("managementForm", "userId", messageOutput.message("requiredUserId"));
			 result.addError(userId);
		}
		if (!"9999/99/99".equals(managementForm.getStartDate().trim())) {
			if(!managementForm.getStartDate().matches("^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$")|| managementForm.getStartDate() == null||managementForm.getStartDate() == ""||managementForm.getStartDate().length() != 10) {
				FieldError startDate = new FieldError("managementForm", "startDate", messageOutput.message("startDateCheck"));
				FieldError startDate2 = new FieldError("managementForm", "startDate", messageOutput.message("acountStopCheck"));
				result.addError(startDate);
				result.addError(startDate2);
			}
		}
	}
}
