package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.DepartmentMapper;
import com.example.demo.model.Department;
import com.example.demo.model.DepartmentForm;

@Service
public class DepartmentService {
	
	@Autowired
	  public DepartmentMapper departmentMapper;
	
	
		//ユーザー管理画面起動時の部署プルダウンに適用させる為に部署を取得
		public List<Department> departmentSearchListUp() {
			List<Department> department = departmentMapper.selectDepartment();
			return department;
		}
		
		
		//登録
		public Integer departmentCheckInsert(DepartmentForm departmentForm) {
			Integer overlappingDepartmentCheck = departmentMapper.insertDepartment(departmentForm);
			return overlappingDepartmentCheck;
		}
		
		//新名前と旧名前を比較後に名前変更
		public Boolean departmentNameUpdate(DepartmentForm departmentForm) {
			if(departmentForm.getNewDepartmentName() != departmentForm.getOldDepartmentName()) {
				departmentMapper.updateDepartmentName(departmentForm);
				return true;
			}else {
				return false;
			}
		}
		
		public void departmentActiveUpdate(DepartmentForm departmentForm) {
				departmentMapper.updateDepartmentActive(departmentForm);
		}
		
}
