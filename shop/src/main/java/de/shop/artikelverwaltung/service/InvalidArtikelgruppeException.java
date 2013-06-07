package de.shop.artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Artikelgruppe;

@ApplicationException(rollback = true)
public class InvalidArtikelgruppeException extends ArtikelgruppeValidationException {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String bezeichnung;

	public InvalidArtikelgruppeException(Artikelgruppe artikelgruppe,
			                     Collection<ConstraintViolation<Artikelgruppe>> violations) {
		super(violations);
		if (artikelgruppe != null) {
			this.id = artikelgruppe.getId();
			this.bezeichnung = artikelgruppe.getBezeichnung();
		}
	}
	
	
	public InvalidArtikelgruppeException(Long id, Collection<ConstraintViolation<Artikelgruppe>> violations) {
		super(violations);
		this.id = id;
	}
	
	public InvalidArtikelgruppeException(String bezeichnung, 
			Collection<ConstraintViolation<Artikelgruppe>> violations) {
		super(violations);
		this.bezeichnung = bezeichnung;
	}
	
	public Long getId() {
		return id;
	}
	public String getBezeichnung() {
		return bezeichnung;
	}
	
	@Override
	public String toString() {
		return "{id=" + id + ", bezeichnung=" + bezeichnung + "}";
	}
}
