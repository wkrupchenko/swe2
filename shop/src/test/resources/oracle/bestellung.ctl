OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE bestellung
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"'  (
	b_id,
	b_kunde_fk,
	b_idx,
	b_erzeugt,
	b_aktualisiert,
	b_offenAbgeschlossen)
