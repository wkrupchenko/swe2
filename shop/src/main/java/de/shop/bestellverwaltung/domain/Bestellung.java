package de.shop.bestellverwaltung.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.validation.constraints.Min;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import de.shop.util.PreExistingGroup;
import de.shop.util.IdGroup;

import java.lang.invoke.MethodHandles;
import java.net.URI;

import org.jboss.logging.Logger;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import de.shop.kundenverwaltung.domain.Kunde;

import static javax.persistence.FetchType.EAGER;
import static de.shop.util.Konstante.KEINE_ID;
import static de.shop.util.Konstante.MIN_ID;
import static de.shop.util.Konstante.ERSTE_VERSION;
import static de.shop.util.Konstante.LONG_ANZ_ZIFFERN;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.TemporalType.TIMESTAMP;
  

/**
 * The persistent class for the bestellung database table.
 * 
 */
@Entity
@Table(name = "bestellung")
@NamedQueries({
    @NamedQuery(name  = Bestellung.FINDE_ALLE_OFFENEN_BESTELLUNGEN,
   	            query = "FROM Bestellung b WHERE b.offenAbgeschlossen = TRUE "),
    @NamedQuery(name  = Bestellung.FINDE_ALLE_GESCHLOSSENEN_BESTELLUNGEN,
	            query = "FROM Bestellung b WHERE b.offenAbgeschlossen = FALSE "),
    @NamedQuery(name  = Bestellung.FINDE_BESTELLUNGEN_VON_KUNDEN_NACH_ID,
    			query = "SELECT b"
    					+ " FROM   Bestellung b"
    					+ " JOIN  b.kunde k WHERE k.id = :" + Bestellung.PARAM_ID),
    @NamedQuery(name  = Bestellung.FINDE_BESTELLUNGEN_VON_KUNDEN_NACH_NACHNAME,
    			query = "SELECT b"
				+ " FROM   Bestellung b"
				+ " JOIN  b.kunde k WHERE k.nachname = :" + Bestellung.PARAM_NACHNAME),
	@NamedQuery(name  = Bestellung.FINDE_BESTELLUNG_NACH_LIEFERUNG_ID,
		    	query = "SELECT b"
		    			+ " FROM   Bestellung b JOIN b.lieferungen l"
			            + " WHERE  l.id = :" + Bestellung.PARAM_ID),
    @NamedQuery(name  = Bestellung.FINDE_BESTELLUNG_NACH_LIEFERUNG_LIEFERNR,
		    	query = "SELECT b"
		    			+ " FROM   Bestellung b JOIN b.lieferungen l"
			            + " WHERE  l.liefernr = :" + Bestellung.PARAM_LIEFERNR),
    @NamedQuery(name  = Bestellung.FINDE_BESTELLUNG_NACH_ID,
				query = "SELECT b"
						+ " FROM   Bestellung b"
						+ " WHERE  b.id = :" + Bestellung.PARAM_ID),
	@NamedQuery(name = Bestellung.FINDE_ALLE_BESTELLUNGEN_NACH_ID_SORTIERT,
				query = "Select b From Bestellung b Order By b.id")
})

public class Bestellung implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	private static final String PREFIX = "Bestellung.";
	public static final String FINDE_ALLE_OFFENEN_BESTELLUNGEN = PREFIX + "findeAlleOffenenBestellungen";
	public static final String FINDE_ALLE_GESCHLOSSENEN_BESTELLUNGEN = PREFIX + "findeAlleGeschlossenenBestellungen";
	public static final String FINDE_BESTELLUNGEN_VON_KUNDEN_NACH_ID = PREFIX + "findeBestellungenVonKundenNachId";
	public static final String FINDE_BESTELLUNGEN_VON_KUNDEN_NACH_NACHNAME = PREFIX 
																	+ "findeBestellungenVonKundenNachNachname";
	public static final String FINDE_BESTELLUNG_NACH_LIEFERUNG_ID = PREFIX + "findeBestellungenNachLieferungId";
	public static final String PARAM_NACHNAME = "nachname";
	public static final String PARAM_ID = "id";
	public static final String PARAM_LIEFERNR = "liefernr";
	public static final String FINDE_BESTELLUNG_NACH_ID = PREFIX + "findeBestellungNachId";
	public static final String FINDE_ALLE_BESTELLUNGEN_NACH_ID_SORTIERT = PREFIX + "findeAlleBestellungen";
	public static final String FINDE_BESTELLUNG_NACH_LIEFERUNG_LIEFERNR = PREFIX 
																	+ "findeBestellungenNachLieferungLiefernr";
	
	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true, updatable = false, precision = LONG_ANZ_ZIFFERN)
	@Min(value = MIN_ID, message = "{bestellverwaltung.bestellung.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;

	@ManyToOne(optional = false)
	@JoinColumn(name = "kunde_fk", nullable = false, insertable = false, updatable = false)
	@NotNull(message = "{bestellverwaltung.bestellung.kunde.notNull}", groups = PreExistingGroup.class)
	@JsonIgnore
	private Kunde kunde;
	
	@Transient
	@JsonProperty("kunde")
	private URI kundeUri;
	
	@OneToMany(fetch = EAGER, cascade = {PERSIST, REMOVE })
	@JoinColumn(name = "bestellung_fk", nullable = false)
	@OrderColumn(name = "idx", nullable = false)
	@NotEmpty(message = "{bestellverwaltung.bestellung.bestellpositionen.notEmpty}")
	@Valid
	private List<Bestellposition> bestellpositionen;
	
	@ManyToMany(fetch = EAGER)
	@JoinTable(name = "bestellung_lieferung", joinColumns = @JoinColumn(name = "bestellung_fk"),
				inverseJoinColumns = @JoinColumn(name = "lieferung_fk"))
	@OrderColumn(name = "idx", nullable = false)
	@JsonIgnore
	private Set<Lieferung> lieferungen;
	
	@Transient
	@JsonProperty("lieferungen")
	private URI lieferungenUri;
	
	@Column(nullable = false)
	private boolean offenAbgeschlossen;
	
	@Temporal(TIMESTAMP)
	@Column(nullable = false)
	@JsonIgnore
	private Date aktualisiert;

	@Temporal(TIMESTAMP)
	@Column(nullable = false)
	@JsonIgnore
	private Date erzeugt;
	
	public void setWerte(Bestellung bestellung) {
		aktualisiert = bestellung.aktualisiert;
		erzeugt = bestellung.erzeugt;
		kunde = bestellung.kunde;
		bestellpositionen = bestellung.bestellpositionen;
		lieferungen = bestellung.lieferungen;
		offenAbgeschlossen = bestellung.offenAbgeschlossen;
	}

	public Bestellung() {
		super();
	}

	public Bestellung(List<Bestellposition> bestellpositionen) {
		super();
		this.bestellpositionen = bestellpositionen;
	}
	
	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PostPersist
	private void postPersist() {
		LOGGER.debugf("Neue Bestellund mit ID=%d", id);
	}
	
	@PostUpdate
	private void postUpdate() {
		LOGGER.debugf("Bestellung mit ID=%d aktualisiert: version=%d", id, version);
	}
	
	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Kunde getKunde() {
		return this.kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}
	
	public URI getKundeUri() {
		return kundeUri;
	}
	
	public void setKundeUri(URI kundeUri) {
		this.kundeUri = kundeUri;
	}
	
	public List<Bestellposition> getBestellpositionen() {
		return this.bestellpositionen == null ? null : Collections.unmodifiableList(bestellpositionen);
	}
	
	public void setBestellpositionen(List<Bestellposition> bestellpositionen) {
		if (this.bestellpositionen == null) {
			this.bestellpositionen = bestellpositionen;
			return;
		}
		
		this.bestellpositionen.clear();
		if (bestellpositionen != null)
			this.bestellpositionen.addAll(bestellpositionen);
	}
	
	public Bestellung addBestellposition(Bestellposition bestellposition) {
		if (this.bestellpositionen == null) {
			this.bestellpositionen = new ArrayList<>();
		}
		this.bestellpositionen.add(bestellposition);
		return this;
	}
	
	public Set<Lieferung> getLieferungen() {
		return lieferungen == null ? null : Collections.unmodifiableSet(lieferungen);
	}
	public void setLieferungen(Set<Lieferung> lieferungen) {
		if (this.lieferungen == null) {
			this.lieferungen = lieferungen;
			return;
		}
		
		this.lieferungen.clear();
		if (lieferungen != null)
			this.lieferungen.addAll(lieferungen);
	}
	
	public void addLieferung(Lieferung lieferung) {
		if (lieferungen == null) {
			lieferungen = new HashSet<>();
		}
		lieferungen.add(lieferung);
	}
	
	@JsonIgnore
	public List<Lieferung> getLieferungenAsList() {
		return lieferungen == null ? null : new ArrayList<>(lieferungen);
	}

	public void setLieferungenAsList(List<Lieferung> lLieferungen) {
		this.lieferungen = lLieferungen == null ? null : new HashSet<>(lLieferungen);
	}

	
	public URI getLieferungenUri() {
		return lieferungenUri;
	}
	public void setLieferungenUri(URI lieferungenUri) {
		this.lieferungenUri = lieferungenUri;
	}
	
	public boolean isOffenAbgeschlossen() {
		return this.offenAbgeschlossen;
	}

	public void setOffenAbgeschlossen(boolean offenAbgeschlossen) {
		this.offenAbgeschlossen = offenAbgeschlossen;
	}
	
	public Date getAktualisiert() {
		return this.aktualisiert == null ? null : (Date) this.aktualisiert.clone();
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert.clone();
	}

	public Date getErzeugt() {
		return this.erzeugt == null ? null : (Date) this.erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kunde == null) ? 0 : kunde.hashCode());
		result = prime * result + ((erzeugt == null) ? 0 : erzeugt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		final Bestellung other = (Bestellung) obj;
		if (kunde == null && other.kunde != null) {
			return false;
		}	
		if (erzeugt == null && other.erzeugt != null) {
			return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		final Long kundeId = kunde == null ? null : kunde.getId();
		return "Bestellung [id=" + id + ", kundeId=" + kundeId
			   + ", kundeUri=" + kundeUri + ", erzeugt=" + erzeugt
		       + ", aktualisiert=" + aktualisiert + "]";
	}

}
