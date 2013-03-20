OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE artikel
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	a_id,
	a_bezeichnung,
	a_preis,
	a_erhaeltlich,
	a_erzeugt,
	a_aktualisiert,
	a_artikelgruppe_fk,
	a_idx)
