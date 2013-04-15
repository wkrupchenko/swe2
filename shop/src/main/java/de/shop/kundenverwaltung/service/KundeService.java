package de.shop.kundenverwaltung.service;

import static de.shop.util.Konstante.KEINE_ID;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellposition_;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Bestellung_;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.domain.Kunde_;
import de.shop.kundenverwaltung.domain.PasswordGroup;
import de.shop.util.IdGroup;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;

@Log
public class KundeService implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	public enum FetchType {
		NUR_KUNDE,
		MIT_BESTELLUNGEN,
	}
	
	public enum OrderType {
		KEINE,
		ID
	}
	
	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private ValidatorProvider validationService;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}

	public List<Kunde> findeAlleKunden(FetchType fetch, OrderType order) {
		List<Kunde> kunden;
		switch (fetch) {
			case NUR_KUNDE:
				kunden = OrderType.ID.equals(order)
				         ? em.createNamedQuery(Kunde.FINDE_KUNDE_SORTIERT_NACH_ID, Kunde.class)
				             .getResultList()
				         : em.createNamedQuery(Kunde.FINDE_KUNDE, Kunde.class)
				             .getResultList();
				break;
			
			case MIT_BESTELLUNGEN:
				kunden = em.createNamedQuery(Kunde.FINDE_KUNDE_BESTELLUNGEN_ABRUFEN, Kunde.class)
						   .getResultList();
				break;

			default:
				kunden = OrderType.ID.equals(order)
		                 ? em.createNamedQuery(Kunde.FINDE_KUNDE_SORTIERT_NACH_ID, Kunde.class)
		                	 .getResultList()
		                 : em.createNamedQuery(Kunde.FINDE_KUNDE, Kunde.class)
		                     .getResultList();
				break;
		}

		return kunden;
	}
	
	public List<Kunde> findeKundeNachNachname(String nachname, FetchType fetch, Locale locale) {
		validateNachname(nachname, locale);
		
		List<Kunde> kunden;
		switch (fetch) {
			case NUR_KUNDE:
				kunden = em.createNamedQuery(Kunde.FINDE_KUNDE_NACH_NACHNAME, Kunde.class)
						   .setParameter(Kunde.PARAM_NACHNAME, nachname)
						   .getResultList();
				break;
			
			case MIT_BESTELLUNGEN:
				kunden = em.createNamedQuery(Kunde.FINDE_KUNDE_NACH_NACHNAME_BESTELLUNGEN_ABRUFEN, Kunde.class)
						   .setParameter(Kunde.PARAM_NACHNAME, nachname)
						   .getResultList();
				break;

			default:
				kunden = em.createNamedQuery(Kunde.FINDE_KUNDE_NACH_NACHNAME, Kunde.class)
						   .setParameter(Kunde.PARAM_NACHNAME, nachname)
						   .getResultList();
				break;
		}

		return kunden;
	}
	
	private void validateNachname(String nachname, Locale locale) {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator.validateValue(Kunde.class,
				                                                                           "nachname",
				                                                                           nachname,
				                                                                           Default.class);
		if (!violations.isEmpty())
			throw new InvalidNachnameException(nachname, violations);
	}
	

	public Kunde findeKundeNachId(Long id, FetchType fetch, Locale locale) {
		validateKundeId(id, locale);
		
		Kunde kunde = null;
		try {
			switch (fetch) {
				case NUR_KUNDE:
					kunde = em.find(Kunde.class, id);
					break;
				
				case MIT_BESTELLUNGEN:
					kunde = em.createNamedQuery(Kunde.FINDE_KUNDE_NACH_ID_BESTELLUNGEN_ABRUFEN, Kunde.class)
							  .setParameter(Kunde.PARAM_ID, id)
							  .getSingleResult();
					break;
						
				default:
					kunde = em.find(Kunde.class, id);
					break;
			}
		}
		catch (NoResultException e) {
			return null;
		}

		return kunde;
	}
	
	private void validateKundeId(Long kundeId, Locale locale) {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator.validateValue(Kunde.class,
				                                                                           "id",
				                                                                           kundeId,
				                                                                           IdGroup.class);
		if (!violations.isEmpty())
			throw new InvalidKundeIdException(kundeId, violations);
	}
	
	public List<String> findeNachnamenNachPrefix(String nachnamePrefix) {
		final List<String> nachnamen = em.createNamedQuery(Kunde.FINDE_NACHNAMEN_NACH_PREFIX,
				                                           String.class)
				                         .setParameter(Kunde.PARAM_KUNDE_NACHNAME_PREFIX, nachnamePrefix + '%')
				                         .getResultList();
		return nachnamen;
	}
	
	public List<Long> findeIdsNachPrefix(String idPrefix) {
		final List<Long> ids = em.createNamedQuery(Kunde.FINDE_IDS_NACH_PREFIX, Long.class)
				                 .setParameter(Kunde.PARAM_KUNDE_ID_PREFIX, idPrefix + '%')
				                 .getResultList();
		return ids;
	}
	
	public List<Kunde> findePrivatkundenFirmenkunden() {
		final List<Kunde> kunden = em.createNamedQuery(Kunde.FINDE_KUNDE_NACH_ART,
                                                               Kunde.class)
											 .setParameter(Kunde.PARAM_ART, "P")
                                             .getResultList();
	    final List<Kunde> kunden2 = em.createNamedQuery(Kunde.FINDE_KUNDE_NACH_ART,
                                                               Kunde.class)
											 .setParameter(Kunde.PARAM_ART, "F")
                                             .getResultList();
	    
	    for (Kunde k: kunden2) {
	    	kunden.add(k);
	    }
	    return kunden;
	}

	

	public Kunde findeKundeNachEmail(String email, Locale locale) {
		validateEmail(email, locale);
		try {
			final Kunde kunde = em.createNamedQuery(Kunde.FINDE_KUNDE_NACH_EMAIL, Kunde.class)
					                      .setParameter(Kunde.PARAM_EMAIL, email)
					                      .getSingleResult();
			return kunde;
		}
		catch (NoResultException e) {
			return null;
		}
	}
	
	private void validateEmail(String email, Locale locale) {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator.validateValue(Kunde.class,
				                                                                           "email",
				                                                                           email,
				                                                                           Default.class);
		if (!violations.isEmpty())
			throw new InvalidEmailException(email, violations);
	}

	public Kunde createKunde(Kunde kunde, Locale locale) {
		if (kunde == null) {
			return kunde;
		}

		validateKunde(kunde, locale, Default.class, PasswordGroup.class);
		
		try {
			em.createNamedQuery(Kunde.FINDE_KUNDE_NACH_EMAIL, Kunde.class)
			  .setParameter(Kunde.PARAM_EMAIL, kunde.getEmail())
			  .getSingleResult();
			throw new EmailExistsException(kunde.getEmail());
		}
		catch (NoResultException e) {
			LOGGER.debugf("Email-Adresse existiert noch nicht");
		}
		
		kunde.setId(KEINE_ID);
		em.persist(kunde);
		return kunde;		
	}
	
	private void validateKunde(Kunde kunde, Locale locale, Class<?>... groups) {
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Kunde>> violations = validator.validate(kunde, groups);
		if (!violations.isEmpty()) {
			throw new KundeValidationException(kunde, violations);
		}
	}
	
	public Kunde updateKunde(Kunde kunde, Locale locale) {
		if (kunde == null) {
			return null;
		}

		validateKunde(kunde, locale, Default.class, PasswordGroup.class, IdGroup.class);
		
		try {
			final Kunde vorhandenerKunde = em.createNamedQuery(Kunde.FINDE_KUNDE_NACH_EMAIL,
					                                                   Kunde.class)
					                                 .setParameter(Kunde.PARAM_EMAIL, kunde.getEmail())
					                                 .getSingleResult();
			
			if (vorhandenerKunde.getId().longValue() != kunde.getId().longValue()) {
				throw new EmailExistsException(kunde.getEmail());
			}
		}
		catch (NoResultException e) {
			LOGGER.debugf("Neue Email-Adresse");
		}

		em.merge(kunde);
		return kunde;
	}

	public void deleteKunde(Kunde kunde) {
		if (kunde == null) {
			return;
		}
		
		try {
			kunde = findeKundeNachId(kunde.getId(), FetchType.MIT_BESTELLUNGEN, Locale.getDefault());
		}
		catch (InvalidKundeIdException e) {
			return;
		}
		
		if (kunde == null) {
			return;
		}
		
		if (!kunde.getBestellungen().isEmpty()) {
			throw new KundeDeleteBestellungException(kunde);
		}

		em.remove(kunde);
	}

	public List<Kunde> findeKundeNachPlz(String plz) {
		final List<Kunde> kunden = em.createNamedQuery(Kunde.FINDE_KUNDE_NACH_PLZ, Kunde.class)
                                             .setParameter(Kunde.PARAM_PLZ, plz)
                                             .getResultList();
		return kunden;
	}

	public List<Kunde> findeKundeSeit(Date seit) {
		final List<Kunde> kunden = em.createNamedQuery(Kunde.FINDE_KUNDE_NACH_DATUM, Kunde.class)
                                             .setParameter(Kunde.PARAM_DATUM, seit)
                                             .getResultList();
		return kunden;
	}
	
	public List<Kunde> findeKundeNachNachnameCriteria(String nachname) {
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Kunde> criteriaQuery = builder.createQuery(Kunde.class);
		final Root<Kunde> k = criteriaQuery.from(Kunde.class);

		final Path<String> nachnamePath = k.get(Kunde_.nachname);
		//final Path<String> nachnamePath = k.get("nachname");
		
		final Predicate pred = builder.equal(nachnamePath, nachname);
		criteriaQuery.where(pred);
		
		final List<Kunde> kunden = em.createQuery(criteriaQuery).getResultList();
		return kunden;
	}
	
	public List<Kunde> findeKundeMitMinBestMenge(short minMenge) {
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Kunde> criteriaQuery  = builder.createQuery(Kunde.class);
		final Root<Kunde> k = criteriaQuery.from(Kunde.class);

		final Join<Kunde, Bestellung> b = k.join(Kunde_.bestellungen);
		final Join<Bestellung, Bestellposition> bp = b.join(Bestellung_.bestellpositionen);
		criteriaQuery.where(builder.gt(bp.<Short>get(Bestellposition_.anzahl), minMenge))
		             .distinct(true);
		
		final List<Kunde> kunden = em.createQuery(criteriaQuery).getResultList();
		return kunden;
	}
	
}
