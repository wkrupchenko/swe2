package de.shop.kundenverwaltung.service;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.CONFLICT;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.shop.util.Log;


@Provider
@ApplicationScoped
@Log
public class KundeDeleteBestellungExceptionMapper implements ExceptionMapper<KundeDeleteBestellungException> {
	@Override
	public Response toResponse(KundeDeleteBestellungException e) {
		final String msg = e.getMessage();
		final Response response = Response.status(CONFLICT)
		                                  .type(TEXT_PLAIN)
		                                  .entity(msg)
		                                  .build();
		return response;
	}

}
