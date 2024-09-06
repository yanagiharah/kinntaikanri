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
	
	
		//ユーザー管理画面起動時の部署プルダウンに適用させる為、部署管理画面に部署を表示させる為に取得
		public List<Department> departmentSearchListUp() {
			List<Department> department = departmentMapper.selectDepartment();
			return department;
		}
		
		//部署管理画面の削除済み部署欄に表示させる為に取得
		public List<Department> deleteDepartmentSearchListUp() {
			List<Department> department = departmentMapper.selectDeleteDepartment();
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
		
		//部署無効化（削除）更新
		public void departmentDeactiveUpdate(DepartmentForm departmentForm) {
				departmentMapper.updateDepartmentDeactive(departmentForm);
		}
		
		//無効（削除済み）部署を有効化更新。(復元ボタン実装後に有効化してください。)
//		public void departmentActiveUpdate(DepartmentForm departmentForm) {
//			departmentMapper.updateDepartmentActive(departmentForm);
//		}
		
}
