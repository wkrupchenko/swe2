package de.shop.bestellverwaltung.service;

import static de.shop.util.Konstante.KEINE_ID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Lieferung;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.ConcurrentDeletedException;
import de.shop.util.IdGroup;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;

@Log
public class BestellungService implements Serializable {
	private static final long serialVersionUID = -9145947650157430928L;
	
	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private ValidatorProvider validationService;
	
	@Inject
	private Logger logger;
	
	@Inject
	@NeueBestellung
	private transient Event<Bestellung> event;
	
	@PostConstruct
	private void postConstruct() {
		logger.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		logger.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	public Bestellung findeBestellungNachId(Long id) {
		final Bestellung bestellung = em.find(Bestellung.class, id);
		return bestellung;
	}
	
	public List<Bestellung> findeBestellungenGeschlossen() {
		final List<Bestellung> bestellungen = em.createNamedQuery(Bestellung.FINDE_ALLE_GESCHLOSSENEN_BESTELLUNGEN, 
												Bestellung.class)
												.getResultList();
		return bestellungen;
	}
	
	public List<Bestellung> findeBestellungenOffen() {
		final List<Bestellung> bestellungen = em.createNamedQuery(Bestellung.FINDE_ALLE_OFFENEN_BESTELLUNGEN, 
												Bestellung.class)
												.getResultList();
		return bestellungen;
	}
	
	public List<Bestellung> findeAlleBestellungen() {
		final List<Bestellung> bestellungen = em.createNamedQuery(Bestellung.FINDE_ALLE_BESTELLUNGEN_NACH_ID_SORTIERT, 
												Bestellung.class)
												.getResultList();

		return bestellungen;
	}

	public List<Bestellung> findeBestellungenNachKundeId(Long kundeId) {
		final List<Bestellung> bestellungen = em.createNamedQuery(Bestellung.FINDE_BESTELLUNGEN_VON_KUNDEN_NACH_ID,
                                                                  Bestellung.class)
                                                .setParameter(Bestellung.PARAM_ID, kundeId)
				                                .getResultList();
		return bestellungen;
	}

	public Bestellung createBestellung(Bestellung bestellung,
			                           Kunde kunde, Lieferung lieferung,
			                           Locale locale) {
		if (bestellung == null) {
			return null;
		}
		
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			logger.debugf("Bestellposition: %s", bp);				
		}
		
		// damit "kunde" dem EntityManager bekannt ("managed") ist
		kunde = ks.findeKundeNachId(kunde.getId(), FetchType.MIT_BESTELLUNGEN, locale);
		kunde.addBestellung(bestellung);
		bestellung.setKunde(kunde);
		
		// Lieferung zuweisen
		bestellung.addLieferung(lieferung);
		lieferung.addBestellung(bestellung);
		
		// Keine IDs vor dem Abspeichern
		bestellung.setId(KEINE_ID);
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			bp.setId(KEINE_ID);
		}
		
		validateBestellung(bestellung, locale, Default.class);
		em.persist(bestellung);
		event.fire(bestellung);

		return bestellung;
	}
	
	public Bestellung updateBestellung(Bestellung bestellung, Locale locale) {
		if (bestellung == null) {
			return null;
		}

		validateBestellung(bestellung, locale, Default.class, IdGroup.class);
		
		// Bestellung vom EntityManager trennen, weil anschliessend z.B. nach Id gesucht wird
		em.detach(bestellung);
		
		// Wurde das Objekt konkurrierend geloescht?
		Bestellung tmp = findeBestellungNachId(bestellung.getId());
		if(tmp == null) {
			throw new ConcurrentDeletedException(bestellung.getId());
		}
		
		// Bestellung erneut vom EntityManager trennen
		em.detach(bestellung);
			
		bestellung = em.merge(bestellung); // OptimisticLockException ggf. geworfen
		return bestellung;
	}
	
	public void deleteBestellung(Bestellung bestellung) {
		if (bestellung == null) {
			return;
		}
	
		bestellung = findeBestellungNachId(bestellung.getId());
	
		if (bestellung == null) {
			return;
		}
		
		if (!bestellung.getLieferungen().isEmpty()) {
			throw new BestellungDeleteLieferungException(bestellung);
		}

		em.remove(bestellung);
	}
	
	private void validateBestellung(Bestellung bestellung, Locale locale, Class<?>... groups) {
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Bestellung>> violations = validator.validate(bestellung);
		if (violations != null && !violations.isEmpty()) {
			logger.debugf("BestellungService", "createBestellung", violations);
			throw new BestellungValidationException(bestellung, violations);
		}
	}
	
	public List<Lieferung> findeLieferungen(String nr) {
		final List<Lieferung> lieferungen =
				              em.createNamedQuery(Lieferung.FINDE_LIEFERUNG_NACH_LIEFERNR,
                                                  Lieferung.class)
                                .setParameter(Lieferung.PARAM_LIEFERNR, nr)
				                .getResultList();
		return lieferungen;
	}
	
	private void validateLieferung(Lieferung lieferung, Locale locale, Class<?>... groups) {
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Lieferung>> violations = validator.validate(lieferung);
		if (violations != null && !violations.isEmpty()) {
			logger.debugf("BestellungService", "createLieferung", violations);
			throw new LieferungValidationException(lieferung, violations);
		}
	}
	
	public Lieferung createLieferung(Lieferung lieferung, List<Bestellung> bestellungen) {
		if (lieferung == null || bestellungen == null || bestellungen.isEmpty()) {
			return null;
		}
		
		final List<Long> ids = new ArrayList<>();
		for (Bestellung b : bestellungen) {
			ids.add(b.getId());
		}
		
		bestellungen = findBestellungenByIds(ids);
		lieferung.setBestellungenAsList(bestellungen);
		for (Bestellung bestellung : bestellungen) {
			bestellung.addLieferung(lieferung);
		}
		
		lieferung.setId(KEINE_ID);
		em.persist(lieferung);		
		return lieferung;
	}

	public Lieferung updateLieferung(Lieferung lieferung, Locale locale) {
		if (lieferung == null) {
			return null;
		}

		validateLieferung(lieferung, locale, Default.class, IdGroup.class);
		
		// Lieferung vom EntityManager trennen, weil anschliessend z.B. nach Id und Email gesucht wird
		em.detach(lieferung);
		
		// Wurde das Objekt konkurrierend geloescht?
		Lieferung tmp = findeLieferungNachId(lieferung.getId());
		if(tmp == null) {
			throw new ConcurrentDeletedException(lieferung.getId());
		}
		
		// Lieferung erneut vom EntityManager trennen
		em.detach(tmp);
			
		lieferung = em.merge(lieferung); // OptimisticLockException ggf. geworfen
		return lieferung;
	}
	
	public void deleteLieferung(Lieferung lieferung) {
		if (lieferung == null) {
			return;
		}
		
		lieferung = findeLieferungNachId(lieferung.getId());
		
		if (lieferung == null) {
			return;
		}
		
		if (!lieferung.getBestellungen().isEmpty()) {
			throw new LieferungDeleteBestellungException(lieferung);
		}

		em.remove(lieferung);
	}
	
	private List<Bestellung> findBestellungenByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return null;
		}

		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Bestellung> criteriaQuery  = builder.createQuery(Bestellung.class);
		final Root<Bestellung> b = criteriaQuery.from(Bestellung.class);
		b.fetch("lieferungen", JoinType.LEFT);
		
		final Path<Long> idPath = b.get("id");
		final List<Predicate> predList = new ArrayList<>();
		for (Long id : ids) {
			final Predicate equal = builder.equal(idPath, id);
			predList.add(equal);
		}

		final Predicate[] predArray = new Predicate[predList.size()];
		final Predicate pred = builder.or(predList.toArray(predArray));
		criteriaQuery.where(pred).distinct(true);

		final TypedQuery<Bestellung> query = em.createQuery(criteriaQuery);
		final List<Bestellung> bestellungen = query.getResultList();
		return bestellungen;
	}
	
	public Lieferung findeLieferungNachId(Long id) {
		final Lieferung lieferung = em.find(Lieferung.class, id);
		return lieferung;
	}
	
	public List<Bestellung> findeBestellungenNachLieferungLiefernr(String liefernr) {
		final List<Bestellung> bestellungen = em.createNamedQuery(Bestellung.FINDE_BESTELLUNG_NACH_LIEFERUNG_LIEFERNR,
                Bestellung.class)
                .setParameter(Bestellung.PARAM_LIEFERNR, liefernr)
                .getResultList();
		return bestellungen;
	}

	public List<Bestellung> findeBestellungenNachLieferungId(Long id) {
		final Lieferung lieferung = em.find(Lieferung.class, id);
		if(lieferung == null) {
			List<Bestellung> temp = new ArrayList<Bestellung>();
			return temp;
		}
		
		final String liefernr = lieferung.getLiefernr();
		
		final List<Bestellung> bestellungen = em.createNamedQuery(Bestellung.FINDE_BESTELLUNG_NACH_LIEFERUNG_LIEFERNR,
                Bestellung.class)
                .setParameter(Bestellung.PARAM_LIEFERNR, liefernr)
                .getResultList();
		return bestellungen;
	}
	
	public List<Lieferung> findeLieferungenNachBestellungId(Long id) {
		final List<Lieferung> lieferungen = em.createNamedQuery(Lieferung.FINDE_LIEFERUNGEN_NACH_BESTELLUNG_ID, 
																								Lieferung.class)
											.setParameter(Lieferung.PARAM_ID, id)
											.getResultList();
		return lieferungen;
	}
}
