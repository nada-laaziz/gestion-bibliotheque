package com.biblio.controller;

import com.biblio.dao.EmpruntDAO;
import com.biblio.dao.LivreDAO;
import com.biblio.model.Emprunt;
import com.biblio.model.Livre;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsController {

    @FXML private Label totalLivresLabel;
    @FXML private Label empruntsEnCoursLabel;
    @FXML private Label empruntsRendusLabel;
    @FXML private Label livresIndisponiblesLabel;
    @FXML private ListView<String> statsListView;

    private final LivreDAO livreDAO = new LivreDAO();
    private final EmpruntDAO empruntDAO = new EmpruntDAO();

    @FXML
    public void initialize() {
        chargerStats();
    }

    public void chargerStats() {
        try {
            List<Livre> livres = livreDAO.getAll();
            List<Emprunt> emprunts = empruntDAO.getAll();

            totalLivresLabel.setText(String.valueOf(livres.size()));

            long enCours = emprunts.stream().filter(e -> "En cours".equals(e.getStatut())).count();
            long rendus = emprunts.stream().filter(e -> "Rendu".equals(e.getStatut())).count();
            long indisponibles = livres.stream().filter(l -> !l.isDisponible()).count();

            empruntsEnCoursLabel.setText(String.valueOf(enCours));
            empruntsRendusLabel.setText(String.valueOf(rendus));
            livresIndisponiblesLabel.setText(String.valueOf(indisponibles));

            Map<String, Long> parCategorie = new HashMap<>();
            for (Livre l : livres) {
                String cat = l.getCategorie() == null ? "Non definie" : l.getCategorie();
                parCategorie.merge(cat, 1L, Long::sum);
            }
            statsListView.getItems().clear();
            parCategorie.forEach((cat, count) ->
                statsListView.getItems().add(cat + " : " + count + " livre(s)")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
