package de.shop.kundenverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.kundenverwaltung.domain.Kunde;

@ApplicationException(rollback = true)
public class InvalidKundeIdException extends AbstractKundeServiceException {
	private static final long serialVersionUID = 1L;
	
	private final Long kundeId;
	private final Collection<ConstraintViolation<Kunde>> violations;
	
	public InvalidKundeIdException(Long kundeId, Collection<ConstraintViolation<Kunde>> violations) {
		super("Ungueltige Kunde-ID: " + kundeId + ", Violations: " + violations);
		this.kundeId = kundeId;
		this.violations = violations;
	}

	public Long getKundeId() {
		return kundeId;
	}

	public Collection<ConstraintViolation<Kunde>> getViolations() {
		return violations;
	}
}