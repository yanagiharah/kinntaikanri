
//進むボタン無効化（location.hrefで現在のurlがとれるらしい）
(function() {
    history.pushState(null, null, location.href);
    window.onpopstate = function () {
        history.pushState(null, null, location.href);
    };
})();
//ボタン連打防止機能　懸念点常に二重送信になっているのでは？という可能性はある。可能性だけ
function disableSubmit(button){
	//ボタンを無効化
	 button.disabled = true;
	 //ボタンに紐づいたform送信
	 button.form.submit();
	 //5秒後にボタン有効化・5秒はパスワードリセットのメール送信ボタンにマージンを取った値
	    setTimeout(function() {
	        button.disabled = false;
	    }, 5000);
	}