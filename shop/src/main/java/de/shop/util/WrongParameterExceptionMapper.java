package de.shop.util;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NOT_ACCEPTABLE;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
@ApplicationScoped
@Log
public class WrongParameterExceptionMapper implements ExceptionMapper<WrongParameterException> {
	@Override
	public Response toResponse(WrongParameterException e) {
		final String msg = e.getMessage();
		final Response response = Response.status(NOT_ACCEPTABLE)
		                                  .type(TEXT_PLAIN)
		                                  .entity(msg)
		                                  .build();
		return response;
	}

}