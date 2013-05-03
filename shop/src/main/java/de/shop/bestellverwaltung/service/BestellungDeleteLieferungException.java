package de.shop.bestellverwaltung.service;

import javax.ejb.ApplicationException;

import de.shop.bestellverwaltung.domain.Bestellung;


/**
 * Exception, die ausgel&ouml;st wird, wenn ein Kunde gel&ouml;scht werden soll, aber mindestens eine Bestellung hat
 */
@ApplicationException(rollback = true)
public class BestellungDeleteLieferungException extends BestellungServiceException {
	private static final long serialVersionUID = 1L;
	private final Long bestellungId;
	private final int anzahlLieferungen;
	
	public BestellungDeleteLieferungException(Bestellung bestellung) {
		super("Bestellung mit ID=" + bestellung.getId() + " kann nicht geloescht werden: "
			  + bestellung.getLieferungen().size() + " Lieferung(en)");
		this.bestellungId = bestellung.getId();
		this.anzahlLieferungen = bestellung.getLieferungen().size();
	}

	public Long getBestellungId() {
		return bestellungId;
	}
	public int getAnzahlLieferungen() {
		return anzahlLieferungen;
	}
	
}
