//各ボタンの活性・非活性について、ページの読み込み完了で作動
 document.addEventListener("DOMContentLoaded", function() {
	 
	 //各入力欄の要素とボタンの要素を取得し定数に代入
	 const checkOldDepartment = document.getElementById("activeDepartmentSelect");
	 const checkDeletedDepartment = document.getElementById("deactiveDepartmentSelect");
	 const checkNewDepartment = document.getElementById("departmentAction");
	 const registrationButton = document.getElementById("registration");
	 const changeButton = document.getElementById("change");
	 const deleteButton = document.getElementById("delete");
	 const restoreButton = document.getElementById("restore");
	 
	 //入力欄へのユーザー操作を監視
	 checkOldDepartment.addEventListener("input", checkTables);
	 checkDeletedDepartment.addEventListener("input", checkTables);
	 checkNewDepartment.addEventListener("input", checkTables);
	 
	 //ボタンの活性・非活性の関数
	 function checkTables() {
		
		//登録ボタン
		if(checkNewDepartment.value.trim() === "") {
			registrationButton.disabled = true;
		} else {
			registrationButton.disabled = false; 
		}
		 
		//変更ボタン
		if(checkOldDepartment.value.trim() === "" ||
			checkNewDepartment.value.trim() === "" ||
			checkOldDepartment.value.trim() === checkNewDepartment.value.trim()) {
			changeButton.disabled = true;
		} else {
			changeButton.disabled = false;
		}
		
		//削除ボタン
		if(checkOldDepartment.value.trim() === "") {
			deleteButton.disabled = true;
		} else {
			deleteButton.disabled = false;
		}
		
		//復元ボタン
		if(checkDeletedDepartment.value.trim() === "") {
			restoreButton.disabled = true;
		} else {
			restoreButton.disabled = false;
		}
	 }
	 
	 //初回チェックの実行（ページ読み込み時）
	 checkTables();
});