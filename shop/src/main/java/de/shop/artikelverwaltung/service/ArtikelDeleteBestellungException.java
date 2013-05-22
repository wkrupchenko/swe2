package de.shop.artikelverwaltung.service;

import javax.ejb.ApplicationException;

import de.shop.artikelverwaltung.domain.Artikel;

/**
 * Exception, die ausgel&ouml;st wird, wenn ein Kunde gel&ouml;scht werden soll, aber mindestens eine Bestellung hat
 */
@ApplicationException(rollback = true)
public class ArtikelDeleteBestellungException extends ArtikelServiceException {
	private static final long serialVersionUID = 1L;
	private final Long artikelId;

	public ArtikelDeleteBestellungException(Artikel artikel) {
		super("Artikel mit ID=" + artikel.getId() + " kann nicht geloescht werden, es sind Bestellungen vorhanden!");
		this.artikelId = artikel.getId();
	}

	public Long getArtikelId() {
		return artikelId;
	}
}
