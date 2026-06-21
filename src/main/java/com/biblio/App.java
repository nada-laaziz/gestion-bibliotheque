package com.biblio;

import java.sql.Connection;
import com.biblio.dao.Database;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        String message;
        try (Connection conn = Database.getConnection()) {
            message = "Connexion à la base de données réussie !";
        } catch (Exception e) {
            message = "Erreur de connexion : " + e.getMessage();
        }

        Label label = new Label(message);
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 600, 400);

        stage.setTitle("Gestion de Bibliothèque");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}