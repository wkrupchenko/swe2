package de.shop.kundenverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.kundenverwaltung.domain.Kunde;


/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte eines Kunden nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class KundeValidationException extends KundeServiceException {
	private static final long serialVersionUID = 725343495627374601L;
	private final Collection<ConstraintViolation<Kunde>> violations;
	
	public KundeValidationException(Collection<ConstraintViolation<Kunde>> violations) {
		super("Violations: " + violations);
		this.violations = violations;
	}
	
	public Collection<ConstraintViolation<Kunde>> getViolations() {
		return violations;
	}
}
