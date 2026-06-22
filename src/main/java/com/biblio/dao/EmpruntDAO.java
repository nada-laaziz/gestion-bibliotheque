package com.biblio.dao;

import com.biblio.model.Emprunt;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpruntDAO {

    // Ajouter un emprunt + marquer le livre comme non disponible
    public void ajouter(Emprunt emprunt) throws SQLException {
        String sql = "INSERT INTO emprunt (livre_id, nom_emprunteur, date_emprunt, date_retour_prevue, statut) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, emprunt.getLivreId());
            ps.setString(2, emprunt.getNomEmprunteur());
            ps.setDate(3, Date.valueOf(emprunt.getDateEmprunt()));
            ps.setDate(4, Date.valueOf(emprunt.getDateRetourPrevue()));
            ps.setString(5, emprunt.getStatut());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    emprunt.setId(rs.getInt(1));
                }
            }
        }

        // Marquer le livre comme indisponible
        marquerDisponibilite(emprunt.getLivreId(), false, conn());
    }

    // Modifier un emprunt
    public void modifier(Emprunt emprunt) throws SQLException {
        String sql = "UPDATE emprunt SET livre_id=?, nom_emprunteur=?, date_emprunt=?, date_retour_prevue=?, statut=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, emprunt.getLivreId());
            ps.setString(2, emprunt.getNomEmprunteur());
            ps.setDate(3, Date.valueOf(emprunt.getDateEmprunt()));
            ps.setDate(4, Date.valueOf(emprunt.getDateRetourPrevue()));
            ps.setString(5, emprunt.getStatut());
            ps.setInt(6, emprunt.getId());

            ps.executeUpdate();
        }
    }

    // Marquer un emprunt comme "Rendu" et remettre le livre disponible
    public void marquerRendu(int empruntId, int livreId) throws SQLException {
        String sql = "UPDATE emprunt SET statut='Rendu' WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empruntId);
            ps.executeUpdate();
        }
        marquerDisponibilite(livreId, true, Database.getConnection());
    }

    // Supprimer un emprunt
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM emprunt WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Récupérer tous les emprunts (avec le titre du livre via jointure)
    public List<Emprunt> getAll() throws SQLException {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT e.*, l.titre AS titre_livre FROM emprunt e " +
                     "JOIN livre l ON e.livre_id = l.id ORDER BY e.id DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                emprunts.add(mapResultSet(rs));
            }
        }
        return emprunts;
    }

    // Recherche par nom d'emprunteur
    public List<Emprunt> rechercher(String motCle) throws SQLException {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT e.*, l.titre AS titre_livre FROM emprunt e " +
                     "JOIN livre l ON e.livre_id = l.id " +
                     "WHERE e.nom_emprunteur LIKE ? ORDER BY e.id DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + motCle + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    emprunts.add(mapResultSet(rs));
                }
            }
        }
        return emprunts;
    }

    // Filtrer par statut
    public List<Emprunt> filtrerParStatut(String statut) throws SQLException {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT e.*, l.titre AS titre_livre FROM emprunt e " +
                     "JOIN livre l ON e.livre_id = l.id " +
                     "WHERE e.statut = ? ORDER BY e.id DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    emprunts.add(mapResultSet(rs));
                }
            }
        }
        return emprunts;
    }

    // Méthode utilitaire : met à jour la disponibilité d'un livre
    private void marquerDisponibilite(int livreId, boolean disponible, Connection conn) throws SQLException {
        String sql = "UPDATE livre SET disponible=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, disponible);
            ps.setInt(2, livreId);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }

    private Connection conn() throws SQLException {
        return Database.getConnection();
    }

    // Transformer une ligne SQL en objet Emprunt
    private Emprunt mapResultSet(ResultSet rs) throws SQLException {
        return new Emprunt(
                rs.getInt("id"),
                rs.getInt("livre_id"),
                rs.getString("titre_livre"),
                rs.getString("nom_emprunteur"),
                rs.getDate("date_emprunt").toLocalDate(),
                rs.getDate("date_retour_prevue").toLocalDate(),
                rs.getString("statut")
        );
    }
}