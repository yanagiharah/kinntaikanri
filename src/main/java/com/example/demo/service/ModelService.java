package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class ModelService {
	
	//同じ名前の部署が無ければ新規登録
	public Model departmentInsertModel(Integer overlappingDepartmentCheck, Model model) {
		if(overlappingDepartmentCheck == 1) {
			model.addAttribute("registrationMessage","部署の登録が完了しました。");
		}else {
			model.addAttribute("registrationMessage","その部署は既に登録されています。");
		}
		return model;
	}
	
	//新部署名と旧部署名が同じでなければ部署名変更
	public Model departmentNameUpdateModel(Boolean departmentNameEqualCheck, Model model) {
		if(departmentNameEqualCheck == true) {
			model.addAttribute("registrationMessage","部署名が変更されました。");
		}else {
			model.addAttribute("registrationMessage","同じ部署名が入力されています。");
		}
		return model;
	}
}
