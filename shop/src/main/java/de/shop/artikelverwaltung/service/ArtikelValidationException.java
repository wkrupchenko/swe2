package de.shop.artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Artikel;


/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte eines Kunden nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class ArtikelValidationException extends ArtikelServiceException {
	private static final long serialVersionUID = 4255133082483647701L;
	private final Collection<ConstraintViolation<Artikel>> violations;
	
//	@Resource(lookup = "java:jboss/UserTransaction")
//	private UserTransaction trans;
	
	public ArtikelValidationException(Collection<ConstraintViolation<Artikel>> violations) {
		super("Violations: " + violations);
		this.violations = violations;
	}

	public Collection<ConstraintViolation<Artikel>> getViolations() {
		return violations;
	}
}
