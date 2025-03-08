package org.example.weatherapp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.List;

public class WeatherServer {
    private static final int PORT = 12346;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);


            Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/weatherdb", "postgres", "Dacialogan2017!"
            );


            while (true) {
                Socket clientSocket = serverSocket.accept(); // Acceptă o conexiune de la client
                System.out.println("New client connected");


                new ClientHandler(clientSocket, connection).start();
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }


    static class ClientHandler extends Thread {
        private Socket socket;
        private Connection connection;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket, Connection connection) {
            this.socket = socket;
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String input;
                while ((input = in.readLine()) != null) {
                    System.out.println("Received input: " + input);


                    if ("save_data".equalsIgnoreCase(input)) {
                        String jsonFilePath = "weather_data.json";
                        saveWeatherDataToDatabase(jsonFilePath);
                        out.println("Weather data has been saved to the database.");
                    } else {
                        out.println("Invalid input.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        private void saveWeatherDataToDatabase(String jsonFilePath) {
            List<WeatherData> weatherDataList = loadWeatherData(jsonFilePath);

            if (weatherDataList != null) {
                try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO weather_data (location, current_weather, temperature, latitude, longitude) VALUES (?, ?, ?, ?, ?)")) {

                    for (WeatherData weatherData : weatherDataList) {
                        stmt.setString(1, weatherData.getLocation());
                        stmt.setString(2, weatherData.getCurrentWeather());
                        stmt.setDouble(3, weatherData.getTemperature());
                        stmt.setDouble(4, weatherData.getLatitude());
                        stmt.setDouble(5, weatherData.getLongitude());
                        stmt.addBatch();
                    }

                    stmt.executeBatch();
                    System.out.println("Weather data has been saved to the database.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Failed to load weather data from JSON.");
            }
        }


        private List<WeatherData> loadWeatherData(String filePath) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {

                return objectMapper.readValue(
                        new File(filePath),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, WeatherData.class)
                );
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
