// 現在の年月を取得
const today = new Date();
const year = today.getFullYear();
const month = String(today.getMonth() + 1).padStart(2, '0'); // 月は0から始まるため+1し、2桁にフォーマット

// 初期値を設定
document.getElementById('stringYearsMonth').value = `${year}-${month}`;

//if (document.getElementById('stringYearsMonth').value === '') {
//        document.getElementById('stringYearsMonth').value = `${year}-${month}`;
//    }

// 表示年月のキーボード入力を無効にする(うまく動作していない)
monthInput.addEventListener('keydown', function(event) {
    event.preventDefault();
});