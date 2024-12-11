
//進むボタン無効化（location.hrefで現在のurlがとれるらしい）
(function() {
    history.pushState(null, null, location.href);
    window.onpopstate = function () {
        history.pushState(null, null, location.href);
    };
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
