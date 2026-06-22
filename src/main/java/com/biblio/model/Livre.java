package com.biblio.model;

import javafx.beans.property.*;

public class Livre {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty titre = new SimpleStringProperty();
    private final StringProperty auteur = new SimpleStringProperty();
    private final StringProperty isbn = new SimpleStringProperty();
    private final StringProperty categorie = new SimpleStringProperty();
    private final IntegerProperty nbExemplaires = new SimpleIntegerProperty();
    private final BooleanProperty disponible = new SimpleBooleanProperty();
    private final StringProperty description = new SimpleStringProperty();

    public Livre() {}

    public Livre(int id, String titre, String auteur, String isbn, String categorie,
                 int nbExemplaires, boolean disponible, String description) {
        setId(id);
        setTitre(titre);
        setAuteur(auteur);
        setIsbn(isbn);
        setCategorie(categorie);
        setNbExemplaires(nbExemplaires);
        setDisponible(disponible);
        setDescription(description);
    }

    // id
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    // titre
    public String getTitre() { return titre.get(); }
    public void setTitre(String value) { titre.set(value); }
    public StringProperty titreProperty() { return titre; }

    // auteur
    public String getAuteur() { return auteur.get(); }
    public void setAuteur(String value) { auteur.set(value); }
    public StringProperty auteurProperty() { return auteur; }

    // isbn
    public String getIsbn() { return isbn.get(); }
    public void setIsbn(String value) { isbn.set(value); }
    public StringProperty isbnProperty() { return isbn; }

    // categorie
    public String getCategorie() { return categorie.get(); }
    public void setCategorie(String value) { categorie.set(value); }
    public StringProperty categorieProperty() { return categorie; }

    // nbExemplaires
    public int getNbExemplaires() { return nbExemplaires.get(); }
    public void setNbExemplaires(int value) { nbExemplaires.set(value); }
    public IntegerProperty nbExemplairesProperty() { return nbExemplaires; }

    // disponible
    public boolean isDisponible() { return disponible.get(); }
    public void setDisponible(boolean value) { disponible.set(value); }
    public BooleanProperty disponibleProperty() { return disponible; }

    // description
    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }
    public StringProperty descriptionProperty() { return description; }
}