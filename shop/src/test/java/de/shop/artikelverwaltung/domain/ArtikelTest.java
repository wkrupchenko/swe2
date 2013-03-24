/*package de.shop.artikelverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import de.shop.util.AbstractDomainTest;

@RunWith(Arquillian.class)
public class ArtikelTest extends AbstractDomainTest {
	
	private static final String BEZEICHNUNG = "Socken";
	private static final Boolean VERFUEGBAR = true;
	private static final String NAME = "Sommermode";
	private static final double PREIS = 25;
	private static final Long ID = Long.valueOf(500);
	private static final Long ID_ARTIKELGRUPPE = Long.valueOf(400);
	
	private static final String BEZEICHNUNG_NEU = "V-Shirt";
	private static final Boolean ERHAELTLICH_NEU = true;
	private static final double PREIS_NEU = 38.99;
	private static final String NAME_NEU = "Testmode";
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findeArtikelNachId() {
		// Given
		final Long id = ID;
		
		// When
		Artikel artikel = getEntityManager().find(Artikel.class, id);
		
		// Then
		assertThat(artikel.getId(), is(id));
	}
	
	@Test
	public void findeArtikelNachBezeichnung() {
		//GIVEN
		final String bez = BEZEICHNUNG;
		
		//WHEN
		TypedQuery<Artikel> query = getEntityManager().createNamedQuery(Artikel.FINDE_ARTIKEL_NACH_BEZ, Artikel.class);
		query.setParameter(Artikel.PARAM_BEZ, bez);
		Artikel artikel = query.getSingleResult();
		
		//THEN
		assertThat(artikel.getBezeichnung(), is(bez));
	}
	
	@Test
	public void findeVerfuegbareArtikel() {
		//GIVEN
		final Boolean verfuegbar = VERFUEGBAR;
		
		//WHEN
		TypedQuery<Artikel> query = getEntityManager().createNamedQuery(Artikel.FINDE_VERFUEGBARE_ARTIKEL,
																		Artikel.class);
		List<Artikel> artikel = query.getResultList();
		
		//THEN
		assertThat(artikel.isEmpty(), is(false));
		for (Artikel a : artikel) {
			assertThat(a.isErhaeltlich(), is(verfuegbar));
		}
	}
	
	@Test
	public void findeArtikelNachArtikelgruppe() {
		//Given
		final String name = NAME;
		
		//WHEN
		TypedQuery<Artikel> query = getEntityManager().createNamedQuery(Artikel.FINDE_ARTIKEL_NACH_ARTIKELGRUPPE,
				                                                        Artikel.class);
		query.setParameter(Artikel.PARAM_NAME, name);
		List<Artikel> artikel = query.getResultList();
		
		//THEN
		assertThat(artikel.isEmpty(), is(false));
		for (Artikel a : artikel) {
			assertThat(a.getArtikelgruppe().getName(), is(name));
		}
	}
	
	@Test
	public void findeArtikelNachMaxPreis() {
		//Given
		final double preis = PREIS;
		
		//WHEN
		TypedQuery<Artikel> query = getEntityManager().createNamedQuery(Artikel.FINDE_ARTIKEL_NACH_MAX_PREIS,
																		Artikel.class);
		query.setParameter(Artikel.PARAM_PREIS, preis);
		List<Artikel> artikel = query.getResultList();
		
		//THEN
		assertThat(artikel.isEmpty(), is(false));
		for (Artikel a : artikel) {
			assertThat(a.getPreis() <= preis, is(true));
		}
	}
	
	@Test
	public void findeArtikelNachMinPreis() {
		//Given
		final double preis = PREIS;
		
		//WHEN
		TypedQuery<Artikel> query = getEntityManager().createNamedQuery(Artikel.
				                                       FINDE_ARTIKEL_NACH_MIN_PREIS, Artikel.class);
		query.setParameter(Artikel.PARAM_PREIS, preis);
		List<Artikel> artikel = query.getResultList();
		
		//THEN
		assertThat(artikel.isEmpty(), is(false));
		for (Artikel a : artikel) {
			assertThat(a.getPreis() >= preis, is(true));
		}
	}
	
	@Test
	public void findeArtikelNachArtikelgruppeId() {
		//Given
		final Long id = ID_ARTIKELGRUPPE;
		
		//When
		TypedQuery<Artikel> query = getEntityManager().createNamedQuery(Artikel.
				                                       FINDE_ARTIKEL_NACH_ARTIKELGRUPPE_ID, Artikel.class);
		query.setParameter(Artikel.PARAM_ID, id);
		List<Artikel> artikel = query.getResultList();
		
		//Then
		assertThat(artikel.isEmpty(), is(false));
		for (Artikel a : artikel) {
			assertThat(a.getArtikelgruppe().getId(), is(id));
		}
	}
		
	@Test 
	public void createArtikel() {
		//GIVEN
		final Artikel artikel = new Artikel();
		artikel.setBezeichnung(BEZEICHNUNG_NEU);
		artikel.setErhaeltlich(ERHAELTLICH_NEU);
		artikel.setPreis(PREIS_NEU);
		
		final Artikelgruppe ag = new Artikelgruppe();
		ag.setName(NAME_NEU);
		ag.addArtikel(artikel);
		artikel.setArtikelgruppe(ag);
		ag.addArtikel(artikel);
		
		// When
		try {
			getEntityManager().persist(artikel);    
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
		
		//THEN
		TypedQuery<Artikel> query = getEntityManager().createNamedQuery(Artikel.FINDE_ARTIKEL_NACH_BEZ,
																		Artikel.class);
		query.setParameter(Artikel.PARAM_BEZ, BEZEICHNUNG_NEU);
		Artikel artikel2 = query.getSingleResult();
		
		assertThat(artikel2.getBezeichnung(), is(BEZEICHNUNG_NEU));
		assertThat(artikel2.getId().longValue() > 0, is(true));
	}
	
	@Test
	public void findeArtikelgruppeNachId() {
		// Given
		final Long id = ID_ARTIKELGRUPPE;
		
		// When
		Artikelgruppe artikelgruppe = getEntityManager().find(Artikelgruppe.class, id);
		
		// Then
		assertThat(artikelgruppe.getId(), is(id));
	}
	
	@Test
	public void findeArtikelgruppeNachArtikelId() {
		//Given
		final Long id = ID;
		
		//When
		TypedQuery<Artikelgruppe> query = getEntityManager().createNamedQuery(Artikelgruppe.
				FINDE_ARTIKELGRUPPE_NACH_ARTIKEL_ID, Artikelgruppe.class);
		query.setParameter(Artikel.PARAM_ID, id);
		List<Artikelgruppe> artikelgruppe = query.getResultList();
		
		//Then
		assertThat(artikelgruppe.isEmpty(), is(false));
	}
	
	@Test
	public void findeArtikelgruppeNachName() {
		//GIVEN
		final String name = NAME;
		
		//WHEN
		TypedQuery<Artikelgruppe> query = getEntityManager().createNamedQuery(Artikelgruppe.
				                                      FINDE_ARTIKELGRUPPE_NACH_NAME, Artikelgruppe.class);
		query.setParameter(Artikelgruppe.PARAM_NAME, name);
		Artikelgruppe artikelgruppe = query.getSingleResult();
		
		//THEN
		assertThat(artikelgruppe.getName(), is(name));
	}
	
	@Test 
	public void createArtikelgruppe() {
		//GIVEN
		final Artikelgruppe ag = new Artikelgruppe();
		ag.setName(NAME_NEU);
		
		// When
		try {
			getEntityManager().persist(ag);    
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
		
		//THEN
		TypedQuery<Artikelgruppe> query = getEntityManager().createNamedQuery(Artikelgruppe.
				                                     FINDE_ARTIKELGRUPPE_NACH_NAME, Artikelgruppe.class);
		query.setParameter(Artikelgruppe.PARAM_NAME, NAME_NEU);
		Artikelgruppe artikelgruppe2 = query.getSingleResult();
		
		assertThat(artikelgruppe2.getName(), is(NAME_NEU));
		assertThat(artikelgruppe2.getId().longValue() > 0, is(true));
	}
}
*/