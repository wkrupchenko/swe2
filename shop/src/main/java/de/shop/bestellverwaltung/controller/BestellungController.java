package de.shop.bestellverwaltung.controller;

import static de.shop.util.Konstante.JSF_DEFAULT_ERROR;
import static java.text.DateFormat.MEDIUM;
import static java.text.DateFormat.SHORT;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import de.shop.auth.controller.AuthController;
import de.shop.auth.controller.KundeLoggedIn;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Lieferung;
import de.shop.bestellverwaltung.domain.TransportTyp;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.bestellverwaltung.service.BestellungValidationException;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.Client;
import de.shop.util.Log;
import de.shop.util.Transactional;

/**
 * Dialogsteuerung fuer die Bestellverwaltung
 */
@Named("bc")
@RequestScoped
@Log
public class BestellungController implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String FLASH_BESTELLUNG = "bestellung";
	private static final String FLASH_LIEFERUNG = "lieferung";
	private static final String FLASH_KUNDE = "kunde";	
	private static final String JSF_VIEW_BESTELLUNG = "/bestellverwaltung/viewBestellung";
	private static final String JSF_BESTELLUNG_STATUS = "/bestellverwaltung/viewBestellungenStatus";
	private static final String JSF_VIEW_LIEFERUNG = "/bestellverwaltung/viewLieferung";
	private static final String JSF_VIEW_KUNDE = "/bestellverwaltung/viewKundeBestId";
	private Boolean bestellungStatus;
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private Flash flash;
		
	@Inject
	private Warenkorb warenkorb;	 
	
	@Inject
	private AuthController auth;
	
	@Inject
	@KundeLoggedIn
	private Kunde kunde;
	
	@Inject
	@Client
	private Locale locale;	
	
	private Long bestellungId;
	
	private Long lieferungId;	 
	
	private String liefernummer;
	
	final private String inlandOderAusland = "I";
	
	final private TransportTyp transportArt = TransportTyp.STRASSE;
	
	@Override
	public String toString() {
		return "BestellungController [bestellungId=" + bestellungId 
				+ " lieferungId=" + lieferungId + " liefernummer=" + liefernummer + "]";
	}

	public void setBestellungId(Long bestellungId) {
		this.bestellungId = bestellungId;
	}

	public Long getBestellungId() {
		return bestellungId;
	}
	
	public void setLiefernummer(String liefernummer) {
		this.liefernummer = liefernummer;
	}

	public String getLiefernummer() {
		return liefernummer;
	}
	
	public void setLieferungId(Long lieferungId) {
		this.lieferungId = lieferungId;
	}

	public Long getLieferungId() {
		return lieferungId;
	}
	
	public void setBestellungStatus(Boolean bestellungStatus) {
		this.bestellungStatus = bestellungStatus;
	}

	public Boolean getBestellungStatus() {
		return bestellungStatus;
	}
			
	public static String generateString() {
		final Calendar cal = Calendar.getInstance();
		final String result;
	    DateFormat df;
	    df = DateFormat.getDateTimeInstance(SHORT, MEDIUM);
	    result = df.format(cal.getTime()).toString();
	    return result;	    
	}
	 

	/**
	 * Action Methode, um eine Bestellung zu gegebener ID zu suchen
	 * @return URL fuer Anzeige der gefundenen Bestellung; sonst null
	 */
	
	@Transactional
	public String findBestellungById() {
		final Bestellung bestellung = bs.findeBestellungNachId(bestellungId);
	 	if (bestellung == null) {
			flash.remove(FLASH_BESTELLUNG);
			return null;
		}
						
		flash.put(FLASH_BESTELLUNG, bestellung);
	 
		return JSF_VIEW_BESTELLUNG;
	}
				
	@Transactional
	public String findLieferungById() {
		final Lieferung lieferung = bs.findeLieferungNachId(lieferungId);
		if (lieferung == null) {
			flash.remove(FLASH_LIEFERUNG);
			return null;
		}
						
		flash.put(FLASH_LIEFERUNG, lieferung);
		return JSF_VIEW_LIEFERUNG;
	}
		 
	@Transactional
	public String findeBestellungNachStatus() {
		if (bestellungStatus) {
			flash.remove(FLASH_BESTELLUNG);
			final List<Bestellung> bestellung = bs.findeBestellungenOffen();
			 if (bestellung.isEmpty()) {
				flash.remove(FLASH_BESTELLUNG);
				return null;
			
			}
			
		
			flash.put(FLASH_BESTELLUNG, bestellung);
			return JSF_BESTELLUNG_STATUS;
		}
		else {
			flash.remove(FLASH_BESTELLUNG);
			final List<Bestellung> bestellung = bs.findeBestellungenGeschlossen();
			 if (bestellung.isEmpty()) {
				flash.remove(FLASH_BESTELLUNG);
				return null;
			}
			
			flash.put(FLASH_BESTELLUNG, bestellung);
			return JSF_BESTELLUNG_STATUS;
		}
	}
				 	
	@Transactional
	public String findeKundeNachBestellungId() {
		final Kunde kundeBestellungen = ks.findeKundeNachBestellung(bestellungId);
		if (kundeBestellungen == null) {
			flash.remove(FLASH_KUNDE);
			return null;
		}
						
		flash.put(FLASH_KUNDE, kundeBestellungen);
		return JSF_VIEW_KUNDE;
	}
	
	@Transactional
		public String createBestellung() {
		  auth.preserveLogin();
			
			if (warenkorb == null || warenkorb.getPositionen() == null || warenkorb.getPositionen().isEmpty()) {
				// Darf nicht passieren, wenn der Button zum Bestellen verfuegbar ist
				return JSF_DEFAULT_ERROR;
			}
			
			// Den eingeloggten Kunden mit seinen Bestellungen ermitteln, und dann die neue Bestellung zu ergaenzen
			kunde = ks.findeKundeNachId(kunde.getId(), FetchType.MIT_BESTELLUNGEN, locale);
			
			// Aus dem Warenkorb nur Positionen mit Anzahl > 0
			final List<Bestellposition> positionen = warenkorb.getPositionen();
			final List<Bestellposition> neuePositionen = new ArrayList<>(positionen.size());
			for (Bestellposition bp : positionen) {
				if (bp.getAnzahl() > 0) {
					neuePositionen.add(bp);
				}
			}
			
			// Warenkorb zuruecksetzen
			warenkorb.endConversation();
			
			// Neue Bestellung mit neuen Bestellpositionen erstellen
			Bestellung bestellung = new Bestellung();
			bestellung.setOffenAbgeschlossen(true);
			bestellung.setBestellpositionen(neuePositionen);
			Lieferung lieferung = new Lieferung();
			lieferung.setInlandOderAusland(inlandOderAusland);
			lieferung.setLiefernr(generateString());
			lieferung.setTransportArt(transportArt);
			lieferung.addBestellung(bestellung);
			bestellung.addLieferung(lieferung);
			final List<Bestellung> bestellungen = new ArrayList<>();
			bestellungen.add(bestellung);
			
			
			LOGGER.debugf("Neue Bestellung: %s\nBestellpositionen: %s", bestellung, bestellung.getBestellpositionen());
			try {
				lieferung = bs.createLieferung(lieferung, bestellungen);
			}
			catch (BestellungValidationException e) {
				// Validierungsfehler KOENNEN NICHT AUFTRETEN, da Attribute durch JSF validiert wurden
				// und in der Klasse Bestellung keine Validierungs-Methoden vorhanden sind
				throw new IllegalStateException(e);
			}
			// Bestellung mit VORHANDENEM Kunden verknuepfen:
			// dessen Bestellungen muessen geladen sein, weil es eine bidirektionale Beziehung ist
			try {
				bestellung = bs.createBestellung(bestellung, kunde, lieferung, locale);
			}
			catch (BestellungValidationException e) {
				// Validierungsfehler KOENNEN NICHT AUFTRETEN, da Attribute durch JSF validiert wurden
				// und in der Klasse Bestellung keine Validierungs-Methoden vorhanden sind
				throw new IllegalStateException(e);
			}
			
			
			// Bestellung im Flash speichern wegen anschliessendem Redirect
			flash.put(FLASH_BESTELLUNG, bestellung);
			
			return JSF_VIEW_BESTELLUNG;
		}
	}
