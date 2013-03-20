package de.shop.bestellverwaltung.domain;

import java.io.Serializable;
import javax.validation.constraints.Min;
import org.hibernate.validator.constraints.NotEmpty;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import de.shop.util.IdGroup;
import de.shop.util.PreExistingGroup;

import static de.shop.util.Konstante.KEINE_ID;
import static de.shop.util.Konstante.MIN_ID;
/**/
 
 
import static java.util.logging.Level.FINER;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.logging.Logger;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@Entity
@Table(name = "lieferung")
@NamedQueries({
	@NamedQuery(name  = Lieferung.FINDE_LIEFERUNG_NACH_LIEFERNR,
			query = "SELECT l"
					+ " FROM   Lieferung l "
		           + " WHERE  l.liefernr = :" + Lieferung.PARAM_LIEFERNR),
   @NamedQuery(name = Lieferung.FINDE_LIEFERUNGEN_NACH_BESTELLUNG_ID,
   			query = "Select l from Lieferung l join l.bestellungen b Where b.id = :" + Lieferung.PARAM_ID)
})
@XmlRootElement
public class Lieferung implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	public static final String PARAM_ID = "id";
	public static final String PARAM_LIEFERNR = "liefernr";
	private static final String PREFIX = "Lieferung.";
	
	public static final String FINDE_LIEFERUNG_NACH_LIEFERNR = PREFIX + "findeLieferungNachLiefernr";
	public static final String FINDE_LIEFERUNGEN_NACH_BESTELLUNG_ID = PREFIX + "findeLieferungenNachBestellungId";

	@Id
	@GeneratedValue
	@Column(name = "l_id", nullable = false)
	@XmlAttribute
	@Min(value = MIN_ID, message = "{bestellverwaltung.bestellung.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;

	@Column(name = "l_aktualisiert", nullable = false)
	@XmlTransient
	private Date aktualisiert;

	@Column(name = "l_erzeugt", nullable = false)
	@XmlTransient
	private Date erzeugt;

	@Column(name = "l_inlandOderAusland")
	@XmlElement
	private String inlandOderAusland;

	@Column(name = "l_liefernr", nullable = false)
	@XmlElement(required = true)
	private String liefernr;

	@Column(name = "l_transport_art")
	@XmlElement
	private String transportArt;
	
	@ManyToMany(mappedBy = "lieferungen")
	@NotEmpty(message = "{bestellverwaltung.lieferung.bestellungen.notEmpty}", groups = PreExistingGroup.class)
	@XmlTransient
	private Set<Bestellung> bestellungen;
	
	@Transient
	@XmlElement(name = "bestellungen", required = true)
	private URI bestellungenUri;

	public Lieferung() {
		super();
	}
	
	public Lieferung(String lieferNr, String transportArt) {
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
		LOGGER.log(FINER, "Neue Lieferung mit ID={0}", id);
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

	public String getTransportArt() {
		return this.transportArt;
	}

	public void setTransportArt(String transportArt) {
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
	
	@Override
	public String toString() {
		return "Lieferung [id=" + id + ", lieferNr=" + liefernr + ", transportArt=" + transportArt
		       + ", erzeugt=" + erzeugt
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
		
		Lieferung other = (Lieferung) obj;
		if (liefernr == null)
			if (other.liefernr != null)
				return false;
		
		return true;
	}

}
