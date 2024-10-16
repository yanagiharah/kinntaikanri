document.addEventListener("DOMContentLoaded", function() {
	//訂正申請理由の見出し表示メソッド表示準備
	const shortReason = document.querySelectorAll(".shortChangeReason");
	const maxLength = 10;

	shortReason.forEach(function(element) {
		const fullReason = element.innerText;
		const truncatedText = truncateText(fullReason, maxLength);
		element.innerText = truncatedText;
	});

	//	一般用カレンダー限定表示用メソッド
	const approvedMonthStrings = approvedMonths.map(req => req.stringTargetYearMonth).filter(month => month != null);

	const monthInput = document.getElementById('approvedMonths');
	const messageDiv = document.getElementById('message');
	const displayInput = document.getElementById('display');

	function updateMonthStyles() {
		const selectedMonth = monthInput.value;
		if (approvedMonthStrings.includes(selectedMonth)) {
			//選択した月がデータに入っていた場合
			monthInput.classList.add('highlight');
			monthInput.classList.remove('dim');
			messageDiv.style.display = 'none';
			displayInput.disabled = false;//表示ボタン使用可
		} else {
			monthInput.classList.add('dim');
			monthInput.classList.remove('highlight');
			messageDiv.style.display = 'block';//メッセージ表示
			displayInput.disabled = true;//表示ボタン使用不可
		}
	}

	// 初期表示の設定
	updateMonthStyles();

	// 選択が変更されたときの処理
	monthInput.addEventListener('input', updateMonthStyles);
});

//承認ボタンオンオフのメソッド
function disabledApprove() {
	const willCorrectReason = document.getElementById('willCorrectReason');
	const willCorrectInput = document.getElementById('willCorrect');
	willCorrectInput.disabled = willCorrectReason.value.trim() === '';
}
//却下ボタンオンオフのメソッド
function disabledReject() {
	const rejectReason = document.getElementById('rejectReason');
	const rejectInput = document.getElementById('reject');
	const approveInput = document.getElementById('approve');

	if (rejectReason.value.trim() === '') {
		approveInput.disabled = false;
		rejectInput.disabled = true;
	} else {
		approveInput.disabled = true;
		rejectInput.disabled = false;
	}
}
//訂正申請理由縮小表示メソッド
function truncateText(text, maxLength) {
	if (text.length > maxLength) {
		return text.substring(0, maxLength);
	}
	return text;
}
