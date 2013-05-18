package de.shop.artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Artikel;

@ApplicationException(rollback = true)
public class InvalidArtikelException extends ArtikelValidationException {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String bezeichnung;

	public InvalidArtikelException(Artikel artikel,
			                     Collection<ConstraintViolation<Artikel>> violations) {
		super(violations);
		if (artikel != null) {
			this.id = artikel.getId();
			this.bezeichnung = artikel.getBezeichnung();
		}
	}
	
	
	public InvalidArtikelException(Long id, Collection<ConstraintViolation<Artikel>> violations) {
		super(violations);
		this.id = id;
	}
	
	public InvalidArtikelException(String bezeichnung, Collection<ConstraintViolation<Artikel>> violations) {
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
