window.onload = function() {
	const submitButton = document.getElementById('send');
	submitButton.disabled = true; // 初期状態でボタンを無効にする

	// 入力フィールドのイベントリスナーを設定
	document.getElementById('userId').addEventListener('input', function() {
		validateInput(this);
		checkInput();
	});

	document.getElementById('address').addEventListener('input', function() {
		checkInput();
	});
};

function validateInput(input) {
	input.value = input.value.replace(/[^0-9]/g, '');
}

function checkInput() {
	const input1 = document.getElementById('userId').value;
	const input2 = document.getElementById('address').value;
	const submitButton = document.getElementById('send');

	if (input1 && input2) {
		submitButton.disabled = false;
	} else {
		submitButton.disabled = true;
	}
}