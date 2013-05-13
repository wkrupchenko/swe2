package de.shop.artikelverwaltung.controller;

import static javax.ejb.TransactionAttributeType.REQUIRED;
import static de.shop.util.Messages.MessagesType.ARTIKELVERWALTUNG;

import java.io.Serializable;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.Log;
import de.shop.util.Messages;
import de.shop.util.Transactional;


/**
 * Dialogsteuerung fuer die Artikelverwaltung
 */
@Named("ac")
@RequestScoped
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
	
	private static final String MSG_KEY_ARTIKEL_NOT_FOUND_BY_BEZEICHNUNG = "viewArtikelBezeichnung.notFound";
	private static final String CLIENT_ID_ARTIKEL_BEZEICHNUNG = "form:bezeichnung";
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private Flash flash;
	
	@Inject
	private Messages messages;
	
	private Long artikelId;
	private String artikelArtikelgruppe;
	private String artikelBezeichnung;
	private Boolean artikelErhaeltlich;
	private double artikelPreis;
	private List<Artikel> ladenhueter;

	@Override
	public String toString() {
		return "ArtikelController [artikelId=" + artikelId + "]";
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
		if (artikel.isEmpty()) {
			flash.remove(FLASH_ARTIKEL);
			return null;
		}
		
		flash.put(FLASH_ARTIKEL, artikel);
		return JSF_VIEW_ARTIKEL_BEZEICHNUNG;
	}
	
	/**
	 * Für rich:autocomplete um potentielle Bezeichnungen zu Prefix zu erhalten
	 * @return Liste der potenziellen Bezeichnungen
	 */
	@TransactionAttribute(REQUIRED)
	public List<String> findeBezeichnungenNachPrefix(String artikelBezeichnungPrefix) {
		// NICHT: Liste von Kunden. Sonst waeren gleiche Nachnamen mehrfach vorhanden.
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
	 *  Action Methode, um Ladenhüter für Startsiete zu laden
	 * 
	 */
	@Transactional
	public void loadLadenhueter() {
		ladenhueter = as.ladenhueter(ANZAHL_LADENHUETER);
	}
}
