package de.shop.bestellverwaltung.service;

import de.shop.util.AbstractShopException;

public class LieferungServiceException extends AbstractShopException {
	private static final long serialVersionUID = 5999208465631860486L;

	public LieferungServiceException(String msg) {
		super(msg);
	}

	public LieferungServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
