/**
 * 
 */
$("check").on("click",function() {
		if (document.LoginForm.userId.value == "") {
			aleart("ユーザーIDを入力してください");
			return false;
		}
		if (document.LoginForm.password.value == "") {
			aleart("ユーザーIDを入力してください");
			return false;
		} return true;
	});
