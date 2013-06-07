package de.shop.kundenverwaltung.service;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class PwdNotEqualException extends KundeServiceException {
	private static final long serialVersionUID = 1L;
	
	public PwdNotEqualException() {
		super("Die Passwörter stimmen nicht überein.");
		return;
	}
}
