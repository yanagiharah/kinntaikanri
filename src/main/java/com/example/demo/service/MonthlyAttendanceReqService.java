package com.example.demo.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.demo.inter.MessageOutput;
import com.example.demo.mapper.MonthlyAttendanceReqMapper;
import com.example.demo.model.AttendanceFormList;
import com.example.demo.model.MonthlyAttendanceReq;
import com.example.demo.model.Users;

@Service
public class MonthlyAttendanceReqService {

	private final MonthlyAttendanceReqMapper monthlyAttendanceReqMapper;

	private final AttendanceManagementService attendanceManagementService;
	
	private final MessageOutput messageOutput;

	MonthlyAttendanceReqService(MonthlyAttendanceReqMapper monthlyAttendanceReqMapper, AttendanceManagementService attendanceManagementService,MessageOutput messageOutput) {
		this.monthlyAttendanceReqMapper = monthlyAttendanceReqMapper;
		this.attendanceManagementService = attendanceManagementService;
		this.messageOutput = messageOutput;
	}

	public List<MonthlyAttendanceReq> selectApprovalPending() {
		//1は承認待ち
		List<MonthlyAttendanceReq> monthlyAttendanceReq = monthlyAttendanceReqMapper.selectApprovalPending(1);
		for (int i = 0; i < monthlyAttendanceReq.size(); i++) {
			//date型の年と月をinteger型で取得する
			SimpleDateFormat getYears = new SimpleDateFormat("yyyy");
			monthlyAttendanceReq.get(i)
					.setYears(Integer.valueOf(getYears.format(monthlyAttendanceReq.get(i).getTargetYearMonth())));
			SimpleDateFormat getMonth = new SimpleDateFormat("MM");
			monthlyAttendanceReq.get(i)
					.setMonth(Integer.valueOf(getMonth.format(monthlyAttendanceReq.get(i).getTargetYearMonth())));
		}
		return monthlyAttendanceReq;
	}

	//承認申請ボタン押下(初めての申請の際)
	public void monthlyAttendanceReqCreate(MonthlyAttendanceReq monthlyAttendanceReq,
			AttendanceFormList attendanceFormList) {
		//申請月の１日をtargetYearMonthにいれる
		String inputDate = getInputDate(attendanceFormList);
		monthlyAttendanceReq.setTargetYearMonth(java.sql.Date.valueOf(inputDate));
		//今日の日付をmonthlyAttendanceReqDateに入れる
		Date date = new Date();
		monthlyAttendanceReq.setMonthlyAttendanceReqDate(date);
		//statusに１を入れる(1は承認待ち)
		monthlyAttendanceReq.setStatus(1);
		monthlyAttendanceReqMapper.insertMonthlyAttendanceReq(monthlyAttendanceReq);
	}

	//承認申請ボタン押下(却下されたやつの更新)
	public void updateMonthlyAttendanceReq(MonthlyAttendanceReq monthlyAttendanceReq) {
		//今日の日付をmonthlyAttendanceReqDateに入れる
		Date date = new Date();
		monthlyAttendanceReq.setMonthlyAttendanceReqDate(date);
		monthlyAttendanceReqMapper.updateMonthlyAttendanceReq(monthlyAttendanceReq);
	}

	//月次勤怠申請テーブルのステータスの更新処理の分岐確認メソッド
	public void monthlyAttendanceUpdate(Date targetYearMonth, Integer userId, MonthlyAttendanceReq monthlyAttendanceReq,
			AttendanceFormList attendanceFormList) {
		MonthlyAttendanceReq monthlyAttendanceCheck = monthlyAttendanceReqMapper
				.selectTargetYearMonthStatus(targetYearMonth, userId);
		//中身がnull、すなわち同一ユーザーかつ同一月の月次勤怠申請がテーブルにない時の処理。statusはnull
		if (monthlyAttendanceCheck == null) {
			monthlyAttendanceReqCreate(monthlyAttendanceReq, attendanceFormList);
			//承認申請が却下または訂正承認されていた際の処理。statusは0か3
		} else if (monthlyAttendanceCheck != null
				&& (monthlyAttendanceCheck.getStatus() == 3 || monthlyAttendanceCheck.getStatus() == 0)) {
			Date firstAttendanceDate = attendanceManagementService.getFirstAttendanceDate(attendanceFormList);
			monthlyAttendanceReq.setTargetYearMonth(firstAttendanceDate);
			updateMonthlyAttendanceReq(monthlyAttendanceReq);
		}
	}

	//承認申請のステータス確認
	public Model submissionStatusCheck(Date targetYearMonth, Integer userId, Model model, HttpSession session) {
		Users users = (Users) session.getAttribute("Users");
		//ステータスチェックを呼び出し、ステータスの記録があればそれを、なければ０を割り当てる処理
		MonthlyAttendanceReq statusCheck = monthlyAttendanceReqMapper.selectTargetYearMonthStatus(targetYearMonth,
				userId);
		if (statusCheck != null) {
			Integer nowStatus = statusCheck.getStatus();
			users.setStatus(nowStatus);
		} else {
			users.setStatus(0);
		}
		model.addAttribute("Users", users);
		return model;
	}

	//マネージャ勤怠申請承認文
	public void approvalStatus(Integer userId, String targetYearMonth) {
		monthlyAttendanceReqMapper.approvalStatus(userId, targetYearMonth);
	}

	//マネージャ勤怠申請却下文
	public void rejectedStatus(Integer userId, String targetYearMonth) {
		monthlyAttendanceReqMapper.rejectedStatus(userId, targetYearMonth);
	}

//*********************************↓↓勤怠訂正↓↓***********************************
	
	//特定のユーザーの承認申請で承認済みを取得 カレンダー（一般）用
	public List<MonthlyAttendanceReq> selectApproval(Integer userId) {
		  // MonthlyAttendanceReqのリストを取得
        List<MonthlyAttendanceReq> targetList = monthlyAttendanceReqMapper.selectApproved(userId);
        
        // 先月の月を取得
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        String lastMonth = dateFormat.format(calendar.getTime());

        // 先月の月のMonthlyAttendanceReqオブジェクトを作成
        MonthlyAttendanceReq lastMonthReq = new MonthlyAttendanceReq();
        lastMonthReq.setStringTargetYearMonth(lastMonth);

        // リストに追加
        targetList.add(lastMonthReq);
        
        return targetList;
	}

	//特定の年月の勤怠取得(勤怠訂正用)
	public List<MonthlyAttendanceReq> selectHasChangeReq(String targetYearMonth) {
		//LocalDate型に変更
		LocalDate dateTargetYearMonth = LocalDate.parse(targetYearMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		//変更依頼ステータスが１の勤怠表呼び出し
		List<MonthlyAttendanceReq> monthlyAttendanceReq = monthlyAttendanceReqMapper.selectHasChangeReq(dateTargetYearMonth);
		//yearとmonthがそれぞれ必要になるので作成
		YearMonth yearMonth = YearMonth.parse(targetYearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
		//以下for文でstringYearsMonthとyear,monthの値をそれぞれMonthlyAttenanceReqの型にセット
		for (MonthlyAttendanceReq req : monthlyAttendanceReq) {
			// targetYearMonthを設定
			req.setTargetYearMonth(java.sql.Date.valueOf(dateTargetYearMonth));
			// 年と月を設定
			req.setYears(yearMonth.getYear());
			req.setMonth(yearMonth.getMonthValue());//整数型で月を取得
		}
		return monthlyAttendanceReq;
	}

	//一般ユーザー用アラート表示機能
	public Model checkMonthlyAttendanceReqStatus(Integer userId,Model model) {
		List<Integer> checkGeneralUsersHasChangeReq = new ArrayList<>();
		
		//訂正申請がされていた（hasChangeReq=0）のステータスを入手
		checkGeneralUsersHasChangeReq = monthlyAttendanceReqMapper.selectMonthlyAttendanceReqHasChangeReq(userId);
		//もし上記があれば
		if(!checkGeneralUsersHasChangeReq.isEmpty()) {
			if(checkGeneralUsersHasChangeReq.contains(0)) {
				model.addAttribute("monthlyAttendanceReqApproved",messageOutput.message("monthlyAttendanceReqApproved"));
			}
			if(checkGeneralUsersHasChangeReq.contains(2)) {
				String monthlyAttendanceReqRejected = messageOutput.message("monthlyAttendanceReqRejected");
				String rejectionReason = monthlyAttendanceReqMapper.selectRejectionReason(userId);
				String combinedMessageAndReason = monthlyAttendanceReqRejected + "\n"+ rejectionReason;
				model.addAttribute("combinedMessageAndReason",combinedMessageAndReason);
			}
			return model;
		}
		return null;
	}
	
	public Model selectMonthlyAttendanceReqAnyHasChangeReq(Model model) {
		Integer existHasChangeReq = monthlyAttendanceReqMapper.selectMonthlyAttendanceReqAnyHasChangeReq();
		if(existHasChangeReq == 1) {
			model.addAttribute("monthlyAttendanceReqArrival",messageOutput.message("monthlyAttendanceReqArrival"));
			return model;
		}
		return null;
	}

	//月次勤怠訂正依頼の更新文
	public void changeRequestMonthlyAttendanceReq(Integer userId, LocalDate targetYearMonth, String changeReason) {
		monthlyAttendanceReqMapper.changeRequestMonthlyAttendanceReq(userId, targetYearMonth, changeReason);
	}

	//月次勤怠訂正の承認更新文
	public void changeApprovalMonthlyAttendanceReq(Integer userId, String targetYearMonth) {
		monthlyAttendanceReqMapper.changeApprovalMonthlyAttendanceReq(userId, targetYearMonth);
	}

	//月次勤怠訂正の却下更新文
	public void changeRejectionMonthlyAttendanceReq(Integer userId, String targetYearMonth, String rejectionReason) {
		monthlyAttendanceReqMapper.changeRejectionMonthlyAttendanceReq(userId, targetYearMonth, rejectionReason);
	}
	
	public void changeRejectedMonthlyAttendanceReq(Integer userId) {
		monthlyAttendanceReqMapper.changeRejectedMonthlyAttendanceReq(userId);
	}

	//カレンダーで選んだ年月を"yyyy-MM-01"の形に変更するメソッド
	public LocalDate convertStringToLocalDate(String stringYearsMonth) {
		try {
			// 元の形式
			DateTimeFormatter monthlyAttendanceReqFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate targetDate = LocalDate.parse(stringYearsMonth + "-01", monthlyAttendanceReqFormat);
			// 日付を1日に設定
			return targetDate.withDayOfMonth(1);
		} catch (DateTimeParseException e) {
			//ほぼあり得ないがエラー時用の処理
			System.err.println("Error parsing date: " + stringYearsMonth);
			e.printStackTrace();
			return null;
		}
	}

	//特定ユーザーだけ表示するフィルター
	public List<MonthlyAttendanceReq> filteringHasChangeReq(Integer userId, String stringYearsMonth) {
		List<MonthlyAttendanceReq> hasChangeReq = selectHasChangeReq(stringYearsMonth);
		//hasChangeReqは複数の要素を持つので、一つに絞るためのフィルターをかける
		List<MonthlyAttendanceReq> currentChangeReq = hasChangeReq.stream()
				.filter(req -> userId.equals(req.getUserId()))
				.collect(Collectors.toList());
		return currentChangeReq;
	}
	
	//InputDate共通化処理
	public String getInputDate(AttendanceFormList attendanceFormList) {
		 if (attendanceFormList != null && !attendanceFormList.getAttendanceList().isEmpty()) {
	            String defaultInputDate=attendanceFormList.getAttendanceList().get(0).getAttendanceDateS();
	            String conversion = defaultInputDate.replace("/", "-");
	            return conversion;
	        }
	        return null; // または適切なデフォルト値
	}
}
