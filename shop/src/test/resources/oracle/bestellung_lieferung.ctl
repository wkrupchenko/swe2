OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE bestellung_lieferung
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	bl_bestellung_fk,
	bl_lieferung_fk)
