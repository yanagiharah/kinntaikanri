package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.model.Department;
import com.example.demo.model.DepartmentForm;

@Mapper
public interface DepartmentMapper {
	
	//日付指定範囲取得
	List<Department> selectDepartment();
	
	//テーブル内のdepartment_nameに重複が無いかを確認ののちに登録(reternしている数字は、insertで挿入された行数をあらわす。0だと同名データが既に存在しinsertされなかった、1だと同名データが無くinsertが実行された。)
	Integer insertDepartment(DepartmentForm departmentForm);
	
	//部署名更新
	void updateDepartmentName(DepartmentForm departmentForm);
	
	//部署無効化更新
	void updateDepartmentActive(DepartmentForm departmentForm);
	
	//日付指定範囲削除
//	int deleteByAttendanceOfMonth(@Param("userId")Integer userId, @Param("targetDate")Date targetDate, @Param("endDate")Date endDate);
}