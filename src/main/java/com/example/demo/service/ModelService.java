package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.inter.MessageOutput;

@Service
public class ModelService {
	
	private final MessageOutput messageOutput;
	
	ModelService(MessageOutput messageOutput){
		this.messageOutput = messageOutput;
	}
	
	//同じ名前の部署が無ければ新規登録
	public Model departmentInsertModel(Integer overlappingDepartmentCheck, RedirectAttributes redirectAttributes) {
		if(overlappingDepartmentCheck == 1) {
			redirectAttributes.addFlashAttribute("departmentMessage", messageOutput.message("registrationSuccess"));
		}else if(overlappingDepartmentCheck == 0){
			redirectAttributes.addFlashAttribute("departmentErrorMessage", messageOutput.message("registrationOverlapping"));
		}
		return redirectAttributes;
	}
	
	//新部署名と旧部署名が同じでなければ部署名変更
	public Model departmentNameUpdateModel(Integer departmentNameEqualCheck, RedirectAttributes redirectAttributes) {
		if(departmentNameEqualCheck == 1) {
			redirectAttributes.addFlashAttribute("departmentMessage", messageOutput.message("updateNameSuccess"));
		}else {
			redirectAttributes.addFlashAttribute("departmentErrorMessage", messageOutput.message("updateNameOverlapping"));
		}
		return redirectAttributes;
	}
	
	//部署を無効化したかの判断分岐
	public Model departmentDeactiveUpdateModel(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("departmentMessage", messageOutput.message("deactiveSuccess"));
		return redirectAttributes;
	}
	
	//部署を有効化したかの判断分岐。(復元ボタン実装後に有効化してください。)
	public Model departmentActiveUpdateModel(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("departmentMessage", messageOutput.message("activeSuccess"));
		return redirectAttributes;
	}
	//先月の月次勤怠があればモデルに詰めてマネージャーの処理メニュー画面に表示
	public Model monthlyAttendanceIsSentInsertModel(Integer attendanceReq, Model model) {
		if (attendanceReq == 1) {
			model.addAttribute("monthlyAttendanceStatusIsSent", messageOutput.message("monthlyAttendanceStatusIsSent"));
        }
		return model;
	}
	//パスワードを忘れた際にユーザーIDとメールアドレスを入力してボタン押下した際の表示
	public Model emailSent(Model model) {
		model.addAttribute("emailSent", messageOutput.message("emailSent"));
		return model;
	}
	
	//パスワードを忘れて変更する際にトークンの有効期限切れ表示
	public Model tokenTimeOut(Model model) {
		model.addAttribute("tokenTimeOut", messageOutput.message("tokenTimeOut"));
		return model;
	}
	
	//パスワードを忘れて変更する際に無事終了した表示
	public Model passwordChangeSuccess(Model model) {
		model.addAttribute("passwordChangeSuccess", messageOutput.message("passwordChangeSuccess"));
		return model;
	}
	
	//入力された二つのパスワードが一致しない際の表示
	public Model passwordNearMiss(Model model) {
		model.addAttribute("passwordNearMiss", messageOutput.message("passwordNearMiss"));
		return model;
	}
	
	//月次勤怠訂正依頼を送信した際の表示
	public Model sendCorrectionApplication(Model model) {
		model.addAttribute("sendCorrectionApplication", messageOutput.message("sendCorrectionApplication"));
		return model;
	}
	
	//月次勤怠訂正依頼を承認した際の表示
	public Model changeMonthlyAttendanceReqApproval(Model model) {
		model.addAttribute("changeMonthlyAttendanceReqApproval", messageOutput.message("changeMonthlyAttendanceReqApproval"));
		return model;
	}

	//月次勤怠訂正依頼を却下した際の表示
	public Model changeMonthlyAttendanceReqReject(Model model) {
		model.addAttribute("changeMonthlyAttendanceReqReject", messageOutput.message("changeMonthlyAttendanceReqReject"));
		return model;
	}
	
	public Model newPasswordErrorCheck(Model model) {
		model.addAttribute("requiredHannkaku", messageOutput.message("requiredHannkaku"));
		return model;
	}
}
