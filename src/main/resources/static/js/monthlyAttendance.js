function checkInput() {
    const willCorrectReason = document.getElementById('willCorrectReason');
    const willCorrectInput = document.getElementById('willCorrect');
    willCorrectInput.disabled = willCorrectReason.value.trim() === '';
}

function checkInput2() {
    const rejectReason = document.getElementById('rejectReason');
    const rejectInput = document.getElementById('reject');
    const approveInput = document.getElementById('approve');

    if (rejectReason.value.trim() === '') {
        approveInput.disabled = false;
        rejectInput.disabled = true;
    } else {
        approveInput.disabled = true;
        rejectInput.disabled = false;
    }
}

document.addEventListener("DOMContentLoaded", function() {
    // すべてのボタン要素を取得
    let approvalApplicantButtons = document.querySelectorAll(".approvalApplicantDisplay");

    // 各ボタンのクリックイベントをリスン
    approvalApplicantButtons.forEach(function(button) {
        button.addEventListener("click", function(event) {

            // 表を表示
            let attendanceList = document.getElementById("attendanceList");
            attendanceList.style.display = "table";

            // すべてのshortChangeReasonクラスの要素を取得
            let changeReasonElements = document.querySelectorAll(".shortChangeReason");

            // 各要素に対して最初の10文字を設定
            changeReasonElements.forEach(function(changeReasonwhy) {
                let changeReason = changeReasonwhy.innerText;
                let shortenedReason = changeReason.substring(0, 10);
                changeReasonwhy.innerText = shortenedReason;
            });
        });
    });
});
