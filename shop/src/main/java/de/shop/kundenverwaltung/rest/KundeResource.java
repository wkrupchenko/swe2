package de.shop.kundenverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.rest.UriHelperBestellung;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.Transactional;
import de.shop.util.LocaleHelper;

@Path("/kunden")
@Produces(APPLICATION_JSON)
@Consumes
@RequestScoped
@Transactional
@Log
public class KundeResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	@Inject
	private KundeService ks;
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private UriHelperKunde uriHelperKunde;
	
	
	@Inject
	private UriHelperBestellung uriHelperBestellung;
	
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

	/**
	 * Mit der URL /kunden/{id} einen Kunden ermitteln
	 * @param id ID des Kunden
	 * @return Objekt mit Kundendaten, falls die ID vorhanden ist
	 */
	@GET
	@Path("{id:[1-9][0-9]*}")
	//@Formatted    XML formatieren, d.h. Einruecken und Zeilenumbruch
	public Kunde findeKundeNachId(@PathParam("id") Long id,
			                           @Context UriInfo uriInfo,
			                           @Context HttpHeaders headers) {
		final Locale locale = localeHelper.getLocale(headers);
		final Kunde kunde = ks.findeKundeNachId(id, FetchType.NUR_KUNDE, locale);
		if (kunde == null) {
			final String msg = "Kein Kunde gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
	
		uriHelperKunde.updateUriKunde(kunde, uriInfo);
		
		return kunde;
	}
	
	/**
	 * Mit der URL /kunden werden alle Kunden ermittelt oder
	 * mit kundenverwaltung/kunden?nachname=... diejenigen mit einem bestimmten Nachnamen.
	 * @return Collection mit den gefundenen Kundendaten
	 */
	@GET
	public Collection<Kunde> findeKundeNachNachname(@QueryParam("nachname") @DefaultValue("") String nachname,
			                                              @Context UriInfo uriInfo,
			                                              @Context HttpHeaders headers) {
		Collection<Kunde> kunden = null;
		if ("".equals(nachname)) {
			kunden = ks.findeAlleKunden(FetchType.NUR_KUNDE, null);
			if (kunden.isEmpty()) {
				final String msg = "Keine Kunden vorhanden";
				throw new NotFoundException(msg);
			}
		}
		else {
			final Locale locale = localeHelper.getLocale(headers);
			kunden = ks.findeKundeNachNachname(nachname, FetchType.NUR_KUNDE, locale);
			if (kunden.isEmpty()) {
				final String msg = "Kein Kunde gefunden mit Nachname " + nachname;
				throw new NotFoundException(msg);
			}
		}
		
		
		for (Kunde kunde : kunden) {
			uriHelperKunde.updateUriKunde(kunde, uriInfo);
		}
		
		return kunden;
	}
	
	@GET
	@Path("/prefix/id/{id:[1-9][0-9]*}")
	public Collection<Long> findeIdsNachPrefix(@PathParam("id") String idPrefix, 
			@Context UriInfo uriInfo) {
		final Collection<Long> ids = ks.findeIdsNachPrefix(idPrefix);
		return ids;
	}
	
	@GET
	@Path("/prefix/nachname/{nachname}")
	public Collection<String> findeNachnamenNachPrefix(@PathParam("nachname") String nachnamePrefix,
			                                                   @Context UriInfo uriInfo) {
		final Collection<String> nachnamen = ks.findeNachnamenNachPrefix(nachnamePrefix);
		return nachnamen;
	}
	
	/**
	 * Mit der URL /kunden/{id}/bestellungen die Bestellungen zu eine Kunden ermitteln
	 * @param kundeId ID des Kunden
	 * @return Objekt mit Bestellungsdaten, falls die ID vorhanden ist
	 */
	@GET
	@Path("{id:[1-9][0-9]*}/bestellungen")
	public Collection<Bestellung> findeBestellungenNachKundeId(@PathParam("id") Long kundeId,  
			@Context UriInfo uriInfo) {
		final Collection<Bestellung> bestellungen = bs.findeBestellungenNachKundeId(kundeId);
		if (bestellungen.isEmpty()) {
			final String msg = "Kein Bestellung gefunden zum Kunde mit der ID " + kundeId;
			throw new NotFoundException(msg);
		}
		
		for (Bestellung bestellung : bestellungen) {
			uriHelperBestellung.updateUriBestellung(bestellung, uriInfo);
		}
		
		return bestellungen;
	}

	@GET
	@Path("{id:[1-9][0-9]*}/bestellungenIds")
	public Collection<Long> findeBestellungenIdsNachKundeId(@PathParam("id") Long kundeId,  
			@Context UriInfo uriInfo) {
		final Collection<Bestellung> bestellungen = bs.findeBestellungenNachKundeId(kundeId);
		if (bestellungen.isEmpty()) {
			final String msg = "Kein Kunde gefunden mit der ID " + kundeId;
			throw new NotFoundException(msg);
		}
		
		final int anzahl = bestellungen.size();
		final Collection<Long> bestellungenIds = new ArrayList<>(anzahl);
		for (Bestellung bestellung : bestellungen) {
			bestellungenIds.add(Long.valueOf(bestellung.getId()));
		}
		
		return bestellungenIds;
	}

	/**
	 * Mit der URL /kunden einen Privatkunden per POST anlegen.
	 * @param kunde neuer Kunde
	 * @return Response-Objekt mit URL des neuen Privatkunden
	 */
	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createKunde(Kunde kunde, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		final Locale locale = localeHelper.getLocale(headers);		 
		final Adresse adresse = kunde.getAdresse();
		if (adresse != null) {
		  adresse.setKunde(kunde);
		}
		kunde.setPasswortWdh(kunde.getPasswort());
		kunde = ks.createKunde(kunde, locale);
		LOGGER.debugf("Kunde: %s", kunde);
		
		final URI kundeUri = uriHelperKunde.getUriKunde(kunde, uriInfo);
		return Response.created(kundeUri).build();
	}
	
	/**
	 * Mit der URL /kunden einen Kunden per PUT aktualisieren
	 * @param kunde zu aktualisierende Daten des Kunden
	 */
	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public void updateKunde(Kunde kunde, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		final Locale locale = localeHelper.getLocale(headers);
		final Kunde origKunde = ks.findeKundeNachId(kunde.getId(), FetchType.NUR_KUNDE, locale);
		if (origKunde == null) {
			final String msg = "Kein Kunde gefunden mit der ID " + kunde.getId();
			throw new NotFoundException(msg);
		}
		LOGGER.debugf("Kunde vorher: %s", origKunde);
	
		origKunde.setWerte(kunde);
		LOGGER.debugf("Kunde nachher: %s", origKunde);
		
		// Update durchfuehren
		kunde = ks.updateKunde(origKunde, locale);
		if (kunde == null) {
			final String msg = "Kein Kunde gefunden mit der ID " + origKunde.getId();
			throw new NotFoundException(msg);
		}
	}
	
	/**
	 * Mit der URL /kunden{id} einen Kunden per DELETE l&ouml;schen
	 * @param kundeId des zu l&ouml;schenden Kunden
	 */
	@Path("{id:[0-9]+}")
	@DELETE
	@Produces
	public void deleteKunde(@PathParam("id") Long kundeId, @Context HttpHeaders headers) {
		ks.deleteKundeNachId(kundeId);
	}
	
}
