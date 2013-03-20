package de.shop.kundenverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.Log;
import de.shop.kundenverwaltung.service.KundeService;


import javax.inject.Inject;


@ApplicationScoped
@Log
public class UriHelperKunde {
	
	@Inject
	private KundeService ks;
	
	public URI getUriKunde(Kunde kunde, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(KundeResource.class)
		                             .path(KundeResource.class, "findeKundeNachId");
		final URI kundeUri = ub.build(kunde.getId());
		return kundeUri;
	}
	
	
	public void updateUriKunde(Kunde kunde, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
                                     .path(KundeResource.class)
                                     .path(KundeResource.class, "findeBestellungenNachKundeId");
		final URI bestellungenUri = ub.build(kunde.getId());
		kunde.setBestellungenUri(bestellungenUri);
	}
}
