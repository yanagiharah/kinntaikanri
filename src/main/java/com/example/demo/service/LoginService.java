package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.UsersMapper;
import com.example.demo.model.Users;

@Service
public class LoginService {
  @Autowired
  private UsersMapper usersMapper;
	public Users LoginListUp(Integer userId ,String password) {
		Users users = usersMapper.selectByPrimaryKey(userId, password);
		return users;
		
	}
}
