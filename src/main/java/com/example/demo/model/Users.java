package com.example.demo.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import lombok.Data;
@Component
@SessionScope
@Data
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Users implements Serializable {
	
	private Integer userId;
	
	private String userName;
	
	private String password;
	
	private String role;
	
	private Date startDate;

}
