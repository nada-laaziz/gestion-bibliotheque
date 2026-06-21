CREATE DATABASE IF NOT EXISTS bibliotheque_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE bibliotheque_db;

CREATE TABLE livre (
  id INT AUTO_INCREMENT PRIMARY KEY,
  titre VARCHAR(150) NOT NULL,
  auteur VARCHAR(100),
  isbn VARCHAR(30),
  categorie VARCHAR(50),
  nb_exemplaires INT DEFAULT 1,
  disponible BOOLEAN DEFAULT TRUE,
  description TEXT
);

CREATE TABLE emprunt (
  id INT AUTO_INCREMENT PRIMARY KEY,
  livre_id INT,
  nom_emprunteur VARCHAR(100),
  date_emprunt DATE,
  date_retour_prevue DATE,
  statut VARCHAR(20) DEFAULT 'En cours',
  FOREIGN KEY (livre_id) REFERENCES livre(id)
);