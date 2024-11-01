
//進むボタン無効化（location.hrefで現在のurlがとれるらしい）
(function() {
    history.pushState(null, null, location.href);
    window.onpopstate = function () {
        history.pushState(null, null, location.href);
    };
})();

