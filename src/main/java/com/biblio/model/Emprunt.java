package com.biblio.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Emprunt {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty livreId = new SimpleIntegerProperty();
    private final StringProperty titreLivre = new SimpleStringProperty(); // pour affichage TableView
    private final StringProperty nomEmprunteur = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dateEmprunt = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> dateRetourPrevue = new SimpleObjectProperty<>();
    private final StringProperty statut = new SimpleStringProperty();

    public Emprunt() {}

    public Emprunt(int id, int livreId, String titreLivre, String nomEmprunteur,
                    LocalDate dateEmprunt, LocalDate dateRetourPrevue, String statut) {
        setId(id);
        setLivreId(livreId);
        setTitreLivre(titreLivre);
        setNomEmprunteur(nomEmprunteur);
        setDateEmprunt(dateEmprunt);
        setDateRetourPrevue(dateRetourPrevue);
        setStatut(statut);
    }

    // id
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    // livreId
    public int getLivreId() { return livreId.get(); }
    public void setLivreId(int value) { livreId.set(value); }
    public IntegerProperty livreIdProperty() { return livreId; }

    // titreLivre
    public String getTitreLivre() { return titreLivre.get(); }
    public void setTitreLivre(String value) { titreLivre.set(value); }
    public StringProperty titreLivreProperty() { return titreLivre; }

    // nomEmprunteur
    public String getNomEmprunteur() { return nomEmprunteur.get(); }
    public void setNomEmprunteur(String value) { nomEmprunteur.set(value); }
    public StringProperty nomEmprunteurProperty() { return nomEmprunteur; }

    // dateEmprunt
    public LocalDate getDateEmprunt() { return dateEmprunt.get(); }
    public void setDateEmprunt(LocalDate value) { dateEmprunt.set(value); }
    public ObjectProperty<LocalDate> dateEmpruntProperty() { return dateEmprunt; }

    // dateRetourPrevue
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue.get(); }
    public void setDateRetourPrevue(LocalDate value) { dateRetourPrevue.set(value); }
    public ObjectProperty<LocalDate> dateRetourPrevueProperty() { return dateRetourPrevue; }

    // statut
    public String getStatut() { return statut.get(); }
    public void setStatut(String value) { statut.set(value); }
    public StringProperty statutProperty() { return statut; }
}