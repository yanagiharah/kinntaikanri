package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.MustUser;

@Mapper
public interface UserMapper {
	
	//全件取得
	List<MustUser> selectAll();
	
	//１件取得
	MustUser selectByPrimaryKey(@Param("id")Integer id, @Param("password")char password);
	
	//登録
	int insert(MustUser user);
	
	//更新
	int updateByPrimaryKey(MustUser user);
	
	//削除
	int deleteByPrimaryKey(Long id);
}