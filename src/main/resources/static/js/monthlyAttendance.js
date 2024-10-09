function checkInput(){
	const willCorrectReason= document.getElementById('willCorrectReason') 
	const willCorrectInput= document.getElementById('willCorrect')
	willCorrectInput.disabled = willCorrectReason.value.trim() === '';
}

function checkInput2(){
	const rejectReason= document.getElementById('rejectReason')
	const rejectInput= document.getElementById('reject')
	const approveInput= document.getElementById('approve')
	
	if(rejectReason.value.trim() === ''){
			rejectInput.disabled = true;
		} else {
			approveInput.disabled = true;
			rejectInput.disabled = false;
		};
}
