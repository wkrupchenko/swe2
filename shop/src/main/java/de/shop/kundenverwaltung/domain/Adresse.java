package de.shop.kundenverwaltung.domain;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Date;

import org.jboss.logging.Logger;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.shop.util.IdGroup;
import static de.shop.util.Konstante.KEINE_ID;
import static de.shop.util.Konstante.MIN_ID;
import static de.shop.util.Konstante.ERSTE_VERSION;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * The persistent class for the adresse database table.
 * 
 */
@Entity
@Table(name = "adresse")
public class Adresse implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());

	public static final int PLZ_LAENGE_MAX = 5;
	public static final int ORT_LAENGE_MIN = 2;
	public static final int ORT_LAENGE_MAX = 32;
	public static final int STRASSE_LAENGE_MIN = 2;
	public static final int STRASSE_LAENGE_MAX = 32;
	public static final int HAUSNR_LAENGE_MAX = 4;
	
	@Id
	@GeneratedValue
	@Column(nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{kundenverwaltung.adresse.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	@Column(length = STRASSE_LAENGE_MAX, nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.strasse.notNull}")
	@Size(min = STRASSE_LAENGE_MIN, max = STRASSE_LAENGE_MAX, message = "{kundenverwaltung.adresse.strasse.length}")
	private String strasse;

	@Column(length = HAUSNR_LAENGE_MAX)
	@Size(max = HAUSNR_LAENGE_MAX, message = "{kundenverwaltung.adresse.hausnr.length}")
	private String hausnr;

	@Column(length = PLZ_LAENGE_MAX , nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.plz.notNull}")
	@Pattern(regexp = "\\d{5}", message = "{kundenverwaltung.adresse.plz}")
	private String plz;
	
	@Column(length = ORT_LAENGE_MAX, nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.ort.notNull}")
	@Size(min = ORT_LAENGE_MIN, max = ORT_LAENGE_MAX, message = "{kundenverwaltung.adresse.ort.length}")
	private String ort;
	
	@OneToOne
	@JoinColumn(name = "kunde_fk", nullable = false)
	@JsonIgnore
	private Kunde kunde;
	
	@Temporal(TIMESTAMP)
	@Column(nullable = false)
	@JsonIgnore
	private Date aktualisiert;

	@Temporal(TIMESTAMP)
	@Column(nullable = false)
	@JsonIgnore
	private Date erzeugt;

	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PostPersist
	private void postPersist() {
		LOGGER.debugf("Neue Adresse mit ID=%s", id);
	}
	
	@PostUpdate
	private void postUpdate() {
		LOGGER.debugf("Adresse mit ID=%d aktualisiert: version=%d", id, version);
	}
	
	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}

	public Adresse() {
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

	public String getHausnr() {
		return this.hausnr;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}
	
	public Kunde getKunde() {
		return this.kunde;
	}

	public void setHausnr(String hausnr) {
		this.hausnr = hausnr;
	}

	public String getOrt() {
		return this.ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getPlz() {
		return this.plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getStrasse() {
		return this.strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
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
		result = prime * result + ((hausnr == null) ? 0 : hausnr.hashCode());
		result = prime * result + ((ort == null) ? 0 : ort.hashCode());
		result = prime * result + ((plz == null) ? 0 : plz.hashCode());
		result = prime * result + ((strasse == null) ? 0 : strasse.hashCode());
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
		
		Adresse other = (Adresse) obj;
		if (hausnr == null)
			if (other.hausnr != null)
				return false;
		
		if (ort == null)
			if (other.ort != null)
				return false;
		
		if (plz == null)
			if (other.plz != null)
				return false;
		
		if (strasse == null) 
			if (other.strasse != null)
				return false;
		
		return true;
	}

	@Override
	public String toString() {
		return id + ": " + strasse + " " + hausnr + " " + plz + " " +  ort;
	}

}