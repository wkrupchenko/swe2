package de.shop.artikelverwaltung.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.domain.Artikelgruppe;
import de.shop.util.AbstractTest;

@RunWith(Arquillian.class)
public class ArtikelServiceTest extends AbstractTest {
	
	private static final String BEZEICHNUNG_NEU = "Jeans";
	private static final Boolean ERHAELTLICH_NEU = true;
	private static final double PREIS_NEU = 78.89;
	private static final String ARTIKELGRUPPE_NAME_NEU = "Mischmode";
	private static final Long SUCHE_ID_1 = 500L;
	private static final Long SUCHE_ID_2 = 501L;
	private static final Long SUCHE_ID_3 = 503L;
	private static final double NEUER_PREIS_ERHOEHUNG = 15;
	
	private static final Long ARTIKELGRUPPE_ID_OHNE_ARTIKEL = 402L;
	private static final Long ARTIKELGRUPPE_ID_VORHANDEN = 400L;
	
	private static final String BEZEICHNUNG = "Pollunder";
	private static final double PREIS = 50.00;
	private static final String NAME = "Herren";
	private static final Long ARTIKEL_ID_OHNE_BESTELLUNGEN = 504L;
	private static final String NAME_NEU = "Verschiedenes";
	private static final Long ARTIKEL_ID_VORHANDEN = 503L;
	private static final Long ID_ARTIKELGRUPPE = 400L;
	
	@Inject
	private ArtikelService as;
	
	@Test
	public void findeVerfuegbareArtikel() {
		// Given
		
		// When
		final List<Artikel> artikel = as.findeVerfuegbareArtikel();
		
		// Then
		for (Artikel a: artikel) {
			assertThat(a.isErhaeltlich(), is(true));
		}
	}
	
	@Test 
	public void findeArtikelNachIds() {
		// Given
		final List<Long> ids = new ArrayList<Long>();
		ids.add(SUCHE_ID_1);
		ids.add(SUCHE_ID_2);
		ids.add(SUCHE_ID_3);
		
		// When
		final List<Artikel> artikel = as.findeArtikelNachIds(ids);
		
		// Then
		int i = 0;
		for (Artikel a: artikel) {
			assertThat(a.getId(), is(ids.get(i)));
			++i;
		}	
	}
	
	@Test
	public void findeArtikelNachBezeichnung() {
		// Given
		final String bezeichnung = BEZEICHNUNG;
		
		// When
		List<Artikel> artikel = as.findeArtikelNachBezeichnung(bezeichnung);
		
		// Then
		for (Artikel a: artikel) {
			assertThat(a.getBezeichnung(), is(bezeichnung));
		}
	}
	
	@Test
	public void findeArtikelNachMaxPreis() {
		// Given
		final double preis = PREIS;
		
		// When
		List<Artikel> artikel = as.findeArtikelNachMaxPreis(preis);
		
		// Then
		for (Artikel a: artikel) {
			assertThat(a.getPreis() <= preis, is(true));
		}
	}
	
	@Test
	public void findeArtikelNachMinPreis() {
		// Given
		final double preis = PREIS;
		
		// When
		List<Artikel> artikel = as.findeArtikelNachMinPreis(preis);
		
		// Then
		for (Artikel a: artikel) {
			assertThat(a.getPreis() >= preis, is(true));
		}
	}
	
	@Test
	public void findeArtikelNachArtikelgruppe() {
		// Given
		final String name = NAME;
		
		// When
		List<Artikel> artikel = as.findeArtikelNachArtikelgruppe(name);
		
		// Then
		for (Artikel a: artikel) {
			assertThat(a.getArtikelgruppe().getName(), is(name));
		}
	}
	
	@Test
	public void findeArtikelNachArtikelgruppeId() {
		//Given
		final Long id = ID_ARTIKELGRUPPE;
		
		//When
		List<Artikel> artikel = as.findeArtikelNachArtikelgruppeId(id);
		
		//Then
		for (Artikel a: artikel) {
			assertThat(a.getArtikelgruppe().getId(), is(id));
		}
	}
	
	
	
	@Test
    public void createArtikel() {
        // Given
        final Long anzahlAlt = as.artikelanzahl();

        final String bezeichnung = BEZEICHNUNG_NEU;
        final Boolean erhaeltlich = ERHAELTLICH_NEU;
        final double preis = PREIS_NEU;
        final String name = NAME_NEU;
        
        // When
        Artikel artikel = new Artikel();
        artikel.setBezeichnung(bezeichnung);
        artikel.setErhaeltlich(erhaeltlich);
        artikel.setPreis(preis);
        
        Artikelgruppe ag = new Artikelgruppe();
        ag.setName(name);
        ag.addArtikel(artikel);
		artikel.setArtikelgruppe(ag);
		ag.addArtikel(artikel);
 
        artikel = as.createArtikel(artikel, LOCALE);

        // Then
        Long anzahlNeu = as.artikelanzahl();

        assertTrue((anzahlNeu - 1) == anzahlAlt);
    }
	
	@Test
	public void deleteArtikel() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                 SystemException, NotSupportedException {
		// Given
		final Long artikelId = ARTIKEL_ID_OHNE_BESTELLUNGEN;

		final List<Artikel> artikelVorher = as.findeAlleArtikel();
	
		// When
		final Artikel artikel = as.findeArtikelNachId(artikelId);
		as.deleteArtikel(artikel);
	
		// Then
		final List<Artikel> artikelNachher = as.findeAlleArtikel();
		assertThat(artikelVorher.size() - 1, is(artikelNachher.size()));
	}
	
	@Test
	public void neuerPreisFuerArtikel() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                        SystemException, NotSupportedException {
		// Given
		final Long artikelId = ARTIKEL_ID_VORHANDEN;
		final double erhoehung = NEUER_PREIS_ERHOEHUNG;

		// When
		Artikel artikel = as.findeArtikelNachId(artikelId);
		
		final double alterPreis = artikel.getPreis();
		final double neuerPreis = alterPreis + erhoehung;
		artikel.setPreis(neuerPreis);

		artikel = as.updateArtikel(artikel, LOCALE);
		
		// Then
		assertThat(artikel.getPreis(), is(neuerPreis));
		artikel = as.findeArtikelNachId(artikelId);
		assertThat(artikel.getPreis(), is(neuerPreis));
	}
	
	@Test
	public void findeArtikelgruppeNachArtikel() {
		//Given
		final Long id = ARTIKEL_ID_VORHANDEN;
		
		//When
		Artikelgruppe artikelgruppe = as.findeArtikelgruppeNachArtikel(id);
		
		//Then
		List<Artikel> artikel = artikelgruppe.getArtikel();
		for (Artikel a: artikel) {
			if (a.getId() == id)
				assertThat(a.getId(), is(id));
		}
	}
	
	@Test
	public void findeArtikelgruppeNachName() {
		//Given
		final String name = NAME;
		
		//When
		List<Artikelgruppe> artikelgruppe = as.findeArtikelgruppeNachName(name);
		
		//Then
		for (Artikelgruppe ag: artikelgruppe) {
			assertThat(ag.getName(), is(name));
		}
	}
	
	@Test
	public void findeArtikelgruppeNachId() {
		//Given
		final Long id = ID_ARTIKELGRUPPE;
		
		//When
		Artikelgruppe artikelgruppe = as.findeArtikelgruppeNachId(id);
		
		//Then
		assertThat(artikelgruppe.getId(), is(id));
	}
	
	@Test
    public void createArtikelgruppe() {
        // Given
        final Long anzahlAlt = as.artikelgruppenanzahl();

        final String name = ARTIKELGRUPPE_NAME_NEU;
        
        // When
        Artikelgruppe ag = new Artikelgruppe();
        ag.setName(name);
 
        ag = as.createArtikelgruppe(ag, LOCALE);

        // Then
        Long anzahlNeu = as.artikelgruppenanzahl();

        assertTrue((anzahlNeu - 1) == anzahlAlt);
    }
	
	@Test
	public void deleteArtikelgruppe() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                 SystemException, NotSupportedException {
		// Given
		final Long artikelgruppeId = ARTIKELGRUPPE_ID_OHNE_ARTIKEL;

		final List<Artikelgruppe> artikelgruppeVorher = as.findeAlleArtikelgruppen();
	
		// When
		final Artikelgruppe artikelgruppe = as.findeArtikelgruppeNachId(artikelgruppeId);
		as.deleteArtikelgruppe(artikelgruppe);
	
		// Then
		final List<Artikelgruppe> artikelgruppeNachher = as.findeAlleArtikelgruppen();
		assertThat(artikelgruppeVorher.size() - 1, is(artikelgruppeNachher.size()));
	}
	
	@Test
	public void neuerNameFuerArtikelgruppe() throws RollbackException, 
	                                                HeuristicMixedException, HeuristicRollbackException,
	                                                SystemException, NotSupportedException {
		// Given
		final Long artikelgruppeId = ARTIKELGRUPPE_ID_VORHANDEN;

		// When
		Artikelgruppe artikelgruppe = as.findeArtikelgruppeNachId(artikelgruppeId);
		
		final String alterName = artikelgruppe.getName();
		final String neuerName = alterName + "Änderung";
		artikelgruppe.setName(neuerName);

		artikelgruppe = as.updateArtikelgruppe(artikelgruppe, LOCALE);
		
		// Then
		assertThat(artikelgruppe.getName(), is(neuerName));
		artikelgruppe = as.findeArtikelgruppeNachId(artikelgruppeId);
		assertThat(artikelgruppe.getName(), is(neuerName));
	}
}