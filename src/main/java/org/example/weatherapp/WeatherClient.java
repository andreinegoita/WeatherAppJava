package org.example.weatherapp;

import java.io.*;
import java.net.*;

public class WeatherClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12346;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to the server");

            String input;
            while (true) {
                System.out.print("Enter coordinates (latitude,longitude) or 'exit' to quit: ");
                input = userInput.readLine();

                if ("exit".equalsIgnoreCase(input)) {
                    System.out.println("Exiting...");
                    break;
                }

                out.println(input);

                String response;
                while ((response = in.readLine()) != null) {
                    System.out.println(response);
                    if (response.equals("")) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
