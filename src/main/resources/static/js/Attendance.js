
//if (document.getElementById('stringYearsMonth').value === '') {
//        document.getElementById('stringYearsMonth').value = `${year}-${month}`;
//    }

// 表示年月のキーボード入力を無効にする(うまく動作していない)
//monthInput.addEventListener('keydown', function(event) {
//    event.preventDefault();
//});
//managerでないとき

//pageが読み込み終わってからすぐに
document.addEventListener('DOMContentLoaded', function() {
	// 初期値が空の場合、setYearMonthを呼び出す
	if(userRole !== "Manager"){
		if (document.getElementById('stringYearsMonth').value === "" ) {
			setYearsMonth();
		}
		
		var calendarElement = document.querySelector('#stringYearsMonth');
		calendarElement.addEventListener('change',function(){
		document.getElementById('display').click();
		});
	}
	
	// attendanceListが配列であることを確認
	   if (!Array.isArray(attendanceList)) {
	       attendanceList = []; // デフォルト値として空の配列を設定
	   }

//	// attendanceListの中でattendanceDateSがholidayと一致するものをフィルタリング
//	const matchingAttendances = attendanceList.filter(attendance => {
//		return holidays.includes(attendance.attendanceDateS);
//	});
	//勤怠リスト分indexをあたえて繰り返す
	attendanceList.forEach((attendance, index) => {
		const dayOfWeek = attendance.dayOfWeek; // '土' または '日' を取得 dayOfWeek['月'、'火'、'水'、'木'、'金'、'土'、'日']
		const statusSelect = document.getElementById(`status-${index}`);//各indexに応じて値を取得
		const attendanceElement = document.getElementById(`dayOfWeek-${index}`);

		if (dayOfWeek === '土') {
			attendanceElement.classList.add('saturday');
		} else if (dayOfWeek === '日') {
			attendanceElement.classList.add('sunday');
		}
		//		 土日なら「休日」を選択
		if ((dayOfWeek === '土' || dayOfWeek === '日') && (statusSelect.value === '12' && userRole !== "Manager")) {
			statusSelect.value = '1'; // '1'は休日の値
		}
		// attendanceDateSがholidayと一致するものにクラスを付与
		if (holidays.includes(attendance.attendanceDateS)) {
			if (attendanceElement) {
				attendanceElement.classList.add('holiday');
				if ((statusSelect.value === '12' || statusSelect.value === '1') && userRole !== "Manager") {
					statusSelect.value = '2';
				}
			}
		}
	})
});


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
	// イベントのターゲット要素を取得
	var userRoleElement = event.target;

	// ユーザーの役割とIDを取得
	//userRoleElementが存在する場合（真の場合）、userRoleElement.getAttribute('role')が実行されます。そうでなければnullです。
	var userRole = userRoleElement ? userRoleElement.getAttribute('role') : null;
	var id = userRoleElement ? userRoleElement.getAttribute('id') : null;
	var message = "";

	if (userRole == 'Regular' || userRole == 'UnitManager') {
		message = "データを送信しますか？";
	} else if (userRole == 'Manager' && id == 'reject') {
		message = "本当に却下しますか？";
	} else if (userRole == 'Manager' && id == 'approve') {
		message = "承認しますか？";
	} else {
		//処理なし
	}
	// 確認ダイアログを表示し、ユーザーの応答を取得
	var confirmation = confirm(message);

	// ユーザーがキャンセルした場合、データ送信をキャンセル
	if (!confirmation) {
		event.preventDefault(); // データ送信をキャンセル
	}
}

// input typeがtimeの要素に対して適用し、各inputにindexを与えて処理を行う
document.querySelectorAll('input[type="time"]').forEach((input, index) => {
	// indexの2倍のinputが存在するため、indexを2で割ることでstatusIndexを決定します。
	// これにより、statusIndexは2回ごとに増加し、合計30までのinputに対応します（例：30日分）。
	const statusIndex = Math.trunc(index / 2);
	//status-(statusIndex番目)のIDから情報を取得(status[0,1,2,3,4,5,6,7,8,9,10,11,12]のいずれか)
	const statusSelect = document.getElementById(`status-${statusIndex}`);
	//出勤に値するstatusはこれ
	const workdayStatuses = ['0', '3', '6', '7', '8', '10'];
	//未選択に値するstatusはこれ
	const noSelected = ['12'];

	// 現在の時刻を取得
	const now = new Date();
	const hours = String(now.getHours()).padStart(2, '0'); // 時間を2桁に
	const minutes = String(now.getMinutes()).padStart(2, '0'); // 分を2桁に

	// フォーカス時に休日に値しないものであればデフォルト値を設定
	input.addEventListener('focus', () => {
		//すでに値が入力されていないinputで、かつ休日に該当しない場合
		if (input.value === '' && (workdayStatuses.includes(statusSelect.value) || noSelected.includes(statusSelect.value))) {
			input.value = `${hours}:${minutes}`; // デフォルト値として現在時刻を設定
		}
	});
	//もし値が入力されている場合
	if (input.value !== '') {
		//バックスペースキーが押されることを起点にして
		input.addEventListener('keydown', (event) => {
			if (event.key === 'Backspace') {
				//値をクリア
				input.value = '';
			}
		});
	}
});




//	時間補正用のプログラム・レアケースが対応しきれていないのでinput="time"を採用
// function formatTime(timeInput) {
//	    // コロンを削除・正規表現で":"を指定,''に置き換える。一貫したチェックのため
//	    timeInput = timeInput.replace(/:/g, '');
//
//	    // 入力が5桁の場合（HHMM形式）
//	    if (timeInput.length === 5) {
//	        return timeInput.slice(0, 2) + ':' + timeInput.slice(2, 4);
//	    }
//	    // 入力が4桁の場合（HHMM形式）
//	    else if (timeInput.length === 4) {
//	        return timeInput.slice(0, 2) + ':' + timeInput.slice(2);
//	    }
//	    // 入力が3桁の場合（HMM形式）
//	    else if (timeInput.length === 3) {
//	        return '0' + timeInput.slice(0, 1) + ':' + timeInput.slice(1);
//	    }
//		// 入力が2桁の場合(HH形式)
//		else if(timeInput.length === 2){
//			return timeInput + ':00';
//		}
//	    // 入力が1桁の場合（HH形式）
//	    else if (timeInput.length === 1) {
//	        return '0' + timeInput + ':00';
//	    }
//	    // それ以外はエラーメッセージを返す
//	    return '無効な時間形式';
// }
//
//document.addEventListener('DOMContentLoaded', () => {
//	    const timeInputs = document.querySelectorAll('.time-input');
//	    timeInputs.forEach(timeInput => {
//	        timeInput.addEventListener('change', () => {
//	            timeInput.value = formatTime(timeInput.value);
//	        });
//	    });
//});