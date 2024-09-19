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
}
