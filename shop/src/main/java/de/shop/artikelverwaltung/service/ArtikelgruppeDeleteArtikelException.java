package de.shop.artikelverwaltung.service;

import javax.ejb.ApplicationException;

import de.shop.artikelverwaltung.domain.Artikelgruppe;

/**
 * Exception, die ausgel&ouml;st wird, wenn ein Artikel gel&ouml;scht werden soll, aber mindestens einen Artikel hat
 */
@ApplicationException(rollback = true)
public class ArtikelgruppeDeleteArtikelException extends ArtikelServiceException {
	private static final long serialVersionUID = 1L;

	public ArtikelgruppeDeleteArtikelException(Artikelgruppe artikelgruppe) {
		super("Artikelgruppe mit ID=" + artikelgruppe.getId()
			  + " kann nicht geloescht werden, es sind Artikel vorhanden!");
	}
}
