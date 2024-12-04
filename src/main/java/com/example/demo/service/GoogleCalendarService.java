package com.example.demo.service;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Events;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "Google Calendar API Java Spring Boot";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "iwantholiday-d8c7401a6a5b.json";
    private Calendar cachedService;

    //初回カレンダー取得
    public Calendar getCalendar() throws GeneralSecurityException, IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

        return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    //キャッシュカレンダー取得
    private Calendar getCachedCalendar() throws GeneralSecurityException, IOException {
        if (cachedService == null) {
            cachedService = getCalendar();
        }
        return cachedService;
    }
    //取得したカレンダーの値を格納
    public Events setListEvents(Date date) {
    	 Calendar service = null;
         try {
             service = getCachedCalendar();
         } catch (GeneralSecurityException | IOException e) {
             e.printStackTrace();
         }

         DateTime dateTime = new DateTime(date);
         Events events = null;
         try {
             events = service.events().list("ja.japanese.official#holiday@group.v.calendar.google.com")//祝日カレンダ（祭日除く）
                     .setMaxResults(10)//10件取得
                     .setTimeMin(dateTime)
                     .setOrderBy("startTime")//開始時間で照準
                     .setSingleEvents(true)
                     .setFields("items(start,end,summary)")//必要なものだけ取得
                     .execute();
         } catch (IOException e) {
        	 System.out.println("error has occured");
             e.printStackTrace();
         }
         return events;
    }
//APIから受け取ったカレンダーの日付を取得
    public List<String> listEvents(Events events){
//    	    System.out.println(events);
    	    List<String> holidays = events.getItems().stream()
    	        .map(event -> {
    	            DateTime startDateTime = event.getStart().getDateTime();
    	            if (startDateTime == null) {
    	                startDateTime = event.getStart().getDate(); // 終日イベントの場合
    	            }
    	            LocalDate localDate = Instant.ofEpochMilli(startDateTime.getValue()).atZone(ZoneId.systemDefault()).toLocalDate();
    	            return localDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    	        })
    	        .collect(Collectors.toList());
    	    return holidays;
    	}
    
    //APIから受け取ったカレンダーの日付と名前を格納したMapを取得
    public Map<String, String> listEventsName(Events events) {
        int initialCapacity = (int) (events.getItems().size() / 0.75) + 1;
        Map<LocalDate, String> holidaysMap = new HashMap<>(initialCapacity);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        // 日付と祝日名をMapに格納
        events.getItems().forEach(event -> {
            DateTime startDateTime = event.getStart().getDateTime();
            if (startDateTime == null) {
                startDateTime = event.getStart().getDate(); // 終日イベントの場合
            }
            LocalDate localDate = Instant.ofEpochMilli(startDateTime.getValue())
                                         .atZone(ZoneId.systemDefault())
                                         .toLocalDate();
            String holidayName = event.getSummary();
            holidaysMap.put(localDate, holidayName);
        });

        System.out.println(holidaysMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        entry -> entry.getKey().format(dateFormatter),
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, // マージ関数
                        LinkedHashMap::new // 順序を保持するためにLinkedHashMapを使用
                )));
        // フォーマットされたMapの生成
        return holidaysMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        entry -> entry.getKey().format(dateFormatter),
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, // マージ関数
                        LinkedHashMap::new // 順序を保持するためにLinkedHashMapを使用
                ));
    }
    
    public Date convertToDate(int year, int month){
        String dateString = year + "-" + String.format("%02d", month) + "-01";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dateYearMonth = null;
		try {
			dateYearMonth = sdf.parse(dateString);
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
        return dateYearMonth;
    }
    
    //祝日の日付返却用
    public Events getHolidaysEvents(Integer years,Integer month){
		Date date = convertToDate(years, month);
		Events events = setListEvents(date);
		return events;
	}
    //祝日の日付と名前のリスト返却用（勤怠管理一般でしか使いません、現状）
    public List<String> getListHolidays(Integer years,Integer month){
    	Events events = getHolidaysEvents(years,month);
		List<String> holidays = listEvents(events);
		return holidays;
    }
    
}
//package com.example.demo.service;
//
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.util.Arrays;
//
//import org.springframework.stereotype.Service;
//
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.services.calendar.Calendar;
//import com.google.api.services.calendar.model.CalendarList;
//import com.google.api.services.calendar.model.CalendarListEntry;
//import com.google.auth.http.HttpCredentialsAdapter;
//import com.google.auth.oauth2.GoogleCredentials;
//@Service
//public class GoogleCalenderService {
//
//	public Calendar  initializeCalendar() throws Exception {
//	    HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
//	    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
//	    String filePath = "iwantholiday-f0994d4b6fbe.json";
//	    try(InputStream input = new FileInputStream(filePath)){
//	        GoogleCredentials credentials = GoogleCredentials
//	                .fromStream(input)
//	                .createScoped(Arrays.asList("https://www.googleapis.com/auth/calendar"));
//	        
//	        return new Calendar.Builder(transport, jsonFactory, new HttpCredentialsAdapter(credentials))
//	        		.setApplicationName("IWantHoliday")
//	        		.build();
//	    }
//	}
//	
//	public void addCalendar() throws Exception{
//	    Calendar calendar = initializeCalendar();
//	    Calendar.CalendarList calendarList = calendar.calendarList();
//	    
//	    CalendarListEntry content = new CalendarListEntry();
//	    content.setId("ja.japanese#holiday@group.v.calendar.google.com");
//	    Calendar.CalendarList.Insert insert = calendarList.insert(content);
//	    
//	    CalendarListEntry res = insert.execute();
//	    System.out.println(res);
//	}
//	public void listCalendars() throws Exception{
//	    Calendar calendar = initializeCalendar();
//	    Calendar.CalendarList calendarList = calendar.calendarList();
//	    Calendar.CalendarList.List list = calendarList.list();
//	    
//	    CalendarList res = list.execute();
//	    System.out.println(res);
//	}
//
//}
//
////Calendar calendar = null;
////try {
////	calendar = googleCalenderService.getCalendar();
////} catch (Exception e) {
////	// TODO 自動生成された catch ブロック
////	e.printStackTrace();
////}
////Calendar.CalendarList calendarList = calendar.calendarList();
////Calendar.CalendarList.List list = null;
////try {
////	list = calendarList.list();
////} catch (IOException e) {
////	// TODO 自動生成された catch ブロック
////	e.printStackTrace();
////}
////
////CalendarList res = null;
////try {
////	res = list.execute();
////} catch (IOException e) {
////	// TODO 自動生成された catch ブロック
////	e.printStackTrace();
////}
////System.out.println(res);