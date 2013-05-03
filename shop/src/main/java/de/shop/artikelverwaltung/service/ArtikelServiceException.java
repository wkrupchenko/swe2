package de.shop.artikelverwaltung.service;

import de.shop.util.AbstractShopException;

public class ArtikelServiceException extends AbstractShopException {
	private static final long serialVersionUID = 5999208465631860486L;

	public ArtikelServiceException(String msg) {
		super(msg);
	}

	public ArtikelServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
