package de.shop.bestellverwaltung.service;

import javax.ejb.ApplicationException;

import de.shop.bestellverwaltung.domain.Lieferung;


/**
 * Exception, die ausgel&ouml;st wird, wenn eine Lieferung gel&ouml;scht werden soll, 
 * aber mindestens eine Bestellung hat
 */
@ApplicationException(rollback = true)
public class LieferungDeleteBestellungException extends LieferungServiceException {
	private static final long serialVersionUID = 1L;
	private final Long lieferungId;
	private final int anzahlBestellungen;
	
	public LieferungDeleteBestellungException(Lieferung lieferung) {
		super("Lieferung mit ID=" + lieferung.getId() + " kann nicht geloescht werden: "
			  + lieferung.getBestellungen().size() + " Bestellung(en)");
		this.lieferungId = lieferung.getId();
		this.anzahlBestellungen = lieferung.getBestellungen().size();
	}

	public Long getLieferungId() {
		return lieferungId;
	}
	public int getAnzahlBestellungen() {
		return anzahlBestellungen;
	}
}