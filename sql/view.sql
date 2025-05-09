-- Vue qui retourne le prix minimal et maximal pour chaque vol 
CREATE OR REPLACE VIEW v_volPrix AS
SELECT v.id, v.id_avion, v.id_ville_depart, v.id_ville_arrivee, v.depart, v.arrivee, MIN(pv.prix) AS prix_min, MAX(pv.prix) AS prix_max
FROM Vol v
LEFT JOIN PrixVol pv ON v.id = pv.id_vol
GROUP BY v.id, v.id_avion, v.id_ville_depart, v.id_ville_arrivee, v.depart, v.arrivee;