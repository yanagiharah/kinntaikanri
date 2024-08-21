package com.example.demo.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.demo.mapper.DepartmentMapper;
import com.example.demo.mapper.UsersMapper;
import com.example.demo.model.Department;
import com.example.demo.model.ManagementForm;
import com.example.demo.model.Users;

@Service
public class UserManagementService {
	@Autowired
	  public UsersMapper userSearchMapper;
	@Autowired
	  public DepartmentMapper departmentMapper;
	
	//ユーザー管理画面起動時の部署プルダウンに適用させる為に部署を取得
	public List<Department> departmentSearchListUp(){
		List<Department> department = departmentMapper.selectDepartment();
		return department;
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
			  FieldError userName = new FieldError(managementForm.getUserName(), "userName", "ユーザー名は必須です");
			  result.addError(userName);
		}
		
		if (!managementForm.getUserName().matches("^[^ -~｡-ﾟ]+$")) {
			FieldError userName = new FieldError(managementForm.getUserName(), "userName", "ユーザー名は全角で入力してください");
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
		if(managementForm.getPassword().matches(".*[^ -~｡-ﾟ]+.*")) {
			 FieldError password = new FieldError("managementForm", "password", "半角文字で入力してください");
			 result.addError(password);
		}
		if(managementForm.getRole() == null||managementForm.getRole() == "") {
			FieldError role = new FieldError("managementForm", "role", "権限は必須です");
			 result.addError(role);
		}
		if(managementForm.getDepartmentId() == 1) {
			FieldError department = new FieldError("managementForm", "department", "所属部署は必須です");
			 result.addError(department);
		}
		if(managementForm.getUserId() == null||managementForm.getUserId() == 0) {
			FieldError userId = new FieldError("managementForm", "userId", "ユーザーIDは必須です");
			 result.addError(userId);
		}
		if (!"9999/99/99".equals(managementForm.getStartDate().trim())) {
			if(!managementForm.getStartDate().matches("^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$")|| managementForm.getStartDate() == null||managementForm.getStartDate() == ""||managementForm.getStartDate().length() != 10) {
				FieldError startDate = new FieldError("managementForm", "startDate", "利用開始日はyyyy-mm-ddで必ず半角で入力してください");
				FieldError startDate2 = new FieldError("managementForm", "startDate", "休職やアカウントの一時停止を行いたい場合は9999/99/99で入力してください");
				result.addError(startDate);
				result.addError(startDate2);
			}
		}
	}
}
