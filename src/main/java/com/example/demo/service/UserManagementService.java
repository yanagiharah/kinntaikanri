package com.example.demo.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.UserSearchMapper;
import com.example.demo.model.Users;

@Service
public class UserManagementService {
  @Autowired
  public UserSearchMapper userSearchMapper;
	public Users userSearchListUp(String userName) {
		Users users = userSearchMapper.selectByPrimaryKey(userName);		
		return users;
	}
	
	public Users userCreate(String password, String userName, String role, Integer departmentId, Date startDate) {
		Users users = userSearchMapper.insert(password, userName, role, departmentId, startDate);
		return users;
	}
}
