INSERT INTO Role (nom) VALUES ('admin'), ('user');

INSERT INTO Utilisateur (nom, email, mdp, id_role) VALUES
('Rakoto', 'rakoto@gmail.com', '123', 1),
('Rabe', 'rabe@gmail.com', '123', 2);

INSERT INTO TypeSiege (nom) VALUES 
('Economie'),
('Affaire');

INSERT INTO Avion (modele, capacite, date_fabrication) VALUES 
('Air Madagascar', 160, '2010-06-15'),
('Air France', 180, '2012-03-22');

INSERT INTO Ville (nom) VALUES 
('Antananarivo'),         
('Paris CDG'),           
('Mauritius'),            
('Addis Abeba');  

----------------------------------------------------------------------------------------
----------------------PROMOTIONS--------------------------------------------------------
----------------------------------------------------------------------------------------

-- Promotions pour VOL1
INSERT INTO Promotion_Alea (nombre, date_fin, prix, id_typeSiege, id_vol)
VALUES (4, '2025-08-27', 200, 1, 1);

INSERT INTO Promotion_Alea (nombre, date_fin, prix, id_typeSiege, id_vol)
VALUES (2, '2025-09-03', 300, 1, 1);

-- Promotions pour VOL2
INSERT INTO Promotion_Alea (nombre, date_fin, prix, id_typeSiege, id_vol)
VALUES (3, '2025-09-05', 350, 1, 2);

INSERT INTO Promotion_Alea (nombre, date_fin, prix, id_typeSiege, id_vol)
VALUES (1, '2025-09-13', 400, 1, 2);



---------------------------------------------------------------------------------
------------------------------RESERVATIONS---------------------------------------
---------------------------------------------------------------------------------

-- Réservations vol1
INSERT INTO Reservation (id_vol, id_utilisateur, id_typeSiege, nombre, date, passeport)
VALUES (1, 2, 1, 1, '2025-08-20', NULL);

INSERT INTO Reservation (id_vol, id_utilisateur, id_typeSiege, nombre, date, passeport)
VALUES (1, 2, 1, 1, '2025-08-21', NULL);

INSERT INTO Reservation (id_vol, id_utilisateur, id_typeSiege, nombre, date, passeport)
VALUES (1, 2, 1, 1, '2025-08-21', NULL);

INSERT INTO Reservation (id_vol, id_utilisateur, id_typeSiege, nombre, date, passeport)
VALUES (1, 2, 1, 1, '2025-08-28', NULL);

INSERT INTO Reservation (id_vol, id_utilisateur, id_typeSiege, nombre, date, passeport)
VALUES (1, 2, 1, 1, '2025-08-29', NULL);

INSERT INTO Reservation (id_vol, id_utilisateur, id_typeSiege, nombre, date, passeport)
VALUES (1, 2, 1, 1, '2025-09-01', NULL);

INSERT INTO Reservation (id_vol, id_utilisateur, id_typeSiege, nombre, date, passeport)
VALUES (1, 2, 1, 1, '2025-09-02', NULL);

-- Réservations vol2
INSERT INTO Reservation (id_vol, id_utilisateur, id_typeSiege, nombre, date, passeport)
VALUES (2, 2, 1, 1, '2025-09-01', NULL);

INSERT INTO Reservation (id_vol, id_utilisateur, id_typeSiege, nombre, date, passeport)
VALUES (2, 2, 1, 1, '2025-09-02', NULL);

INSERT INTO Reservation (id_vol, id_utilisateur, id_typeSiege, nombre, date, passeport)
VALUES (2, 2, 1, 1, '2025-09-08', NULL);

INSERT INTO Reservation (id_vol, id_utilisateur, id_typeSiege, nombre, date, passeport)
VALUES (2, 2, 1, 1, '2025-09-10', NULL);

