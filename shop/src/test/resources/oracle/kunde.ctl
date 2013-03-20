OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE kunde
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	k_id,
	k_nachname,
	k_vorname,
	k_seit,
	k_art ,
	k_familienstand,
	k_geschlecht,
	k_rabatt,
	k_umsatz,
	k_email,
	k_newsletter,
	k_passwort,
	k_erzeugt,
	k_aktualisiert,
	k_adresse_fk)
