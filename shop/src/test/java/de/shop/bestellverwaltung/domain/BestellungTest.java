/*package de.shop.bestellverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.domain.Artikelgruppe;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.FamilienstandTyp;
import de.shop.kundenverwaltung.domain.GeschlechtTyp;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.AbstractDomainTest;

@RunWith(Arquillian.class)
public class BestellungTest extends AbstractDomainTest {
	
	private static final Boolean OFFEN = true;
	
	private static final String NACHNAME = "Mustermann";
	private final Long iD = Long.valueOf(701);
	
	private static final String NACHNAME_NEU = "Test";
	private static final String VORNAME_NEU = "Theo";
	private static final String EMAIL_NEU = "theo@test.de";
	private static final float RABATT_NEU = 0.5f;
	private static final double UMSATZ_NEU = 10_001_000;
	private static final String PLZ_NEU = "11111";
	private static final String ORT_NEU = "Testort";
	private static final String STRASSE_NEU = "Testweg";
	private static final String HAUSNR_NEU = "1";
	private static final String ART_NEU = "p";
	private static final int TAG = 20;
	private static final int MONAT = Calendar.JANUARY;
	private static final int JAHR = 2001;
	private static final Date SEIT_NEU = new GregorianCalendar(JAHR, MONAT, TAG).getTime();
	private static final String BEZEICHNUNG_NEU = "V-Shirt";
	private static final Boolean ERHAELTLICH_NEU = true;
	private static final double PREIS_NEU = 38.99;
	private static final String NAME_NEU = "Testmode";
	private static final Boolean OFFEN_NEU = true;
	private static final short ANZAHL_NEU = 1;
	private static final String INLAND_NEU = "I";
	private static final String TRANSPORTART_NEU = "Schiff";
	private static final String LIEFERNUMMER_NEU = "4303";
	private static final String PASSWORT_NEU = "pwd";
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}

	@Test
	public void findeAlleOffenenBestellungen() {
		// Given
		final Boolean offen = OFFEN;

		// When
		TypedQuery<Bestellung> query = getEntityManager().createNamedQuery(Bestellung.FINDE_ALLE_OFFENEN_BESTELLUNGEN, 
																		Bestellung.class);
	 
		List<Bestellung> bestellungen = query.getResultList();

		// Then
		assertThat(bestellungen.isEmpty(), is(false));
	
		for (Bestellung b : bestellungen) {
			assertThat(b.getOffenAbgeschlossen(), is(offen));
		}
	}
	
	
	@Test
	public void findeBestellungenVonKunden() {
		// Given
		final String nachname = NACHNAME;

		// When
		TypedQuery<Bestellung> query = getEntityManager().createNamedQuery(
				    Bestellung.FINDE_BESTELLUNGEN_VON_KUNDEN_NACH_NACHNAME,
					Bestellung.class);
		query.setParameter(Bestellung.PARAM_NACHNAME, nachname);
	 
		List<Bestellung> bestellungen = query.getResultList();

		// Then
		assertThat(bestellungen.isEmpty(), is(false));
	
		for (Bestellung b : bestellungen) {
			assertThat(b.getKunde().getNachname(), is(nachname));
		}
	}
	
 
	
	@Test
	public void findeBestellungNachLieferungen() {
		// Given
		final Long id = iD;

		// When
		TypedQuery<Bestellung> query = getEntityManager().createNamedQuery(
			Bestellung.FINDE_BESTELLUNG_NACH_LIEFERUNG_ID, Bestellung.class);
		query.setParameter(Bestellung.PARAM_ID, id);
		List<Bestellung> bestellungen = query.getResultList();
	 
		// Then
		assertThat(bestellungen.isEmpty(), is(false));
	
		for (Bestellung b : bestellungen) {
			for (Lieferung l : b.getLieferungen()) {
				assertThat(l.getId(), is(id));	
			}
		}
	}

	@Test
	public void createBestellung() {
		// Given
		final Bestellung bestellung = new Bestellung();
		bestellung.setOffenAbgeschlossen(OFFEN_NEU); 
		bestellung.setErzeugt(new GregorianCalendar().getTime());
		bestellung.setAktualisiert(new GregorianCalendar().getTime());
		
		
		
		final Kunde kunde = new Kunde();
		kunde.setArt(ART_NEU);
		kunde.setEmail(EMAIL_NEU);
		kunde.setNachname(NACHNAME_NEU);
		kunde.setRabatt(RABATT_NEU);
		kunde.setSeit(SEIT_NEU);
		kunde.setUmsatz(UMSATZ_NEU);
		kunde.setVorname(VORNAME_NEU);
		kunde.setPasswort(PASSWORT_NEU);
		kunde.setErzeugt(new GregorianCalendar().getTime());
		kunde.setAktualisiert(new GregorianCalendar().getTime());
		
		
		final Adresse adresse = new Adresse();
		adresse.setPlz(PLZ_NEU);
		adresse.setOrt(ORT_NEU);
		adresse.setStrasse(STRASSE_NEU);
		adresse.setHausnr(HAUSNR_NEU);
		adresse.setErzeugt(new GregorianCalendar().getTime());
		adresse.setAktualisiert(new GregorianCalendar().getTime());
		kunde.setAdresse(adresse);
		bestellung.setKunde(kunde);
		kunde.addBestellung(bestellung);
		
		final Bestellposition bestellposition = new Bestellposition();
		bestellposition.setAnzahl(ANZAHL_NEU);
		
		final Artikel artikel = new Artikel();
		artikel.setBezeichnung(BEZEICHNUNG_NEU);
		artikel.setErhaeltlich(ERHAELTLICH_NEU);
		artikel.setPreis(PREIS_NEU);
		artikel.setErzeugt(new GregorianCalendar().getTime());
		artikel.setAktualisiert(new GregorianCalendar().getTime());

		final Artikelgruppe ag = new Artikelgruppe();
		ag.setName(NAME_NEU);
		ag.addArtikel(artikel);
		artikel.setArtikelgruppe(ag);
		
		bestellposition.setArtikel(artikel);
		bestellung.addBestellposition(bestellposition);
		
		final Lieferung lieferung = new Lieferung();
		lieferung.setInlandOderAusland(INLAND_NEU);
		lieferung.setLiefernr(LIEFERNUMMER_NEU);
		lieferung.setTransportArt(TRANSPORTART_NEU);
		lieferung.addBestellung(bestellung);
		lieferung.setErzeugt(new GregorianCalendar().getTime());
		lieferung.setAktualisiert(new GregorianCalendar().getTime());
		
		
		 	
			
		kunde.setNewsletter(true);
		kunde.setGeschlecht(GeschlechtTyp.WEIBLICH);
		kunde.setFamilienstand(FamilienstandTyp.VERHEIRATET);
		 
		// When
		try {
			getEntityManager().persist(kunde);     
			getEntityManager().persist(ag);
			getEntityManager().persist(artikel);
			getEntityManager().persist(bestellung);
			getEntityManager().persist(lieferung);
		}
		catch (ConstraintViolationException e) {
			final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
			for (ConstraintViolation<?> v : violations) {
				System.err.println("!!! FEHLERMELDUNG>>> " + v.getMessage());
				System.err.println("!!! ATTRIBUT>>> " + v.getPropertyPath());
				System.err.println("!!! ATTRIBUTWERT>>> " + v.getInvalidValue());
			}
			
			throw new RuntimeException(e);
		}
		
		// Then
		TypedQuery<Bestellung> query = getEntityManager().createNamedQuery(
				Bestellung.FINDE_BESTELLUNGEN_VON_KUNDEN_NACH_NACHNAME,
				Bestellung.class);
		query.setParameter(Bestellung.PARAM_NACHNAME, NACHNAME_NEU);
		 
		List<Bestellung> bestellungen = query.getResultList();
		
		assertThat(bestellungen.isEmpty(), is(false));
		
		for (Bestellung b : bestellungen) {
			assertThat(b.getKunde().getNachname(), is(NACHNAME_NEU));
		}
	}
}
*/