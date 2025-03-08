package org.example.weatherapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginController {

    @FXML
    private Button cancelButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField enterPasswordField;
    @FXML
    private Label loginMessageLabel;

    public void cancelButtonAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void loginButtonAction(ActionEvent event) {
        if (!usernameTextField.getText().isBlank() && !enterPasswordField.getText().isBlank()) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Please enter username and password");
            loginMessageLabel.setVisible(true);
        }
    }

    public void validateLogin() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

        String verifyLogin = "SELECT esteadmin FROM useraccount WHERE username = '" + usernameTextField.getText() +
                "' AND password = '" + enterPasswordField.getText() + "'";

        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            if (queryResult.next()) {
                boolean isAdmin = queryResult.getBoolean("esteadmin");
                if (isAdmin) {
                    loginMessageLabel.setText("Welcome, Admin!");
                    openNewWindow("admin.fxml");
                } else {
                    loginMessageLabel.setText("Welcome, User!");
                    openNewWindow("user.fxml");
                }
            } else {
                loginMessageLabel.setText("Invalid login. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            loginMessageLabel.setText("An error occurred. Please try again.");
        }
    }

    private void openNewWindow(String fxmlFile) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("New Window");
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.show();


            Stage currentStage = (Stage) loginMessageLabel.getScene().getWindow();
            currentStage.close();


                startServerAndClient();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startServerAndClient() {
        new Thread(() -> {
            WeatherServer.main(new String[0]);
        }).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            WeatherClient.main(new String[0]);
        }).start();
    }
}
