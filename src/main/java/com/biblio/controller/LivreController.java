package com.biblio.controller;

import com.biblio.dao.LivreDAO;
import com.biblio.model.Livre;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LivreController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterCategorie;
    @FXML private TextField titreField;
    @FXML private TextField auteurField;
    @FXML private TextField isbnField;
    @FXML private ComboBox<String> categorieField;
    @FXML private Spinner<Integer> nbExemplairesSpinner;
    @FXML private CheckBox disponibleCheck;
    @FXML private TextArea descriptionArea;
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnVider;
    @FXML private Button btnExporter;
    @FXML private TableView<Livre> livreTable;
    @FXML private TableColumn<Livre, Integer> colId;
    @FXML private TableColumn<Livre, String> colTitre;
    @FXML private TableColumn<Livre, String> colAuteur;
    @FXML private TableColumn<Livre, String> colIsbn;
    @FXML private TableColumn<Livre, String> colCategorie;
    @FXML private TableColumn<Livre, Integer> colNbEx;
    @FXML private TableColumn<Livre, Boolean> colDispo;
    @FXML private ProgressBar progressBar;
    @FXML private Label totalLabel;

    private final LivreDAO livreDAO = new LivreDAO();
    private final ObservableList<Livre> livreList = FXCollections.observableArrayList();
    private Livre livreSelectionne = null;

    private static final List<String> CATEGORIES = List.of(
        "Roman", "Science", "Histoire", "Informatique",
        "Philosophie", "Art", "Jeunesse", "Autre"
    );

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colNbEx.setCellValueFactory(new PropertyValueFactory<>("nbExemplaires"));
        colDispo.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        categorieField.setItems(FXCollections.observableArrayList(CATEGORIES));
        filterCategorie.setItems(FXCollections.observableArrayList(CATEGORIES));

        nbExemplairesSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1)
        );

        btnAjouter.setTooltip(new Tooltip("Ajouter un nouveau livre"));
        btnModifier.setTooltip(new Tooltip("Modifier le livre selectionne"));
        btnSupprimer.setTooltip(new Tooltip("Supprimer le livre selectionne"));
        btnExporter.setTooltip(new Tooltip("Exporter la liste en CSV"));

        livreTable.setItems(livreList);

        livreTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    livreSelectionne = newVal;
                    remplirFormulaire(newVal);
                }
            }
        );

        btnAjouter.setOnAction(e -> ajouterLivre());
        btnModifier.setOnAction(e -> modifierLivre());
        btnSupprimer.setOnAction(e -> supprimerLivre());
        btnVider.setOnAction(e -> viderFormulaire());
        btnRechercher();
        btnExporter.setOnAction(e -> exporterCSV());

        chargerLivres();
    }

    private void btnRechercher() {
        Button btnR = new Button();
        btnR.setOnAction(e -> rechercherLivre());
    }

    private void chargerLivres() {
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        try {
            List<Livre> livres = livreDAO.getAll();
            livreList.setAll(livres);
            totalLabel.setText("Total : " + livres.size() + " livre(s)");
        } catch (SQLException e) {
            afficherErreur("Erreur chargement : " + e.getMessage());
        } finally {
            progressBar.setProgress(1.0);
        }
    }

    private void ajouterLivre() {
        if (titreField.getText().trim().isEmpty()) {
            afficherErreur("Le titre est obligatoire !");
            return;
        }
        try {
            livreDAO.ajouter(getFormulaireLivre());
            chargerLivres();
            viderFormulaire();
            afficherInfo("Livre ajoute avec succes !");
        } catch (SQLException e) {
            afficherErreur("Erreur ajout : " + e.getMessage());
        }
    }

    private void modifierLivre() {
        if (livreSelectionne == null) { afficherErreur("Selectionnez un livre !"); return; }
        Livre livre = getFormulaireLivre();
        livre.setId(livreSelectionne.getId());
        try {
            livreDAO.modifier(livre);
            chargerLivres();
            viderFormulaire();
            afficherInfo("Livre modifie !");
        } catch (SQLException e) {
            afficherErreur("Erreur modification : " + e.getMessage());
        }
    }

    private void supprimerLivre() {
        if (livreSelectionne == null) { afficherErreur("Selectionnez un livre !"); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Supprimer " + livreSelectionne.getTitre() + " ?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    livreDAO.supprimer(livreSelectionne.getId());
                    chargerLivres();
                    viderFormulaire();
                } catch (SQLException e) {
                    afficherErreur("Erreur suppression : " + e.getMessage());
                }
            }
        });
    }

    private void rechercherLivre() {
        String motCle = searchField.getText().trim();
        try {
            List<Livre> livres = motCle.isEmpty() ? livreDAO.getAll() : livreDAO.rechercher(motCle);
            livreList.setAll(livres);
            totalLabel.setText("Total : " + livres.size() + " livre(s)");
        } catch (SQLException e) {
            afficherErreur("Erreur recherche : " + e.getMessage());
        }
    }

    private void exporterCSV() {
        FileChooser fc = new FileChooser();
        fc.setInitialFileName("livres.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        java.io.File file = fc.showSaveDialog(livreTable.getScene().getWindow());
        if (file != null) {
            try (FileWriter w = new FileWriter(file)) {
                w.write("ID,Titre,Auteur,ISBN,Categorie,Exemplaires,Disponible\n");
                for (Livre l : livreList) {
                    w.write(l.getId()+","+l.getTitre()+","+l.getAuteur()+","+
                            l.getIsbn()+","+l.getCategorie()+","+
                            l.getNbExemplaires()+","+l.isDisponible()+"\n");
                }
                afficherInfo("Export reussi !");
            } catch (IOException e) {
                afficherErreur("Erreur export : " + e.getMessage());
            }
        }
    }

    private Livre getFormulaireLivre() {
        Livre l = new Livre();
        l.setTitre(titreField.getText().trim());
        l.setAuteur(auteurField.getText().trim());
        l.setIsbn(isbnField.getText().trim());
        l.setCategorie(categorieField.getValue());
        l.setNbExemplaires(nbExemplairesSpinner.getValue());
        l.setDisponible(disponibleCheck.isSelected());
        l.setDescription(descriptionArea.getText().trim());
        return l;
    }

    private void remplirFormulaire(Livre l) {
        titreField.setText(l.getTitre());
        auteurField.setText(l.getAuteur());
        isbnField.setText(l.getIsbn());
        categorieField.setValue(l.getCategorie());
        nbExemplairesSpinner.getValueFactory().setValue(l.getNbExemplaires());
        disponibleCheck.setSelected(l.isDisponible());
        descriptionArea.setText(l.getDescription());
    }

    private void viderFormulaire() {
        titreField.clear(); auteurField.clear(); isbnField.clear();
        categorieField.setValue(null);
        nbExemplairesSpinner.getValueFactory().setValue(1);
        disponibleCheck.setSelected(true);
        descriptionArea.clear();
        livreSelectionne = null;
        livreTable.getSelectionModel().clearSelection();
    }

    private void afficherErreur(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private void afficherInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}
