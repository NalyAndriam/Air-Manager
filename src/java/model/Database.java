package model;

import java.sql.DriverManager;

public class Database {

    public static java.sql.Connection getConnection() throws Exception {

        String userName = "postgres" ;
        String password = "itu16" ; 
        String url = "jdbc:postgresql://localhost:5432/avion";
        Class.forName("org.postgresql.Driver");
        java.sql.Connection connect =  DriverManager.getConnection(url, userName ,password);
        return connect; 

    }
}
