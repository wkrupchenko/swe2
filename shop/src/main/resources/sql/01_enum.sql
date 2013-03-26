-- ===============================================================================
-- Tabellen fuer Enum-Werte *einmalig* anlegen und jeweils Werte einfuegen
-- Beim ALLERERSTEN Aufruf die Zeilen mit "DROP TABLE ..." durch -- auskommentieren
-- ===============================================================================
-- DROP TABLE geschlecht;
CREATE TABLE geschlecht(id NUMBER(1) NOT NULL PRIMARY KEY, txt VARCHAR2(10) NOT NULL UNIQUE) CACHE;
INSERT INTO geschlecht VALUES (0, 'MAENNLICH');
INSERT INTO geschlecht VALUES (1, 'WEIBLICH');

-- DROP TABLE familienstand;
CREATE TABLE familienstand(id NUMBER(1) NOT NULL PRIMARY KEY, txt VARCHAR2(12) NOT NULL UNIQUE) CACHE;
INSERT INTO familienstand VALUES(0, 'LEDIG');
INSERT INTO familienstand VALUES(1, 'VERHEIRATET');
INSERT INTO familienstand VALUES(2, 'GESCHIEDEN');
INSERT INTO familienstand VALUES(3, 'VERWITWET');

-- DROP TABLE transport_art;
CREATE TABLE transport_art(id NUMBER(1) NOT NULL PRIMARY KEY, txt VARCHAR2(8) NOT NULL UNIQUE) CACHE;
INSERT INTO transport_art VALUES (0, 'STRASSE');
INSERT INTO transport_art VALUES (1, 'SCHIENE');
INSERT INTO transport_art VALUES (2, 'LUFT');
INSERT INTO transport_art VALUES (3, 'WASSER');



-- ===============================================================================
-- Fremdschluessel in den bereits *generierten* Tabellen auf die obigen "Enum-Tabellen" anlegen
-- ===============================================================================
ALTER TABLE kunde ADD CONSTRAINT kunde__geschlecht_fk FOREIGN KEY (geschlecht_fk) REFERENCES geschlecht;
ALTER TABLE kunde ADD CONSTRAINT kunde__familienstand_fk FOREIGN KEY (familienstand_fk) REFERENCES familienstand;
ALTER TABLE lieferung ADD CONSTRAINT lieferung__transport_art_fk FOREIGN KEY (transport_art_fk) REFERENCES transport_art;
