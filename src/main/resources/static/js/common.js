
//進むボタン無効化（location.hrefで現在のurlがとれるらしい）
//(function() {
//    history.pushState(null, null, location.href);
//    window.onpopstate = function () {
//        history.pushState(null, null, location.href);
//    };
//})();

(function() {
    function handlePageLoad() {
        history.pushState(null, null, location.href);
//        window.onpopstate = function () {
//            history.pushState(null, null, location.href);
//        };
    }

    // ページが読み込まれたときに処理を実行
    window.addEventListener('load', handlePageLoad);

    // ページが表示されたときに処理を実行（キャッシュからの読み込みも含む）
    window.addEventListener('pageshow', function(event) {
        if (event.persisted) {
            handlePageLoad();
        }
    });
})();


document.querySelectorAll('input[type="submit"]').forEach(function(button) {
	button.addEventListener('mouseup', function(event) {
	            // ボタンから手を離したときにボタンを無効化
	            setTimeout(function() {
	                button.disabled = true;
	                setTimeout(function() {
	                    button.disabled = false; // 5秒後にボタンを有効化
	                }, 5000);
	            }, 0); // フォーム送信後にボタンを無効化
	        });
	    });
