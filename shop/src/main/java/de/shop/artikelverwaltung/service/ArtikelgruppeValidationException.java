package de.shop.artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Artikelgruppe;


/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte eines Kunden nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class ArtikelgruppeValidationException extends ArtikelServiceException {
	private static final long serialVersionUID = 4255133082483647701L;
	private final Artikelgruppe artikelgruppe;
	private final Collection<ConstraintViolation<Artikelgruppe>> violations;
	
//	@Resource(lookup = "java:jboss/UserTransaction")
//	private UserTransaction trans;

	public ArtikelgruppeValidationException(Artikelgruppe artikelgruppe,
			                        Collection<ConstraintViolation<Artikelgruppe>> violations) {
		super("Ungueltiger Artikelgruppe: " + artikelgruppe + ", Violations: " + violations);
		this.artikelgruppe = artikelgruppe;
		this.violations = violations;
	}
	
	public Artikelgruppe getArtikelgruppe() {
		return artikelgruppe;
	}

	public Collection<ConstraintViolation<Artikelgruppe>> getViolations() {
		return violations;
	}
}
