package com.biblio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;

public class MainController {

    @FXML private MenuItem menuExportLivres;
    @FXML private MenuItem menuExportEmprunts;
    @FXML private MenuItem menuQuitter;
    @FXML private MenuItem menuAPropos;

    @FXML
    public void initialize() {
        menuQuitter.setOnAction(e -> System.exit(0));

        menuAPropos.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("À propos");
            alert.setHeaderText("Gestion de Bibliothèque");
            alert.setContentText("Application développée avec JavaFX\nEnsao GI3 - 2025/2026");
            alert.showAndWait();
        });
    }
}