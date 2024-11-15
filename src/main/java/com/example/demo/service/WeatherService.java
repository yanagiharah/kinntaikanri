package com.example.demo.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.WeatherData;

@Service
public class WeatherService {
	/**天気予報API課金で使用可能
    @Value("${openweathermap.api.key}")
    private String apiKey;

    public WeatherResponse getWeather(double lat, double lon) {
    	//無料プラン
//        String url = String.format("http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&lang=ja&units=metric", lat, lon, apiKey);
        
        //有料プラン
        String url = String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&exclude=minutely,hourly&appid=%s&lang=ja&units=metric", lat, lon, apiKey);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, WeatherResponse.class);
    }
    */
	@Value("${openweather.api.key}")
    private String apiKey;

    @Value("${openweather.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherData getWeather(String city) {
        String url = String.format("%s?q=%s&appid=%s&units=metric&lang=ja", apiUrl, city, apiKey);
        String response = restTemplate.getForObject(url, String.class);
        System.out.println("API Response: " + response);
        return parseWeatherData(response);
    }

    private WeatherData parseWeatherData(String response) {
        JSONObject json = new JSONObject(response);
        JSONArray list = json.getJSONArray("list");

        JSONObject today = list.getJSONObject(0);
        JSONObject tomorrow = list.getJSONObject(8); // 3時間ごとのデータなので、8番目が24時間後

        WeatherData weatherData = new WeatherData();
        weatherData.setTodayDescription(today.getJSONArray("weather").getJSONObject(0).getString("description"));
        weatherData.setTodayWeatherType(today.getJSONArray("weather").getJSONObject(0).getString("main")); // 天気の種類を取得
        weatherData.setTodayTemp(today.getJSONObject("main").getDouble("temp"));
        weatherData.setTodayHumidity(today.getJSONObject("main").getInt("humidity"));
        weatherData.setTodayPressure(today.getJSONObject("main").getInt("pressure"));

        weatherData.setTomorrowDescription(tomorrow.getJSONArray("weather").getJSONObject(0).getString("description"));
        weatherData.setTomorrowWeatherType(tomorrow.getJSONArray("weather").getJSONObject(0).getString("main")); // 天気の種類を取得
        weatherData.setTomorrowTemp(tomorrow.getJSONObject("main").getDouble("temp"));
        weatherData.setTomorrowHumidity(tomorrow.getJSONObject("main").getInt("humidity"));
        weatherData.setTomorrowPressure(tomorrow.getJSONObject("main").getInt("pressure"));

        return weatherData;
    }
}
