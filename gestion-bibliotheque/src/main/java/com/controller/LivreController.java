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
    @FXML private Button btnRechercher;
    @FXML private Button btnReset;

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
        // Configurer les colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colNbEx.setCellValueFactory(new PropertyValueFactory<>("nbExemplaires"));
        colDispo.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        // Remplir les ComboBox
        categorieField.setItems(FXCollections.observableArrayList(CATEGORIES));
        filterCategorie.setItems(FXCollections.observableArrayList(CATEGORIES));

        // Spinner
        nbExemplairesSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1)
        );

        // Tooltips
        btnAjouter.setTooltip(new Tooltip("Ajouter un nouveau livre"));
        btnModifier.setTooltip(new Tooltip("Modifier le livre sélectionné"));
        btnSupprimer.setTooltip(new Tooltip("Supprimer le livre sélectionné"));
        btnExporter.setTooltip(new Tooltip("Exporter la liste en CSV"));

        // Lier la table à la liste
        livreTable.setItems(livreList);

        // Sélection dans la table → remplir le formulaire
        livreTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    livreSelectionne = newVal;
                    remplirFormulaire(newVal);
                }
            }
        );

        // Actions des boutons
        btnAjouter.setOnAction(e -> ajouterLivre());
        btnModifier.setOnAction(e -> modifierLivre());
        btnSupprimer.setOnAction(e -> supprimerLivre());
        btnVider.setOnAction(e -> viderFormulaire());
        btnRechercher.setOnAction(e -> rechercherLivre());
        btnReset.setOnAction(e -> chargerLivres());
        btnExporter.setOnAction(e -> exporterCSV());

        // Charger les données
        chargerLivres();
    }

    private void chargerLivres() {
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        try {
            List<Livre> livres = livreDAO.getAll();
            livreList.setAll(livres);
            totalLabel.setText("Total : " + livres.size() + " livre(s)");
            filterCategorie.setValue(null);
            searchField.clear();
        } catch (SQLException e) {
            afficherErreur("Erreur lors du chargement : " + e.getMessage());
        } finally {
            progressBar.setProgress(1.0);
        }
    }

    private void ajouterLivre() {
        if (titreField.getText().trim().isEmpty()) {
            afficherErreur("Le titre est obligatoire !");
            return;
        }
        Livre livre = getFormulaireLivre();
        try {
            livreDAO.ajouter(livre);
            chargerLivres();
            viderFormulaire();
            afficherInfo("Livre ajouté avec succès !");
        } catch (SQLException e) {
            afficherErreur("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    private void modifierLivre() {
        if (livreSelectionne == null) {
            afficherErreur("Sélectionnez un livre à modifier !");
            return;
        }
        Livre livre = getFormulaireLivre();
        livre.setId(livreSelectionne.getId());
        try {
            livreDAO.modifier(livre);
            chargerLivres();
            viderFormulaire();
            afficherInfo("Livre modifié avec succès !");
        } catch (SQLException e) {
            afficherErreur("Erreur lors de la modification : " + e.getMessage());
        }
    }

    private void supprimerLivre() {
        if (livreSelectionne == null) {
            afficherErreur("Sélectionnez un livre à supprimer !");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le livre ?");
        confirm.setContentText("Voulez-vous vraiment supprimer \"" + livreSelectionne.getTitre() + "\" ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    livreDAO.supprimer(livreSelectionne.getId());
                    chargerLivres();
                    viderFormulaire();
                    afficherInfo("Livre supprimé avec succès !");
                } catch (SQLException e) {
                    afficherErreur("Erreur lors de la suppression : " + e.getMessage());
                }
            }
        });
    }

    private void rechercherLivre() {
        String motCle = searchField.getText().trim();
        String categorie = filterCategorie.getValue();
        try {
            List<Livre> livres;
            if (!motCle.isEmpty()) {
                livres = livreDAO.rechercher(motCle);
            } else if (categorie != null && !categorie.isEmpty()) {
                livres = livreDAO.filtrerParCategorie(categorie);
            } else {
                livres = livreDAO.getAll();
            }
            livreList.setAll(livres);
            totalLabel.setText("Total : " + livres.size() + " livre(s)");
        } catch (SQLException e) {
            afficherErreur("Erreur lors de la recherche : " + e.getMessage());
        }
    }

    private void exporterCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les livres en CSV");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        fileChooser.setInitialFileName("livres.csv");
        java.io.File file = fileChooser.showSaveDialog(livreTable.getScene().getWindow());
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("ID,Titre,Auteur,ISBN,Catégorie,Nb Exemplaires,Disponible\n");
                for (Livre l : livreList) {
                    writer.write(l.getId() + "," + l.getTitre() + "," +
                                 l.getAuteur() + "," + l.getIsbn() + "," +
                                 l.getCategorie() + "," + l.getNbExemplaires() + "," +
                                 l.isDisponible() + "\n");
                }
                afficherInfo("Export CSV réussi : " + file.getName());
            } catch (IOException e) {
                afficherErreur("Erreur lors de l'export : " + e.getMessage());
            }
        }
    }

    private Livre getFormulaireLivre() {
        Livre livre = new Livre();
        livre.setTitre(titreField.getText().trim());
        livre.setAuteur(auteurField.getText().trim());
        livre.setIsbn(isbnField.getText().trim());
        livre.setCategorie(categorieField.getValue());
        livre.setNbExemplaires(nbExemplairesSpinner.getValue());
        livre.setDisponible(disponibleCheck.isSelected());
        livre.setDescription(descriptionArea.getText().trim());
        return livre;
    }

    private void remplirFormulaire(Livre livre) {
        titreField.setText(livre.getTitre());
        auteurField.setText(livre.getAuteur());
        isbnField.setText(livre.getIsbn());
        categorieField.setValue(livre.getCategorie());
        nbExemplairesSpinner.getValueFactory().setValue(livre.getNbExemplaires());
        disponibleCheck.setSelected(livre.isDisponible());
        descriptionArea.setText(livre.getDescription());
    }

    private void viderFormulaire() {
        titreField.clear();
        auteurField.clear();
        isbnField.clear();
        categorieField.setValue(null);
        nbExemplairesSpinner.getValueFactory().setValue(1);
        disponibleCheck.setSelected(true);
        descriptionArea.clear();
        livreSelectionne = null;
        livreTable.getSelectionModel().clearSelection();
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}