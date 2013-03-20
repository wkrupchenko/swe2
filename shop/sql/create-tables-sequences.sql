CREATE SEQUENCE hibernate_sequence START WITH ${sequence.start};

CREATE TABLE adresse(
	a_id INTEGER NOT NULL PRIMARY KEY,
	a_plz CHAR(5) NOT NULL,
	a_ort VARCHAR2(32) NOT NULL,
	a_strasse VARCHAR2(32),
	a_hausnr VARCHAR2(4),
	a_erzeugt TIMESTAMP NOT NULL,
	a_aktualisiert TIMESTAMP NOT NULL
) CACHE;

CREATE TABLE kunde(
	k_id INTEGER NOT NULL PRIMARY KEY,
	k_nachname VARCHAR2(32) NOT NULL,
	k_vorname VARCHAR2(32)NOT NULL,
	k_seit DATE,
	k_art CHAR(1) DEFAULT 'P',
	k_familienstand INTEGER,
	k_geschlecht INTEGER,
	k_rabatt REAL,
	k_umsatz DOUBLE PRECISION,
	k_email VARCHAR2(50) NOT NULL UNIQUE,
	k_newsletter NUMBER(1),
	k_passwort VARCHAR2(50) NOT NULL,
	k_erzeugt TIMESTAMP NOT NULL,
	k_aktualisiert TIMESTAMP NOT NULL,
	k_adresse_fk INTEGER NOT NULL REFERENCES adresse(a_id)
) CACHE;

CREATE TABLE bestellung(
	b_id INTEGER NOT NULL PRIMARY KEY,
	b_kunde_fk INTEGER REFERENCES kunde(k_id),
	b_idx SMALLINT NOT NULL,
	b_erzeugt TIMESTAMP NOT NULL,
	b_aktualisiert TIMESTAMP NOT NULL,
	b_offenAbgeschlossen NUMBER(1) NOT NULL
) CACHE;

CREATE INDEX bestellung_kunde_index ON bestellung(b_kunde_fk);

CREATE TABLE artikelgruppe(
        ag_id INTEGER NOT NULL PRIMARY KEY,
		ag_name VARCHAR2(50) NOT NULL
) CACHE;

CREATE TABLE artikel(
	a_id INTEGER NOT NULL PRIMARY KEY,
	a_bezeichnung VARCHAR2(32) NOT NULL,
	a_preis DOUBLE PRECISION NOT NULL,
	a_erhaeltlich NUMBER(1),
	a_erzeugt TIMESTAMP NOT NULL,
	a_aktualisiert TIMESTAMP NOT NULL,
	a_artikelgruppe_fk INTEGER NOT NULL REFERENCES artikelgruppe (ag_id),
	a_idx SMALLINT NOT NULL
) CACHE;

CREATE TABLE bestellposition(
	bp_id INTEGER NOT NULL PRIMARY KEY,
	bp_bestellung_fk INTEGER NOT NULL REFERENCES bestellung(b_id),
	bp_artikel_fk INTEGER NOT NULL REFERENCES artikel(a_id),
	bp_anzahl SMALLINT NOT NULL,
	bp_idx SMALLINT NOT NULL
) CACHE;

CREATE INDEX bestpos_bestellung_index ON bestellposition(bp_bestellung_fk);
CREATE INDEX bestpos_artikel_index ON bestellposition(bp_artikel_fk);

CREATE TABLE lieferung(
	l_id INTEGER NOT NULL PRIMARY KEY,
	l_liefernr VARCHAR2(12) NOT NULL UNIQUE,
	l_transport_art VARCHAR2(7),
	l_inlandOderAusland  CHAR(1) DEFAULT 'I',
	l_erzeugt TIMESTAMP NOT NULL,
	l_aktualisiert TIMESTAMP NOT NULL
);

CREATE TABLE bestellung_lieferung(
	bl_bestellung_fk INTEGER NOT NULL REFERENCES bestellung(b_id),
	bl_lieferung_fk INTEGER NOT NULL REFERENCES lieferung(l_id),
	PRIMARY KEY(bl_bestellung_fk, bl_lieferung_fk)
);