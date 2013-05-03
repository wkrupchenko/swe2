package de.shop.bestellverwaltung.service;

import java.util.Collection;
import java.util.Date;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.bestellverwaltung.domain.Lieferung;


/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte einer Lieferung nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class LieferungValidationException extends LieferungServiceException {
	private static final long serialVersionUID = 1L;
	private final Date erzeugt;
	private final Long lieferungId;
	private final Collection<ConstraintViolation<Lieferung>> violations;
	
	public LieferungValidationException(Lieferung lieferung,
			                             Collection<ConstraintViolation<Lieferung>> violations) {
		super(violations.toString());
		
		if (lieferung == null) {
			this.erzeugt = null;
			this.lieferungId = null;
		}
		else {
			this.erzeugt = lieferung.getErzeugt();
			this.lieferungId = lieferung.getId();
		}
		
		this.violations = violations;
	}
	
	public Date getErzeugt() {
		return erzeugt == null ? null : (Date) erzeugt.clone();
	}
	
	public Long getLieferungId() {
		return lieferungId;
	}
	
	public Collection<ConstraintViolation<Lieferung>> getViolations() {
		return violations;
	}
}
