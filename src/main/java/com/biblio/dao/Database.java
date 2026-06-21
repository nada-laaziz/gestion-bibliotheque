package com.biblio.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/bibliotheque_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // mets ton mot de passe MySQL si tu en as un

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}