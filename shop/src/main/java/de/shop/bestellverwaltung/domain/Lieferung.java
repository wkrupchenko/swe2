package de.shop.bestellverwaltung.domain;

import static de.shop.util.Konstante.ERSTE_VERSION;
import static de.shop.util.Konstante.KEINE_ID;
import static de.shop.util.Konstante.MIN_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.logging.Logger;

import de.shop.util.IdGroup;
import de.shop.util.PreExistingGroup;

@Entity
@Table(name = "lieferung")
@NamedQueries({
	@NamedQuery(name  = Lieferung.FINDE_LIEFERUNG_NACH_LIEFERNR,
			query = "SELECT l"
					+ " FROM   Lieferung l "
		           + " WHERE  l.liefernr = :" + Lieferung.PARAM_LIEFERNR),
    @NamedQuery(name  = Lieferung.FINDE_LIEFERUNG_NACH_ID,
					query = "SELECT l"
							+ " FROM   Lieferung l "
				           + " WHERE  l.id = :" + Lieferung.PARAM_ID),
   @NamedQuery(name = Lieferung.FINDE_LIEFERUNGEN_NACH_BESTELLUNG_ID,
   			query = "Select l from Lieferung l join l.bestellungen b Where b.id = :" + Lieferung.PARAM_ID)
})
public class Lieferung implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final int LIEFERNR_LAENGE_MAX = 12;
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	public static final String PARAM_ID = "id";
	public static final String PARAM_LIEFERNR = "liefernr";
	private static final String PREFIX = "Lieferung.";
	
	public static final String FINDE_LIEFERUNG_NACH_LIEFERNR = PREFIX + "findeLieferungNachLiefernr";
	public static final String FINDE_LIEFERUNG_NACH_ID = PREFIX + "findeLieferungNachId";
	public static final String FINDE_LIEFERUNGEN_NACH_BESTELLUNG_ID = PREFIX + "findeLieferungenNachBestellungId";

	@Id
	@GeneratedValue
	@Column(nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{bestellverwaltung.bestellung.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;

	@Column(name = "inlandOderAusland")
	private String inlandOderAusland;

	@Column(length = LIEFERNR_LAENGE_MAX, nullable = false, unique = true)
	@NotNull(message = "{bestellverwaltung.lieferung.lieferNr.notNull}")
	@Size(max = LIEFERNR_LAENGE_MAX, message = "{bestellverwaltung.lieferung.lieferNr.length}")
	private String liefernr;

	@Column(name = "transport_art_fk")
	@Enumerated
	private TransportTyp transportArt;
	
	@ManyToMany(mappedBy = "lieferungen", cascade = PERSIST)
	@OrderBy("id ASC")
	@NotEmpty(message = "{bestellverwaltung.lieferung.bestellungen.notEmpty}", groups = PreExistingGroup.class)
	@Valid
	@JsonIgnore
	private Set<Bestellung> bestellungen;
	
	@Transient
	@JsonProperty("bestellungen")
	private URI bestellungenUri;
	
	@Temporal(TIMESTAMP)
	@Column(nullable = false)
	@JsonIgnore
	private Date aktualisiert;

	@Temporal(TIMESTAMP)
	@Column(nullable = false)
	@JsonIgnore
	private Date erzeugt;

	public Lieferung() {
		super();
	}
	
	public Lieferung(String lieferNr, TransportTyp transportArt) {
		super();
		this.liefernr = lieferNr;
		this.transportArt = transportArt;
	}

	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PostPersist
	private void postPersist() {
		LOGGER.debugf("Neue Lieferung mit ID=%d", id);
	}
	
	@PostUpdate
	private void postUpdate() {
		LOGGER.debugf("Lieferung mit ID=%d aktualisiert: version=%d", id, version);
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
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}

	public String getInlandOderAusland() {
		return this.inlandOderAusland;
	}

	public void setInlandOderAusland(String inlandOderAusland) {
		this.inlandOderAusland = inlandOderAusland;
	}

	public String getLiefernr() {
		return this.liefernr;
	}

	public void setLiefernr(String liefernr) {
		this.liefernr = liefernr;
	}

	public TransportTyp getTransportArt() {
		return this.transportArt;
	}

	public void setTransportArt(TransportTyp transportArt) {
		this.transportArt = transportArt;
	}
	
	public Set<Bestellung> getBestellungen() {
		return bestellungen == null ? null : Collections.unmodifiableSet(bestellungen);
	}
	
	public void setBestellungen(Set<Bestellung> bestellungen) {
		if (this.bestellungen == null) {
			this.bestellungen = bestellungen;
			return;
		}
		
		this.bestellungen.clear();
		if (bestellungen != null)
			this.bestellungen.addAll(bestellungen);
	}
	
	public void addBestellung(Bestellung bestellung) {
		if (this.bestellungen == null) {
			this.bestellungen = new HashSet<>();
		}
		this.bestellungen.add(bestellung);
	}
	
	@JsonIgnore
	public List<Bestellung> getBestellungenAsList() {
		return this.bestellungen == null ? null : new ArrayList<>(bestellungen);
	}
	
	public void setBestellungenAsList(List<Bestellung> lBestellungen) {
		this.bestellungen = lBestellungen == null ? null : new HashSet<>(lBestellungen);
	}
	
	public URI getBestellungenUri() {
		return bestellungenUri;
	}
	
	public void setBestellungenUri(URI bestellungenUri) {
		this.bestellungenUri = bestellungenUri;
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
	public String toString() {
		return "Lieferung [id=" + id + ", lieferNr=" + liefernr 
				+ ", transportArt=" + transportArt + ", erzeugt=" + erzeugt
				+ ", aktualisiert=" + aktualisiert + ']';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((liefernr == null) ? 0 : liefernr.hashCode());
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
		
		final Lieferung other = (Lieferung) obj;
		if (liefernr == null)
			if (other.liefernr != null)
				return false;
		
		return true;
	}

}
