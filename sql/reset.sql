CREATE OR REPLACE FUNCTION vider_vol_et_dependances()
RETURNS void AS $$
BEGIN
    -- Supprimer d'abord les dépendances (ordre important)
    DELETE FROM Reservation;
    DELETE FROM VolSiege;
    DELETE FROM PrixVol;

    -- Puis les vols
    DELETE FROM Vol;

    -- Réinitialiser les séquences (id)
    PERFORM setval('reservation_id_seq', 1, false);
    PERFORM setval('volsiege_id_seq', 1, false);
    PERFORM setval('prixvol_id_seq', 1, false);
    PERFORM setval('vol_id_seq', 1, false);
END;
$$ LANGUAGE plpgsql;

SELECT vider_vol_et_dependances();
