package com.example.demo.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.demo.inter.MessageOutput;
import com.example.demo.mapper.MonthlyAttendanceReqMapper;
import com.example.demo.model.MonthlyAttendanceReq;
import com.example.demo.model.Users;
import com.example.demo.model.WeatherData;

@Service
public class CommonActivityService {

	private final DailyReportService dailyReportService;

	private final AttendanceManagementService attendanceManagementService;

	private final MessageOutput messageOutput;

	private final MonthlyAttendanceReqMapper monthlyAttendanceReqMapper;

	private final ModelService modelService;

	private final WeatherService weatherService;
	
	private final MonthlyAttendanceReqService monthlyAttendanceReqService;

	CommonActivityService(DailyReportService dailyReportService,
			AttendanceManagementService attendanceManagementService, MessageOutput messageOutput,
			MonthlyAttendanceReqMapper monthlyAttendanceReqMapper, ModelService modelService,
			WeatherService weatherService,MonthlyAttendanceReqService monthlyAttendanceReqService) {
		this.dailyReportService = dailyReportService;
		this.attendanceManagementService = attendanceManagementService;
		this.messageOutput = messageOutput;
		this.monthlyAttendanceReqMapper = monthlyAttendanceReqMapper;
		this.modelService = modelService;
		this.weatherService = weatherService;
		this.monthlyAttendanceReqService = monthlyAttendanceReqService;
	}
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
	
	public Users getByUsers(Model model) {
		Users users = (Users) model.getAttribute("Users");
		return users;
	}
	
	public void getForMenuPage(Model model) {
		String isMenuPage = null;
        model.addAttribute("isMenuPage", isMenuPage);
    }
	
	public void getForNotMenuPage(Model model) {
		String isMenuPage = "notMenuPage";
		model.addAttribute("isMenuPage", isMenuPage);
	}

	//メニュー画面に戻る挙動(アカウント情報をmodelに詰めてメニュー画面へ遷移)
	public void backMenu(Model model, HttpSession session) {
		Boolean infoAboutMenu = true;
		Users users = getCommonInfoAddUsers(model,session,infoAboutMenu);
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);
//		model.addAttribute("Users", users);
		if ("Regular".equals(users.getRole()) || "UnitManager".equals(users.getRole())) {
			//メソッドの位置をそれぞれ変えたほうがいい。あるいはヘルパークラスを作成してそこに入れる
			List<String> checkDailyReport = dailyReportService.checkYesterdayDailyReport(users.getUserId(), yesterday);
			Integer checkAttendance = attendanceManagementService.checkYesterdayAttendance(users.getUserId(),
					yesterday);
			if (checkDailyReport != null) {
				model.addAttribute("CheckDailyReport", messageOutput.message("checkDailyReport"));
				model.addAttribute("MissingSubmitDReport",checkDailyReport);
			}
			if (checkAttendance == 0) {
				model.addAttribute("CheckAttendance", messageOutput.message("checkAttendance"));
			}
			
			//)勤怠訂正結果表示アラート
			Integer userId = users.getUserId();
			monthlyAttendanceReqService.checkMonthlyAttendanceReqStatus(userId,model);

			//勤怠管理アラート
			//先月の一日をDate型で取得
			Date firstDayOfLastMonthDate = oneDayLastMonth();
			//先月のmonthlyAttendanceReqを変数に詰める
			MonthlyAttendanceReq monthlyAttendanceReq = monthlyAttendanceReqMapper.selectTargetYearMonthStatus(firstDayOfLastMonthDate, users.getUserId());
			//先月のmonthlyAttendanceReqが存在し、かつ月次勤怠承認状況をあらわすstatusが３（却下）のとき、処理メニュー画面にメッセージを表示させる。
			if (monthlyAttendanceReq != null && monthlyAttendanceReq.getStatus() == 3) {
				model.addAttribute("monthlyAttendanceStatusIsThree", messageOutput.message("monthlyAttendanceStatusIsThree"));
			}
		}

		if ("Manager".equals(users.getRole())) {
			Date lastMonth = oneDayLastMonth();
			// データベースから月次出席情報を取得
			Integer attendanceReq = monthlyAttendanceReqMapper.selectMonthlyAttendanceReq(lastMonth);
			modelService.monthlyAttendanceIsSentInsertModel(attendanceReq, model);
			List<String> dailyRepExists=dailyReportService.selectAlertForConfirm(yesterday);
			if(dailyRepExists != null) {
				model.addAttribute("dailyReportArrival", messageOutput.message("dailyReportArrival"));
				model.addAttribute("dailyRepExists",dailyRepExists);
			}
			
			monthlyAttendanceReqService.selectMonthlyAttendanceReqAnyHasChangeReq(model);
		}
		//天気予報API課金で使用可能
		//天気情報をmodelに詰める
		//		double tokyoLat = 35.6895;
		//        double tokyoLon = 139.6917;
		//        WeatherResponse weatherResponse = weatherService.getWeather(tokyoLat, tokyoLon);
		//        model.addAttribute("currentWeather", weatherResponse.getCurrent());
		//        model.addAttribute("todayWeather", weatherResponse.getDaily().get(0));
		//        model.addAttribute("tomorrowWeather", weatherResponse.getDaily().get(1));
		
		//天気情報をmodelに詰める
		WeatherData weatherData = weatherService.getWeather("Tokyo");
		model.addAttribute("weatherData", weatherData);
	}

	public Date oneDayLastMonth() {
		// 現在の日付を取得
		LocalDate now = LocalDate.now();
		// 先月の1日を取得
		LocalDate firstDayOfLastMonth = now.minusMonths(1).withDayOfMonth(1);
		// Date型に変換
		Date firstDayOfLastMonthDate = Date.from(firstDayOfLastMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return firstDayOfLastMonthDate;
	}
	
	public String lastYearsMonth() {
		LocalDate now = LocalDate.now();
		// 先月の1日を取得
		LocalDate firstDayOfLastMonth = now.minusMonths(1).withDayOfMonth(1);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");//年月の形式に変換
		String lastYearsMonth = firstDayOfLastMonth.format(formatter);//String型に変換
		return lastYearsMonth;
	}

	//現在の日にちを"yyyy-MM"の型で取得するメソッド
	public String yearsMonth() {
		LocalDate now = LocalDate.now();//現在の日にちを取得
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");//年月の形式に変換
		String stringYearsMonth = now.format(formatter);//String型に変換
		return stringYearsMonth;
	}
}
