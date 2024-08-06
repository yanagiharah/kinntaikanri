package com.example.demo.model;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginForm {
	
	@NotBlank(message="ユーザーIDは必須です")
	@Length(max = 16 ,message="不正な入力です")
	@Pattern(regexp = "^[0-9]+$" ,message="ユーザーID、パスワードが不正、もしくはユーザーが無効です。")
	public String userId;
	
	@NotBlank(message="パスワードは必須です")
	@Length(max = 16 ,message="不正な入力です")
	@Pattern(regexp = "^[a-zA-Z0-9]+$" ,message="ユーザーID、パスワードが不正、もしくはユーザーが無効です。")
	public String password; 
}
