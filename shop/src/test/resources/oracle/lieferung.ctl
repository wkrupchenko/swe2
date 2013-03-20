OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE lieferung
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	l_id,
	l_liefernr,
	l_transport_art,
	l_inlandOderAusland,
	l_erzeugt,
	l_aktualisiert)
