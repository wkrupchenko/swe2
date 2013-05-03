package de.shop.bestellverwaltung.service;

import de.shop.util.AbstractShopException;

public class BestellungServiceException extends AbstractShopException {
	private static final long serialVersionUID = -626920099480136224L;

	public BestellungServiceException(String msg) {
		super(msg);
	}
}
