CREATE DATABASE avion;
\c avion

CREATE TABLE Avion(
    id SERIAL PRIMARY KEY,
    modele VARCHAR(30),
    capacite DECIMAL,
    date_fabrication DATE
);

CREATE TABLE Ville(
    id SERIAL PRIMARY KEY,
    nom VARCHAR(70)
);

CREATE TABLE Vol(
    id SERIAL PRIMARY KEY,
    id_avion INT REFERENCES Avion(id) NOT NULL,
    id_ville_depart INT REFERENCES Ville(id) NOT NULL,
    id_ville_arrivee INT REFERENCES Ville(id) NOT NULL,
    depart TIMESTAMP NOT NULL,
    arrivee TIMESTAMP NOT NULL
);

CREATE TABLE TypeSiege(
    id SERIAL PRIMARY KEY,
    nom VARCHAR(30)
);

CREATE TABLE VolSiege(
    id SERIAL PRIMARY KEY,
    id_vol INT REFERENCES Vol(id) NOT NULL,
    id_typeSiege INT REFERENCES TypeSiege(id) NOT NULL,
    nombre DECIMAL NOT NULL
);

CREATE TABLE PrixVol(
    id SERIAL PRIMARY KEY,
    id_vol INT REFERENCES Vol(id) NOT NULL,
    id_typeSiege INT REFERENCES TypeSiege(id) NOT NULL,
    prix DECIMAL NOT NULL
);

CREATE TABLE Role(
    id SERIAL PRIMARY KEY,
    nom VARCHAR(30)
);

CREATE TABLE Utilisateur(
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    mdp VARCHAR(256) NOT NULL,
    id_role INT REFERENCES Role(id)
);

CREATE TABLE Reservation(
    id SERIAL PRIMARY KEY, 
    id_vol INT REFERENCES Vol(id) NOT NULL,
    id_utilisateur INT REFERENCES Utilisateur(id) NOT NULL,
    id_typeSiege INT REFERENCES TypeSiege(id) NOT NULL,
    nombre INT NOT NULL,
    date TIMESTAMP NOT NULL
);

CREATE TABLE Promotion(
    id SERIAL PRIMARY KEY,
    id_vol INT REFERENCES Vol(id),
    id_typeSiege INT REFERENCES TypeSiege(id),
    nombre DECIMAL NOT NULL,
    pourcentage DECIMAL NOT NULL
);

CREATE TABLE ReservationConfig(
    id SERIAL PRIMARY KEY,
    heure_reservation DECIMAL,
    heure_annulation DECIMAL
);

