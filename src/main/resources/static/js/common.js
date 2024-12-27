
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
		

//読み込み時起動
document.addEventListener('DOMContentLoaded',() =>{	
	//darkmode機能
	const changeButton = document.getElementById('theme-change');
	const htmlElement = document.documentElement;//＜html＞要素を指すためページ全体のレイアウトを操作
	
	let savedTheme = localStorage.getItem('theme');//現在のdata-bs-themeの値を取得
	
	if(!savedTheme){
		savedTheme = 'light';//もし、ない場合（現在の仕様だとないはず）lightに設定して
		localStorage.setItem('theme',savedTheme);//"light"をローカルストレージに格納…jsのローカルストレージはウェブブラウザにデータを保存します。ブラウザ再起動後も保持
	}
	htmlElement.setAttribute('data-bs-theme',savedTheme);//savedThemeをdata-bs-themeに適用
		
	//changeButton押下後起動
	changeButton.addEventListener('click',() => {
		const currentTheme = htmlElement.getAttribute('data-bs-theme');
		const newTheme = currentTheme === 'light' ? 'dark' :'light';//currentThemeがlightならdark（?以下True時）darkならlight(:以下False時)
	
		htmlElement.setAttribute('data-bs-theme',newTheme);//mode切り替え
		localStorage.setItem('theme',newTheme);//modeを保存
	})	
	});
