package org.example.weatherapp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class WeatherDataSaver {
    public static void saveWeatherData(List<WeatherData> weatherDataList, String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(filePath), weatherDataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
