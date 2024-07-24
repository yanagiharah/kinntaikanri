package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.MonthlyAttendanceReqMapper;
import com.example.demo.model.MonthlyAttendanceReq;

@Service
public class MonthlyAttendanceReqService {
  @Autowired
  public MonthlyAttendanceReqMapper monthlyAttendanceReqMapper;
  
  public List<MonthlyAttendanceReq> selectApprovalPending() {
	  //1は承認待ち
	  List<MonthlyAttendanceReq> monthlyAttendanceReq = monthlyAttendanceReqMapper.selectApprovalPending(1);		
		return monthlyAttendanceReq;
		
	}
}
