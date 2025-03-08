package org.example.weatherapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.Socket;

public class AdminController {

    @FXML
    private TextArea jsonTextArea;
    @FXML
    private Button sendButton;
    @FXML
    private Label responseLabel;


    @FXML
    public void handleSendButtonAction(ActionEvent event) {
        String jsonData = jsonTextArea.getText().trim();


        if (jsonData.isEmpty()) {
            responseLabel.setText("Please enter some JSON data.");
            return;
        }


        String jsonFilePath = "weather_data.json";
        try (FileWriter fileWriter = new FileWriter(jsonFilePath)) {
            fileWriter.write(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
            responseLabel.setText("Failed to save JSON to file.");
            return;
        }


        sendToServer(jsonFilePath);
    }


    private void sendToServer(String jsonFilePath) {
        String serverAddress = "localhost";
        int serverPort = 12346;

        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {


            out.println("save_data");


            String serverResponse = in.readLine();
            responseLabel.setText(serverResponse);

        } catch (IOException e) {
            e.printStackTrace();
            responseLabel.setText("Failed to connect to server.");
        }
    }
}
