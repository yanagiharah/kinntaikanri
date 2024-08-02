package com.example.demo.model;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginForm {
	
	@NotBlank(message="ユーザーIDは必須です")
	@Length(max = 16)
	@Pattern(regexp = "^[0-9]+$" ,message="半角の数字で記入してください")
	public String userId;
	
	@NotBlank(message="パスワードは必須です")
	@Length(max = 16)
	@Pattern(regexp = "^[a-zA-Z0-9]+$" ,message="半角の英数字で記入してください")
	public String password; 
}
