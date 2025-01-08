package com.example.demo.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.demo.mapper.MonthlyAttendanceReqMapper;
import com.example.demo.model.Users;
import com.example.demo.model.WeatherData;

@Service
public class CommonActivityService {

	private final DailyReportService dailyReportService;

	private final AttendanceManagementService attendanceManagementService;


	private final MonthlyAttendanceReqMapper monthlyAttendanceReqMapper;

	private final ModelService modelService;

	private final WeatherService weatherService;
	
	private final MonthlyAttendanceReqService monthlyAttendanceReqService;

	CommonActivityService(DailyReportService dailyReportService,
			AttendanceManagementService attendanceManagementService,
			MonthlyAttendanceReqMapper monthlyAttendanceReqMapper, ModelService modelService,
			WeatherService weatherService,MonthlyAttendanceReqService monthlyAttendanceReqService) {
		this.dailyReportService = dailyReportService;
		this.attendanceManagementService = attendanceManagementService;
		this.monthlyAttendanceReqMapper = monthlyAttendanceReqMapper;
		this.modelService = modelService;
		this.weatherService = weatherService;
		this.monthlyAttendanceReqService = monthlyAttendanceReqService;
	}
	
	//Usersをセッションから取得、Usersとmenupage情報をモデルに追加、
	public void getCommonInfo(Model model,HttpSession session,Boolean infoAboutMenu) {
		usersModelSession(model,session);
		if(infoAboutMenu == null) {
			infoAboutMenu = false;
		}
		if(infoAboutMenu == false) {
		getForNotMenuPage(model);
		}else {
		getForMenuPage(model);
		}
	}
	
	//Usersをセッションから取得、Usersとmenupage情報をモデルに追加、Usersを返す。
	public Users getCommonInfoAddUsers(Model model,HttpSession session,Boolean infoAboutMenu) {
		getCommonInfo(model,session,infoAboutMenu);
		Users users = getByUsers(model);
		return users;
	}
	
	//Users情報をセッションから取得し、モデルに追加する
	public void usersModelSession(Model model, HttpSession session) {
		Users users = (Users) session.getAttribute("Users");
		model.addAttribute("Users", users);
	}
	
	//usersをモデルから確保
	public Users getByUsers(Model model) {
		Users users = (Users) model.getAttribute("Users");
		return users;
	}
	
	//メニューページである情報を持たせる。
	public void getForMenuPage(Model model) {
		String isMenuPage = null;
        model.addAttribute("isMenuPage", isMenuPage);
    }
	
	//メニューページではない情報を持たせる。
	public void getForNotMenuPage(Model model) {
		String isNotMenuPage = "notMenuPage";
		model.addAttribute("isMenuPage", isNotMenuPage);
	}

	//メニュー画面に戻る挙動(アカウント情報をmodelに詰めてメニュー画面へ遷移)
	public void backMenu(Model model, HttpSession session) {
		Boolean infoAboutMenu = true;//メニューページフラグ
		
		Users users = getCommonInfoAddUsers(model,session,infoAboutMenu);
		String userRole = users.getRole();
		
		LocalDate yesterday = LocalDate.now().minusDays(1);
		Date lastMonthFirstDay = firstDayLastMonth();
		
		if ("Regular".equals(userRole) || "UnitManager".equals(userRole)) {
			//userIdはマネージャーは使わないのでここで定義
			Integer userId = users.getUserId();
			//昨日から一週間前までの日報ステータスを取得
			dailyReportService.checkYesterdayDailyReport(userId, yesterday,model);
			//昨日の勤怠ステータスを取得
			attendanceManagementService.checkYesterdayAttendance(userId,yesterday,model);
			//勤怠訂正結果表示アラート　（勤怠訂正の結果が来ていた場合、hasChangeReq!=null）
			monthlyAttendanceReqService.checkMonthlyAttendanceReqStatus(userId,model);
			//勤怠申請を却下されたか確認
			monthlyAttendanceReqService.checkForAlertStatusThree(model,lastMonthFirstDay,userId);
		}

		if ("Manager".equals(userRole)) {
			//勤怠申請承認待ち確認
			monthlyAttendanceReqService.checkForAlertMonthlyAttendanceReq(lastMonthFirstDay,model);
			//直近一週間の日報承認待ち確認
			dailyReportService.selectAlertForConfirm(yesterday,model);
			//勤怠訂正承認待ち確認
			monthlyAttendanceReqService.selectMonthlyAttendanceReqAnyHasChangeReq(model);
		}
		//アラート欄表示可否を返す
		outcomeForAlert(model,userRole);
		//天気情報をmodelに詰める
		WeatherData weatherData = weatherService.getWeather("Tokyo");
		model.addAttribute("weatherData", weatherData);
	}
	//天気予報API課金で使用可能
	//天気情報をmodelに詰める
	//		double tokyoLat = 35.6895;
	//        double tokyoLon = 139.6917;
	//        WeatherResponse weatherResponse = weatherService.getWeather(tokyoLat, tokyoLon);
	//        model.addAttribute("currentWeather", weatherResponse.getCurrent());
	//        model.addAttribute("todayWeather", weatherResponse.getDaily().get(0));
	//        model.addAttribute("tomorrowWeather", weatherResponse.getDaily().get(1));
	
	
	
	
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
	private Model  outcomeForAlert(Model model,String userRole) {
		Boolean result = checkingExistAnyModel(model, userRole);
		if(result == true) {
			return modelService.menuInfoExists(model,result);
		}else {
			return null;
		}
	}
	
	//先月初日を返すメソッド
	public LocalDate getFirstDayLastMonthByLocalDate(){
		// 現在の日付を取得
		LocalDate now = LocalDate.now();
		// 先月の1日を返す
		return now.minusMonths(1).withDayOfMonth(1);
	}
	
	//先月一日をdate型で取得
	public Date firstDayLastMonth() {
		LocalDate firstDayOfLastMonth = getFirstDayLastMonthByLocalDate();
		// Date型に変換
		Date firstDayOfLastMonthDate = Date.from(firstDayOfLastMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return firstDayOfLastMonthDate;
	}
	
	//LocalDate型変数を"yyyy-MM"型に変更するフォーマッターメソッド
	public String formatLocalDate(LocalDate localDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");//年月の形式に変換
		String formattedLocalDate = localDate.format(formatter);//String型に変換
		return formattedLocalDate;
	}
	
	//先月の年・月を"yyyy-MM"型で取得
	public String lastYearsMonth() {
		LocalDate lastYearsMonth = getFirstDayLastMonthByLocalDate();
		return formatLocalDate(lastYearsMonth);
	}

	//現在の年・月を"yyyy-MM"の型で取得
	public String yearsMonth() {
		return  formatLocalDate(LocalDate.now());
	}
}
