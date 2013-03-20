package de.shop.bestellverwaltung.rest;


import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.bestellverwaltung.domain.Lieferung;
import de.shop.util.Log;


@ApplicationScoped
@Log
public class UriHelperLieferung {

	public URI getUriLieferung(Lieferung lieferung, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(BestellverwaltungResource.class)
		                             .path(BestellverwaltungResource.class, "findeLieferungNachId");
		final URI uri = ub.build(lieferung.getId());
		return uri;
	}
	
	public void updateUriLieferung(Lieferung lieferung, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
									 .path(BestellverwaltungResource.class)
									 .path(BestellverwaltungResource.class, "findeBestellungenNachLieferungId");
		final URI uri = ub.build(lieferung.getId());
		lieferung.setBestellungenUri(uri);
	}
}

