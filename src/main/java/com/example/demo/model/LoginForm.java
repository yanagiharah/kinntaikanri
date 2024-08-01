package com.example.demo.model;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginForm {
	
	@NotBlank(message="ユーザーIDは必須です")
	@Length(max = 16)
	//@Pattern(regexp = "^[0-9]+$")
	public String userId;
	
	@NotBlank(message="パスワードは必須です")
	@Length(max = 16)
	//@Pattern(regexp = "^[a-zA-Z0-9]+$")
	public String password; 
}
