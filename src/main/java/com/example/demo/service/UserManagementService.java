package com.example.demo.service;

import java.util.Date;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.example.demo.Factory.UserManagementFactory;
import com.example.demo.inter.MessageOutput;
import com.example.demo.mapper.UsersMapper;
import com.example.demo.model.ManagementForm;
import com.example.demo.model.Users;
import com.example.demo.validation.UserManagementValidation;

@Service
public class UserManagementService {
	
	private final UsersMapper usersMapper;
	private final MessageOutput messageOutput;
	private final DepartmentService departmentService;
	private final UserManagementFactory usersFactory;
	private final UserManagementValidation userManagementValidation;
	private final PasswordEncoder passwordEncoder;
	private final ModelService modelService;
	
	UserManagementService(UsersMapper usersMapper, MessageOutput messageOutput,DepartmentService departmentService,UserManagementFactory usersFactory,UserManagementValidation userManagementValidation, ModelService modelService){
		this.usersMapper = usersMapper;
		this.messageOutput = messageOutput;
		this.departmentService = departmentService;
		this.usersFactory = usersFactory;
		this.userManagementValidation = userManagementValidation;
		this.passwordEncoder = new BCryptPasswordEncoder();
		this.modelService = modelService;
	}
	
	/**
	 * ユーザーデータ存在メソッド
	 * @param 
	 * @return users
	 */
	public Users selectByAccount(String userName, Integer userId) {
		Users users = new Users();
		if(userName != null) {
			users = usersMapper.selectByAccount(userName, userId);
		} else {
			users = usersMapper.selectByAccountBy(userId);
		}
		return users;
	}
	/**
	 * ユーザー名に基づいてアカウント情報を選択し、対応する {@link ManagementForm} オブジェクトを返します。
	 * 
	 * <p>このメソッドは、入力されたユーザー名を使用してデータベースから {@link Users} 情報を検索します。
	 * ユーザーが見つかった場合は、そのユーザー情報を基にした {@link ManagementForm} オブジェクトを作成します。
	 * 見つからなかった場合は、新しいアカウントを作成し、それを {@link ManagementForm} に格納します。</p>
	 * 
	 * @param managementForm フォームから渡されたユーザー情報
	 * @return ユーザー情報が格納された {@link ManagementForm} オブジェクト
	 */
	public ManagementForm useAccountChoice(ManagementForm managementForm) {
		ManagementForm account;
		Users users = selectByAccount(managementForm.getUserName(), null);
		if (users != null) {
			account = usersFactory.dbAccount(users,departmentService.departmentSearchListUp());
		}else {
			account = usersFactory.newAccontCreate(managementForm.getUserName(),departmentService.departmentSearchListUp());
		}
		return account;
	}
	/**
	 * データベースのユーザー情報を選択し、ユーザーが存在しない場合は新しく作成します。
	 * ユーザーが存在する場合、開始日が特定の値の場合に日付を更新し、メッセージを設定します。
	 *
	 * <p>このメソッドは、指定されたユーザーIDを使ってデータベースからユーザー情報を取得します。
	 * ユーザーが存在しない場合は新しくユーザーを作成します。ユーザーが存在する場合、開始日が特定の
	 * フォーマット（"9999/99/99"）であれば、その開始日を "9999-12-31" に変更し、ユーザー情報を
	 * 更新します。メッセージをモデルに追加して、処理結果を伝えます。</p>
	 *
	 * @param managementForm 処理対象の {@link ManagementForm} オブジェクト
	 * @param model 処理結果を格納するための {@link Model} オブジェクト
	 * @return 処理結果を含む {@link Model} オブジェクト
	 */
	public Model dbActionchoice(ManagementForm managementForm, Model model) {
		managementForm.setPassword(passwordEncoder.encode(managementForm.getPassword()));
		if ("9999/99/99".equals(managementForm.getStartDate().trim())) {
			managementForm.setStartDate("9999-12-31");
			usersMapper.userCreate(usersFactory.usersCreate(managementForm));
		} else {
			usersMapper.userCreate(usersFactory.usersCreate(managementForm));
		}
		return model.addAttribute("check", messageOutput.message("update", managementForm.getUserName()));
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
		userManagementValidation.errorCheck(userName,result);
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
		userManagementValidation.errorCheck(managementForm,result);
	}
	
	//パスワードを忘れた時にユーザーIDとメールアドレスでユーザーを探す
	public Integer selectUserIdAddressCheck(Integer userId, String address) {
		Integer userIdAddressCheck = usersMapper.selectUserIdAddressCheck(userId, address);
		return userIdAddressCheck;
	}
	
	//パスワードを忘れてユーザーIDとメールアドレスでユーザーが見つかった際にトークンと有効期限を発行しデータベースに登録
	public String tokenUpdate(Integer userId) {
		Users users = usersFactory.newToken(userId);
		usersMapper.tokenUpdate(users);
		String token = users.getResetToken();
		return token;
	}
	
	//パスワードを忘れてパスワードを変更
	public void passwordChange(String password, Users user) {
		
		user.setPassword(passwordEncoder.encode(password));
		usersMapper.passwordUpdate(user);

	}
	
	//パスワードを忘れてパスワードを変更する際に、トータルに一致するユーザーの取得
	public Users selectUserToken(String token) {
		Users user = usersMapper.selectToken(token);
		return user;
	}
	
	//パスワードを忘れてパスワードを変更する際に、トークンの有効期限確認
	public Users tokenExpirationDateCheck(Users user) {
		if (user == null || user.getTokenExpiryDate().before(new Date())) {
			// トークンが無効または期限切れ
			user.setTokenExpirationDateCheck(false);
			return user;
		}else {
			user.setTokenExpirationDateCheck(true);
			return user;
		}
	}
}
