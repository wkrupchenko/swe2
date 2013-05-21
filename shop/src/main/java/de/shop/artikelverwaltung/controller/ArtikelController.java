package de.shop.artikelverwaltung.controller;

import static javax.ejb.TransactionAttributeType.REQUIRED;
import static de.shop.util.Messages.MessagesType.ARTIKELVERWALTUNG;
import static de.shop.util.Konstante.JSF_REDIRECT_SUFFIX;
import static de.shop.util.Konstante.JSF_INDEX;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ejb.TransactionAttribute;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.context.Flash;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.OptimisticLockException;
import javax.validation.groups.Default;

import org.richfaces.cdi.push.Push;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.domain.Artikelgruppe;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.artikelverwaltung.service.InvalidArtikelException;
import de.shop.artikelverwaltung.service.InvalidArtikelgruppeException;
import de.shop.util.AbstractShopException;
import de.shop.util.Client;
import de.shop.util.ConcurrentDeletedException;
import de.shop.util.Log;
import de.shop.util.Messages;
import de.shop.util.Transactional;


/**
 * Dialogsteuerung fuer die Artikelverwaltung
 */
@Named("ac")
@SessionScoped
@Log
public class ArtikelController implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String FLASH_ARTIKEL = "artikel";
	
	private static final int ANZAHL_LADENHUETER = 5;
	private static final int MAX_AUTOCOMPLETE = 10;
	
	private static final String JSF_VIEW_ARTIKEL = "/artikelverwaltung/viewArtikel";
	private static final String JSF_VIEW_ARTIKEL_ARTIKELGRUPPE = "/artikelverwaltung/viewArtikelArtikelgruppe";
	private static final String JSF_VIEW_ARTIKEL_BEZEICHNUNG = "/artikelverwaltung/viewArtikelBezeichnung";
	private static final String JSF_VIEW_ARTIKEL_VERFUEGBARKEIT = "/artikelverwaltung/viewArtikelVerfuegbarkeit";
	private static final String JSF_VIEW_ARTIKEL_MAX_PREIS = "/artikelverwaltung/viewArtikelMaxPreis";
	private static final String JSF_VIEW_ARTIKEL_MIN_PREIS = "/artikelverwaltung/viewArtikelMinPreis";
	private static final String JSF_UPDATE_ARTIKEL = "/artikelverwaltung/updateArtikel";
	
	private static final String MSG_KEY_ARTIKEL_NOT_FOUND_BY_BEZEICHNUNG = "viewArtikelBezeichnung.notFound";
	private static final String MSG_KEY_UPDATE_ARTIKEL_CONCURRENT_UPDATE = "updateArtikel.concurrentUpdate";
	private static final String MSG_KEY_UPDATE_ARTIKEL_CONCURRENT_DELETE = "updateArtikel.concurrentDelete";
	
	private static final String CLIENT_ID_ARTIKEL_BEZEICHNUNG = "form:bezeichnung";
	
	private static final Class<?>[] DEFAULT_GROUP = { Default.class };
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private Flash flash;
	
	@Inject
	private Messages messages;
	
	@Inject
	@Client
	private Locale locale;
	
	@Inject
	@Push(topic = "createArtikel")
	private transient Event<String> neuerArtikelEvent;
	
	@Inject
	@Push(topic = "createArtikelgruppe")
	private transient Event<String> neueArtikelgruppeEvent;
	
	@Inject
	@Push(topic = "updateArtikel")
	private transient Event<String> updateArtikelEvent;
	
	private Long artikelId;
	private String artikelArtikelgruppe;
	private String artikelBezeichnung;
	private Boolean artikelErhaeltlich;
	private double artikelPreis;
	private List<Artikel> ladenhueter;
	private Artikel artikel;
	private boolean geaendertArtikel;    // fuer ValueChangeListener
	private Artikel neuerArtikel;
	private List<Artikelgruppe> alleArtikelgruppen;
	private Long neuerArtikelArtikelgruppeId;
	private Artikelgruppe neueArtikelgruppe;
	private Long updateArtikelArtikelgruppeId;

	@Override
	public String toString() {
		return "ArtikelController [artikelId=" + artikelId + " artikelBezeichnung=" + artikelBezeichnung + "]";
	}

	public void setArtikelId(Long artikelId) {
		this.artikelId = artikelId;
	}

	public Long getArtikelId() {
		return artikelId;
	}
	
	public void setArtikelArtikelgruppe(String artikelArtikelgruppe) {
		this.artikelArtikelgruppe = artikelArtikelgruppe;
	}

	public String getArtikelArtikelgruppe() {
		return artikelArtikelgruppe;
	}
	
	public void setArtikelBezeichnung(String artikelBezeichnung) {
		this.artikelBezeichnung = artikelBezeichnung;
	}

	public String getArtikelBezeichnung() {
		return artikelBezeichnung;
	}
	
	public void setArtikelErhaeltlich(Boolean artikelErhaeltlich) {
		this.artikelErhaeltlich = artikelErhaeltlich;
	}

	public Boolean getArtikelErhaeltlich() {
		return artikelErhaeltlich;
	}
	
	public void setArtikelPreis(double artikelPreis) {
		this.artikelPreis = artikelPreis;
	}

	public double getArtikelPreis() {
		return artikelPreis;
	}
	
	public List<Artikel> getLadenhueter() {
		return ladenhueter;
	}
	
	public Artikel getArtikel() {
		return artikel;
	}
	
	public Artikel getNeuerArtikel() {
		return neuerArtikel;
	}
	
	public List<Artikelgruppe> getAlleArtikelgruppen() {
		alleArtikelgruppen = as.findeAlleArtikelgruppen();
		return alleArtikelgruppen;
	}
	
	public Long getNeuerArtikelArtikelgruppeId() {
		return neuerArtikelArtikelgruppeId;
	}
	
	public void setNeuerArtikelArtikelgruppeId(Long artikelgruppeId) {
		this.neuerArtikelArtikelgruppeId = artikelgruppeId;
	}
	
	public Artikelgruppe getNeueArtikelgruppe() {
		return neueArtikelgruppe;
	}
	
	public Long getUpdateArtikelArtikelgruppeId() {
		return updateArtikelArtikelgruppeId;
	}
	
	public void setUpdateArtikelArtikelgruppeId(Long artikelgruppeId) {
		this.updateArtikelArtikelgruppeId = artikelgruppeId;
	}
	
	public Class<?>[] getDefaultGroup() {
		return DEFAULT_GROUP.clone();
	}

	/**
	 * Action Methode, um einen Artikel zu gegebener ID zu suchen
	 * @return URL fuer Anzeige des gefundenen Artikel; sonst null
	 */
	@Transactional
	public String findeArtikelNachId() {
		final Artikel artikel = as.findeArtikelNachId(artikelId);
		if (artikel == null) {
			flash.remove(FLASH_ARTIKEL);
			return null;
		}
		
		flash.put(FLASH_ARTIKEL, artikel);
		return JSF_VIEW_ARTIKEL;
	}
	
	/**
	 * Action Methode, um Artikel zu einer gegebenen Artikelgruppe zu suchen
	 * @return URL fuer Anzeige des gefundenen Artikel; sonst null
	 */
	@Transactional
	public String findeArtikelNachArtikelgruppe() {
		final List<Artikel> artikel = as.findeArtikelNachArtikelgruppe(artikelArtikelgruppe);
		if (artikel.isEmpty()) {
			flash.remove(FLASH_ARTIKEL);
			return null;
		}
		
		flash.put(FLASH_ARTIKEL, artikel);
		return JSF_VIEW_ARTIKEL_ARTIKELGRUPPE;
	}
	
	/**
	 * Action Methode, um Artikel zu einer gegebenen Bezeichnung zu suchen
	 * @return URL fuer Anzeige des gefundenen Artikel; sonst null
	 */
	@Transactional
	public String findeArtikelNachBezeichnung() {
		final List<Artikel> artikel = as.findeArtikelNachBezeichnung(artikelBezeichnung);
		/*
		if (artikel.isEmpty()) {
			flash.remove(FLASH_ARTIKEL);
			return null;
		}
		*/
		
		flash.put(FLASH_ARTIKEL, artikel);
		return JSF_VIEW_ARTIKEL_BEZEICHNUNG;
	}
	
	/**
	 * F�r rich:autocomplete um potentielle Bezeichnungen zu Prefix zu erhalten
	 * @return Liste der potenziellen Bezeichnungen
	 */
	@TransactionAttribute(REQUIRED)
	public List<String> findeBezeichnungenNachPrefix(String artikelBezeichnungPrefix) {
		final List<String> bezeichnungen = as.findeBezeichnungenNachPrefix(artikelBezeichnungPrefix);
		if (bezeichnungen.isEmpty()) {
			messages.error(ARTIKELVERWALTUNG, MSG_KEY_ARTIKEL_NOT_FOUND_BY_BEZEICHNUNG, CLIENT_ID_ARTIKEL_BEZEICHNUNG, artikelId);
			return bezeichnungen;
		}

		if (bezeichnungen.size() > MAX_AUTOCOMPLETE) {
			return bezeichnungen.subList(0, MAX_AUTOCOMPLETE);
		}

		return bezeichnungen;
	}
	
	/**
	 * Action Methode, um Artikel zu gegebener Verfuegbarkeit zu suchen
	 * @return URL fuer Anzeige des gefundenen Artikel; sonst null
	 */
	@Transactional
	public String findeArtikelNachVerfuegbarkeit() {
		if(artikelErhaeltlich) {
			final List<Artikel> artikel = as.findeVerfuegbareArtikel();
			if (artikel.isEmpty()) {
				flash.remove(FLASH_ARTIKEL);
				return null;
			}
		
			flash.put(FLASH_ARTIKEL, artikel);
			return JSF_VIEW_ARTIKEL_VERFUEGBARKEIT;
		}
		else {
			final List<Artikel> artikel = as.findeNichtVerfuegbareArtikel();
			if (artikel.isEmpty()) {
				flash.remove(FLASH_ARTIKEL);
				return null;
			}
		
			flash.put(FLASH_ARTIKEL, artikel);
			return JSF_VIEW_ARTIKEL_VERFUEGBARKEIT;
		}
	}
	
	/**
	 * Action Methode, um Artikel zu einem maximal gegebenen Preis zu suchen
	 * @return URL fuer Anzeige des gefundenen Artikel; sonst null
	 */
	@Transactional
	public String findeArtikelNachMaxPreis() {
		final List<Artikel> artikel = as.findeArtikelNachMaxPreis(artikelPreis);
		if (artikel.isEmpty()) {
			flash.remove(FLASH_ARTIKEL);
			return null;
		}
		
		flash.put(FLASH_ARTIKEL, artikel);
		return JSF_VIEW_ARTIKEL_MAX_PREIS;
	}
	
	/**
	 * Action Methode, um Artikel zu einem minimal gegebenen Preis zu suchen
	 * @return URL fuer Anzeige des gefundenen Artikel; sonst null
	 */
	@Transactional
	public String findeArtikelNachMinPreis() {
		final List<Artikel> artikel = as.findeArtikelNachMinPreis(artikelPreis);
		if (artikel.isEmpty()) {
			flash.remove(FLASH_ARTIKEL);
			return null;
		}
		
		flash.put(FLASH_ARTIKEL, artikel);
		return JSF_VIEW_ARTIKEL_MIN_PREIS;
	}
	
	/**
	 *  Action Methode, um Ladenh�ter f�r Startsiete zu laden
	 * 
	 */
	@Transactional
	public void loadLadenhueter() {
		ladenhueter = as.ladenhueter(ANZAHL_LADENHUETER);
	}
	
	/**
	 *  Action Methode, um eigentlichen Artikel zu erzeugen bzw zu bef�llen
	 *  
	 */
	@Transactional
	public String createArtikel() {
		// Artikelgruppe anhand der vorgegebenen ID suchen und anschlie�end dem Artikel und umgekehrt zuweisen
		Artikelgruppe ag = as.findeArtikelgruppeNachId(neuerArtikelArtikelgruppeId);
		neuerArtikel.setArtikelgruppe(ag);
		ag.addArtikel(neuerArtikel);
		try {
			neuerArtikel = as.createArtikel(neuerArtikel, locale);
		}
		catch (InvalidArtikelException e) {
			final String outcome = createArtikelErrorMsg(e);
			return outcome;
		}

		// Push-Event fuer Webbrowser
		neuerArtikelEvent.fire(String.valueOf(neuerArtikel.getId()));
		
		// Aufbereitung fuer viewArtikel.xhtml
		artikelId = neuerArtikel.getId();
		artikel = neuerArtikel;
		neuerArtikel = null;  // zuruecksetzen
		
		return JSF_VIEW_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}
	
	private String createArtikelErrorMsg(AbstractShopException e) {
		final Class<? extends AbstractShopException> exceptionClass = e.getClass();

		if (exceptionClass.equals(InvalidArtikelException.class)) {
			final InvalidArtikelException orig = (InvalidArtikelException) e;
			messages.error(orig.getViolations(), null);
		}
		
		return null;
	}
	
	/**
	 *  Action Methode, um leeren Artikel zu erzeugen. Aufruf durch preRenderView
	 *  Keine eigene Transaktion, daher kein @Transactional
	 */
	public void createEmptyArtikel() {
		if (neuerArtikel != null) {
			return;
		}
		neuerArtikel = new Artikel();
	}
	
	/**
	 *  Action Methode, um eigentliche Artikelgruppe zu erzeugen bzw zu bef�llen
	 *  
	 */
	@Transactional
	public String createArtikelgruppe() {
		try {
			neueArtikelgruppe = as.createArtikelgruppe(neueArtikelgruppe, locale);
		}
		catch (InvalidArtikelgruppeException e) {
			final String outcome = createArtikelgruppeErrorMsg(e);
			return outcome;
		}

		// Push-Event fuer Webbrowser
		neueArtikelgruppeEvent.fire(String.valueOf(neueArtikelgruppe.getId()));
		
		// Aufbereitung fuer viewArtikelArtikelgruppe.xhtml
		artikelArtikelgruppe = neueArtikelgruppe.getBezeichnung();
		neueArtikelgruppe = null;  // zuruecksetzen
		
		return JSF_VIEW_ARTIKEL_ARTIKELGRUPPE + JSF_REDIRECT_SUFFIX;
	}
	
	private String createArtikelgruppeErrorMsg(AbstractShopException e) {
		final Class<? extends AbstractShopException> exceptionClass = e.getClass();

		if (exceptionClass.equals(InvalidArtikelException.class)) {
			final InvalidArtikelException orig = (InvalidArtikelException) e;
			messages.error(orig.getViolations(), null);
		}
		
		return null;
	}
	
	/**
	 *  Action Methode, um leere Artikelgruppe zu erzeugen. Aufruf durch preRenderView
	 *  Keine eigene Transaktion, daher kein @Transactional
	 */
	public void createEmptyArtikelgruppe() {
		if (neueArtikelgruppe != null) {
			return;
		}
		neueArtikelgruppe = new Artikelgruppe();
	}
	
	/**
	 * Verwendung als ValueChangeListener bei updateArtikel.xhtml
	 */
	public void geaendert(ValueChangeEvent e) {
		if (geaendertArtikel) {
			return;
		}
		
		if (e.getOldValue() == null) {
			if (e.getNewValue() != null) {
				geaendertArtikel = true;
			}
			return;
		}

		if (!e.getOldValue().equals(e.getNewValue())) {
			geaendertArtikel = true;				
		}
	}
	
	/**
	 *  Action Methode, um vorhandenen Artikel zu �ndern
	 */
	//@TransactionAttribute(REQUIRED)
	@Transactional
	public String update() {
		
		if (!geaendertArtikel || artikel == null) {
			return JSF_INDEX;
		}
		// Falls sich Artikelgruppe ge�ndert hat
		boolean artikelgruppeGeaendert = false;
		if((as.findeArtikelgruppeNachId(updateArtikelArtikelgruppeId) != null) && 
			(!artikel.getArtikelgruppe().getId().equals(updateArtikelArtikelgruppeId))) {	
				artikelgruppeGeaendert = true;
		}
		
		try {
			if(artikelgruppeGeaendert) {
				// Artikelgruppe anhand der vorgegebenen ID suchen und anschlie�end dem Artikel und umgekehrt zuweisen
				Artikelgruppe ag = as.findeArtikelgruppeNachId(updateArtikelArtikelgruppeId);
				artikel.setArtikelgruppe(ag);
				ag.addArtikel(artikel);
				artikel = as.updateArtikel(artikel, locale);
			}
			else
				artikel = as.updateArtikel(artikel, locale);
		}
		catch (InvalidArtikelException  | OptimisticLockException | ConcurrentDeletedException e) {
			final String outcome = updateArtikelErrorMsg(e);
			return outcome;
		}

		// Push-Event fuer Webbrowser
		updateArtikelEvent.fire(String.valueOf(artikel.getId()));
		
		// ValueChangeListener zuruecksetzen
		geaendertArtikel = false;
		
		// Aufbereitung fuer viewKunde.xhtml
		artikelId = artikel.getId();
		
		// Zur�cksetzen
		updateArtikelArtikelgruppeId = null;
		
		return JSF_VIEW_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}
	
	private String updateArtikelErrorMsg(RuntimeException e) {
		final Class<? extends RuntimeException> exceptionClass = e.getClass();

		if (exceptionClass.equals(InvalidArtikelException.class)) {
			final InvalidArtikelException orig = (InvalidArtikelException) e;
			messages.error(orig.getViolations(), null);
		}
		else if (exceptionClass.equals(OptimisticLockException.class)) {
			messages.error(ARTIKELVERWALTUNG, MSG_KEY_UPDATE_ARTIKEL_CONCURRENT_UPDATE, null);

		}
		else if (exceptionClass.equals(ConcurrentDeletedException.class)) {
			messages.error(ARTIKELVERWALTUNG, MSG_KEY_UPDATE_ARTIKEL_CONCURRENT_DELETE, null);
		}
		return null;
	}
	
	public String selectForUpdate(Artikel ausgewaehlterArtikel) {
		if (ausgewaehlterArtikel == null) {
			return null;
		}
		
		artikel = ausgewaehlterArtikel;
		
		return JSF_UPDATE_ARTIKEL;
	}
}
