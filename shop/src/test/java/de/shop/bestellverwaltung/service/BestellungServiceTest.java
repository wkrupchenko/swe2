/*package de.shop.bestellverwaltung.service;


import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Lieferung;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.AbstractTest;


@RunWith(Arquillian.class)
public class BestellungServiceTest extends AbstractTest {
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(101);
	private static final String NAME = "Mustermann";
	
	private static final Long ARTIKEL_1_ID = Long.valueOf(500);
	private static final short ARTIKEL_1_ANZAHL = 1;
	private static final Long ARTIKEL_2_ID = Long.valueOf(501);
	private static final short ARTIKEL_2_ANZAHL = 2;
	
	private static final String LIEFERNR_VORHANDEN = "500123-004";
	private static final String LIEFERNR_ZU_ALT = "1888%";
	private static final Date LIEFERUNG_ERZEUGT = new GregorianCalendar().getTime();
	private static final String NEUE_LIEFERNR = "20100101-001";
	private static final String NEUE_LIEFERNR2 = "20100101-002";
	private static final String SCHIENE = "Schiene";
	private static final String STRASSE = "Strasse";
	
	private static final Long BESTELLUNG_ID_VORHANDEN = Long.valueOf(300);
	private static final Long BESTELLUNG_ID2A_VORHANDEN = Long.valueOf(301);
	private static final Long BESTELLUNG_ID2B_VORHANDEN = Long.valueOf(302);
	private static final Date BESTELLUNG_ERZEUGT = new GregorianCalendar().getTime();
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private ArtikelService as;
	
	
	@Test
	public void createBestellung() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                      SystemException, NotSupportedException {
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		final Long artikel1Id = ARTIKEL_1_ID;
		final short artikel1Anzahl = ARTIKEL_1_ANZAHL;
		final Long artikel2Id = ARTIKEL_2_ID;
		final short artikel2Anzahl = ARTIKEL_2_ANZAHL;
		final Date erzeugt = BESTELLUNG_ERZEUGT;
		final Date aktualisiert = BESTELLUNG_ERZEUGT;

		// When
		Artikel artikel = as.findeArtikelNachId(artikel1Id);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		Bestellung bestellung = new Bestellung();
		bestellung.setErzeugt(erzeugt);
		bestellung.setAktualisiert(aktualisiert);
		Bestellposition bpos = new Bestellposition();
		bpos.setArtikel(artikel);		
		bpos.setAnzahl(artikel1Anzahl);
				
		trans.begin();
		artikel = as.findeArtikelNachId(artikel2Id);
		trans.commit();
		
		bpos = new Bestellposition();
		bpos.setArtikel(artikel);		
		bpos.setAnzahl(artikel2Anzahl);
		bestellung.addBestellposition(bpos);

		trans.begin();
		Kunde kunde = ks.findeKundeNachId(kundeId, FetchType.MIT_BESTELLUNGEN, LOCALE);
		trans.commit();

		trans.begin();
		bestellung = bs.createBestellung(bestellung, kunde, LOCALE);
		trans.commit();
		
		// Then
		assertThat(bestellung.getBestellpositionen().size(), is(1));
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			assertThat(bp.getArtikel().getId(), anyOf(is(artikel1Id), is(artikel2Id)));
		}
			
		kunde = bestellung.getKunde();
		assertThat(kunde.getId(), is(kundeId));
	}
	
	@Test
	public void findLieferungVorhanden() {
		// Given
		final String lieferNr = LIEFERNR_VORHANDEN;
		
		// When
		final Collection<Lieferung> lieferungen = bs.findeLieferungen(lieferNr);
		
		// Then
		assertThat(lieferungen.isEmpty(), is(false));
		final String lieferNrPraefix = lieferNr.substring(0, lieferNr.length() - 2);  // '%' ausblenden
		for (Lieferung l : lieferungen) {
			assertThat(l.getLiefernr().startsWith(lieferNrPraefix), is(true));
	
			final Collection<Bestellung> bestellungen = l.getBestellungen();
			assertThat(bestellungen.isEmpty(), is(false));
	
			for (Bestellung b : bestellungen) {
				assertThat(b.getKunde(), is(notNullValue()));
			}
		}
	}

	@Test
	public void findLieferungNichtVorhanden() {
		// Given
		final String lieferNr = LIEFERNR_ZU_ALT;

		// When
		final List<Lieferung> lieferungen = bs.findeLieferungen(lieferNr);
		
		// Then
		assertThat(lieferungen.isEmpty(), is(true));
	}
	
	@Test
	public void findeBestellungOffen() {
		// Given
		
		// When 
		final List<Bestellung> bestellungen = bs.findeBestellungenOffen();
		
		// Then
		assertThat(bestellungen.isEmpty(), is(false));
		for (Bestellung b: bestellungen) {
			assertThat(b.getOffenAbgeschlossen(), is(true));
		}
	}
	
	@Test
	public void findeBestellungGeschlossen() {
		// Given
		
		// When 
		final List<Bestellung> bestellungen = bs.findeBestellungenGeschlossen();
		
		// Then
		assertThat(bestellungen.isEmpty(), is(false));
		for (Bestellung b: bestellungen) {
			assertThat(b.getOffenAbgeschlossen(), is(false));
		}
	}
	
	@Test
	public void findeBestellungVonKunden() {
		// Given
		final String name = NAME;
		final List<Kunde> kunden = ks.findeKundeNachNachname(name, KundeService.FetchType.NUR_KUNDE, LOCALE);
		final List<Long> ids = new ArrayList<Long>();
		for (Kunde k: kunden) {
			ids.add(k.getId());
		}
		
		// When
		final List<Bestellung> bestellungen = new ArrayList<Bestellung>();
		for (Long id: ids) {
			final List<Bestellung> bestellungenTemp = bs.findeBestellungenNachKundeId(id);
			bestellungen.addAll(bestellungenTemp);
		}
		
		// Then
		assertThat(bestellungen.isEmpty(), is(false));
		for (Bestellung b: bestellungen) {
			assertThat(b.getKunde().getNachname(), is(name));
		}
	}
	
	@Test
	public void findeBestellungenVonLieferung() {
		// Given
		final String liefernr = LIEFERNR_VORHANDEN;
		
		//When
		final List<Bestellung> bestellungen = bs.findeBestellungenNachLieferungLiefernr(liefernr);
		
		// Then
		assertThat(bestellungen.isEmpty(), is(false));
	}
	
	@Test
	public void createLieferung() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                     SystemException, NotSupportedException {
		// Given
		final Long bestellungId = BESTELLUNG_ID_VORHANDEN;
		final String neueLiefernr = NEUE_LIEFERNR;
		final String transportArt = STRASSE;
		final Date erzeugt = LIEFERUNG_ERZEUGT;
		
		// When
		Lieferung neueLieferung = new Lieferung();
		neueLieferung.setTransportArt(transportArt);
		neueLieferung.setLiefernr(neueLiefernr);
		neueLieferung.setErzeugt(erzeugt);
		neueLieferung.setAktualisiert(erzeugt);
		
		final List<Bestellung> bestellungen = new ArrayList<Bestellung>();
		final Bestellung bestellung = bs.findeBestellungNachId(bestellungId);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		assertThat(bestellung.getId(), is(bestellungId));
		bestellungen.add(bestellung);
		
		trans.begin();
		neueLieferung = bs.createLieferung(neueLieferung, bestellungen);
		trans.commit();
		
		// Then
		assertThat(neueLieferung.getId(), is(notNullValue()));
		assertThat(neueLieferung.getLiefernr(), is(neueLiefernr));
		assertThat(neueLieferung.getBestellungen().size(), is(1));
		assertThat(neueLieferung.getBestellungen().iterator().next().getId(), is(bestellungId));
	}
	
	@Test
	public void createLieferung2() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                      SystemException, NotSupportedException {
		// Given
		final Long bestellungId2a = BESTELLUNG_ID2A_VORHANDEN;
		final Long bestellungId2b = BESTELLUNG_ID2B_VORHANDEN;
		final String neueLiefernr = NEUE_LIEFERNR2;
		final String transportArt = SCHIENE;
		final Date erzeugt = LIEFERUNG_ERZEUGT;
		
		// When
		Lieferung neueLieferung = new Lieferung();
		neueLieferung.setTransportArt(transportArt);
		neueLieferung.setLiefernr(neueLiefernr);
		neueLieferung.setErzeugt(erzeugt);
		neueLieferung.setAktualisiert(erzeugt);
		
		final List<Bestellung> bestellungen = new ArrayList<Bestellung>();
		Bestellung bestellung = bs.findeBestellungNachId(bestellungId2a);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		assertThat(bestellung.getId(), is(bestellungId2a));
		bestellungen.add(bestellung);
		
		trans.begin();
		bestellung = bs.findeBestellungNachId(bestellungId2b);
		trans.commit();
		
		assertThat(bestellung.getId(), is(bestellungId2b));
		bestellungen.add(bestellung);
		
		trans.begin();
		neueLieferung = bs.createLieferung(neueLieferung, bestellungen);
		trans.commit();
		
		// Then
		assertThat(neueLieferung.getId(), is(notNullValue()));
		assertThat(neueLieferung.getLiefernr(), is(neueLiefernr));
		
		assertThat(neueLieferung.getBestellungen().size(), is(2));
		for (Bestellung b : neueLieferung.getBestellungen()) {
			assertThat(b.getId(), anyOf(is(bestellungId2a), is(bestellungId2b)));
		}
	}
}
*/