package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class ModelService {
	
	public Model departmentInsertModel(Integer overlappingDepartmentCheck, Model model) {
		if(overlappingDepartmentCheck == 0) {
			model.addAttribute("registrationMessage","その部署は既に登録されています。");
		}else {
			model.addAttribute("registrationMessage","部署の登録が完了しました。。");
		}
		return model;
	}
}
