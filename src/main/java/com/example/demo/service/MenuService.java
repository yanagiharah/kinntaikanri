package com.example.demo.service;

import java.time.LocalDate;
import java.util.Date;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.demo.helper.DateHelper;
import com.example.demo.model.Users;

@Service
public class MenuService {
	
	private final DailyReportService dailyReportService;

	private final AttendanceManagementService attendanceManagementService;

	private final ModelService modelService;

	private final WeatherService weatherService;
	
	private final DateHelper dateHelper;
	
	private final CommonActivityService commonActivityService;
	
	MenuService(DailyReportService dailyReportService, DateHelper dateHelper,
			AttendanceManagementService attendanceManagementService, ModelService modelService,
			WeatherService weatherService,CommonActivityService commonActivityService) {
		this.dailyReportService = dailyReportService;
		this.attendanceManagementService = attendanceManagementService;
		this.modelService = modelService;
		this.weatherService = weatherService;
		this.dateHelper = dateHelper;
		this.commonActivityService = commonActivityService;
	}
	
	public void backMenu(Model model, HttpSession session) {
		Boolean infoAboutMenu = true;//メニューページフラグ
		
		Users users = commonActivityService.getCommonInfoWithUsers(model,session,infoAboutMenu);
		String userRole = users.getRole();
		
		LocalDate yesterday = LocalDate.now().minusDays(1);
		Date lastMonthFirstDay = dateHelper.firstDayLastMonth();
		
		if ("Regular".equals(userRole) || "UnitManager".equals(userRole)) {
			//userIdはマネージャーは使わないのでここで定義
			Integer userId = users.getUserId();
			//昨日から一週間前までの日報ステータスを取得
			dailyReportService.checkYesterdayDailyReport(userId, yesterday,model);
			//昨日の勤怠ステータスを取得
			attendanceManagementService.checkYesterdayAttendance(userId,yesterday,model);
			//勤怠訂正結果表示アラート　（勤怠訂正の結果が来ていた場合、hasChangeReq!=null）
			attendanceManagementService.checkMonthlyAttendanceReqStatus(userId,model);
			//先月の勤怠申請を却下されたか確認
			attendanceManagementService.checkForAlertStatusThree(userId,lastMonthFirstDay,model);
		}

		if ("Manager".equals(userRole)) {
			//勤怠申請承認待ち確認
			attendanceManagementService.checkForAlertMonthlyAttendanceReq(lastMonthFirstDay,model);
			//直近一週間の日報承認待ち確認
			dailyReportService.selectAlertForConfirm(yesterday,model);
			//勤怠訂正承認待ち確認
			attendanceManagementService.selectMonthlyAttendanceReqAnyHasChangeReq(model);
		}
		//アラート欄表示可否を返す
		outcomeForAlert(model,userRole);
		//天気情報をmodelに詰める
		modelService.addWeatherData(model,weatherService.getWeather("Tokyo"));
	}
	
	//htmlでアラート欄を表示するフラグを立てるためのメソッド
		private Boolean checkingExistAnyModel(Model model,String userRole) {
			//admin時
			if(userRole.equals("Admin")) {
				return false;
				//adminでなくマネージャーでないとき
			} else if(!userRole.equals("Manager")) {
				if(model.containsAttribute("CheckDailyReport")) {
					return true;
				}
				if(model.containsAttribute("CheckAttendance")) {
					return true;
				}
				if(model.containsAttribute("combinedMessageAndReason")) {
					return true;
				}
				if(model.containsAttribute("monthlyAttendanceReqApproved")) {
					return true;
				}
				if(model.containsAttribute("monthlyAttendanceStatusIsThree")) {
					return true;
				}
				//マネージャーであるとき
			} else if(userRole.equals("Manager")) {
				if(model.containsAttribute("monthlyAttendanceStatusIsSent")) {
					return true;
				}
				if(model.containsAttribute("dailyReportArrival")) {
					return true;
				}
				if(model.containsAttribute("monthlyAttendanceReqArrival")) {
					return true;
				}
			}
			return false;
		}
		
		//アラート表示可否判定メソッド
		private Model outcomeForAlert(Model model,String userRole) {
			Boolean result = checkingExistAnyModel(model, userRole);
			if(result == true) {
				return modelService.menuInfoExists(model,result);
			}else {
				return null;
			}
		}

}
