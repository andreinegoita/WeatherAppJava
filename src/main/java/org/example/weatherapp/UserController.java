package org.example.weatherapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class UserController {

    @FXML
    private TextField latitudeField;

    @FXML
    private TextField longitudeField;

    @FXML
    private Label weatherInfoLabel;

    @FXML
    private Label temperatureTodayLabel;

    @FXML
    private Label temperatureTomorrowLabel;

    @FXML
    private Label temperatureDayAfterTomorrowLabel;

    @FXML
    private ImageView weatherIconToday;

    @FXML
    private ImageView weatherIconTomorrow;

    @FXML
    private ImageView weatherIconDayAfterTomorrow;

    @FXML
    private Button cancelButton;

    @FXML
    public void handleSearchButtonAction(ActionEvent event) {
        String latitude = latitudeField.getText();
        String longitude = longitudeField.getText();

        if (latitude.isEmpty() || longitude.isEmpty()) {
            weatherInfoLabel.setText("Please enter both latitude and longitude.");
            return;
        }

        WeatherData weatherData = getWeatherDataFromDatabase(latitude, longitude);

        if (weatherData != null) {
            weatherInfoLabel.setText("Here's the weather info for location: " + weatherData.getLocation());
            temperatureTodayLabel.setText("Temperature: " + weatherData.getTemperature() + "°C");

            WeatherData.Forecast forecastTomorrow = weatherData.getForecast().get("Tomorrow");
            WeatherData.Forecast forecastDayAfterTomorrow = weatherData.getForecast().get("Day after Tomorrow");

            if (forecastTomorrow != null) {
                temperatureTomorrowLabel.setText("Temperature: " + forecastTomorrow.getTemperature() + "°C");
                weatherIconTomorrow.setImage(new Image(getClass().getResource(getWeatherIcon(forecastTomorrow.getWeather())).toString()));
            } else {
                temperatureTomorrowLabel.setText("No forecast available for Tomorrow.");
            }

            if (forecastDayAfterTomorrow != null) {
                temperatureDayAfterTomorrowLabel.setText("Temperature: " + forecastDayAfterTomorrow.getTemperature() + "°C");
                weatherIconDayAfterTomorrow.setImage(new Image(getClass().getResource(getWeatherIcon(forecastDayAfterTomorrow.getWeather())).toString()));
            } else {
                temperatureDayAfterTomorrowLabel.setText("No forecast available for Day after Tomorrow.");
            }

            weatherIconToday.setImage(new Image(getClass().getResource(getWeatherIcon(weatherData.getCurrentWeather())).toString()));
        } else {
            weatherInfoLabel.setText("No data found for the given coordinates.");
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private WeatherData getWeatherDataFromDatabase(String latitude, String longitude) {
        String query = "SELECT wd.location, wd.current_weather, wd.temperature, wd.latitude, wd.longitude, " +
                "f.day, f.weather AS forecast_weather, f.temperature AS forecast_temperature " +
                "FROM weather_data wd " +
                "JOIN forecast f ON wd.id = f.weather_data_id " +
                "WHERE wd.latitude = ? AND wd.longitude = ?";

        double inputLatitude = Double.parseDouble(latitude);
        double inputLongitude = Double.parseDouble(longitude);

        WeatherData closestWeatherData = null;
        double closestDistance = Double.MAX_VALUE;

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/weatherdb", "postgres", "Dacialogan2017!");
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDouble(1, inputLatitude);
            statement.setDouble(2, inputLongitude);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                double dbLatitude = resultSet.getDouble("latitude");
                double dbLongitude = resultSet.getDouble("longitude");

                double distance = calculateDistance(inputLatitude, inputLongitude, dbLatitude, dbLongitude);
                System.out.println("Distance: " + distance + " km");

                if (distance < closestDistance) {
                    closestDistance = distance;

                    if (closestDistance <= 7000) {
                        String location = resultSet.getString("location");
                        String currentWeather = resultSet.getString("current_weather");
                        double temperature = resultSet.getDouble("temperature");

                        closestWeatherData = new WeatherData();
                        closestWeatherData.setLocation(location);
                        closestWeatherData.setCurrentWeather(currentWeather);
                        closestWeatherData.setTemperature(temperature);
                        closestWeatherData.setForecast(new HashMap<>());
                    }
                }

                if (closestWeatherData != null) {
                    String forecastDay = resultSet.getString("day").trim();
                    WeatherData.Forecast forecastData = new WeatherData.Forecast();
                    forecastData.setWeather(resultSet.getString("forecast_weather"));
                    forecastData.setTemperature(resultSet.getDouble("forecast_temperature"));

                    if ("Tomorrow".equalsIgnoreCase(forecastDay)) {
                        closestWeatherData.getForecast().put("Tomorrow", forecastData);
                    } else if ("Day after Tomorrow".equalsIgnoreCase(forecastDay)) {
                        closestWeatherData.getForecast().put("Day after Tomorrow", forecastData);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return closestWeatherData;
    }

    public void handleCloseButtonAction(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private String getWeatherIcon(String weatherCondition) {
        switch (weatherCondition.toLowerCase()) {
            case "clear":
                return "/sun_7716499.png";
            case "cloudy":
                return "/sky_16227679.png";
            case "rainy":
                return "/raining_7687017.png";
            case "partly cloudy":
                return "/partly-cloudy_9369630.png";
            case "windy":
                return "/wind_7284164.png";
            case "snowy":
                return "/snowy_460494.png";
            default:
                return "/icons/default.png";
        }
    }
}
