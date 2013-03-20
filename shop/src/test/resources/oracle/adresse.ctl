OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE adresse
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	a_id,
	a_plz,
	a_ort,
	a_strasse,
	a_hausnr,
	a_erzeugt,
	a_aktualisiert)
