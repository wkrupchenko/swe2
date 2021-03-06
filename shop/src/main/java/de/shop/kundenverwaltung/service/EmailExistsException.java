package de.shop.kundenverwaltung.service;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class EmailExistsException extends KundeServiceException {
	private static final long serialVersionUID = 1L;
	private final String email;
	
	public EmailExistsException(String email) {
		super("Die Email-Adresse " + email + " existiert bereits");
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
}
