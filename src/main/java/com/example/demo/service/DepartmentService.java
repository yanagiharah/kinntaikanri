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
			return departmentMapper.selectDepartment();
		}
		
		//部署管理画面の削除済み部署欄に表示させる為に取得
		public List<Department> deleteDepartmentSearchListUp() {
			return departmentMapper.selectDeleteDepartment();
		}
		
		//登録
		public Integer departmentCheckInsert(DepartmentForm departmentForm) {
			return departmentMapper.insertDepartment(departmentForm);
		}
		
		//旧名前を新名前に変更
		public Integer departmentNameUpdate(DepartmentForm departmentForm) {
			//0or1が返される。0はテーブルのデータに新部署名が存在したので変更していない。1はテーブルのデータに新部署名が存在しなかったので変更した。
			return departmentMapper.updateDepartmentName(departmentForm);
		}
		
		//部署無効化（削除）更新
		public void departmentDeactiveUpdate(DepartmentForm departmentForm) {
			departmentMapper.updateDepartmentDeactive(departmentForm);
		}
		
		//無効（削除済み）部署を有効化更新。
		public void departmentActiveUpdate(DepartmentForm departmentForm) {
			departmentMapper.updateDepartmentActive(departmentForm);
		}
		
}
