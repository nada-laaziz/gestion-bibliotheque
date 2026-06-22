package com.biblio.dao;

import com.biblio.model.Livre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivreDAO {

    // Ajouter un livre
    public void ajouter(Livre livre) throws SQLException {
        String sql = "INSERT INTO livre (titre, auteur, isbn, categorie, nb_exemplaires, disponible, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, livre.getTitre());
            ps.setString(2, livre.getAuteur());
            ps.setString(3, livre.getIsbn());
            ps.setString(4, livre.getCategorie());
            ps.setInt(5, livre.getNbExemplaires());
            ps.setBoolean(6, livre.isDisponible());
            ps.setString(7, livre.getDescription());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    livre.setId(rs.getInt(1));
                }
            }
        }
    }

    // Modifier un livre
    public void modifier(Livre livre) throws SQLException {
        String sql = "UPDATE livre SET titre=?, auteur=?, isbn=?, categorie=?, nb_exemplaires=?, disponible=?, description=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, livre.getTitre());
            ps.setString(2, livre.getAuteur());
            ps.setString(3, livre.getIsbn());
            ps.setString(4, livre.getCategorie());
            ps.setInt(5, livre.getNbExemplaires());
            ps.setBoolean(6, livre.isDisponible());
            ps.setString(7, livre.getDescription());
            ps.setInt(8, livre.getId());

            ps.executeUpdate();
        }
    }

    // Supprimer un livre
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM livre WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Récupérer tous les livres
    public List<Livre> getAll() throws SQLException {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livre ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                livres.add(mapResultSet(rs));
            }
        }
        return livres;
    }

    // Rechercher par titre ou auteur
    public List<Livre> rechercher(String motCle) throws SQLException {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livre WHERE titre LIKE ? OR auteur LIKE ? ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String motRecherche = "%" + motCle + "%";
            ps.setString(1, motRecherche);
            ps.setString(2, motRecherche);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    livres.add(mapResultSet(rs));
                }
            }
        }
        return livres;
    }

    // Filtrer par catégorie
    public List<Livre> filtrerParCategorie(String categorie) throws SQLException {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livre WHERE categorie = ? ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categorie);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    livres.add(mapResultSet(rs));
                }
            }
        }
        return livres;
    }

    // Méthode utilitaire pour transformer une ligne SQL en objet Livre
    private Livre mapResultSet(ResultSet rs) throws SQLException {
        return new Livre(
                rs.getInt("id"),
                rs.getString("titre"),
                rs.getString("auteur"),
                rs.getString("isbn"),
                rs.getString("categorie"),
                rs.getInt("nb_exemplaires"),
                rs.getBoolean("disponible"),
                rs.getString("description")
        );
    }
}
