package de.shop.bestellverwaltung.controller;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Lieferung;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
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
	private static final String FLASH_LIEFERUNG = "lieferung";
	private static final String FLASH_KUNDE = "kunde";	
	private static final String JSF_VIEW_BESTELLUNG = "/bestellverwaltung/viewBestellung";
	private static final String JSF_BESTELLUNG_STATUS = "/bestellverwaltung/viewBestellungenStatus";
	private static final String JSF_VIEW_LIEFUNGEN = "/bestellverwaltung/viewLieferungenBestid";
	private static final String JSF_VIEW_KUNDE = "/bestellverwaltung/viewKundeBestId";
	private Boolean bestellungStatus;
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private KundeService ks;
	
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
	
	public void setBestellungStatus(Boolean bestellungStatus) {
		this.bestellungStatus = bestellungStatus;
	}

	public Boolean getBestellungStatus() {
		return bestellungStatus;
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
	public String findeBestellungNachStatus() {
		if(bestellungStatus) {
			final List<Bestellung> bestellung = bs.findeBestellungenGeschlossen();
			 if (bestellung.isEmpty()) {
				flash.remove(FLASH_BESTELLUNG);
				return null;
			}
			
		
			flash.put(FLASH_BESTELLUNG, bestellung);
			return JSF_BESTELLUNG_STATUS;
		}
		else {
			final List<Bestellung> bestellung = bs.findeBestellungenOffen();
			 if (bestellung.isEmpty()) {
				flash.remove(FLASH_BESTELLUNG);
				return null;
			}
			
			flash.put(FLASH_BESTELLUNG, bestellung);
			return JSF_BESTELLUNG_STATUS;
		}
	}
	
	@Transactional
	public String findeLieferungenNachBestellungId() {
		final List<Lieferung> lieferungen = bs.findeLieferungenNachBestellungId(bestellungId);
		if (lieferungen == null) {
			flash.remove(FLASH_LIEFERUNG);
			return null;
		}
						
		flash.put(FLASH_LIEFERUNG, lieferungen);
		return JSF_VIEW_LIEFUNGEN;
	}
		 	
		@Transactional
		public String findeKundeNachBestellungId() {
		final Kunde kunde = ks.findeKundeNachBestellung(bestellungId);
		if (kunde == null) {
			flash.remove(FLASH_KUNDE);
			return null;
		}
						
		flash.put(FLASH_KUNDE, kunde);
		return JSF_VIEW_KUNDE;
	}
	
	
	 
	
	
}
