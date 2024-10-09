package com.example.demo.model;

import lombok.Data;

@Data
public class WeatherData {
    private String todayDescription;
    private double todayTemp;
    private int todayHumidity;
    private int todayPressure;

    private String tomorrowDescription;
    private double tomorrowTemp;
    private int tomorrowHumidity;
    private int tomorrowPressure;

    // Getters and Setters
}
