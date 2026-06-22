package com.biblio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/biblio/view/main-view.fxml")
        );
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(
            getClass().getResource("/com/biblio/style.css").toExternalForm()
        );
        stage.setTitle("Gestion de Bibliotheque - ENSAO GI3");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
