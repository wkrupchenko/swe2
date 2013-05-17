package de.shop.bestellverwaltung.controller;

import static de.shop.util.Konstante.KEINE_ID;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;

import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.service.BestellungService;
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
	
	private static final String FLASH_BESTELLUNG = "bestellung";
	private static final String JSF_VIEW_BESTELLUNG = "/bestellverwaltung/viewBestellung";
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private Flash flash;
	
	private Long bestellungId;

	@Override
	public String toString() {
		return "BestellungController [bestellungId=" + bestellungId + "]";
	}

	public void setBestellungId(Long bestellungId) {
		this.bestellungId = bestellungId;
	}

	public Long getBestellungId() {
		return bestellungId;
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
	
	 
	
	
}
