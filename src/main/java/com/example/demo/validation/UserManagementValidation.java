package com.example.demo.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.demo.inter.MessageOutput;
import com.example.demo.model.ManagementForm;

@Component
public class UserManagementValidation {
	
	private final MessageOutput messageOutput;
	
	UserManagementValidation(MessageOutput messageOutput){
		this.messageOutput = messageOutput;
	}
	
	/**
	 * ユーザー名の検索時に入力チェックを行い、エラーがある場合は
	 * {@link BindingResult} にエラー情報を追加します。
	 *
	 * <p>このメソッドは、ユーザー名が空である、長すぎる、または全角でない場合に
	 * エラーメッセージを設定し、エラーを管理します。</p>
	 *
	 * @param userName チェック対象のユーザー名
	 * @param result エラー情報を格納するための {@link BindingResult} オブジェクト
	 */
	public void errorCheck(String userName,BindingResult result) {
		if (userName == null || userName == "") {
			FieldError userName2 = new FieldError("managementForm", "userName", messageOutput.message("requiredUserName"));
			result.addError(userName2);
		} else if (userName.length() > 20) {
			 FieldError userName2 = new FieldError("managementForm", "userName", messageOutput.message("overUserName"));
			  result.addError(userName2);
		}
		else if (!userName.matches("^[^ -~｡-ﾟ]+$")) {
			FieldError userName2 = new FieldError("managementForm", "userName", messageOutput.message("requiredZennkaku"));
			result.addError(userName2);
		}
	}
	
	/**
	 * アカウントの登録時に入力チェックを行い、エラーがある場合は
	 * {@link BindingResult} にエラー情報を追加します。
	 *
	 * <p>このメソッドは、formに入力された内容に対して
	 * エラーメッセージを設定し、エラーを管理します。</p>
	 *
	 * @param managementForm チェック対象のアカウント内容
	 * @param result エラー情報を格納するための {@link BindingResult} オブジェクト
	 */
	public void errorCheck(ManagementForm managementForm,BindingResult result) {
		
		if(managementForm.getUserId() == null||managementForm.getUserId() == 0) {
			FieldError userId = new FieldError("managementForm", "userId", messageOutput.message("requiredUserId"));
			 result.addError(userId);
			 return;
		}
		if (managementForm.getUserName() == null ||managementForm.getUserName() == "") {
			  FieldError userName = new FieldError(managementForm.getUserName(), "userName", messageOutput.message("requiredUserName"));
			  result.addError(userName);
		}
		if (!managementForm.getUserName().equals("") && !managementForm.getUserName().matches("^[^ -~｡-ﾟ]+$")) {
			FieldError userName = new FieldError(managementForm.getUserName(), "userName", messageOutput.message("requiredZennkaku"));
			result.addError(userName);
		}
		if(managementForm.getPassword() == null || managementForm.getPassword() == "" ) {
			 FieldError password = new FieldError("managementForm", "password", messageOutput.message("requiredPassword"));
			  result.addError(password);
		}
		if(managementForm.getPassword().length() >= 16 ) {
			 FieldError password = new FieldError("managementForm", "password", messageOutput.message("CharacterLimit"));
			  result.addError(password);
		}
		if(managementForm.getPassword().matches(".*[^ -~]+.*")) {
			 FieldError password = new FieldError("managementForm", "password", messageOutput.message("requiredHannkaku"));
			 result.addError(password);
		}
		if(managementForm.getRole() == null||managementForm.getRole() == "") {
			FieldError role = new FieldError("managementForm", "role", messageOutput.message("requiredRole"));
			 result.addError(role);
		}
		if(managementForm.getDepartmentId() == 0) {
			FieldError department = new FieldError("managementForm", "department", messageOutput.message("requiredDepartment"));
			 result.addError(department);
		}
		if (managementForm.getTel().equals("")) {
		    FieldError tel = new FieldError("managementForm", "tel", messageOutput.message("requiredTel"));
		    result.addError(tel);
		}
		if (!managementForm.getTel().equals("") && !managementForm.getTel().matches("^\\d{2,4}-\\d{2,4}-\\d{4}$")) {
		    FieldError tel = new FieldError("managementForm", "tel", messageOutput.message("fraudTel"));
		    result.addError(tel);
		}
		if (managementForm.getAddress().equals("")) {
		    FieldError address = new FieldError("managementForm", "address", messageOutput.message("requiredEmail"));
		    result.addError(address);
		}
		if(!managementForm.getAddress().equals("") && !managementForm.getAddress().matches("^[a-zA-Z0-9_+-]+(\\.[a-zA-Z0-9_+-]+)*@([a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9]*\\.)+[a-zA-Z]{2,}$")) {
			 FieldError address = new FieldError("managementForm", "address", messageOutput.message("fraudEmail"));
			 result.addError(address);
		}
		if (!"9999/99/99".equals(managementForm.getStartDate().trim())) {
			if(!managementForm.getStartDate().matches("^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$")|| managementForm.getStartDate() == null||managementForm.getStartDate() == ""||managementForm.getStartDate().length() != 10) {
				FieldError startDate = new FieldError("managementForm", "startDate", messageOutput.message("startDateCheck"));
				FieldError startDate2 = new FieldError("managementForm", "startDate", messageOutput.message("acountStopCheck"));
				result.addError(startDate);
				result.addError(startDate2);
			}
		}
	}
}
