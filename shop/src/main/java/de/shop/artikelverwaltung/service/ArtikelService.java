package de.shop.artikelverwaltung.service;

import static de.shop.util.Konstante.KEINE_ID;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jboss.logging.Logger;
import com.google.common.base.Strings;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Validator;
import javax.validation.groups.Default;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.domain.Artikelgruppe;
import de.shop.util.ConcurrentDeletedException;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.util.IdGroup;

@Log
public class ArtikelService implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ValidatorProvider validationService;
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private Logger logger;
	
	@PersistenceContext
	private transient EntityManager em;
	
	@PostConstruct
	private void postConstruct() {
		logger.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		logger.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	public List<Artikel> findeVerfuegbareArtikel() {
		final List<Artikel> result = em.createNamedQuery(Artikel.FINDE_VERFUEGBARE_ARTIKEL, Artikel.class)
				                       .getResultList();
		return result;
	}

	public Artikel findeArtikelNachId(Long id) {
		final Artikel artikel = em.find(Artikel.class, id);
		return artikel;
	}
	
	public List<Artikel> findeArtikelNachIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Artikel> criteriaQuery = builder.createQuery(Artikel.class);
		final Root<Artikel> a = criteriaQuery.from(Artikel.class);

		final Path<Long> idPath = a.get("id");
		
		Predicate pred = null;
		if (ids.size() == 1) {
			pred = builder.equal(idPath, ids.get(0));
		}
		else {
			
			final Predicate[] equals = new Predicate[ids.size()];
			int i = 0;
			for (Long id : ids) {
				equals[i++] = builder.equal(idPath, id);
			}
			
			pred = builder.or(equals);
		}
		
		criteriaQuery.where(pred);
		
		final TypedQuery<Artikel> query = em.createQuery(criteriaQuery);

		final List<Artikel> artikel = query.getResultList();
		return artikel;
	}

	public List<Artikel> findeArtikelNachBezeichnung(String bezeichnung) {
		if (Strings.isNullOrEmpty(bezeichnung)) {
			final List<Artikel> artikel = findeVerfuegbareArtikel();
			return artikel;
		}
		
		final List<Artikel> artikel = em.createNamedQuery(Artikel.FINDE_ARTIKEL_NACH_BEZ, Artikel.class)
				                        .setParameter(Artikel.PARAM_BEZ, bezeichnung)
				                        .getResultList();
		return artikel;
	}
	
	public List<Artikel> findeArtikelNachMaxPreis(double preis) {
		final List<Artikel> artikel = em.createNamedQuery(Artikel.FINDE_ARTIKEL_NACH_MAX_PREIS, Artikel.class)
				                        .setParameter(Artikel.PARAM_PREIS, preis)
				                        .getResultList();
		return artikel;
	}
	
	public List<Artikel> findeArtikelNachMinPreis(double preis) {
		final List<Artikel> artikel = em.createNamedQuery(Artikel.FINDE_ARTIKEL_NACH_MIN_PREIS, Artikel.class)
				                        .setParameter(Artikel.PARAM_PREIS, preis)
				                        .getResultList();
		return artikel;
	}
	
	public List<Artikel> findeArtikelNachArtikelgruppe(String name) {
		final List<Artikel> artikel = em.createNamedQuery(Artikel.FINDE_ARTIKEL_NACH_ARTIKELGRUPPE, Artikel.class)
										.setParameter(Artikel.PARAM_NAME, name)
										.getResultList();
		return artikel;
	}
	
	public List<Artikel> findeArtikelNachArtikelgruppeId(Long id) {
		final List<Artikel> artikel = em.createNamedQuery(Artikel.FINDE_ARTIKEL_NACH_ARTIKELGRUPPE_ID, Artikel.class)
										.setParameter(Artikel.PARAM_ID, id)
										.getResultList();
		return artikel;
	}
	
	public List<Artikel> findeAlleArtikel() {
		final List<Artikel> artikel = em.createNamedQuery(Artikel.FINDE_ARTIKEL_SORTIERT_NACH_ID, Artikel.class)
										.getResultList();
		
		return artikel;
	}
	
	private void validateArtikel(Artikel artikel, Locale locale, Class<?>... groups) {
	
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Artikel>> violations = validator.validate(artikel, groups);
		if (!violations.isEmpty()) {
			throw new ArtikelValidationException(artikel, violations);
		}
	}
	
	public Artikel createArtikel(Artikel artikel, Locale locale) {
		if (artikel == null) {
			return artikel;
		}

		validateArtikel(artikel, locale, Default.class);
		
		artikel.setId(KEINE_ID);
		em.persist(artikel);
		return artikel;		
	}
	
	public void deleteArtikel(Artikel artikel) {
		if (artikel == null) {
			return;
		}
		
		final Artikel artikel2 = findeArtikelNachId(artikel.getId());
		if (!artikel2.equals(artikel))
			return;
		
		List<Bestellung> bestellungen = bs.findeAlleBestellungen();
		for (Bestellung b: bestellungen) {
			List<Bestellposition> bestellpositionen = b.getBestellpositionen();
			for (Bestellposition bp: bestellpositionen) {			
				if (bp.getArtikel().getId().equals(artikel.getId()))
					throw new ArtikelDeleteBestellungException(artikel);
			}
		}
	
		em.remove(artikel);
	}
	
	public Artikel updateArtikel(Artikel artikel, Locale locale) {
		if (artikel == null) {
			return null;
		}

		validateArtikel(artikel, locale, Default.class, IdGroup.class);
		
		// Artikel  vom EntityManager trennen
		em.detach(artikel);
		
		// Wurde Artikel gel�scht?
		Artikel tmp = findeArtikelNachId(artikel.getId());
		if(tmp == null) {
			throw new ConcurrentDeletedException(artikel.getId());
		}
		
		// Artikel erneut vom EntityManager trennen
		em.detach(tmp);
		
		artikel = em.merge(artikel); //OptimisticLockException ggf. geworfen
		return artikel;
	}
	
	public Long artikelanzahl() {
		final List<Artikel> result = em.createNamedQuery(Artikel.FINDE_ARTIKEL_SORTIERT_NACH_ID, Artikel.class)
                .getResultList();
		Long ret = 0L;
		for (@SuppressWarnings("unused") Artikel a: result) {
			++ret;
		}
		 return ret;
	}
	
	public Artikelgruppe findeArtikelgruppeNachArtikel(Long id) {
		final Artikelgruppe artikelgruppe = em.createNamedQuery(Artikelgruppe.FINDE_ARTIKELGRUPPE_NACH_ARTIKEL_ID,
															    Artikelgruppe.class)
								  .setParameter(Artikelgruppe.PARAM_ID, id)
								  .getSingleResult();
		return artikelgruppe;
	}
	
	public List<Artikelgruppe> findeAlleArtikelgruppen() {
		final List<Artikelgruppe> artikelgruppen = em.createNamedQuery(Artikelgruppe.
				FINDE_ARTIKELGRUPPEN_SORTIERT_NACH_ID, Artikelgruppe.class)
													 .getResultList();
		return artikelgruppen;
	}
	
	public List<Artikelgruppe> findeArtikelgruppeNachName(String name) {
		final List<Artikelgruppe> artikelgruppe = em.createNamedQuery(Artikelgruppe.FINDE_ARTIKELGRUPPE_NACH_BEZEICHNUNG, 
																	  Artikelgruppe.class)
								  .setParameter(Artikelgruppe.PARAM_BEZEICHNUNG, name)
								  .getResultList();
		return artikelgruppe;
	}
	
	public Artikelgruppe findeArtikelgruppeNachId(Long id) {
		final Artikelgruppe artikelgruppe = em.find(Artikelgruppe.class, id);
		return artikelgruppe;
	}
	
	private void validateArtikelgruppe(Artikelgruppe artikelgruppe, Locale locale, Class<?>... groups) {
		
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Artikelgruppe>> violations = validator.validate(artikelgruppe, groups);
		if (!violations.isEmpty()) {
			throw new ArtikelgruppeValidationException(artikelgruppe, violations);
		}
	}
	
	public Artikelgruppe createArtikelgruppe(Artikelgruppe artikelgruppe, Locale locale) {
		if (artikelgruppe == null) {
			return artikelgruppe;
		}

		validateArtikelgruppe(artikelgruppe, locale, Default.class);
		
		artikelgruppe.setId(KEINE_ID);
		em.persist(artikelgruppe);
		return artikelgruppe;		
	}
	
	public Artikelgruppe updateArtikelgruppe(Artikelgruppe artikelgruppe, Locale locale) {
		if (artikelgruppe == null) {
			return null;
		}

		validateArtikelgruppe(artikelgruppe, locale, Default.class);
		
		// Artikelgruppe  vom EntityManager trennen
		em.detach(artikelgruppe);
				
		// Wurde Artikelgruppe gel�scht?
		Artikelgruppe tmp = findeArtikelgruppeNachId(artikelgruppe.getId());
		if(tmp == null) {
			throw new ConcurrentDeletedException(artikelgruppe.getId());
		}
				
		// Artikel erneut vom EntityManager trennen
		em.detach(tmp);
				
		artikelgruppe = em.merge(artikelgruppe); //OptimisticLockException ggf. geworfen
		return artikelgruppe;

	}
	
	public void deleteArtikelgruppe(Artikelgruppe artikelgruppe) {
		if (artikelgruppe == null) {
			return;
		}
		
		final Artikelgruppe artikelgruppe2 = findeArtikelgruppeNachId(artikelgruppe.getId());
		if (artikelgruppe2 != artikelgruppe)
			return;
		
		List<Artikel> artikel = artikelgruppe.getArtikel();
		if (!artikel.isEmpty()) {
					throw new ArtikelgruppeDeleteArtikelException(artikelgruppe);
		}
	
		em.remove(artikelgruppe);
	}
	
	public Long artikelgruppenanzahl() {
		final List<Artikelgruppe> result = em.createNamedQuery(Artikelgruppe.FINDE_ARTIKELGRUPPEN_SORTIERT_NACH_ID, 
															   Artikelgruppe.class)
                .getResultList();
		Long ret = 0L;
		for (@SuppressWarnings("unused") Artikelgruppe ag: result) {
			++ret;
		}
		 return ret;
	}
}
