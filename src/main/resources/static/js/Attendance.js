
//if (document.getElementById('stringYearsMonth').value === '') {
//        document.getElementById('stringYearsMonth').value = `${year}-${month}`;
//    }

// 表示年月のキーボード入力を無効にする(うまく動作していない)
//monthInput.addEventListener('keydown', function(event) {
//    event.preventDefault();
//});


document.addEventListener('DOMContentLoaded', function() {
    // 初期値が空の場合、setYearMonthを呼び出す
    if (document.getElementById('stringYearsMonth').value === "") {
        setYearsMonth();
    }
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
