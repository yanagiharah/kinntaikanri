package com.example.demo.Factory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.example.demo.model.Department;
import com.example.demo.model.ManagementForm;
import com.example.demo.model.Users;

@Component
public class UserManagementFactory {
	
	/**
	 * {@link ManagementForm} のデータを使用して、新しい {@link Users} オブジェクトを作成します。
	 * 
	 * <p>このメソッドは、ユーザー管理フォームから取得したデータを基に {@link Users} オブジェクトを作成します。
	 * 開始日は文字列形式から SQL 日付形式に変換され、他の属性（ユーザーID、名前、パスワード、役割、部門IDなど）
	 * も {@link ManagementForm} から {@link Users} にコピーされます。</p>
	 * 
	 * @param managementForm ユーザー情報を含む {@link ManagementForm} オブジェクト
	 * @return 作成された {@link Users} オブジェクト
	 */
	public Users usersCreate(ManagementForm managementForm) {
		String strDate = managementForm.getStartDate();
		Date sqlDate = java.sql.Date.valueOf(strDate);
		Users users = new Users();
		users.setUserId(managementForm.getUserId());
		users.setUserName(managementForm.getUserName());
		users.setPassword(managementForm.getPassword());
		users.setRole(managementForm.getRole());
		users.setDepartmentId(managementForm.getDepartmentId());
		users.setStartDate(sqlDate);
		return users;
	}
	
	/**
	 * DBから取得したユーザー情報を基に、新しい {@link ManagementForm} オブジェクトを作成します。
	 * 
	 * <p>このメソッドは、DBから取得したユーザー情報（{@link Users}）を {@link ManagementForm} に
	 * マッピングし、返します。Department情報も合わせて設定します。</p>
	 *
	 * @param managementForm 元のフォーム情報（無視されることがある）
	 * @param users DBから取得したユーザー情報
	 * @return DBからのユーザー情報を基にした {@link ManagementForm} オブジェクト
	 */
	public ManagementForm dbAccount(Users users,List<Department> department) {
		ManagementForm dbAccount = new ManagementForm();
		dbAccount.setUserId(users.getUserId());
		dbAccount.setUserName(users.getUserName());
		dbAccount.setPassword(users.getPassword());
		dbAccount.setRole(users.getRole());
		dbAccount.setDepartmentId(users.getDepartmentId());
		dbAccount.setDepartment(department);
		String str = new SimpleDateFormat("yyyy-MM-dd").format(users.getStartDate());
		dbAccount.setStartDate(str);
		return dbAccount;
	}
	
	/**
	 * ユーザー名を基に、新しい {@link ManagementForm} オブジェクトを作成します。
	 * 
	 * <p>このメソッドは、ユーザー名とランダムに生成したユーザーIDを持つ
	 * {@link ManagementForm} を作成し、返します。Department情報も合わせて設定します。</p>
	 *
	 * @param userName 新しいユーザーの名前
	 * @return ランダムなユーザーIDと指定されたユーザー名を持つ {@link ManagementForm} オブジェクト
	 */
	public  ManagementForm newAccontCreate(String userName,List<Department> department) {
		Random rand = new Random();
		ManagementForm newAccont = new ManagementForm();
		Integer randomNumber = rand.nextInt(2147483647);
		//RandomNumberがdbにあるか確認する処理を行う
		//dbに存在した場合再度RandomNumberの生成forぶんで繰り返す、dbに存在しない数字がでるまで
		newAccont.setUserId(randomNumber);
		newAccont.setUserName(userName);
		newAccont.setDepartment(department);
		return newAccont;
	}
}
