package de.shop.artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.domain.Artikelgruppe;


/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte eines Kunden nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class ArtikelgruppeValidationException extends ArtikelServiceException {
	private static final long serialVersionUID = 4255133082483647701L;
	private final Collection<ConstraintViolation<Artikelgruppe>> violations;
	
//	@Resource(lookup = "java:jboss/UserTransaction")
//	private UserTransaction trans;

	public ArtikelgruppeValidationException(Collection<ConstraintViolation<Artikelgruppe>> violations) {
		super("Violations: " + violations);
		this.violations = violations;
	}

	public Collection<ConstraintViolation<Artikelgruppe>> getViolations() {
		return violations;
	}
}
