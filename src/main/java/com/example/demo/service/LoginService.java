package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.Users;

@Service
public class LoginService {
  @Autowired
  private UserMapper userMapper;
	public Users LoginListUp(Integer userId ,String password) {
		Users users = userMapper.selectByPrimaryKey(userId, password);
		return users;
		
	}
}
