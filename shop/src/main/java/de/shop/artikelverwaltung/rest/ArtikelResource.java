package de.shop.artikelverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.lang.invoke.MethodHandles;
import org.jboss.logging.Logger;
import java.util.Collection;
import java.util.Locale;
import java.net.URI;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.HttpHeaders;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.domain.Artikelgruppe;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.Transactional;
import de.shop.util.LocaleHelper;


@Path("/artikel")
@Produces(APPLICATION_JSON)
@Consumes
@RequestScoped
@Transactional
@Log
public class ArtikelResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
	@Inject
	private UriHelperArtikelgruppe uriHelperArtikelgruppe;
	
	@Inject
	private LocaleHelper localeHelper;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	/* rest/artikel
	 * Es werden alle Artikel angezeigt
	 * mit rest/artikel?bezeichnung=... diejenigen mit einer bestimmten Bezeichnung
	 */
	@GET
	public Collection<Artikel> findeArtikelNachBezeichnung(@QueryParam("bezeichnung")
	                                                       @DefaultValue("") String bezeichnung,
														   @Context UriInfo uriInfo) {
		Collection<Artikel> artikel = null;
		if ("".equals(bezeichnung)) {
			artikel = as.findeAlleArtikel();
			if (artikel.isEmpty()) {
				final String msg = "Keine Artikel vorhanden";
				throw new NotFoundException(msg);
			}
		}
		else {
			artikel = as.findeArtikelNachBezeichnung(bezeichnung);
			if (artikel.isEmpty()) {
				final String msg = "Keinen Artikel gefunden mit Bezeichnung " + bezeichnung;
				throw new NotFoundException(msg);
			}
		}
		
		//URIs der einzelnen Artikel anpassen
		for (Artikel a : artikel) {
			uriHelperArtikel.updateUriArtikel(a, uriInfo);
		}
		return artikel;
	}
	
	/* rest/artikel/{id}
	 * Sucht einen Artikel anahand der ID
	 */
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Artikel findeArtikel(@PathParam("id") Long id, @Context UriInfo uriInfo) {
		final Artikel artikel = as.findeArtikelNachId(id);
		if (artikel == null) {
			final String msg = "Kein Artikel gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		
		//URI von Artikelgruppe anpassen
		uriHelperArtikel.updateUriArtikel(artikel, uriInfo);

		return artikel;
	}
	
	/* rest/{id}/artikelgruppe
	 * Sucht die Artikelgruppe zum Artikel
	 */
	@GET
	@Path("{id:[1-9][0-9]*}/artikelgruppe")
	public Artikelgruppe findeArtikelgruppeNachArtikel(@PathParam("id") Long id, @Context UriInfo uriInfo) {
		final Artikelgruppe artikelgruppe = as.findeArtikelgruppeNachArtikel(id);
		if (artikelgruppe == null) {
			final String msg = "Kein Artikel gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		
		//URI von Artikelgruppe anpassen
		uriHelperArtikelgruppe.updateUriArtikelgruppe(artikelgruppe, uriInfo);
		
		return artikelgruppe;
	}
	
	/*
	 * Mit der URL /artikel einen Artikel per POST anlegen.
	 * @param artikel neuer Artikel
	 */
	@POST
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createArtikel(Artikel artikel, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		final Locale locale = localeHelper.getLocale(headers);
		Artikelgruppe artikelgruppe = uriHelperArtikelgruppe.getArtikelgruppe(artikel.getArtikelgruppeUri());
		artikel.setArtikelgruppe(artikelgruppe);
		artikelgruppe.addArtikel(artikel);
		artikel = as.createArtikel(artikel, locale);
		LOGGER.debugf("Artikel: %s", artikel);
		
		final URI artikelUri = uriHelperArtikel.getUriArtikel(artikel, uriInfo);
		return Response.created(artikelUri).build();
	}
	
	/**
	 * Mit der URL /artikel einen Artikel per PUT aktualisieren
	 * @param artikel zu aktualisierende Daten des Artikel
	 */
	@PUT
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	public void updateArtikel(Artikel artikel, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		// Vorhandenen Artikel ermitteln
		final Locale locale = localeHelper.getLocale(headers);
		Artikel origArtikel = as.findeArtikelNachId(artikel.getId());
		if (origArtikel == null) {
			final String msg = "Keinen Artikel gefunden mit der ID " + artikel.getId();
			throw new NotFoundException(msg);
		}
		LOGGER.debugf("Artikel vorher: %s", origArtikel);
	
		// Daten des vorhandenen Artikel ueberschreiben
		origArtikel.setWerte(artikel);
		LOGGER.debugf("Artikel nachher: %s", origArtikel);
		
		// Update durchfuehren
		artikel = as.updateArtikel(origArtikel, locale);
		if (artikel == null) {
			final String msg = "Keinen Artikel gefunden mit der ID " + origArtikel.getId();
			throw new NotFoundException(msg);
		}
	}
	
	/*
	 * Mit der URL /artikel{id} einen Artikel per DELETE löschen
	 * @param artikelId des zu löschenden Artikel
	 */
	@Path("{id:[0-9]+}")
	@DELETE
	@Produces
	public void deleteArtikel(@PathParam("id") Long artikelId) {
		final Artikel artikel = as.findeArtikelNachId(artikelId);
		as.deleteArtikel(artikel);
	}
	
	/* rest/artikel/artikelgruppe
	 * Es werden alle Artikelgruppen angezeigt
	 * mit rest/artikelgruppe?name=... diejenigen mit einem bestimmten Namen
	 */
	@GET
	@Path("artikelgruppe")
	public Collection<Artikelgruppe> findeArtikelgruppeNachName(@QueryParam("name") @DefaultValue("") String name,
														   		 @Context UriInfo uriInfo) {
		Collection<Artikelgruppe> artikelgruppen = null;
		if ("".equals(name)) {
			artikelgruppen = as.findeAlleArtikelgruppen();
			if (artikelgruppen.isEmpty()) {
				final String msg = "Keine Artikelgruppen vorhanden";
				throw new NotFoundException(msg);
			}
		}
		else {
			artikelgruppen = as.findeArtikelgruppeNachName(name);
			if (artikelgruppen.isEmpty()) {
				final String msg = "Keinen Artikelgruppe gefunden mit Bezeichnung " + name;
				throw new NotFoundException(msg);
			}
		}
		
		//URI von Artikelgruppe anpassen
		for (Artikelgruppe ag: artikelgruppen) {
			uriHelperArtikelgruppe.updateUriArtikelgruppe(ag, uriInfo);
		}
		return artikelgruppen;
	}
	
	@GET
	@Path("artikelgruppe/{id:[1-9][0-9]*}")
	public Artikelgruppe findeArtikelgruppe(@PathParam("id") Long id, @Context UriInfo uriInfo) {
		final Artikelgruppe artikelgruppe = as.findeArtikelgruppeNachId(id);
		if (artikelgruppe == null) {
			final String msg = "Kein Artikelgruppe gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		// URI von Artikelgruppe anpassen
		uriHelperArtikelgruppe.updateUriArtikelgruppe(artikelgruppe, uriInfo);
		
		return artikelgruppe;
	}
	
	@GET
	@Path("artikelgruppe/{id:[1-9][0-9]*}/artikel")
	public Collection<Artikel> findeArtikelNachArtikelgruppe(@PathParam("id") Long id, @Context UriInfo uriInfo) {
		final Collection<Artikel> artikel = as.findeArtikelNachArtikelgruppeId(id);
		if (artikel == null) {
			final String msg = "Keine Artikelgruppe gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		//Uri von Artikelgruppe anpassen
		for (Artikel a : artikel) {
			uriHelperArtikel.updateUriArtikel(a, uriInfo);
		}
		return artikel;
	}
	
	/*
	 * Mit der URL /artikelgruppe eine Artikelgruppe per POST anlegen.
	 * @param artikelgruppe neue Artikelgruppe
	 */
	@POST
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	@Path("artikelgruppe")
	public Response createArtikelgruppe(Artikelgruppe artikelgruppe, 
										@Context UriInfo uriInfo, @Context HttpHeaders headers) {
		final Locale locale = localeHelper.getLocale(headers);
		artikelgruppe = as.createArtikelgruppe(artikelgruppe, locale);
		LOGGER.debugf("Artikelgruppe: %s", artikelgruppe);
		
		final URI artikelgruppeUri = uriHelperArtikelgruppe.getUriArtikelgruppe(artikelgruppe, uriInfo);
		return Response.created(artikelgruppeUri).build();
	}
	
	/**
	 * Mit der URL /artikelgruppe eine Artikelgruppe per PUT aktualisieren
	 * @param artikelgruppe zu aktualisierende Daten der Artikelgruppe
	 */
	@PUT
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	@Path("artikelgruppe")
	public void updateArtikelgruppe(Artikelgruppe artikelgruppe, 
									@Context UriInfo uriInfo, @Context HttpHeaders headers) {
		// Vorhandenen Artikelgruppe ermitteln
		final Locale locale = localeHelper.getLocale(headers);
		Artikelgruppe origArtikelgruppe = as.findeArtikelgruppeNachId(artikelgruppe.getId());
		if (origArtikelgruppe == null) {
			final String msg = "Keine Artikelgruppe gefunden mit der ID " + artikelgruppe.getId();
			throw new NotFoundException(msg);
		}
		LOGGER.debugf("Artikelgruppe vorher: %s", origArtikelgruppe);
	
		// Daten der vorhandenen Artikelgruppe ueberschreiben
		origArtikelgruppe.setWerte(artikelgruppe);
		LOGGER.debugf("Artikelgruppe nachher: %s", origArtikelgruppe);
		
		// Update durchfuehren
		artikelgruppe = as.updateArtikelgruppe(origArtikelgruppe, locale);
		if (artikelgruppe == null) {
			final String msg = "Keinen Artikel gefunden mit der ID " + origArtikelgruppe.getId();
			throw new NotFoundException(msg);
		}
	}
	
	/*
	 * Mit der URL /artikelgruppe{id} eine Artikelgruppe per DELETE löschen
	 * @param artikelgruppeId der zu löschenden Artikelgruppe
	 */
	@Path("artikelgruppe/{id:[0-9]+}")
	@DELETE
	@Produces
	public void deleteArtikelgruppe(@PathParam("id") Long artikelgruppeId) {
		final Artikelgruppe artikelgruppe = as.findeArtikelgruppeNachId(artikelgruppeId);
		as.deleteArtikelgruppe(artikelgruppe);
	}
}