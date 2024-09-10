document.addEventListener("DOMContentLoaded", function() {
	 
	 //各入力欄の要素とボタンの要素を取得し定数に代入
	 const checkOldDepartment = document.getElementById("activeDepartmentSelect");
	 const checkNewDepartment = document.getElementById("departmentAction");
	 const checkDeletedDepartment = document.getElementById("deactiveDepartmentSelect");
	 const registrationButton = document.getElementById("registration");
	 const changeButton = document.getElementById("change");
	 const deleteButton = document.getElementById("delete");
	 const restoreButton = document.getElementById("restore");
	 
	 //入力欄へのユーザー操作を監視
	 checkOldDepartment.addEventListener("input", checkTables);
	 checkDeletedDepartment.addEventListener("input", checkTables);
	 checkNewDepartment.addEventListener("input", checkTables);
	 
	 //ボタンの活性・非活性を制御する関数
	 function checkTables() {
		 
		 const oldDepartmentValue = checkOldDepartment.value.trim();
		 const newDepartmentValue = checkNewDepartment.value.trim();
		 const deletedDepartmentValue = checkDeletedDepartment.value.trim();
		 
		 //登録ボタン
		 registrationButton.disabled = !(
			oldDepartmentValue === ""
			&& newDepartmentValue !== ""
			&& deletedDepartmentValue === ""
		 );

		 //変更ボタン
		 changeButton.disabled = !(
			oldDepartmentValue !== ""
			&& newDepartmentValue !== ""
			&& deletedDepartmentValue === ""
			&& oldDepartmentValue !== newDepartmentValue
		 );
		 
		 //削除ボタン
		 deleteButton.disabled = !(
			oldDepartmentValue !== ""
			&& newDepartmentValue === ""
			&& deletedDepartmentValue === ""
		 );

		 //復元ボタン
		 restoreButton.disabled = !(
			oldDepartmentValue === ""
			&& newDepartmentValue === ""
			&& deletedDepartmentValue !== ""
		 );					   
	 }
	 
	 //ページ読込み時にボタンの状態をチェック
	 checkTables();
});