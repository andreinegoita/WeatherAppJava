package org.example.weatherapp;

import java.util.Map;

public class WeatherData {
    private String location;
    private String currentWeather;
    private double temperature;
    private double latitude;
    private double longitude;
    private Map<String, Forecast> forecast;


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(String currentWeather) {
        this.currentWeather = currentWeather;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Map<String, Forecast> getForecast() {
        return forecast;
    }

    public void setForecast(Map<String, Forecast> forecast) {
        this.forecast = forecast;
    }

    public boolean getWeatherInfo() {
        return true;
    }

    public static class Forecast {
        private String weather;
        private double temperature;

        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }
    }
}
