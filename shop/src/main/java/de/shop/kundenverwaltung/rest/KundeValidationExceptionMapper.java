package de.shop.kundenverwaltung.rest;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.CONFLICT;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeValidationException;
import de.shop.util.Log;


@Provider
@ApplicationScoped
@Log
public class KundeValidationExceptionMapper implements ExceptionMapper<KundeValidationException> {
	private static final String NEWLINE = System.getProperty("line.separator");
	
	@Override
	public Response toResponse(KundeValidationException e) {
		final Collection<ConstraintViolation<Kunde>> violations = e.getViolations();
		final StringBuilder sb = new StringBuilder();
		for (ConstraintViolation<Kunde> v : violations) {
			sb.append(v.getMessage());
			sb.append(NEWLINE);
		}
		
		final String responseStr = sb.toString();
		final Response response = Response.status(CONFLICT)
		                                  .type(TEXT_PLAIN)
		                                  .entity(responseStr)
		                                  .build();
		
		return response;
	}
}
