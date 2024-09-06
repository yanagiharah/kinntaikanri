package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.model.Department;
import com.example.demo.model.DepartmentForm;

@Mapper
public interface DepartmentMapper {
	
	//有効部署取得
	List<Department> selectDepartment();
	
	//無効（削除済み）部署取得。
	List<Department> selectDeleteDepartment();
	
	//テーブル内のdepartment_nameに重複が無いかを確認ののちに登録(reternしている数字は、insertで挿入された行数をあらわす。0だと同名データが既に存在しinsertされなかった、1だと同名データが無くinsertが実行された。)
	Integer insertDepartment(DepartmentForm departmentForm);
	
	//部署名更新
	Integer updateDepartmentName(DepartmentForm departmentForm);
	
	//部署無効化（削除）更新
	void updateDepartmentDeactive(DepartmentForm departmentForm);
	
	//無効（削除済み）部署を有効化更新。
	void updateDepartmentActive(DepartmentForm departmentForm);
	
	
}