package org.example.weatherapp;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class WeatherDatabaseManager {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/weatherdb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Dacialogan2017!";

    public static void saveWeatherDataToDatabase(List<WeatherData> weatherDataList) {
        String insertWeatherSQL = "INSERT INTO weather_data (location, current_weather, temperature, latitude, longitude) VALUES (?, ?, ?, ?, ?)";
        String insertForecastSQL = "INSERT INTO forecast (weather_data_id, day, weather, temperature) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement weatherStatement = connection.prepareStatement(insertWeatherSQL, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement forecastStatement = connection.prepareStatement(insertForecastSQL)) {

            for (WeatherData data : weatherDataList) {
                weatherStatement.setString(1, data.getLocation());
                weatherStatement.setString(2, data.getCurrentWeather());
                weatherStatement.setDouble(3, data.getTemperature());
                weatherStatement.setDouble(4, data.getLatitude());
                weatherStatement.setDouble(5, data.getLongitude());
                int affectedRows = weatherStatement.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Failed to insert weather data, no rows affected.");
                }

                ResultSet generatedKeys = weatherStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int weatherDataId = generatedKeys.getInt(1);

                    for (Map.Entry<String, WeatherData.Forecast> entry : data.getForecast().entrySet()) {
                        forecastStatement.setInt(1, weatherDataId);
                        forecastStatement.setString(2, entry.getKey());
                        forecastStatement.setString(3, entry.getValue().getWeather());
                        forecastStatement.setDouble(4, entry.getValue().getTemperature());
                        forecastStatement.executeUpdate();
                    }
                }
            }

            System.out.println("Data has been saved to the database successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

