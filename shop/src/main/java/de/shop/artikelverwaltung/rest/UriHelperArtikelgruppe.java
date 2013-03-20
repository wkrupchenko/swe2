package de.shop.artikelverwaltung.rest;


import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Artikelgruppe;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.Log;
import javax.inject.Inject;


@ApplicationScoped
@Log
public class UriHelperArtikelgruppe {
	
	@Inject
	private ArtikelService as;
	
	public URI getUriArtikelgruppe(Artikelgruppe artikelgruppe, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(ArtikelResource.class)
		                             .path(ArtikelResource.class, "findeArtikelgruppe");
		final URI uri = ub.build(artikelgruppe.getId());
		return uri;
	}
	
	public void updateUriArtikelgruppe(Artikelgruppe artikelgruppe, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
									 .path(ArtikelResource.class)
									 .path(ArtikelResource.class, "findeArtikelNachArtikelgruppe");
		final URI uri = ub.build(artikelgruppe.getId());
		artikelgruppe.setArtikelUri(uri);
	}
	
	public Artikelgruppe getArtikelgruppe(URI uri) {
		//localhost:8080/shop/rest/artikel/artikelgruppe/400
		final String link = uri.toString();
		final String id = link.substring(link.lastIndexOf("/") + 1);
		final Long aId = Long.valueOf(id);
		final Artikelgruppe artikelgruppe = as.findeArtikelgruppeNachId(aId);
		return artikelgruppe;
	}
}
