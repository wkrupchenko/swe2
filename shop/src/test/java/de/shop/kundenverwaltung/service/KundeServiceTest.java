package de.shop.kundenverwaltung.service;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.AbstractTest;


@RunWith(Arquillian.class)
public class KundeServiceTest extends AbstractTest {
	private static final String KUNDE_NACHNAME_VORHANDEN = "Mustermann";
	private static final Long ID_PREFIX_VORHANDEN = 1L;
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(101);
	private static final Long KUNDE_ID_NICHT_VORHANDEN = Long.valueOf(1000);
	private static final Long KUNDE_ID_OHNE_BESTELLUNGEN = Long.valueOf(102);
	private static final String NACHNAME_PREFIX_VORHANDEN = "G";
	private static final String KUNDE_NACHNAME_NICHT_VORHANDEN = "Beta";
	private static final String KUNDE_NACHNAME_UNGUELTIG = "!§$%&/";
	private static final String KUNDE_EMAIL_NICHT_VORHANDEN = "nicht.vorhanden@nicht.vorhanden.de";
	private static final int TAG = 9;
	private static final int MONAT = Calendar.JULY;
	private static final int JAHR = 2010;
	private static final Date SEIT_VORHANDEN = new GregorianCalendar(JAHR, MONAT, TAG).getTime();
	private static final String PLZ_VORHANDEN = "76133";
	private static final String PLZ_NICHT_VORHANDEN = "111";
	private static final String KUNDE_NEU_NACHNAME = "Alphaneu";
	private static final String KUNDE_NEU_VORNAME = "Alphatier";
	private static final String KUNDE_NEU_NACHNAME2 = "Alphanew";
	private static final String KUNDE_NEU_EMAIL = "neu.test@hska.de";
	private static final String KUNDE_NEU_EMAIL2 = "new.test@hska.de";
	private static final String PLZ_NEU = "76133";
	private static final String ORT_NEU = "Karlsruhe";
	private static final String STRASSE_NEU = "Moltkestra\u00DFe";
	private static final String HAUSNR_NEU = "40";
	private static final String PASSWORD_NEU = "testpassword";
	private static final String PASSWORDWDH_FALSCH = "testpasswort";
	private static final Date ADRESSE_ERZEUGT = new GregorianCalendar().getTime();
	private static final Date KUNDE_ERZEUGT = new GregorianCalendar().getTime();
	private static final String PASSWORTWDH = "evam";
	
	
	@Inject
	private KundeService ks;
	
	@Test
	public void findeKundeMitNachnameVorhanden() {
		// Given
		final String nachname = KUNDE_NACHNAME_VORHANDEN;

		// When
		final Collection<Kunde> kunden = ks.findeKundeNachNachname(nachname, FetchType.NUR_KUNDE, LOCALE);
		
		// Then
		assertThat(kunden, is(notNullValue()));
		assertThat(kunden.isEmpty(), is(false));
		
		for (Kunde k : kunden) {
			assertThat(k.getNachname(), is(nachname));
			assertThat(k.getArt(), either(is("p")).or(is("f")));
			
			assertThat(k.getNachname(), both(is(notNullValue())).and(is((Object) nachname)));
			
			assertThat(k.getNachname(), allOf(is(notNullValue()), is(nachname))); 
		} 
	}
	
	@Test
	public void findeKundeMitNachnameNichtVorhanden() {
		// Given
		final String nachname = KUNDE_NACHNAME_NICHT_VORHANDEN;

		// When
		final List<Kunde> kunden = ks.findeKundeNachNachname(nachname, FetchType.NUR_KUNDE, LOCALE);
		
		// Then
		assertThat(kunden.isEmpty(), is(true));
	}
	
	@Test
	public void findeKundeMitNachnameUngueltig() {
		// Given
		final String nachname = KUNDE_NACHNAME_UNGUELTIG;

		// When / Then
		thrown.expect(InvalidNachnameException.class);
		ks.findeKundeNachNachname(nachname, FetchType.NUR_KUNDE, LOCALE);
	}
	
	@Test
	public void findeNachnamenNachPrefix() {
		// Given
		final String nachname = NACHNAME_PREFIX_VORHANDEN;

		// When / Then
		thrown.expect(InvalidNachnameException.class);
		ks.findeKundeNachNachname(nachname, FetchType.NUR_KUNDE, LOCALE);
	}
	
	@Test
	public void findeKundeMitIdNichtVorhanden() {
		// Given
		final Long id = KUNDE_ID_NICHT_VORHANDEN;

		// When
		final Kunde kunde = ks.findeKundeNachId(id, FetchType.NUR_KUNDE, LOCALE);
		
		// Then
		assertThat(kunde, is(nullValue()));
	}
	
	@Test
	public void findeIdsNachPrefix() {
		// Given
		final Long id = ID_PREFIX_VORHANDEN;

		// When
		final Kunde kunde = ks.findeKundeNachId(id, FetchType.NUR_KUNDE, LOCALE);
		
		// Then
		assertThat(kunde, is(nullValue()));
	}
	
	@Test
	public void findeKundeMitPLZVorhanden() {
		// Given
		final String plz = PLZ_VORHANDEN;

		// When
		final Collection<Kunde> kunden = ks.findeKundeNachPlz(plz);

		// Then
		assertThat(kunden, is(notNullValue()));
		assertThat(kunden.isEmpty(), is(false));
		
		for (Kunde k : kunden) {
			assertThat(k.getAdresse(), is(notNullValue()));
			assertThat(k.getAdresse().getPlz(), is(plz));
		}
	}
	
	@Test
	public void findeKundeMitPLZNichtVorhanden() {
		// Given
		final String plz = PLZ_NICHT_VORHANDEN;
		
		// When
		final List<Kunde> kunden = ks.findeKundeNachPlz(plz);
		
		// Then
		assertThat(kunden.isEmpty(), is(true));	}
	
	@Test
	public void findeKundeMitMinBestMenge() {
		// Given
		final short minMenge = 2;
		
		// When
		final Collection<Kunde> kunden = ks.findeKundeMitMinBestMenge(minMenge);
		
		// Then
		for (Kunde k : kunden) {
			final Kunde kundeMitBestellungen = ks.findeKundeNachId(k.getId(), FetchType.MIT_BESTELLUNGEN, LOCALE);
			final List<Bestellung> bestellungen = kundeMitBestellungen.getBestellungen();
			int bestellmenge = 0;   // short-Werte werden aufsummiert
			for (Bestellung b : bestellungen) {
				if (b.getBestellpositionen() == null) {
					fail("Bestellung " + b + " ohne Bestellpositionen");
				}
				for (Bestellposition bp : b.getBestellpositionen()) {
					bestellmenge += bp.getAnzahl();
				}
			}
			
			assertTrue(bestellmenge >= minMenge);
		}
	}

	@Test
	public void findePrivatkundenFirmenkunden() {
		// Given
		
		// When
		final List<Kunde> kunden = ks.findePrivatkundenFirmenkunden();
		
		// Then
		for (Kunde k : kunden) {
			assertThat(k.getArt(), either(is("P")).or(is("F")));
		}
	} 
	
	@Test
	public void createPrivatkunde() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                       SystemException, NotSupportedException {
		// Given
		final String nachname = KUNDE_NEU_NACHNAME;
		final String vorname = KUNDE_NEU_VORNAME;
		final String email = KUNDE_NEU_EMAIL;
		final Date seit = SEIT_VORHANDEN;
		final String plz = PLZ_NEU;
		final String ort = ORT_NEU;
		final String strasse = STRASSE_NEU;
		final String hausnr = HAUSNR_NEU;
		final String password = PASSWORD_NEU;
		final Date erzeugt = ADRESSE_ERZEUGT;
		final Date erzeugt2 = KUNDE_ERZEUGT;

		// When
		final Collection<Kunde> kundenVorher = ks.findeAlleKunden(FetchType.NUR_KUNDE, null);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		Kunde neuerPrivatkunde = new Kunde();
		neuerPrivatkunde.setNachname(nachname);
		neuerPrivatkunde.setVorname(vorname);
		neuerPrivatkunde.setEmail(email);
		neuerPrivatkunde.setSeit(seit);
		neuerPrivatkunde.setArt("P");
		final Adresse adresse = new Adresse();
		adresse.setPlz(plz);
		adresse.setOrt(ort);
		adresse.setStrasse(strasse);
		adresse.setHausnr(hausnr);
		adresse.setErzeugt(erzeugt2);
		adresse.setAktualisiert(erzeugt2);
		neuerPrivatkunde.setAdresse(adresse);
		neuerPrivatkunde.setPasswort(password);
		neuerPrivatkunde.setPasswortWdh(password);
		neuerPrivatkunde.setAdresse(adresse);
		neuerPrivatkunde.setErzeugt(erzeugt);
		neuerPrivatkunde.setAktualisiert(erzeugt);
		
		trans.begin();
		Kunde neuerKunde = ks.createKunde(neuerPrivatkunde, LOCALE);
		trans.commit();
		
		// Then

		trans.begin();
		final Collection<Kunde> kundenNachher = ks.findeAlleKunden(FetchType.NUR_KUNDE, null);
		trans.commit();
		
		assertThat(kundenVorher.size() + 1, is(kundenNachher.size()));
		for (Kunde k : kundenVorher) {
			assertTrue(k.getId() < neuerKunde.getId());
			assertTrue(k.getErzeugt().getTime() < neuerKunde.getErzeugt().getTime());
		}
		
		trans.begin();
		neuerKunde = ks.findeKundeNachId(neuerKunde.getId(), FetchType.NUR_KUNDE, LOCALE);
		trans.commit();
		
		assertThat(neuerKunde.getNachname(), is(nachname));
		assertThat(neuerKunde.getEmail(), is(email));
		assertThat(neuerKunde.getArt(), is("P"));
		neuerPrivatkunde = (Kunde) neuerKunde;
	}
	
	@Test
	public void createDuplikatPrivatkunde() throws RollbackException, HeuristicMixedException,
	                                               HeuristicRollbackException, SystemException,
	                                               NotSupportedException {
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		
		final Kunde k = ks.findeKundeNachId(kundeId, FetchType.NUR_KUNDE, LOCALE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		assertThat(k, is(notNullValue()));
		assertThat(k, is(instanceOf(Kunde.class)));
		
		// When
		final Kunde neuerKunde = new Kunde();
		neuerKunde.setNachname(k.getNachname());
		neuerKunde.setVorname(k.getVorname());
		neuerKunde.setSeit(k.getSeit());
		neuerKunde.setEmail(k.getEmail());
		neuerKunde.setAdresse(k.getAdresse());
		
		// Then
		thrown.expect(EmailExistsException.class);
		trans.begin();
		ks.createKunde(neuerKunde, LOCALE);
	}
	
	@Test
	public void createKundeOhneAdresse() {
		// Given
		final String nachname = KUNDE_NACHNAME_NICHT_VORHANDEN;
		final String email = KUNDE_EMAIL_NICHT_VORHANDEN;
		
		// When
		final Kunde neuerKunde = new Kunde();
		neuerKunde.setNachname(nachname);
		neuerKunde.setEmail(email);
		neuerKunde.setAdresse(null);
		
		// Then
		thrown.expect(KundeValidationException.class);
		thrown.expectMessage("Ungueltiger Kunde:");
		ks.createKunde(neuerKunde, LOCALE);
	}
	
	@Test
	public void createKundeFalschesPassword() {
		// Given
		final String nachname = KUNDE_NEU_NACHNAME2;
		final String email = KUNDE_NEU_EMAIL2;
		final Date seit = SEIT_VORHANDEN;
		final String plz = PLZ_NEU;
		final String ort = ORT_NEU;
		final String strasse = STRASSE_NEU;
		final String hausnr = HAUSNR_NEU;
		final String password = PASSWORD_NEU;
		final String passwordWdh = PASSWORDWDH_FALSCH;

		// When
		Kunde neuerPrivatkunde = new Kunde();
		neuerPrivatkunde.setNachname(nachname);
		neuerPrivatkunde.setEmail(email);
		neuerPrivatkunde.setSeit(seit);
		final Adresse adresse = new Adresse();
		adresse.setPlz(plz);
		adresse.setOrt(ort);
		adresse.setStrasse(strasse);
		adresse.setHausnr(hausnr);
		neuerPrivatkunde.setAdresse(adresse);
		neuerPrivatkunde.setPasswort(password);
		neuerPrivatkunde.setPasswortWdh(passwordWdh);

		// Then
		thrown.expect(KundeValidationException.class);
		thrown.expectMessage("kundenverwaltung.kunde.passwort.notEqual");
		ks.createKunde(neuerPrivatkunde, LOCALE);
	}
	
	@Test
	public void deleteKunde() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                 SystemException, NotSupportedException {
		// Given
		final Long kundeId = KUNDE_ID_OHNE_BESTELLUNGEN;

		final Collection<Kunde> kundenVorher = ks.findeAlleKunden(FetchType.NUR_KUNDE, null);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
	
		// When
		trans.begin();
		final Kunde kunde = ks.findeKundeNachId(kundeId, FetchType.MIT_BESTELLUNGEN, LOCALE);
		trans.commit();
		trans.begin();
		ks.deleteKunde(kunde);
		trans.commit();
	
		// Then
		trans.begin();
		final Collection<Kunde> kundenNachher = ks.findeAlleKunden(FetchType.NUR_KUNDE, null);
		trans.commit();
		assertThat(kundenVorher.size() - 1, is(kundenNachher.size()));
	}

	@Test
	public void neuerNameFuerKunde() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                        SystemException, NotSupportedException {
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;

		// When
		Kunde kunde = ks.findeKundeNachId(kundeId, FetchType.NUR_KUNDE, LOCALE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		final String alterNachname = kunde.getNachname();
		final String neuerNachname = alterNachname + alterNachname.charAt(alterNachname.length() - 1);
		kunde.setNachname(neuerNachname);
		kunde.setPasswortWdh(PASSWORTWDH);
	
		trans.begin();
		kunde = ks.updateKunde(kunde, LOCALE);
		trans.commit();
		
		// Then
		assertThat(kunde.getNachname(), is(neuerNachname));
		trans.begin();
		kunde = ks.findeKundeNachId(kundeId, FetchType.NUR_KUNDE, LOCALE);
		trans.commit();
		assertThat(kunde.getNachname(), is(neuerNachname));
	}
}