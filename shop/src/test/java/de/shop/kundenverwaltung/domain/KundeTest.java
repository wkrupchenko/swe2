package de.shop.kundenverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.util.AbstractDomainTest;

@RunWith(Arquillian.class)
public class KundeTest extends AbstractDomainTest {
	private static final Long ID = Long.valueOf(100);
	private static final String ID_PREFIX = "1";
	private static final String NACHNAME = "Mustermann";
	private static final String EMAIL = "mm@web.de";
	private static final int TAG2 = 9;
	private static final int MONAT2 = Calendar.JULY;
	private static final int JAHR2 = 2010;
	private static final Date DATUM = new GregorianCalendar(JAHR2, MONAT2, TAG2).getTime();
	private static final String PLZ = "76133";
    private static final String ART = "p";
    private static final double UMSATZ = 50;
    
   
	private static final int TAG = 31;
	private static final int MONAT = Calendar.JANUARY;
	private static final int JAHR = 2001;
	private static final Date SEIT_VORHANDEN = new GregorianCalendar(JAHR, MONAT, TAG).getTime();  
	private static final String NACHNAME_PREFIX = "G";
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
	private static final String PASSWORT_NEU = "pwd";
    
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findKundeNachId() {
		// Given
		final Long id = ID;
		
		// When
		final Kunde kunde = getEntityManager().find(Kunde.class, id);
		
		// Then
		assertThat(kunde.getId(), is(id));
	}
	
	@Test
	public void findeKundeNachEmail() {
		// Given
		final String email = EMAIL;
		
		// When
		final TypedQuery<Kunde> query = getEntityManager().createNamedQuery(Kunde.FINDE_KUNDE_NACH_EMAIL,
				                                                            Kunde.class);
		query.setParameter(Kunde.PARAM_EMAIL, email);
		Kunde kunde = query.getSingleResult();
		
		// Then
		assertThat(kunde.getEmail(), is(email));
	}
	
	@Test
	public void findeKundeNachNachname() {
		// Given
		final String nachname = NACHNAME;
		
		// When
		final TypedQuery<Kunde> query = getEntityManager().createNamedQuery(Kunde.FINDE_KUNDE_NACH_NACHNAME,
                                                                            Kunde.class);
		query.setParameter(Kunde.PARAM_NACHNAME, nachname);
		final List<Kunde> kunden = query.getResultList();
		
		// Then
		assertThat(kunden.isEmpty(), is(false));
		for (Kunde k : kunden) {
			assertThat(k.getNachname(), is(nachname));
		}
	}
	
	@Test
	public void findeIdsNachPrefix() {
		// Given
		final String idPrefix = ID_PREFIX;
		
		// When
		final TypedQuery<Long> query = getEntityManager().createNamedQuery(Kunde.FINDE_IDS_NACH_PREFIX,
                                                                            Long.class);
		query.setParameter(Kunde.PARAM_KUNDE_ID_PREFIX, idPrefix + '%');
		final List<Long> kunden = query.getResultList();
		
		// Then
		assertThat(kunden.isEmpty(), is(false));
		for (Long k : kunden) {
			assertThat(k.toString().substring(0, 1), is(idPrefix.toString()));
		}
	}
	
	@Test
	public void findeNachnamenNachPrefix() {
		// Given
		final String nachnamePrefix = NACHNAME_PREFIX;
		
		// When
		final TypedQuery<String> query = getEntityManager().createNamedQuery(Kunde.FINDE_NACHNAMEN_NACH_PREFIX,
                                                                            String.class);
		query.setParameter(Kunde.PARAM_KUNDE_NACHNAME_PREFIX, nachnamePrefix + '%');
		final List<String> kunden = query.getResultList();
		
		// Then
		assertThat(kunden.isEmpty(), is(false));
		for (String k : kunden) {
			assertThat(k.substring(0, 1), is(nachnamePrefix));
		}
	}
	
	@Test
	public void findeKundeNachPlz() {
		// Given
		final String plz = PLZ;
		
		// When
		final TypedQuery<Kunde> query = getEntityManager().createNamedQuery(Kunde.FINDE_KUNDE_NACH_PLZ,
                                                                            Kunde.class);
		query.setParameter(Kunde.PARAM_PLZ, plz);
		final List<Kunde> kunden = query.getResultList();
		
		// Then
		assertThat(kunden.isEmpty(), is(false));
		for (Kunde k : kunden) {
			assertThat(k.getAdresse().getPlz(), is(plz));
		}
	}
	
	@Test
	public void findeKundeNachDatum() {
		// Given
		final Date datum = DATUM;
		
		// When
		final TypedQuery<Kunde> query = getEntityManager().createNamedQuery(Kunde.FINDE_KUNDE_NACH_DATUM,
                                                                            Kunde.class);
		query.setParameter(Kunde.PARAM_DATUM, datum, TemporalType.DATE);
		final List<Kunde> kunden = query.getResultList();
		
		// Then
		assertThat(kunden.isEmpty(), is(false));
		for (Kunde k : kunden) {
			assertThat(k.getSeit(), is(datum));
		}
	}
	
	@Test
	public void findeKundeNachArt() {
		// Given
		final String art = ART;
		
		// When
		final TypedQuery<Kunde> query = getEntityManager().createNamedQuery(Kunde.FINDE_KUNDE_NACH_ART,
                                                                            Kunde.class);
		query.setParameter(Kunde.PARAM_ART, art);
		final List<Kunde> kunden = query.getResultList();
		
		// Then
		assertThat(kunden.isEmpty(), is(false));
		for (Kunde k : kunden) {
			assertThat(k.getArt(), is(art));
		}
	}
	
	@Test
	public void findeKundeNachMaxUmsatz() {
		// Given
		final double umsatz = UMSATZ;
		
		// When
		final TypedQuery<Kunde> query = getEntityManager().createNamedQuery(Kunde.FINDE_KUNDE_NACH_MAX_UMSATZ,
                                                                            Kunde.class);
		query.setParameter(Kunde.PARAM_UMSATZ, umsatz);
		final List<Kunde> kunden = query.getResultList();
		
		// Then
		assertThat(kunden.isEmpty(), is(false));
		for (Kunde k : kunden) {
			assertThat(k.getUmsatz() <= umsatz, is(true));
		}
	}
	
	@Test
	public void findeKundeNachMinUmsatz() {
		// Given
		final double umsatz = UMSATZ;
		
		// When
		final TypedQuery<Kunde> query = getEntityManager().createNamedQuery(Kunde.FINDE_KUNDE_NACH_MIN_UMSATZ,
                                                                            Kunde.class);
		query.setParameter(Kunde.PARAM_UMSATZ, umsatz);
		final List<Kunde> kunden = query.getResultList();
		
		// Then
		assertThat(kunden.isEmpty(), is(false));
		for (Kunde k : kunden) {
			assertThat(k.getUmsatz() >= umsatz, is(true));
		}
	}
	

	@Test
	public void createKunde() {
		// Given
		final Kunde kunde = new Kunde();
		kunde.setNachname(NACHNAME_NEU);
		kunde.setVorname(VORNAME_NEU);
		kunde.setEmail(EMAIL_NEU);
		kunde.setRabatt(RABATT_NEU);
		kunde.setUmsatz(UMSATZ_NEU);
		kunde.setSeit(SEIT_VORHANDEN);
		kunde.setArt(ART_NEU);
		kunde.setPasswort(PASSWORT_NEU);
		kunde.setPasswortWdh(PASSWORT_NEU);
		
		final Adresse adresse = new Adresse();
		adresse.setPlz(PLZ_NEU);
		adresse.setOrt(ORT_NEU);
		adresse.setStrasse(STRASSE_NEU);
		adresse.setHausnr(HAUSNR_NEU);
		kunde.setAdresse(adresse);
		
		kunde.setNewsletter(true);
		kunde.setGeschlecht(GeschlechtTyp.WEIBLICH);
		kunde.setFamilienstand(FamilienstandTyp.VERHEIRATET);
		
		// When
		try {    
			getEntityManager().persist(kunde);		// abspeichern einschl. Adresse
		}
		catch (ConstraintViolationException e) {
			// Es gibt Verletzungen bzgl. Bean Validation: auf der Console ausgeben
			final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
			for (ConstraintViolation<?> v : violations) {
				System.err.println("!!! FEHLERMELDUNG>>> " + v.getMessage());
				System.err.println("!!! ATTRIBUT>>> " + v.getPropertyPath());
				System.err.println("!!! ATTRIBUTWERT>>> " + v.getInvalidValue());
			}
			
			throw new RuntimeException(e);
		}
		
		// Then
		
		// Den abgespeicherten Kunden ueber eine Named Query ermitteln
		final TypedQuery<Kunde> query = getEntityManager().createNamedQuery(Kunde.FINDE_KUNDE_NACH_NACHNAME,
				                                                            Kunde.class);
		query.setParameter(Kunde.PARAM_NACHNAME, NACHNAME_NEU);
		final List<Kunde> kunden = query.getResultList();
		
		// Ueberpruefung des ausgelesenen Objekts
		assertThat(kunden.size(), is(1));
		for (Kunde k : kunden) {
			assertThat(k.getId().longValue() > 0, is(true));
			assertThat(k.getNachname(), is(NACHNAME_NEU));
		}
	}
}