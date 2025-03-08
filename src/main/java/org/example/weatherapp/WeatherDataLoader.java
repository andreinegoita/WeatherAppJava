package org.example.weatherapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.weatherapp.WeatherData;
import org.example.weatherapp.WeatherDatabaseManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class WeatherDataLoader {

    public static void loadWeatherDataFromFile(String jsonFilePath) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<WeatherData> weatherDataList = objectMapper.readValue(new File(jsonFilePath),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, WeatherData.class));

            WeatherDatabaseManager.saveWeatherDataToDatabase(weatherDataList);

            System.out.println("Data has been successfully loaded and saved to the database.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to read JSON file.");
        }
    }
}
