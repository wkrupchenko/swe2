package de.shop.artikelverwaltung.service;

import de.shop.util.AbstractShopException;

public abstract class AbstractArtikelServiceException extends AbstractShopException {
	private static final long serialVersionUID = 1L;

	public AbstractArtikelServiceException(String msg) {
		super(msg);
	}
	
	public AbstractArtikelServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
