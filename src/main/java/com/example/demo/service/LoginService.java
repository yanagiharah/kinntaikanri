package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.MustUser;

@Service
public class LoginService {
  @Autowired
  public UserMapper userMapper;
	public MustUser LoginListUp(Integer userId ,char password) {
		MustUser mustUser = userMapper.selectByPrimaryKey(userId, password);
		return mustUser;
		
	}
}
