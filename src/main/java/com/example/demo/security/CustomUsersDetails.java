package com.example.demo.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.demo.model.Users;

@Service
public class CustomUsersDetails implements UserDetails{
	
	private final Users users;
	
	public CustomUsersDetails(Users users) {
		this.users = users;
	}
	
	public Users getUsers() {
        return this.users;
    }
	
	/**
	 * ロールの取得（今回は使わないのでnullをリターン）
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		   String role = users.getRole();
	        if ("Admin".equals(role)) {
	            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
	        } else {
	            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	        }
	}
	
	/**
	 * パスワードの取得
     * ログインユーザーのパスワードをGetする
	 */
	@Override
	public String getPassword() {
		return this.users.getPassword();
	}
	
	/**
	 * ユーザー名の取得
     * ログインユーザーの名前をGetする
	 */
	@Override
	public String getUsername() {
		return this.users.getUserName();
	}
	
	/**
	 * アカウントが有効期限でないか(使わないので常にtrue)
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	/**
	 * アカウントがロックされていないか(使わないので常にtrue)
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	/**
	 * 認証情報が有効期限切れでないか(使わないので常にtrue)
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	/**
	 * アカウントが有効であるかどうか(使わないので常にtrue)
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}
}
