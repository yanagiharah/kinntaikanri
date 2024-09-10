package com.example.demo.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.inter.MessageOutput;
import com.example.demo.model.DailyReportForm;
import com.example.demo.model.Users;
import com.example.demo.service.CommonActivityService;
import com.example.demo.service.DailyReportService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * 日報管理に関するリクエストを処理するコントローラークラスです。
 */
@Controller
@RequestMapping("/daily")
public class DailyReportController {
	
	private final DailyReportService dailyReportService;
	private final MessageOutput messageOutput;
	private final CommonActivityService commonActivityService;
	
	/**
	 * コンストラクタ。日報管理機能に必要なサービスとメッセージ出力を初期化します。
	 * 
	 *@param dailyReportService 日報管理の内部ロジック
	 *@param messageOutput メッセージソース
	 *@param commonActivityService 共通処理のロジック
	 */
	public DailyReportController(
			DailyReportService dailyReportService,
			MessageOutput messageOutput,
			CommonActivityService commonActivityService
	){
		this.dailyReportService = dailyReportService;
		this.messageOutput = messageOutput;
		this.commonActivityService = commonActivityService;
	}

	/**
	 * 初期表示画面を表示します。日付は本日分です。
	 * 
	 * @param date 表示する日付（初期表示は本日分）
	 * @param session ログイン情報
	 * @param model ビューに渡す内容
	 * @return　日報画面のビュー名
	 */
	@RequestMapping("/detail")
	public String dailyReportDetail(String date, HttpSession session, Model model) {
		Users users = commonActivityService.usersSession(model, session);
	    DailyReportForm dailyReportForm = dailyReportService.initialSet(users, date);
	    model.addAttribute("dailyReportForm", dailyReportForm);
	    return "DailyReport/dailyReport";
	    
	}
	
	/**
	 * 提出ボタン押下時の処理を行います。
	 * 
	 * @param dailyReportForm　日報フォームのオブジェクト
	 * @param result　バリデーションの結果
	 * @param session ログイン情報
	 * @param model ビューに渡す内容
	 * @return 次のビューの名前
	 */
	@RequestMapping(value = "/detailUpdate", params = "submission", method = RequestMethod.POST)
	public String updateDailyReportDetail(
			@Valid @ModelAttribute("dailyReportForm") DailyReportForm dailyReportForm,
			BindingResult result,
			HttpSession session,
			Model model
	){
		//エラーチェック
		if (result.hasErrors()) {
			commonActivityService.usersSession(model, session);
			return "DailyReport/dailyReport";
		}
		
		//更新処理
		dailyReportService.updateDailyReportDetail(dailyReportForm);
		
		//更新メッセージ取得
		model.addAttribute("message", messageOutput.message("dailyReport.update.success"));
		
		//日付のフォーマット処理
		LocalDate calendarDate = dailyReportForm.getDailyReportDetailForm().get(0).getDailyReportDetailDate();
		String calendarDateS = dailyReportService.formatLocalDate(calendarDate, "yyyy-MM-dd");
		
		return dailyReportDetail(calendarDateS, session, model);
	}
	
	/**
	 * 戻るボタン押下時の処理を行います。
	 *
	 * @param session ログイン情報
	 * @param model ビューに渡すモデル
	 * @return メニュー画面のビュー名
	 */
	@RequestMapping(value = "/detailUpdate", params = "back", method = RequestMethod.POST)
	public String back(Model model, HttpSession session) {
		commonActivityService.backMenu(model, session);
		return "menu/processMenu";
	}
	
	/**
	 * カレンダーコントロール押下時の処理を行います。
	 *
	 * @param dateS 選択された日付
	 * @param session ログイン情報
	 * @param model ビューに渡すモデル
	 * @return 日報詳細表示画面のModelAndViewオブジェクト
	 */
	@RequestMapping(value = "/date", params = "dailyReportDate", method = RequestMethod.POST)
    public ModelAndView handleDateSubmission(
    		@ModelAttribute("dailyReportDate") String dateS,
    		HttpSession session,
    		Model model
    ){
		return new ModelAndView(dailyReportDetail(dateS,session,model));
    }
}
	
///----------★一時的にとっておきたい(javaScript連携の為)------------------
	
	//カレンダーコントロール押下後
//	@ResponseBody
//	@PostMapping("/calendarDate")
//	public  Map<String, Object> receiveDate(@RequestParam("date") String date, HttpSession session, Model model) {
//		Users users = (Users) session.getAttribute("Users");
//		
//		Integer userId = users.getUserId();
//		LocalDate calendarDate ;
//		
//		if(date == null) {
//			calendarDate = LocalDate.now();
//		} else {
//			calendarDate = LocalDate.parse(date);
//		}
//		
//		DailyReportForm dailyReportForm = dailyReportService.getDailyReport(userId, calendarDate); 
//		if (dailyReportForm== null) {
//			dailyReportForm= new DailyReportForm();
//			dailyReportForm.setUserId(userId);
//			dailyReportForm.setDailyReportDate(calendarDate);
//		}
//		
//		
//		// 日報詳細を取得 nullまたは空なら新しいリストを初期化
//		List<DailyReportDetailForm> dailyReportDetailForm = dailyReportService.getDailyReportDetail(userId, calendarDate);
//	    if (dailyReportDetailForm == null || dailyReportDetailForm.isEmpty()) {
//	        dailyReportDetailForm = new ArrayList<>();
//	    }
//	    
//	    //空のリストを10行まで追加で作成（今日の日付とユーザーIDをセット）
//	    
//	    for( dailyReportDetailForm.size() ; dailyReportDetailForm.size() < 10 ; ) {
//	    	DailyReportDetailForm emptyDetailForm = new DailyReportDetailForm();
//	    	emptyDetailForm.setUserId(userId);
//	    	emptyDetailForm.setDailyReportDetailDate(calendarDate);
//	    	dailyReportDetailForm.add(emptyDetailForm);
//	    }
//	    
//	    dailyReportForm.setDailyReportDetailForm(dailyReportDetailForm);
//	    dailyReportForm.setUserId(userId);
//		
//		Map<String, Object> response = new HashMap<>();
//	    response.put("dailyReportForm", dailyReportForm);
//	    response.put("Users", users);
//		return response;
//	    //return "redirect:/daily/detail?date=" + date;
//	}
