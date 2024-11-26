package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.inter.MessageOutput;
import com.example.demo.model.Attendance;
import com.example.demo.model.AttendanceFormList;
import com.example.demo.model.MonthlyAttendanceReq;
import com.example.demo.model.Users;
import com.example.demo.service.AttendanceManagementService;
import com.example.demo.service.CommonActivityService;
import com.example.demo.service.MonthlyAttendanceReqService;

@Controller
@RequestMapping("/attendance")
public class AttendanceManagementController {

	private final AttendanceManagementService attendanceManagementService;
	private final MonthlyAttendanceReqService monthlyAttendanceReqService;
	private final CommonActivityService commonActivityService;
	private final MessageOutput messageOutput;

	public AttendanceManagementController(AttendanceManagementService attendanceManagementService,
			MonthlyAttendanceReqService monthlyAttendanceReqService, CommonActivityService commonActivityService,
			MessageOutput messageOutput) {
		this.attendanceManagementService = attendanceManagementService;
		this.monthlyAttendanceReqService = monthlyAttendanceReqService;
		this.commonActivityService = commonActivityService;
		this.messageOutput = messageOutput;
	}

	@RequestMapping("/index") //9/30if文にelseを追加し初期カレンダー表示のメソッドを移動。マネージャークラスでデータのない表が表示される問題の解消
	public String start(HttpSession session, MonthlyAttendanceReq monthlyAttendanceReq, Model model) {
		commonActivityService.usersModelSession(model, session);
		Users users = (Users) model.getAttribute("Users");
		//Serviceへ。AttendanceManagementService見込み。メソッド名HandleUserAttendance
		//Managerなら
		if (users.getRole().equalsIgnoreCase("Manager")) {
			//approvalPendingデータを取得しモデルに追加
			List<MonthlyAttendanceReq> ApprovalPending = monthlyAttendanceReqService.selectApprovalPending();
			model.addAttribute("ApprovalPending", ApprovalPending);
		}else {
			//Managerでないなら現在の年月を取得して、それをもとに自身の勤怠情報を取得する。
			String stringYearsMonth = commonActivityService.yearsMonth();
			attendanceSearch(users.getUserId(), stringYearsMonth, model, session);	
		}
		return "attendance/registration";
	}

	//表示ボタンの処理
	@RequestMapping(value = "/management", params = "search", method = RequestMethod.POST)
	public String attendanceSearch(Integer userId, String stringYearsMonth, Model model,
			HttpSession session) {

		Integer years = Integer.parseInt(stringYearsMonth.substring(0, 4));
		Integer month = Integer.parseInt(stringYearsMonth.substring(5, 7));
		//勤怠詳細と勤怠データをそれぞれ取得。モデルに追加
		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month);
		AttendanceFormList attendanceFormList = attendanceManagementService.setInAttendance(attendance, years, month,
				stringYearsMonth);
		model.addAttribute("attendanceFormList", attendanceFormList);

		//月次勤怠テーブルのstatusをユーザー)モデルのstatusに詰める
		monthlyAttendanceReqService.submissionStatusCheck(attendance.get(0).getAttendanceDate(), userId, model,
				session);
		//承認申請ボタンのOnOff切り替え設定
		attendanceManagementService.requestActivityCheck(attendanceFormList);

		return "attendance/registration";
	}

	//登録ボタンの処理
	@RequestMapping(value = "/management", params = "insert", method = RequestMethod.POST)
	public String insert(@ModelAttribute AttendanceFormList attendanceFormList, BindingResult result, Model model,
			HttpSession session) {
		commonActivityService.usersModelSession(model, session);
		Users users = (Users) model.getAttribute("Users");

		//承認申請ボタンのONOffきりかえと書式のエラーチェック
		attendanceManagementService.requestActivityCheck(attendanceFormList);
		attendanceManagementService.errorCheck(attendanceFormList, result);

		if (result.hasErrors()) {
			return "attendance/registration";
		}
		//新しいデータに修正してデータベースに登録
		AttendanceFormList updateAttendanceFormEntity = attendanceManagementService.updateAttendanceFormCreate(attendanceFormList, users.getUserId());
		attendanceManagementService.attendanceUpsert(updateAttendanceFormEntity);

		model.addAttribute("attendanceMessage", messageOutput.message("attendanceSuccess"));
		model.addAttribute("attendanceFormList", attendanceFormList);
		return "attendance/registration";
	}

	//承認申請ボタン押下
	@RequestMapping(value = "/management", params = "approvalApplicationRegistration", method = RequestMethod.POST)
	public String monthlyAttendanceReqCreate(MonthlyAttendanceReq monthlyAttendanceReq,
			AttendanceFormList attendanceFormList, Model model, HttpSession session) {
		commonActivityService.usersModelSession(model, session);
		Users users = (Users) model.getAttribute("Users");

		//サービス層へ。monthlyAttendanceService見込み
		//勤怠リストのサイズを取得し、そのサイズだけ回す
		for (int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {
			//形を整える
			String inputDate = monthlyAttendanceReqService.getInputDate(attendanceFormList);
			//日数分日付を埋める
			attendanceFormList.getAttendanceList().get(i).setAttendanceDate(java.sql.Date.valueOf(inputDate));
		}
		//月初めの日にちを取る
		Date firstAttendanceDate = attendanceManagementService.getFirstAttendanceDate(attendanceFormList);
		//月初日、Listから取得したUserId、勤怠データ、勤怠詳細をもとに勤怠詳細の作成・更新処理
		monthlyAttendanceReqService.monthlyAttendanceUpdate(firstAttendanceDate, monthlyAttendanceReq.getUserId(),
				monthlyAttendanceReq, attendanceFormList);
		//ステータスの更新処理
		monthlyAttendanceReqService.submissionStatusCheck(firstAttendanceDate, users.getUserId(), model, session);
		model.addAttribute("attendanceFormList", attendanceFormList);
		return "attendance/registration";
	}

	//マネージャー承認申請者の勤怠表表示ボタン
	@RequestMapping(value = "/management", params = "ApprovalApplicantDisplay", method = RequestMethod.POST)
	public String attendance(@RequestParam("approvalUserId") Integer userId, @RequestParam("Years") Integer years,
			@RequestParam("Month") Integer month, Model model,
			RedirectAttributes redirectAttributes, HttpSession session) {
		commonActivityService.usersModelSession(model, session);

		List<Attendance> attendance = attendanceManagementService.attendanceSearchListUp(userId, years, month);

		//勤怠詳細があるとき
		if (attendance != null) {
			//初期化
			AttendanceFormList attendanceFormList = new AttendanceFormList();
			ArrayList<Attendance> attendanceList = new ArrayList<Attendance>();
			//勤怠データ(AttendanceList)と勤怠詳細(attendance)をそれぞれに追加する
			attendanceFormList.setAttendanceList(attendanceList);
			attendanceList.addAll(attendance);

			//★	approvalUserIdをセットするfor文を回す
			for (int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {
				attendanceFormList.getAttendanceList().get(i).setUserId(userId);
			}
			model.addAttribute("attendanceFormList", attendanceFormList);
		}
		List<MonthlyAttendanceReq> ApprovalPending = monthlyAttendanceReqService.selectApprovalPending();
		model.addAttribute("ApprovalPending", ApprovalPending);

		return "attendance/registration";
	}

	//マネージャー承認ボタン
	@RequestMapping(value = "/management", params = "approval", method = RequestMethod.POST)
	public String approval(AttendanceFormList attendanceFormList, Model model, HttpSession session,
			RedirectAttributes redirectAttributes) {
		if (attendanceFormList.getAttendanceList() == null) {
			redirectAttributes.addFlashAttribute("choiceUsers", messageOutput.message("choiceUsers"));
		} else {
			String inputDate = monthlyAttendanceReqService.getInputDate(attendanceFormList);
			Integer firstUserId=attendanceManagementService.getFirstAttendanceUserId(attendanceFormList);
			monthlyAttendanceReqService.approvalStatus(firstUserId, inputDate);
		}
		return "redirect:/attendance/index";
	}

	//マネージャー却下ボタン押下
	@RequestMapping(value = "/management", params = "rejected", method = RequestMethod.POST)
	public String Rejected(AttendanceFormList attendanceFormList, Model model, HttpSession session,
			RedirectAttributes redirectAttributes) {
		if (attendanceFormList.getAttendanceList() == null) {
			redirectAttributes.addFlashAttribute("choiceUsers", messageOutput.message("choiceUsers"));
		} else {
			String inputDate =  monthlyAttendanceReqService.getInputDate(attendanceFormList);
			Integer firstUserId=attendanceManagementService.getFirstAttendanceUserId(attendanceFormList);
			monthlyAttendanceReqService.rejectedStatus(firstUserId, inputDate);
		}
		return "redirect:/attendance/index";
	}

	//更新ボタンが押された場合の処理
	@RequestMapping(value = "/management",method = RequestMethod.GET)
	public String reload(Model model,HttpSession session,RedirectAttributes redirectAttributes) {
		return "redirect:/attendance/index";
	}

}
