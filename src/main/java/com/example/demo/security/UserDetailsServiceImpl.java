package com.example.demo.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.service.LoginService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private LoginService loginService;
	
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		try {
			 Date today = new Date();
			var user = loginService.loginCheck(Integer.valueOf(userName));
			if (user == null) {
				throw new UsernameNotFoundException("not found :" + userName);
			}else if( user.getStartDate().compareTo(today) == 1) {
				throw new UsernameNotFoundException("not found :" + userName);
			}
			return new CustomUsersDetails(user);
		} catch (Exception e) {
			throw new UsernameNotFoundException("not found :" + userName);
		}
	}
		
//		String inputRole =null;
//		
//		//security用の権限の分岐(二種類しかない)
//		if(user.getRole().equals("Admin")) {
//			inputRole ="ADMIN";
//		}else {
//			inputRole ="USER";
//		}
		

		
//		return  User.withUsername(String.valueOf(user.getUserId()))
//				.password(user.getPassword())
//				.roles(inputRole)
//				.build(); //このタイミングでuser(security用のUserクラス)取得したデータとリクエストのデータをチェックする
//	}
}
////取り出すメソッドは編集する
//var user = loginUsersDao.searchByUserName(userName);
////Users user= null;