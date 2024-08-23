
// ページが完全に読み込まれた後に実行する
$(document).ready(function() {
	setTodayDate(); // 今日の日付をセット
});

//カレンダーに現在の日付を登録
function setTodayDate() {
	var today = new Date();
	var yyyy = today.getFullYear();
	var mm = ("0" + (today.getMonth() + 1)).slice(-2);
	var dd = ("0" + today.getDate()).slice(-2);
	document.getElementById("today").value = yyyy + '-' + mm + '-' + dd;
}

//表に対しての提出ボタンの活性化処理
document.addEventListener('DOMContentLoaded', function() {
	// テーブル内の「作業時間」と「作業内容」のすべての入力フィールドを取得
	const dailyReportDetailTime = document.querySelectorAll('input[name^="dailyReportDetailForm"][name$=".dailyReportDetailTime"]');
	const content = document.querySelectorAll('input[name^="dailyReportDetailForm"][name$=".content"]');

	// 作業時間の入力フィールドに対してinputイベントを設定
	dailyReportDetailTime.forEach(input => {
		input.addEventListener('input', function(event) {
			if (event.target.value.trim() == "" && event.target.value.trim() == "") {

				document.getElementById("submission").disabled = true;
			} else {
				document.getElementById("submission").disabled = false;
			}
		});
	});

	// 作業内容の入力フィールドに対してinputイベントを設定
	content.forEach(input => {
		input.addEventListener('input', function(event) {
			if (event.target.value.trim() == "" && event.target.value.trim() == "") {

				//if文の条件が真の場合、カウントを+1する　カウントが入っている場合非活性
				//もしかしたら表の一行のみにforEachを起こしているので
				//全行数に対してforEachを行う作りに変える必要がある

				//              	console.log("作業時間が入力されました: " + event.target.value);
				document.getElementById("submission").disabled = true;
			} else {
				document.getElementById("submission").disabled = false;
			}
		});
	});
});

//★戻るボタンに何か使う場合に残しておく
//$(document).ready(function() {
//    // 戻るボタンのクリックイベントを設定
//    $('#back').click(function(event) {
//        event.preventDefault(); // フォームの送信を防止（必要に応じて）
//        console.log('戻るボタンがクリックされました。'); // コンソールにメッセージを表示
//    });
//    
//});