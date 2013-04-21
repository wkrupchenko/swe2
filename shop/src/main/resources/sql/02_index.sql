CREATE INDEX adresse_kunde_index ON adresse(kunde_fk);
CREATE INDEX bestellung_kunde_index ON bestellung(kunde_fk);
CREATE INDEX bestpos_bestellung_index ON bestellposition(bestellung_fk);
CREATE INDEX bestpos_artikel_index ON bestellposition(artikel_fk);