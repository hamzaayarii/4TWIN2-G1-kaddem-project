-- Création de la table Universite
CREATE TABLE universite (
    id_univ INT PRIMARY KEY AUTO_INCREMENT,
    nom_univ VARCHAR(255) NOT NULL
);

-- Insertion des données de test
INSERT INTO universite (id_univ, nom_univ) VALUES (1, 'Université de Tunis');
INSERT INTO universite (id_univ, nom_univ) VALUES (2, 'Université de Sfax');
INSERT INTO universite (id_univ, nom_univ) VALUES (3, 'Université de Monastir');