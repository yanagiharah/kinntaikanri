package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.UserSearchMapper;
import com.example.demo.model.Users;

@Service
public class UserManagementService {
  @Autowired
  public UserSearchMapper userSearchMapper;
	public Users UserSearchListUp(String name) {
		Users users = userSearchMapper.selectByPrimaryKey(name);
		return users;
		
	}
}
