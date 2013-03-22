package de.shop.bestellverwaltung.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static de.shop.util.Konstante.KEINE_ID;
import static de.shop.util.Konstante.MIN_ID;
import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.IdGroup;
import static java.util.logging.Level.FINER;



import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.logging.Logger;



import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * The persistent class for the bestellposition database table.
 * 
 */
@Entity
@Table(name = "bestellposition")
public class Bestellposition implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final int ANZAHL_MIN = 1;

	@Id
	@GeneratedValue
	@Column(name = "bp_id")
	@Min(value = MIN_ID, message = "{bestellverwaltung.bestellung.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;

	@Column(name = "bp_anzahl")
	@Min(value = ANZAHL_MIN, message = "{bestellverwaltung.bestellposition.anzahl.min}")
	private short anzahl;

	@ManyToOne(optional = false)
	@JoinColumn(name = "bp_artikel_fk", nullable = false)
	@NotNull(message = "{bestellverwaltung.bestellposition.artikel.notNull}")
	@JsonIgnore
	private Artikel artikel;
	
	
	@Transient
	@JsonProperty("artikel")
	private URI artikelUri;

	public Bestellposition() {
		super();
	}

	public Bestellposition(Artikel artikel) {
		super();
		this.artikel = artikel;
		this.anzahl = 1;
	}
	
	public Bestellposition(Artikel artikel, short anzahl) {
		super();
		this.artikel = artikel;
		this.anzahl = anzahl;
	}
	
	@PostPersist
	private void postPersist() {
		LOGGER.log(FINER, "Neue Bestellposition mit ID={0}", id);
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public short getAnzahl() {
		return this.anzahl;
	}

	public void setAnzahl(short anzahl) {
		this.anzahl = anzahl;
	}

	public Artikel getArtikel() {
		return this.artikel;
	}

	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}

	public URI getArtikelUri() {
		return artikelUri;
	}
	
	public void setArtikelUri(URI artikelUri) {
		this.artikelUri = artikelUri;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + anzahl;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((artikel == null) ? 0 : artikel.hashCode());
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
		
		Bestellposition other = (Bestellposition) obj;
		if (id == null)
			if (other.id != null)
				return false;

		if (artikel == null)
			if (other.artikel != null)
				return false;
		
		return true;
	}

	@Override
	public String toString() {
		final Long artikelId = artikel == null ? null : artikel.getId();
		return "Bestellposition [id=" + id + ", artikel=" + artikelId
			   + ", artikelUri=" + artikelUri + ", anzahl=" + anzahl + "]";
	}

}
