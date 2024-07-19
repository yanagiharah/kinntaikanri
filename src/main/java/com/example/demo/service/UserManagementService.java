package com.example.demo.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.UserSearchMapper;
import com.example.demo.model.ManagementForm;
import com.example.demo.model.Users;

@Service
public class UserManagementService {
  @Autowired
  public UserSearchMapper userSearchMapper;
  
	public Users userSearchListUp(String userName) {
		Users users = userSearchMapper.selectByPrimaryKey(userName);		
		return users;
	}
	
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
	
	public void userDelete(ManagementForm managementForm) {
		 userSearchMapper.delete(managementForm);
	}
}
