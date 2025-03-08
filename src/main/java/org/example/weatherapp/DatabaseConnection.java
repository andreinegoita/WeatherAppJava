package org.example.weatherapp;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public Connection databaseLink;

    public Connection getConnection(){
        String databaseName="users";
        String databaseUser="postgres";
        String databasePassword="Dacialogan2017!";
        String url="jdbc:postgresql://localhost/"+databaseName;

        try{
            Class.forName("org.postgresql.Driver");
            databaseLink=DriverManager.getConnection(url,databaseUser,databasePassword);
        }catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }
        return databaseLink;
    }

}
