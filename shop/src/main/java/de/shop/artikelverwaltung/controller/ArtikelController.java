package de.shop.artikelverwaltung.controller;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.Log;
import de.shop.util.Transactional;


/**
 * Dialogsteuerung fuer die Artikelverwaltung
 */
@Named("ac")
@RequestScoped
@Log
public class ArtikelController implements Serializable {
	private static final long serialVersionUID = -3244325907963L;
	
	private static final String FLASH_ARTIKEL = "artikel";
	private static final String JSF_VIEW_ARTIKEL = "/artikelverwaltung/viewArtikel";
	private static final String JSF_VIEW_ARTIKEL_BEZEICHNUNG = "/artikelverwaltung/viewArtikelBezeichnung";
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private Flash flash;
	
	private Long artikelId;
	private String artikelBezeichnung;

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
	
	
	public void setArtikelBezeichnung(String artikelBezeichnung) {
		this.artikelBezeichnung = artikelBezeichnung;
	}

	public String getArtikelBezeichnung() {
		return artikelBezeichnung;
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
	 * Action Methode, um einen Artikel zu gegebener Bezeichnung zu suchen
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
}
