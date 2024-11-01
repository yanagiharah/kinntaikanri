
//if (document.getElementById('stringYearsMonth').value === '') {
//        document.getElementById('stringYearsMonth').value = `${year}-${month}`;
//    }

// 表示年月のキーボード入力を無効にする(うまく動作していない)
//monthInput.addEventListener('keydown', function(event) {
//    event.preventDefault();
//});

if(userRole !== "Manager"){
document.addEventListener('DOMContentLoaded', function() {
    // 初期値が空の場合、setYearMonthを呼び出す
    if (document.getElementById('stringYearsMonth').value === "") {
        setYearsMonth();
    }
});
}

function setYearsMonth() {
    // 現在の年月を取得
    var today = new Date();
    var year = today.getFullYear();
    var month = String(today.getMonth() + 1).padStart(2, '0'); // 月は0から始まるため+1し、2桁にフォーマット
    var yearsMonth = year + '-' + month;
    
    // 初期値を設定
    var inputElement = document.getElementById('stringYearsMonth');
    if (inputElement.value === '') {
        inputElement.value = yearsMonth;
    }
}

function confirmSubmission(event) {
	var userRoleElement=event.target;
	var userRole=userRoleElement ? userRoleElement.getAttribute('role') : null;
	var id = userRoleElement ? userRoleElement.getAttribute('id') : null;
	var message="";
	
	if (userRole == 'Regular' || userRole =='UnitManager') {
	            message = "データを送信しますか？";
	        } else if (userRole == 'Manager' && id=='reject') {
	            message = "本当に却下しますか？";
	        } else if (userRole == 'Manager' && id=='approve'){
	            message = "承認しますか？";
			} else{
				//処理なし
	        }
	
    var confirmation = confirm(message);
    if (!confirmation) {
        event.preventDefault(); // データ送信をキャンセル
    }
	
//	時間補正用のプログラム、検証してないので要検証
//	function formatTime(event) {
//	           let input = event.target.value;
//	           let parts = input.split(':');
//
//	           if (parts.length === 2) {
//	               let hours = parts.padStart(2, '0');
//	               let minutes = parts.padStart(2, '0');
//	               event.target.value = `${hours}:${minutes}`;
//	           }
//	       }
 }
