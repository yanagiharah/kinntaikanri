package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class ModelService {
	
	//同じ名前の部署が無ければ新規登録
	public Model departmentInsertModel(Integer overlappingDepartmentCheck, RedirectAttributes redirectAttributes) {
		if(overlappingDepartmentCheck == 1) {
			redirectAttributes.addAttribute("departmentMessage","部署の登録が完了しました。");
		}else {
			redirectAttributes.addAttribute("departmentMessage","その部署は既に登録されています。");
		}
		return redirectAttributes;
	}
	
	//新部署名と旧部署名が同じでなければ部署名変更
	public Model departmentNameUpdateModel(Boolean departmentNameEqualCheck, RedirectAttributes redirectAttributes) {
		if(departmentNameEqualCheck == true) {
			redirectAttributes.addAttribute("departmentMessage","部署名が変更されました。");
		}else {
			redirectAttributes.addAttribute("departmentMessage","同じ部署名が入力されています。");
		}
		return redirectAttributes;
	}
	
	//部署を無効化したかの判断分岐
	public Model departmentDeactiveUpdateModel(RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("departmentMessage", "部署を削除しました。");
		return redirectAttributes;
	}
	
	//部署を有効化したかの判断分岐。(復元ボタン実装後に有効化してください。)
//	public Model departmentActiveUpdateModel(RedirectAttributes redirectAttributes) {
//		redirectAttributes.addAttribute("departmentMessage", "部署を復元しました。");
//		return redirectAttributes;
//	}
}
