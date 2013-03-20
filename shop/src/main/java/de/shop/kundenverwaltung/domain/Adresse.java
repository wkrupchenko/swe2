package de.shop.kundenverwaltung.domain;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import de.shop.util.IdGroup;

import static de.shop.util.Konstante.KEINE_ID;
import static de.shop.util.Konstante.MIN_ID;
import static java.util.logging.Level.FINER;
import static javax.persistence.TemporalType.DATE;

/**
 * The persistent class for the adresse database table.
 * 
 */
@Entity
@Table(name = "adresse")
public class Adresse implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static final int PLZ_LAENGE_MAX = 5;
	public static final int ORT_LAENGE_MIN = 2;
	public static final int ORT_LAENGE_MAX = 32;
	public static final int STRASSE_LAENGE_MIN = 2;
	public static final int STRASSE_LAENGE_MAX = 32;
	public static final int HAUSNR_LAENGE_MAX = 4;
	
	@Id
	@GeneratedValue
	@Column(name = "a_id", nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{kundenverwaltung.adresse.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;

	@Temporal(DATE)
	@Column(name = "a_aktualisiert", nullable = false)
	@XmlTransient
	private Date aktualisiert;

	@Temporal(DATE)
	@Column(name = "a_erzeugt", nullable = false)
	@XmlTransient
	private Date erzeugt;
	
	@Column(name = "a_strasse")
	@NotNull(message = "{kundenverwaltung.adresse.strasse.notNull}")
	@Size(min = STRASSE_LAENGE_MIN, max = STRASSE_LAENGE_MAX, message = "{kundenverwaltung.adresse.strasse.length}")
	private String strasse;

	@Column(name = "a_hausnr", length = PLZ_LAENGE_MAX)
	@Size(max = HAUSNR_LAENGE_MAX, message = "{kundenverwaltung.adresse.hausnr.length}")
	private String hausnr;

	@Column(name = "a_plz", length = PLZ_LAENGE_MAX , nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.plz.notNull}")
	@Pattern(regexp = "\\d{5}", message = "{kundenverwaltung.adresse.plz}")
	private String plz;
	
	@Column(name = "a_ort", length = ORT_LAENGE_MAX, nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.ort.notNull}")
	@Size(min = ORT_LAENGE_MIN, max = ORT_LAENGE_MAX, message = "{kundenverwaltung.adresse.ort.length}")
	private String ort;

	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PostPersist
	private void postPersist() {
		LOGGER.log(FINER, "Neue Adresse mit ID={0}", id);
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

	public String getHausnr() {
		return this.hausnr;
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