package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.inter.MessageOutput;
import com.example.demo.model.AttendanceFormList;
import com.example.demo.model.DailyReportForm;
import com.example.demo.model.DepartmentForm;
import com.example.demo.model.ManagementForm;
import com.example.demo.model.MonthlyAttendanceReq;
import com.example.demo.model.Users;
import com.example.demo.model.WeatherData;

@Service
public class ModelService {
	
	private final MessageOutput messageOutput;
	
	ModelService(MessageOutput messageOutput){
		this.messageOutput = messageOutput;
	}
	//redirectAttributeにメッセージ追加するメソッド
	public void addFlashMessage(RedirectAttributes redirectAttributes, String messageKey, String message) {
        String mainMessage = messageOutput.message(message);
        redirectAttributes.addFlashAttribute(messageKey, mainMessage);
    }
	
	//messageOutput.message系
	
	//同じ名前の部署が無ければ新規登録
	public Model departmentInsertModel(Integer overlappingDepartmentCheck, RedirectAttributes redirectAttributes) {
		if(overlappingDepartmentCheck == 1) {
//			redirectAttributes.addFlashAttribute("departmentMessage", messageOutput.message("registrationSuccess"));
			addFlashMessage(redirectAttributes,"departmentMessage","registrationSuccess");
		}else if(overlappingDepartmentCheck == 0){
//			redirectAttributes.addFlashAttribute("departmentErrorMessage", messageOutput.message("registrationOverlapping"));
			addFlashMessage(redirectAttributes,"departmentErrorMessage","registrationOverlapping");
		}
		return redirectAttributes;
	}
	
	//新部署名と旧部署名が同じでなければ部署名変更
	public Model departmentNameUpdateModel(Integer departmentNameEqualCheck, RedirectAttributes redirectAttributes) {
		if(departmentNameEqualCheck == 1) {
//			redirectAttributes.addFlashAttribute("departmentMessage", messageOutput.message("updateNameSuccess"));
			addFlashMessage(redirectAttributes,"departmentMessage","updateNameSuccess");
		}else {
//			redirectAttributes.addFlashAttribute("departmentErrorMessage", messageOutput.message("updateNameOverlapping"));
			addFlashMessage(redirectAttributes,"departmentErrorMessage","updateNameOverlapping");
		}
		return redirectAttributes;
	}
	
	//部署を無効化したかの判断分岐
	public Model departmentDeactiveUpdateModel(RedirectAttributes redirectAttributes) {
//		redirectAttributes.addFlashAttribute("departmentMessage", messageOutput.message("deactiveSuccess"));
		addFlashMessage(redirectAttributes,"departmentMessage","deactiveSuccess");
		return redirectAttributes;
	}
	
	//部署を有効化したかの判断分岐。(復元ボタン実装後に有効化してください。)
	public Model departmentActiveUpdateModel(RedirectAttributes redirectAttributes) {
//		redirectAttributes.addFlashAttribute("departmentMessage", messageOutput.message("activeSuccess"));
		addFlashMessage(redirectAttributes,"departmentMessage","activeSuccess");
		return redirectAttributes;
	}
	
	public void choiceUsers(RedirectAttributes redirectAttributes) {
		addFlashMessage(redirectAttributes,"choiceUsers","choiceUsers");
		//modelService.choiceUsers(redirectAttributes);
	}
	
	public void attendanceApplove(RedirectAttributes redirectAttributes) {
		addFlashMessage(redirectAttributes,"choiceUsers","attendanceApplove");
		//modelService.attendanceApplove(redirectAttributes);
	}
	
	public void attendanceReject(RedirectAttributes redirectAttributes) {
		addFlashMessage(redirectAttributes,"choiceUsers","attendanceReject");
		//modelService.attendanceReject(redirectAttributes);
	}
	
	public void stringYearsMonth(RedirectAttributes redirectAttributes,String stringYearsMonth) {
		redirectAttributes.addFlashAttribute("stringYearsMonth", stringYearsMonth);
		//modelService.stringYearsMonth(redirectAttributes);
	}
	
	//月次勤怠訂正依頼を承認した際の表示
	public void changeMonthlyAttendanceReqApproval(RedirectAttributes redirectAttributes) {
		addFlashMessage(redirectAttributes,"changeMonthlyAttendanceReqApproval","changeMonthlyAttendanceReqApproval");
		//modelService.changeMonthlyAttendanceReqApproval(redirectAttributes);
	}
	
	public void changeMonthlyAttendanceReqReject(RedirectAttributes redirectAttributes) {
		addFlashMessage(redirectAttributes,"changeMonthlyAttendanceReqReject","changeMonthlyAttendanceReqReject");
		//modelService.changeMonthlyAttendanceReqReject(redirectAttributes);
	}
	
	//先月の月次勤怠があればモデルに詰めてマネージャーの処理メニュー画面に表示
	public Model monthlyAttendanceIsSentInsertModel(Model model) {
		model.addAttribute("monthlyAttendanceStatusIsSent", messageOutput.message("monthlyAttendanceStatusIsSent"));
		return model;
	}
	//パスワードを忘れた際にユーザーIDとメールアドレスを入力してボタン押下した際の表示
	public Model emailSent(Model model) {
		model.addAttribute("emailSent", messageOutput.message("emailSent"));
		return model;
	}
	
	//パスワードを忘れて変更する際にトークンの有効期限切れ表示
	public Model tokenTimeOut(Model model) {
		model.addAttribute("tokenTimeOut", messageOutput.message("tokenTimeOut"));
		return model;
	}
	
	//パスワードを忘れて変更する際に無事終了した表示
	public Model passwordChangeSuccess(Model model) {
		model.addAttribute("passwordChangeSuccess", messageOutput.message("passwordChangeSuccess"));
		return model;
	}
	
	//入力された二つのパスワードが一致しない際の表示
	public Model passwordNearMiss(Model model) {
		model.addAttribute("passwordNearMiss", messageOutput.message("passwordNearMiss"));
		return model;
	}
	
	//月次勤怠訂正依頼を送信した際の表示
	public Model sendCorrectionApplication(Model model) {
		model.addAttribute("sendCorrectionApplication", messageOutput.message("sendCorrectionApplication"));
		return model;
	}
	
	
	
	public Model newPasswordErrorCheck(Model model) {
		model.addAttribute("requiredHannkaku", messageOutput.message("requiredHannkaku"));
		return model;
	}
	
	public Model CheckDailyReport(Model model,List<String> checkDailyReport) {
		model.addAttribute("CheckDailyReport", messageOutput.message("checkDailyReport"));
		model.addAttribute("MissingSubmitDReport",checkDailyReport);
		return model;
	}
	
	public Model CheckAttendance(Model model) {
		model.addAttribute("CheckAttendance", messageOutput.message("checkAttendance"));
		return model;
	}
	
	public Model monthlyAttendanceStatusIsThree(Model model) {
		model.addAttribute("monthlyAttendanceStatusIsThree", messageOutput.message("monthlyAttendanceStatusIsThree"));
		return model;
	}
	
	public Model dailyReportArrival(Model model,List<String> dailyRepExists) {
		model.addAttribute("dailyReportArrival", messageOutput.message("dailyReportArrival"));
		model.addAttribute("dailyRepExists",dailyRepExists);
		return model;
	}
	
	public Model monthlyAttendanceReqArrival(Model model) {
		model.addAttribute("monthlyAttendanceReqArrival",messageOutput.message("monthlyAttendanceReqArrival"));
		return model;
	}
	
	public Model monthlyAttendanceReqApproved(Model model) {
		model.addAttribute("monthlyAttendanceReqApproved",messageOutput.message("monthlyAttendanceReqApproved"));
		return model;
	}
	
	public Model checkAddModel(Model model,String userName) {
		model.addAttribute("check", messageOutput.message("update",userName));
		return model;
	}
	
	public Model combinedMessageAndReason(Model model,String rejectionReason) {
		String monthlyAttendanceReqRejected = messageOutput.message("monthlyAttendanceReqRejected");
		String combinedMessageAndReason = monthlyAttendanceReqRejected + "\n"+ rejectionReason;
		model.addAttribute("combinedMessageAndReason",combinedMessageAndReason);
		return model;
	}
	
	public Model dailyReportAllSubmitted(Model model) {
		model.addAttribute("dailyReportAllSubmitted",messageOutput.message("dailyReportAllSubmitted"));
		return model;
	}
	
	public Model addMessage(Model model) {
		model.addAttribute("message", messageOutput.message("dailyReport.update.success"));
		return model;
		//modelService.addMessage(model,message);
	}
	
	public Model addAttendanceMessage(Model model) {
		model.addAttribute("attendanceMessage", messageOutput.message("attendanceSuccess"));
		return model;
	}
	
	public Model attendanceSubmit(Model model) {
		model.addAttribute("attendanceSubmit",messageOutput.message("attendanceSubmit"));
		return model;
	}

	
	//処理系
	
	//common
	public Model menuInfoExists(Model model,Boolean menuInfoExists) {
		model.addAttribute("menuInfoExists",menuInfoExists);
		return model;
	}
	
	public Model addUsers(Model model, Users users) {
		model.addAttribute("Users", users);
		return model;
		//modelService.addUsers(model,users);
	}
	
	public Model addFlagOfMenuPage(Model model,String menuPageFlag) {
		model.addAttribute("isMenuPage", menuPageFlag);
		return model;
		//modelService.addFlagOfMenuPage(model,menuPageFlag);
	}
	
	//menu
	public Model addDate(Model model,String date) {
		model.addAttribute("Date",date);
		return model;
		//modelService.addDate(model,date)
	}
	
	//attendance
	public Model addHolidays(Model model,List<String> holidays) {
		model.addAttribute("holidays",holidays);
		return model;
		//modelService.addHolidays(model,holidays);
	}
	
	public Model addAttendanceFormList(Model model,AttendanceFormList attendanceFormList) {
		model.addAttribute("attendanceFormList", attendanceFormList);
		return model;
		//modelService.addAttendanceFormList(model,attendanceFormList);
	}
	
	public Model addApprovalPending(Model model, List<MonthlyAttendanceReq> approvalPending) {
		model.addAttribute("ApprovalPending", approvalPending);
		return model;
		//modelService.addApprovalPending(model,approvalPending);
	}
	
	//DailyReport
	public Model addDailyReportForm(Model model,DailyReportForm dailyReportForm) {
		model.addAttribute("dailyReportForm", dailyReportForm);
		return model;
		//modelService.addDailyReportForm(model,dailyReportForm);
	}
	
	public Model addConfirmPending(Model model,List<DailyReportForm> confirmPending) {
		model.addAttribute("ConfirmPending", confirmPending);
		return model;
		//modelService.addConfirmPending(model,confirmPending);
	}
	
	public Model addCalendarDate(Model model,LocalDate calendarDate) {
		model.addAttribute("calendarDate",calendarDate);
		return model;
		//modelService.addCalendarDate(model,calendarDate);
	}
	
	public Model addConfirmPendingStatus1(Model model,List<String> status) {
		model.addAttribute("confirmPendingStatus1",status);
		return model;
		//modelService.addConfirmPendingStatus1(model,confirmPendingStatus1);
	}
	
	//MonthlyAttendanceReq
	public Model addHasChangeReq(Model model,List<MonthlyAttendanceReq> hasChangeReq) {
		model.addAttribute("HasChangeReq", hasChangeReq);
		return model;
		//modelService.addHasChangeReq(model,hasChangeReq);
	}
	
	public Model addApprovedMonths(Model model,List<MonthlyAttendanceReq> approvedMonths) {
		model.addAttribute("approvedMonths",approvedMonths);
		return model;
		//modelService.addApprovedMonths(model,approvedMonths);
	}
	
	public Model addStringYearsMonth(Model model,String stringYearsMonth) {
		model.addAttribute("stringYearsMonth",stringYearsMonth);
		return model;
		//modelService.addStringYearsMonth(model,stringYearsMonth);
	}
	
	public Model addCurrentChangeReq(Model model,List<MonthlyAttendanceReq> currentChangeReq) {
		model.addAttribute("CurrentChangeReq",currentChangeReq);
		return model;
		//modelService.addCurrentChangeReq(model,currentChangeReq);
	}
	
	//department
	public Model addDepartmentForm(Model model,DepartmentForm departmentForm) {
		model.addAttribute("departmentForm", departmentForm);
		return model;
		//modelService.addDepartmentForm(model,departmentForm);
	}
	
	//management 
	public Model addManagementForm(Model model,ManagementForm managementForm) {
		model.addAttribute("managementForm", managementForm);
		return model;
		//modelService.addManagementForm(model,managementForm);
	}
	
	//changeForgotPass
	public Model addUserToken(Model model,Users user) {
		 model.addAttribute("user", user);
		 return model;
		 //modelService.addUserToken(model,user);
		 //Users usersのメソッドとは別です。注意
	}
	
	//weatherdata
	public Model addWeatherData(Model model, WeatherData weatherData) {
		model.addAttribute("weatherData", weatherData);
		return model;
		//modelService.addWeatherData(model,weatherData);
	}
	
}
