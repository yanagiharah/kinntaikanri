package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.WeatherResponse;

@Service
public class WeatherService {

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
}
