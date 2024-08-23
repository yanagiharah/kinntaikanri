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

document.addEventListener('DOMContentLoaded', function() {
    // テーブル内の「作業時間」と「作業内容」のすべての入力フィールドを取得
    const dailyReportDetailTime = document.querySelectorAll('input[name^="dailyReportDetailForm"][name$=".dailyReportDetailTime"]');
    const content = document.querySelectorAll('input[name^="dailyReportDetailForm"][name$=".content"]');
    //const 提出ボタンを取得
   // const submissionButton =document.getElementById("submission");
    
    // 作業時間の入力フィールドに対してinputイベントを設定
    dailyReportDetailTime.forEach(input => {
        input.addEventListener('input', function(event) {
            if (event.target.value.trim() == "") {
				//提出ボタンを活性化
                console.log("作業時間が入力されました: " + event.target.value);
                document.getElementById("submission").disabled = true;
            } else {
				document.getElementById("submission").disabled = false;
                console.log("作業時間の入力がクリアされました");
            }
        });
    });

    // 作業内容の入力フィールドに対してinputイベントを設定
    content.forEach(input => {
        input.addEventListener('input', function(event) {
            if (event.target.value.trim() !== "") {
				//提出ボタンを活性化
                console.log("作業内容が入力されました: " + event.target.value);
            } else {
                console.log("作業内容の入力がクリアされました");
            }
        });
    });
});
