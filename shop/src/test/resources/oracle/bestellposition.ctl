OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE bestellposition
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	bp_id,
	bp_bestellung_fk,
	bp_artikel_fk,
	bp_anzahl,
	bp_idx)
