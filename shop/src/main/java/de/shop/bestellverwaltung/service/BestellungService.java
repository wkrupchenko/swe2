package de.shop.bestellverwaltung.service;

import java.util.List;
import java.util.Locale;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Lieferung;
import de.shop.kundenverwaltung.domain.Kunde;

public interface BestellungService {

	Bestellung findeBestellungNachId(Long id);
	
	List<Bestellung> findeBestellungenGeschlossen();

	List<Bestellung> findeBestellungenNachKundeId(Long kundeId);
	
	List<Bestellung> findeBestellungenOffen();

	Bestellung createBestellung(Bestellung bestellung, Kunde kunde, Locale locale);
	
	Bestellung updateBestellung(Bestellung bestellung, Locale locale);
	
	void deleteBestellung(Bestellung bestellung);

	List<Lieferung> findeLieferungen(String nr);
	
	Lieferung findeLieferungNachId(Long id);
	
	List<Bestellung> findeAlleBestellungen();

	Lieferung createLieferung(Lieferung lieferung, List<Bestellung> bestellungen);
	
	Lieferung updateLieferung(Lieferung lieferung, Locale locale);
	
	void deleteLieferung(Lieferung lieferung);
	
	List<Bestellung> findeBestellungenNachLieferungLiefernr(String liefernr);
	
	List<Bestellung> findeBestellungenNachLieferungId(Long id);
	
	List<Lieferung> findeLieferungenNachBestellungId(Long id);
}
