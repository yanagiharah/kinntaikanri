package com.example.demo.model;

import java.util.List;

import lombok.Data;

@Data
public class WeatherResponse {
    private Current current;
    private List<Daily> daily;

    // Getters and setters

    @Data
    public static class Current {
        private double temp;
        private List<Weather> weather;
        // Getters and setters
    }

    @Data
    public static class Daily {
        private Temp temp;
        private List<Weather> weather;
        // Getters and setters
    }

    @Data
    public static class Temp {
        private double day;
        private double night;
        // Getters and setters
    }

    @Data
    public static class Weather {
        private String description;
        private String icon;
        // Getters and setters
    }
}
