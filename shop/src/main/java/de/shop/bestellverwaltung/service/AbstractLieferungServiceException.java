package de.shop.bestellverwaltung.service;

import de.shop.util.AbstractShopException;

public abstract class AbstractLieferungServiceException extends AbstractShopException {
	private static final long serialVersionUID = 1L;

	public AbstractLieferungServiceException(String msg) {
		super(msg);
	}
}
