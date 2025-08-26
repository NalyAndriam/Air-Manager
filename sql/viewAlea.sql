-------------------------------------------------------------------------------------------------
------------------------------RESERVATIONS REGROUPEES--------------------------------------------
-------------------------------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_resa_admin AS
SELECT 
    r.id_vol,
    ts.nom AS type_siege,
    CASE 
        WHEN pa.id IS NOT NULL AND r.date <= pa.date_fin 
        THEN pa.prix
        ELSE pv.prix
    END AS prix_unitaire,
    SUM(r.nombre) AS total_places,
    SUM(
        CASE 
            WHEN pa.id IS NOT NULL AND r.date <= pa.date_fin 
            THEN pa.prix * r.nombre
            ELSE pv.prix * r.nombre
        END
    ) AS montant_total,
    pa.date_fin AS date_butoire
FROM Reservation r
JOIN Vol v ON r.id_vol = v.id
JOIN TypeSiege ts ON r.id_typeSiege = ts.id
LEFT JOIN PrixVol pv 
       ON r.id_vol = pv.id_vol 
      AND r.id_typeSiege = pv.id_typeSiege
LEFT JOIN Promotion_Alea pa 
       ON r.id_vol = pa.id_vol 
      AND r.id_typeSiege = pa.id_typeSiege
GROUP BY r.id_vol, v.depart, v.arrivee, r.id_typeSiege, ts.nom, prix_unitaire;


------------------------------------------------------------------------------------------------
-----------------------------CONSIDERATION PAIEMENT---------------------------------------------
------------------------------------------------------------------------------------------------

CREATE OR REPLACE VIEW v_resa_promo_grouped AS
WITH resa_promo AS (
    SELECT
        r.id AS reservation_id,
        r.id_vol,
        r.id_utilisateur,
        r.id_typeSiege,
        r.nombre AS nb_places_reservees,
        r.date AS date_reservation,
        CASE
            WHEN p.id_reservation IS NULL THEN (
                SELECT pa2.id
                FROM Promotion_Alea pa2
                WHERE pa2.id_vol = r.id_vol
                  AND pa2.id_typeSiege = r.id_typeSiege
                  AND pa2.date_fin > (
                      SELECT MIN(pa3.date_fin)
                      FROM Promotion_Alea pa3
                      WHERE pa3.id_vol = r.id_vol
                        AND pa3.id_typeSiege = r.id_typeSiege
                        AND r.date <= pa3.date_fin
                  )
                ORDER BY pa2.date_fin ASC
                LIMIT 1
            )
            ELSE (
                SELECT pa.id
                FROM Promotion_Alea pa
                WHERE pa.id_vol = r.id_vol
                  AND pa.id_typeSiege = r.id_typeSiege
                  AND r.date <= pa.date_fin
                ORDER BY pa.date_fin ASC
                LIMIT 1
            )
        END AS promotion_id
    FROM Reservation r
    LEFT JOIN Paiement p ON r.id = p.id_reservation
)
SELECT
    rp.id_vol,
    ts.nom AS type_siege,
    pa.prix AS prix_unitaire,
    SUM(rp.nb_places_reservees) AS total_places,
    SUM(rp.nb_places_reservees * pa.prix) AS montant_total,
    pa.date_fin AS date_butoire
FROM resa_promo rp
JOIN Promotion_Alea pa ON rp.promotion_id = pa.id
JOIN TypeSiege ts ON rp.id_typeSiege = ts.id
WHERE rp.promotion_id IS NOT NULL
GROUP BY rp.id_vol, ts.nom, pa.prix, pa.date_fin
ORDER BY rp.id_vol, ts.nom;

