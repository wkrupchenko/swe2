package de.shop.artikelverwaltung.service;

import de.shop.util.AbstractShopException;

public abstract class AbstractArtikelgruppeServiceException extends AbstractShopException {
	private static final long serialVersionUID = 1L;

	public AbstractArtikelgruppeServiceException(String msg) {
		super(msg);
	}
	
	public AbstractArtikelgruppeServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
