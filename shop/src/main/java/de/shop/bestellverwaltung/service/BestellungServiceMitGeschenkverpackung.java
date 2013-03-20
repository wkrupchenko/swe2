package de.shop.bestellverwaltung.service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Lieferung;
import de.shop.kundenverwaltung.domain.Kunde;

@Decorator
public abstract class BestellungServiceMitGeschenkverpackung implements BestellungService {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	@Delegate
	@Any
	private BestellungService bs;

	@Override
	public Bestellung findeBestellungNachId(Long id) {
		return bs.findeBestellungNachId(id);
	}

	@Override
	public List<Bestellung> findeBestellungenNachKundeId(Long kundeId) {
		return bs.findeBestellungenNachKundeId(kundeId);
	}

	@Override
	public Bestellung createBestellung(Bestellung bestellung, Kunde kunde, Locale locale) {
		LOGGER.warning("Geschenkverpackung noch nicht implementiert");
		
		return bs.createBestellung(bestellung, kunde, locale);
	}

	
	@Override
	public List<Lieferung> findeLieferungen(String nr) {
		return bs.findeLieferungen(nr);
	}

	@Override
	public Lieferung createLieferung(Lieferung lieferung,
			List<Bestellung> bestellungen) {
		return bs.createLieferung(lieferung, bestellungen);
	}

}
