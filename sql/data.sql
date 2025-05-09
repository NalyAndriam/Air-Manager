INSERT INTO Role (nom) VALUES ('admin'), ('user');

INSERT INTO Utilisateur (nom, email, mdp, id_role) VALUES
('Rakoto', 'rakoto@gmail.com', '123', 1),
('Rabe', 'rabe@gmail.com', '123', 2);

INSERT INTO TypeSiege (nom) VALUES 
('Economie'),
('Affaires'),
('Premiere Classe');


INSERT INTO Avion (modele, capacite, date_fabrication) VALUES 
('Boeing 737', 160, '2010-06-15'),
('Airbus A320', 180, '2012-03-22'),
('Boeing 747', 366, '2005-11-05'),
('Embraer E190', 100, '2018-09-17'),
('ATR 72', 70, '2014-01-30');

INSERT INTO Ville (nom) VALUES 
('New York'),         
('London'),           
('Tokyo'),            
('Dubai'),            
('Los Angeles'),      
('Paris'),            
('Singapore'),        
('Frankfurt'),        
('Istanbul'),         
('Hong Kong');        

