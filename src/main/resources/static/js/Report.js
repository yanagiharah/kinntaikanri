/**
 * 
 */
//$("check").on("click",function() {
//		if (document.LoginForm.userId.value == "") {
//			aleart("ユーザーIDを入力してください");
//			return false;
//		}
//		if (document.LoginForm.password.value == "") {
//			aleart("ユーザーIDを入力してください");
//			return false;
//		} return true;
//	});
	
function setTodayDate() {
            var today = new Date();
            var yyyy = today.getFullYear();
            var mm = ("0" + (today.getMonth() + 1)).slice(-2);
            var dd = ("0" + today.getDate()).slice(-2);
            document.getElementById("today").value = yyyy + '-' + mm + '-' + dd;
        }

        // ページが完全に読み込まれた後に実行する
        $(document).ready(function() {
            setTodayDate(); // 今日の日付をセット

            // 戻るボタンのクリックイベントを設定
        });

$(document).ready(function() {
    // 戻るボタンのクリックイベントを設定
    $('#back').click(function(event) {
        event.preventDefault(); // フォームの送信を防止（必要に応じて）
        console.log('戻るボタンがクリックされました。'); // コンソールにメッセージを表示
    });
    
});
