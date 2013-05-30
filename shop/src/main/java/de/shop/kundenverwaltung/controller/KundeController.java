package de.shop.kundenverwaltung.controller;

import static javax.ejb.TransactionAttributeType.REQUIRED;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.TransactionAttribute;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.InvalidEmailException;
import de.shop.kundenverwaltung.service.InvalidKundeIdException;
import de.shop.kundenverwaltung.service.InvalidNachnameException;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.kundenverwaltung.service.KundeService.OrderType;
import de.shop.util.Client;
import de.shop.util.Log;
import de.shop.util.Messages;
import de.shop.util.Transactional;


/**
 * Dialogsteuerung fuer die Kundenverwaltung
 */
@Named("kc")
@RequestScoped
@Log
public class KundeController implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final int MAX_AUTOCOMPLETE = 10;
	private static final String FLASH_KUNDE = "kunde";
	private static final String JSF_VIEW_KUNDE = "/kundenverwaltung/viewKunde";
	private static final String JSF_VIEW_KUNDE_NACHNAME = "/kundenverwaltung/viewKundeNachname";
	private static final String JSF_VIEW_KUNDE_EMAIL = "/kundenverwaltung/viewKundeEmail";
	private static final String JSF_VIEW_KUNDE_USERNAME = "/kundenverwaltung/viewKundeUsername";
	private static final String JSF_VIEW_KUNDE_PLZ = "/kundenverwaltung/viewKundePlz";

	private static final String CLIENT_ID_KUNDEN_NACHNAME = "form:nachname";
	private static final String CLIENT_ID_KUNDEN_EMAIL = "form:email";
	private static final String CLIENT_ID_KUNDEN = "form:id";

	@Inject
	@Client
	private Locale locale;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private Flash flash;
	
	@Inject
	private Messages messages;
	
	private Long kundeId;
	private String nachname;
	private String username;
	private String email;
	private String plz;
	private Kunde kunde;
	private List<Kunde> kunden = Collections.emptyList();
	
	@Override
	public String toString() {
		return "KundeController [kundeId=" + kundeId + "]";
	}

	public void setKundeId(Long kundeId) {
		this.kundeId = kundeId;
	}

	public Long getKundeId() {
		return kundeId;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public Kunde getKunde() {
		return kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	public List<Kunde> getKunden() {
		return kunden;
	}

	public void setKunden(List<Kunde> kunden) {
		this.kunden = kunden;
	}
	
	// Um aktuelles Datum auf Startseite anzuzeigen
	// Kein Attribut, nur Getter der von der Ajax/JSF aufgerufen wird
	public Date getAktuellesDatum() {
		final Date datum = new Date();
		return datum;
	}

	/**
	 * Action Methode, um einen Kunden zu gegebener ID zu suchen
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	@TransactionAttribute(REQUIRED)
	public String findeKundeNachId() {
		try {
			kunde = ks.findeKundeNachId(kundeId, FetchType.MIT_BESTELLUNGEN, locale);
		}
		catch (InvalidKundeIdException e) {
			final Collection<ConstraintViolation<Kunde>> violations = e.getViolations();
			messages.error(violations, CLIENT_ID_KUNDEN);
			return null;
		}
		
		if (kunde == null) {
			flash.remove(FLASH_KUNDE);
			return null;
		}

		flash.put(FLASH_KUNDE, kunde);
		return JSF_VIEW_KUNDE;
	}
	
	/**
	 * Action Methode, um einen Kunden zu gegebenem Nachnamen zu suchen
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	@TransactionAttribute(REQUIRED)
	public String findKundenNachNachname() {
		if (nachname == null || nachname.isEmpty()) {
			kunden = ks.findeAlleKunden(FetchType.MIT_BESTELLUNGEN, OrderType.ID);
			flash.put("listeKunden", kunden);
			return JSF_VIEW_KUNDE_NACHNAME;
		}

		try {
			kunden = ks.findeKundeNachNachname(nachname, FetchType.MIT_BESTELLUNGEN, locale);
		}
		catch (InvalidNachnameException e) {
			final Collection<ConstraintViolation<Kunde>> violations = e.getViolations();
			messages.error(violations, CLIENT_ID_KUNDEN_NACHNAME);
			return null;
		}
		
		flash.put("listeKunden", kunden);
		return JSF_VIEW_KUNDE_NACHNAME;
	}
	
	/**
	 * Für rich:autocomplete um potentielle Nachnamen zu Prefix zu erhalten
	 * @return Liste der potenziellen Nachnamen
	 */
	@TransactionAttribute(REQUIRED)
	public List<String> findeNachnameNachPrefix(String kundeNachnamePrefix) {
		final List<String> nachnamen = ks.findeNachnamenNachPrefix(kundeNachnamePrefix);
		
		if (nachnamen.size() > MAX_AUTOCOMPLETE) {
			return nachnamen.subList(0, MAX_AUTOCOMPLETE);
		}

		return nachnamen;
	}
	
	/**
	 * Action Methode, um einen Kunden zu gegebenem Username zu suchen
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	@TransactionAttribute(REQUIRED)
	public String findKundenNachUsername() {
		if (username == null || username.isEmpty()) {
			kunden = ks.findeAlleKunden(FetchType.MIT_BESTELLUNGEN, OrderType.ID);
			flash.put(FLASH_KUNDE, kunden);
			return JSF_VIEW_KUNDE_USERNAME;
		}

		kunde = ks.findeKundeNachUserName(username);
		
		if (kunde == null) {
			flash.remove(FLASH_KUNDE);
			return null;
		}
		
		flash.put(FLASH_KUNDE, kunde);
		return JSF_VIEW_KUNDE_USERNAME;
	}

	/**
	 * Action Methode, um einen Kunden zu gegebener Email zu suchen
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	@TransactionAttribute(REQUIRED)
	public String findKundenNachEmail() {
		if (email == null || email.isEmpty()) {
			kunden = ks.findeAlleKunden(FetchType.MIT_BESTELLUNGEN, OrderType.ID);
			flash.put(FLASH_KUNDE, kunden);
			return JSF_VIEW_KUNDE_EMAIL;
		}

		try {
			kunde = ks.findeKundeNachEmail(email, locale);
		}
		catch (InvalidEmailException e) {
			final Collection<ConstraintViolation<Kunde>> violations = e.getViolations();
			messages.error(violations, CLIENT_ID_KUNDEN_EMAIL);
			return null;
		}
		
		flash.put(FLASH_KUNDE, kunde);
		return JSF_VIEW_KUNDE_EMAIL;
	}

	/**
	 * Action Methode, um einen Kunden zu gegebener Plz zu suchen
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	@TransactionAttribute(REQUIRED)
	public String findKundenNachPlz() {
		if (plz == null || plz.isEmpty()) {
			kunden = ks.findeAlleKunden(FetchType.MIT_BESTELLUNGEN, OrderType.ID);
			flash.put("listeKunden", kunden);
			return JSF_VIEW_KUNDE_PLZ;
		}

		kunden = ks.findeKundeNachPlz(plz);
		
		if(kunden.isEmpty()) {
			flash.remove(kunden);
			return null;
		}
		
		flash.put("listeKunden", kunden);
		return JSF_VIEW_KUNDE_PLZ;
	}
}
