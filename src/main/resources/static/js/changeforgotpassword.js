window.onload = function() {
	const submitButton = document.getElementById('check');
	submitButton.disabled = true; // 初期状態でボタンを無効にする

	// 入力フィールドのイベントリスナーを設定
	document.getElementById('newPassword').addEventListener('input', function() {
		validateInput(this);
		checkInput();
	});

	document.getElementById('checkNewPassword').addEventListener('input', function() {
		checkInput();
	});
};

function checkInput() {
	const input1 = document.getElementById('newPassword').value;
	const input2 = document.getElementById('checkNewPassword').value;
	const submitButton = document.getElementById('check');

	if (input1 == input2) {
		submitButton.disabled = false;
	} else {
		submitButton.disabled = true;
	}
}

