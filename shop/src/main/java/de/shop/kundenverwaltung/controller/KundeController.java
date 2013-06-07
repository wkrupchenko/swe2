package de.shop.kundenverwaltung.controller;

import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;
import static de.shop.util.Konstante.JSF_REDIRECT_SUFFIX;
import static de.shop.util.Konstante.JSF_INDEX;
import static de.shop.util.Messages.MessagesType.KUNDENVERWALTUNG;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.context.Flash;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.groups.Default;

import org.richfaces.cdi.push.Push;

import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.GeschlechtTyp;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.EmailExistsException;
import de.shop.kundenverwaltung.service.InvalidEmailException;
import de.shop.kundenverwaltung.service.InvalidKundeException;
import de.shop.kundenverwaltung.service.InvalidKundeIdException;
import de.shop.kundenverwaltung.service.InvalidNachnameException;
import de.shop.kundenverwaltung.service.KundeDeleteBestellungException;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.kundenverwaltung.service.KundeService.OrderType;
import de.shop.kundenverwaltung.service.PwdNotEqualException;
import de.shop.util.AbstractShopException;
import de.shop.util.Client;
import de.shop.util.ConcurrentDeletedException;
import de.shop.util.Log;
import de.shop.util.Messages;


/**
 * Dialogsteuerung fuer die Kundenverwaltung
 */
@Named("kc")
@SessionScoped
@Log
@Stateful
@TransactionAttribute(SUPPORTS)
public class KundeController implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final int MAX_AUTOCOMPLETE = 10;
	private static final String JSF_VIEW_ALLE_KUNDEN = "/kundenverwaltung/viewAlleKunden";
	private static final String JSF_VIEW_KUNDE = "/kundenverwaltung/viewKunde";
	private static final String JSF_VIEW_KUNDE_NACHNAME = "/kundenverwaltung/viewKundeNachname";
	private static final String JSF_VIEW_KUNDE_EMAIL = "/kundenverwaltung/viewKundeEmail";
	private static final String JSF_VIEW_KUNDE_USERNAME = "/kundenverwaltung/viewKundeUsername";
	private static final String JSF_VIEW_KUNDE_PLZ = "/kundenverwaltung/viewKundePlz";

	private static final String JSF_UPDATE_KUNDE = "/kundenverwaltung/updateKunde";
	private static final String JSF_DELETE_OK = "/kundenverwaltung/okDeleteKunde";

	private static final String MSG_KEY_UPDATE_KUNDE_CONCURRENT_UPDATE = "updateKunde.concurrentUpdate";
	private static final String MSG_KEY_UPDATE_KUNDE_CONCURRENT_DELETE = "updateKunde.concurrentDelete";
	private static final String MSG_KEY_UPDATE_KUNDE_DUPLIKAT = "updateKunde.duplikat";
	private static final String MSG_KEY_DELETE_KUNDE_BESTELLUNG = "viewKunde.deleteKundeBestellung";
	private static final String MSG_KEY_CREATE_KUNDE_EMAIL_EXISTS = "createKunde.emailExists";
	private static final String MSG_KEY_CREATE_KUNDE_PWD_NOT_EQUAL = "createKunde.passwortNotEqual";

	private static final String CLIENT_ID_KUNDEN_NACHNAME = "form:nachname";
	private static final String CLIENT_ID_KUNDEN_EMAIL = "form:email";
	private static final String CLIENT_ID_KUNDEN = "form:id";
	private static final String CLIENT_ID_UPDATE_PASSWORT = "updateKundeForm:passwort";
	private static final String CLIENT_ID_UPDATE_EMAIL = "updateKundeForm:email";
	private static final String CLIENT_ID_CREATE_EMAIL = "createKundeForm:email";
	private static final String CLIENT_ID_CREATE_PWD = "createKundeForm:passwort";

	private static final String REQUEST_KUNDE_ID = "kundeId";

	private Long kundeId;
	private String nachname;
	private String username;
	private String email;
	private String plz;
	private Kunde kunde;
	private Kunde neuerKunde;
	private List<Kunde> alleKunden = Collections.emptyList();
	private List<Kunde> kunden = Collections.emptyList();
	private boolean geaendertKunde;

	private static final Class<?>[] PASSWORT_GROUP = {
		Default.class };
	
	@Inject
	@Client
	private Locale locale;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private Flash flash;
	
	@Inject
	private Messages messages;

	@Inject
	private transient HttpServletRequest request;
	
	@Inject
	@Push(topic = "createArtikel")
	private transient Event<String> neuerKundeEvent;

	@Inject
	@Push(topic = "updateKunde")
	private transient Event<String> updateKundeEvent;
	
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

	public Kunde getNeuerKunde() {
		return neuerKunde;
	}

	public void setNeuerKunde(Kunde neuerKunde) {
		this.neuerKunde = neuerKunde;
	}

	public List<Kunde> getKunden() {
		return kunden;
	}

	public void setKunden(List<Kunde> kunden) {
		this.kunden = kunden;
	}

	public List<Kunde> getAlleKunden() {
		return alleKunden;
	}

	public void setAlleKunden(List<Kunde> alleKunden) {
		this.alleKunden = alleKunden;
	}

	public Class<?>[] getPasswortGroup() {
		return PASSWORT_GROUP.clone();
	}

	// Um aktuelles Datum auf Startseite anzuzeigen
	// Kein Attribut, nur Getter der von der Ajax/JSF aufgerufen wird
	public Date getAktuellesDatum() {
		final Date datum = new Date();
		return datum;
	}

	public String findAlleKunden() {
		alleKunden = ks.findeAlleKunden(FetchType.MIT_BESTELLUNGEN, OrderType.ID);
		return JSF_VIEW_ALLE_KUNDEN;
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
		
		return JSF_VIEW_KUNDE;
	}
	
	/**
	 * Action Methode, um einen Kunden zu gegebenem Nachnamen zu suchen
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	@TransactionAttribute(REQUIRED)
	public String findKundenNachNachname() {
		if (nachname == null || nachname.isEmpty()) {
			return null;
		}

		try {
			kunden = ks.findeKundeNachNachname(nachname, FetchType.MIT_BESTELLUNGEN, locale);
		}
		catch (InvalidNachnameException e) {
			final Collection<ConstraintViolation<Kunde>> violations = e.getViolations();
			messages.error(violations, CLIENT_ID_KUNDEN_NACHNAME);
			return null;
		}
		
		return JSF_VIEW_KUNDE_NACHNAME;
	}
	
	/**
	 * F�r rich:autocomplete um potentielle Nachnamen zu Prefix zu erhalten
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
			return null;
		}
		
		kunde = ks.findeKundeNachUserName(username);
		
		if (kunde == null) {
			return null;
		}
		
		return JSF_VIEW_KUNDE_USERNAME;
	}

	/**
	 * Action Methode, um einen Kunden zu gegebener Email zu suchen
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	@TransactionAttribute(REQUIRED)
	public String findKundenNachEmail() {
		if (email == null || email.isEmpty()) {
			return null;
		}
		
		try {
			kunde = ks.findeKundeNachEmail(email, locale);
		}
		catch (InvalidEmailException e) {
			final Collection<ConstraintViolation<Kunde>> violations = e.getViolations();
			messages.error(violations, CLIENT_ID_KUNDEN_EMAIL);
			return null;
		}
		
		return JSF_VIEW_KUNDE_EMAIL;
	}

	/**
	 * Action Methode, um einen Kunden zu gegebener Plz zu suchen
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	@TransactionAttribute(REQUIRED)
	public String findKundenNachPlz() {
		if (plz == null || plz.isEmpty()) {
			return null;
		}
		
		kunden = ks.findeKundeNachPlz(plz);
		
		if (kunden.isEmpty()) {
			return null;
		}
		
		return JSF_VIEW_KUNDE_PLZ;
	}

	/**
	 * Verwendung als ValueChangeListener bei updateKunde.xhtml
	 */
	public void geaendert(ValueChangeEvent e) {
		if (geaendertKunde) {
			return;
		}
		
		if (e.getOldValue() == null) {
			if (e.getNewValue() != null) {
				geaendertKunde = true;
			}
			return;
		}

		if (!e.getOldValue().equals(e.getNewValue())) {
			geaendertKunde = true;				
		}
	}
	
	public String selectForUpdate(Kunde ausgewaehlterKunde) {
		if (ausgewaehlterKunde == null) {
			return null;
		}
		
		kunde = ausgewaehlterKunde;
		
		return JSF_UPDATE_KUNDE;
	}

	@TransactionAttribute(REQUIRED)
	public String update() {
		if (!geaendertKunde || kunde == null) {
			return JSF_INDEX;
		}
		
		try {
			kunde = ks.updateKunde(kunde, locale);
		}
		catch (EmailExistsException | InvalidKundeIdException
			  | OptimisticLockException | ConcurrentDeletedException e) {
			final String outcome = updateKundeErrorMsg(e);
			return outcome;
		}

		// Push-Event fuer Webbrowser
		updateKundeEvent.fire(String.valueOf(kunde.getId()));
		
		// ValueChangeListener zuruecksetzen
		geaendertKunde = false;
		
		// Aufbereitung fuer viewKunde.xhtml
		kundeId = kunde.getId();
		
		return JSF_VIEW_KUNDE + JSF_REDIRECT_SUFFIX;
	}

	private String updateKundeErrorMsg(RuntimeException es) {
		final Class<? extends RuntimeException> exceptionClass = es.getClass();
		if (exceptionClass.equals(InvalidKundeIdException.class)) {
			// Ungueltiges Passwort: Attribute wurden bereits von JSF validiert
			final InvalidKundeException orig = (InvalidKundeException) es;
			final Collection<ConstraintViolation<Kunde>> violations = orig.getViolations();
			messages.error(violations, CLIENT_ID_UPDATE_PASSWORT);
		}
		else if (exceptionClass.equals(EmailExistsException.class)) {
			messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_KUNDE_DUPLIKAT, CLIENT_ID_UPDATE_EMAIL);
		}
		else if (exceptionClass.equals(OptimisticLockException.class)) {
			messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_KUNDE_CONCURRENT_UPDATE, null);
		}
		else if (exceptionClass.equals(ConcurrentDeletedException.class)) {
			messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_KUNDE_CONCURRENT_DELETE, null);
		}
		return null;
	}
	
	/**
	 * Action-Methode, die aufgerufen wird wennn ein Kunde gel�scht werden soll.
	 * @TransactionAttribute(REQUIRED), da Sie die Funktion im Anwendungskern aufruft
	 * welche w�hrend der Transaktion stattfinden muss
	 * @return Die Seite mit der L�schbest�tigung
	 */
	@TransactionAttribute(REQUIRED)
	public String delete(Kunde ausgewaehlterKunde) {
		try {
			ks.deleteKunde(ausgewaehlterKunde);
		}
		catch (KundeDeleteBestellungException e) {
			messages.error(KUNDENVERWALTUNG, MSG_KEY_DELETE_KUNDE_BESTELLUNG, null,
					       e.getKundeId());
			return null;
		}
		
		// Aufbereitung fuer okDelete.xhtml
		request.setAttribute(REQUEST_KUNDE_ID, ausgewaehlterKunde.getId());

		return JSF_DELETE_OK;
	}
	
	public void createEmptyKunde() {
		if (neuerKunde != null) {
			return;
		}

		neuerKunde = new Kunde();
		final Adresse adresse = new Adresse();
		adresse.setKunde(neuerKunde);
		neuerKunde.setAdresse(adresse);
		neuerKunde.setGeschlecht(GeschlechtTyp.MAENNLICH);
		neuerKunde.setArt("P");
		neuerKunde.setUmsatz(new BigDecimal(0));
		neuerKunde.setRabatt(new BigDecimal(0));
	}

	@TransactionAttribute(REQUIRED)
	public String createKunde() {
		try {
			neuerKunde = ks.createKunde(neuerKunde, locale);
		}
		catch (PwdNotEqualException | InvalidKundeException | EmailExistsException  e) {
			final String outcome = createKundeErrorMsg(e);
			return outcome;
		}

		// Push-Event fuer Webbrowser
		neuerKundeEvent.fire(String.valueOf(neuerKunde.getId()));
		
		// Aufbereitung fuer viewKunde.xhtml
		kundeId = neuerKunde.getId();
		kunde = neuerKunde;
		neuerKunde = null;  // zuruecksetzen
		
		return JSF_VIEW_KUNDE + JSF_REDIRECT_SUFFIX;
	}

	private String createKundeErrorMsg(AbstractShopException e) {
		final Class<? extends AbstractShopException> exceptionClass = e.getClass();
		if (exceptionClass.equals(EmailExistsException.class)) {
			messages.error(KUNDENVERWALTUNG, MSG_KEY_CREATE_KUNDE_EMAIL_EXISTS, CLIENT_ID_CREATE_EMAIL);
		}
		else if (exceptionClass.equals(PwdNotEqualException.class)) {
			messages.error(KUNDENVERWALTUNG, MSG_KEY_CREATE_KUNDE_PWD_NOT_EQUAL, CLIENT_ID_CREATE_PWD);
		}
		else if (exceptionClass.equals(InvalidKundeException.class)) {
			final InvalidKundeException orig = (InvalidKundeException) e;
			messages.error(orig.getViolations(), null);
		}
		
		return null;
	}
	
	/*
	 * Methode um Kunden zu leeren, damit Tabelle immer wieder leer ist
	 */
	public void clearKunde() {
		kunde = null;
		kunden = null;
	}

}
