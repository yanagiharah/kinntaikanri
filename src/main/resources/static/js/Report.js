// テーブル内の「作業時間」と「作業内容」のすべての入力フィールドを取得
const dailyReportDetailTime = Array.from(document.querySelectorAll('input[name^="dailyReportDetailForm"][name$=".dailyReportDetailTime"]'));
const content = Array.from(document.querySelectorAll('input[name^="dailyReportDetailForm"][name$=".content"]'));

$(document).ready(function() {
    $('#today').change(function() {
        // フォームを自動的に送信
        $('#dateSubmit').click();
    });
});
 
// カレンダーに現在の日付を登録
function setTodayDate() {
    var today = new Date();
    var yyyy = today.getFullYear();
    var mm = ("0" + (today.getMonth() + 1)).slice(-2);
    var dd = ("0" + today.getDate()).slice(-2);
    var todayDate = yyyy + '-' + mm + '-' + dd;
 
    // フィールドが空の場合のみ日付をセット
    if ($('#today').val() === '') {
        $('#today').val(todayDate);
    }
}

function checkTable() {
	// テーブル内の「作業時間」と「作業内容」のすべての入力フィールドを取得
	const dailyReportDetailId = Array.from(document.querySelectorAll('input[name^="dailyReportDetailForm"][name$=".dailyReportDetailId"]'));
	const status =  Array.from(document.querySelectorAll('input[name^="status"]'));
	let activeFlag = false;//ボタンフラグ
	let changedData ;//変更有フラグ
	
	//承認済みを定義
	const isStatusInvalid = status[0].value == 2;
	
	//承認済みならスキップ
	if(!isStatusInvalid){
		
	// 入力フィールドを全てチェック
	dailyReportDetailTime.some((taskTime, index) => {

//		//taskTime 作業時間
		const taskContent = content[index]; //作業内容
		const detailId = dailyReportDetailId[index]; //日報(サブ)ID
		
		//日報IDをもっていて変更がない時
		if (dailyReportDetailId && !changedData) {
				//taskTimeを数字型に変更（比べる対象が数字型であるため）
				  let taskTimeInt = parseInt(taskTime.value.trim(), 10);
			  //作業内容作業時間をともに元データと比べて一致していた場合
				  if (taskContent.value.trim() === dailyReportDetailForm[index].content && taskTimeInt === dailyReportDetailForm[index].dailyReportDetailTime) {
				 	activeFlag = false;
			//またはボタンが非活性で作業内容・時間共に入力済みならば
			  } else if (!activeFlag && taskTime.value.trim() !== "" && taskContent.value.trim() !== "") {
		      activeFlag = true;
			  changedData = true;//変更有フラグをオンに
			  }
		//日報IDをもっておらず、ボタンが非活性かつ、作業時間内容共に入力済みならば
		} else if (!activeFlag && taskTime.value.trim() !== "" && taskContent.value.trim() !== "") {
		  activeFlag = true;
		}
	
		//こちらは常に判定（強制無効化処理なので）
		//作業時間と作業内容が共に空欄ではなく、片方のみ空欄の場合
		if((taskTime.value.trim() ==="" || taskContent.value.trim() ==="") && !(taskContent.value.trim() === "" && taskTime.value.trim() === "")){
			activeFlag = false;
			return true;//コールバック関数（dailyReportDetailTime.some((taskTime, index)）を強制終了
		}
			
			
		})
	}

	//承認済みなら
    if (isStatusInvalid) {
        activeFlag = false;
    }
		
	// 結果に応じて提出ボタンを活性化/非活性化　activeFlag=true=> 有効化　activeFlag=false =>無効
	document.getElementById("submission").disabled = !activeFlag;
	
}



 if(userRole != "Manager"){
document.addEventListener('DOMContentLoaded', function() {
    // 全ての作業時間と作業内容の入力フィールドに対してinputイベントを設定
    dailyReportDetailTime.forEach(input => input.addEventListener('input', checkTable));
    content.forEach(input => input.addEventListener('input', checkTable));
 
    // ページ読み込み時に初回チェックを実行
    checkTable();
})

}

 
//表に対しての提出ボタンの活性化処理
//document.addEventListener('DOMContentLoaded', function() {
//	// テーブル内の「作業時間」と「作業内容」のすべての入力フィールドを取得
//	const dailyReportDetailTime = document.querySelectorAll('input[name^="dailyReportDetailForm"][name$=".dailyReportDetailTime"]');
//	const content = document.querySelectorAll('input[name^="dailyReportDetailForm"][name$=".content"]');
//	const dailyReportDetailId =document.querySelectorAll('input[name^="dailyReportDetailForm"][name$=".dailyReportDetailId"]');
//	var countNumber = 0;
//	
//	// 作業時間の入力フィールドに対してinputイベントを設定
//	dailyReportDetailTime.forEach(input => {
//		input.addEventListener('input', function(event) {
//			if (event.target.value.trim() == "" && event.target.value.trim() == "") {
//
//				document.getElementById("submission").disabled = true;
//			} else {
//				document.getElementById("submission").disabled = false;
//			}
//		});
//	});
//
//	// 作業内容の入力フィールドに対してinputイベントを設定
//	content.forEach(input => {
//		input.addEventListener('input', function(event) {
//			if (event.target.value.trim() == "" && event.target.value.trim() == "" && dailyReportDetailId !=null) {
//				countNumber++;
//				//if条件に日報詳細IDを加えて、IDが存在してる時だけcheckする
//				//if文の条件が真の場合、カウントを+1する　カウントが入っている場合非活性
//				//もしかしたら表の一行のみにforEachを起こしているので
//				//全行数に対してforEachを行う作りに変える必要がある
//				console.log("作業時間が入力されました: " + dailyReportDetailId);
//				
//			}
//			
//			if(countNumber !=0 ){
//				document.getElementById("submission").disabled = true;
//			} else {
//				document.getElementById("submission").disabled = false;
//			}
//			countNumber = 0;
//		});
//	});
//});
 
// ----------------------------------------------------------下記使用していない(★とっておきたい)----------------------------------------
//function sendData() {
//    // body内のデータを取得
//    const dailyReportDate = document.querySelector('input[name="dailyReportDate"]').value;
//    const dailyReportId = document.querySelector('input[name="dailyReportId"]').value;
//    const userId = document.querySelector('input[name="userId"]').value;
//    const status = document.querySelector('input[name="status"]').value;
//
//    // データを送信する処理
//    fetch('/daily/detailUpdate', {
//        method: 'POST',
//        headers: {
//            'Content-Type': 'application/json'
//        },
//        body: JSON.stringify({
//            dailyReportDate: dailyReportDate,
//            dailyReportId: dailyReportId,
//            userId: userId,
//            status: status
//        })
//    })
//    .then(response => {
//        if (!response.ok) {
//            throw new Error('Network response was not ok');
//        }
//        return response.json();
//    })
//    .then(data => {
//        console.log('Success:', data);
//        // 必要に応じて、成功時の処理をここに追加
//    })
//    .catch(error => {
//        console.error('Error:', error);
//        // エラーハンドリングをここに追加
//    });
//} 

// 選択した日付をサーバーに送信する関数(Ajax)
//function sendSelectedDate() {
//    var selectedDate = $('#today').val();
//    $.ajax({
//        type: 'POST',
//         url: 'http://localhost:8080/kinntai/daily/calendarDate',
//        data: { date: selectedDate },
//        success: function(response) {
//			updateTable(response)
//            console.log('サーバーの応答: ' + "aaa");
//        },
// 
//    });
//}
// 
//function updateTable(response) {
//	// レスポンスからデータを取得
//	var details = response.dailyReportForm.dailyReportDetailForm;
// 
//	// 表の tbody 要素を取得
//	var tbody = document.getElementById("dailyReportDetail");
// 
//	// 現在の tbody 内の行を取得
//	var rows = tbody.getElementsByTagName("tr");
//	
//	for (var i = 0; i < rows.length; i++) {
//			var detail = details[i];
//			if (detail) {
//				// 各行のセルを取得
//				var cells = rows[i].getElementsByTagName("td");
// 
//				// 作業時間のセルを更新
//				var timeInput = cells[0+i].querySelector('input[type="text"]');
//				timeInput.value = detail.dailyReportDetailTime;
// 
//				// 作業内容のセルを更新
//				var contentInput = cells[1+i].querySelector('input[type="text"]');
//				contentInput.value = detail.content;
//			}
//		}
//}
// 
 
//★戻るボタンに何か使う場合に残しておく
//$(document).ready(function() {
//    // 戻るボタンのクリックイベントを設定
//    $('#back').click(function(event) {
//        event.preventDefault(); // フォームの送信を防止（必要に応じて）
//        console.log('戻るボタンがクリックされました。'); // コンソールにメッセージを表示
//    });
//    
//});
 
///*[@{/daily/send-date}]*/'',
// 選択した日付をサーバーに送信する関数
//function sendSelectedDate() {
//    var selectedDate = $('#today').val();
//    $.ajax({
//        type: 'POST',
//        url: $('#myForm').attr('action'), // フォームの action 属性から URL を取得
//        data: { date: selectedDate },
//        success: function(response) {
//            console.log('サーバーの応答: ' + response);
//        },
//        error: function() {
//            console.log('エラーが発生しました');
//        }
//    });
//}