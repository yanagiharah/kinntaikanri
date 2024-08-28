/**
 * 
 */

 $(function(){
  $(window).on("beforeunload",function(e){
    return "ブラウザを閉じても良いでしょうか？"; // 文字列はメッセージに反映されません。必ずreturnすればブランクでもOKです。
  });
});