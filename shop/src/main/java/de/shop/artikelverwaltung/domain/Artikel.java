package de.shop.artikelverwaltung.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;
import java.lang.invoke.MethodHandles;
import java.net.URI;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import de.shop.util.IdGroup;
import de.shop.util.PreExistingGroup;

import static de.shop.util.Konstante.KEINE_ID;
import static de.shop.util.Konstante.MIN_ID;
import static java.util.logging.Level.FINER;
import static javax.persistence.TemporalType.DATE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;

@Entity
@Table(name = "artikel")
@NamedQueries({
	@NamedQuery(name = Artikel.FINDE_ARTIKEL_NACH_BEZ,
				query = "FROM Artikel a WHERE a.bezeichnung = :" + Artikel.PARAM_BEZ),
	@NamedQuery(name = Artikel.FINDE_VERFUEGBARE_ARTIKEL,
				query = "FROM Artikel a WHERE a.erhaeltlich = TRUE"),
	@NamedQuery(name = Artikel.FINDE_ARTIKEL_NACH_MAX_PREIS,
				query = "FROM Artikel a WHERE a.preis <= :" + Artikel.PARAM_PREIS),
	@NamedQuery(name = Artikel.FINDE_ARTIKEL_NACH_MIN_PREIS,
				query = "FROM Artikel a WHERE a.preis >= :" + Artikel.PARAM_PREIS),
	@NamedQuery(name = Artikel.FINDE_ARTIKEL_NACH_ARTIKELGRUPPE,
				query = "Select a FROM Artikel a JOIN a.artikelgruppe ag WHERE ag.name = :" + Artikel.PARAM_NAME),
	@NamedQuery(name = Artikel.FINDE_ARTIKEL_SORTIERT_NACH_ID,
				query = "Select a From Artikel a Order By a.id"),
	@NamedQuery(name = Artikel.FINDE_ARTIKEL_NACH_ARTIKELGRUPPE_ID,
				query = "Select a From Artikel a JOIN a.artikelgruppe ag WHERE ag.id = :" + Artikel.PARAM_ID)
})
@XmlRootElement
public class Artikel implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final String PREFIX = "Artikel.";
	public static final String FINDE_ARTIKEL_NACH_BEZ = PREFIX + "findeArtikelNachBezeichnung";
	public static final String FINDE_VERFUEGBARE_ARTIKEL = PREFIX + "findeVerfuegbareArtikel";
	public static final String FINDE_ARTIKEL_NACH_MAX_PREIS = PREFIX + "findeArtikelNachMaxPreis";
	public static final String FINDE_ARTIKEL_NACH_MIN_PREIS = PREFIX + "findeArtikelNachMinPreis";
	public static final String FINDE_ARTIKEL_NACH_ARTIKELGRUPPE = PREFIX + "findeArtikelNachArtikelgruppe";
	public static final String FINDE_ARTIKEL_SORTIERT_NACH_ID = PREFIX + "findeArtikelSortiertNachId";
	public static final String FINDE_ARTIKEL_NACH_ARTIKELGRUPPE_ID = PREFIX + "findeArtikelNachArtikelgruppeId";
	
	public static final String PARAM_BEZ = "bezeichnung";
	public static final String PARAM_PREIS = "preis";
	public static final String PARAM_NAME = "name";
	public static final String PARAM_ID = "id";
	
	private static final int BEZEICHNUNG_LENGTH_MAX = 32;
	
	
	@Id
	@GeneratedValue
	@Column(name = "a_id")
	@Min(value = MIN_ID, message = "{artikelverwaltung.artikel.id.min}", groups = IdGroup.class)
	@XmlAttribute
	private Long id = KEINE_ID;

	@Temporal(DATE)
	@Column(name = "a_aktualisiert", nullable = false)
	@XmlTransient
	private Date aktualisiert;

	@Column(name = "a_bezeichnung")
	@NotNull(message = "{artikelverwaltung.artikel.bezeichnung.notNull}")
	@Size(max = BEZEICHNUNG_LENGTH_MAX, message = "{artikelverwaltung.artikel.bezeichnung.length}")
	@XmlElement(required = true)
	private String bezeichnung;

	@Column(name = "a_erhaeltlich")
	@XmlElement
	private boolean erhaeltlich;

	@Temporal(DATE)
	@Column(name = "a_erzeugt", nullable = false)
	@XmlTransient
	private Date erzeugt;

	@Column(name = "a_preis")
	@XmlElement
	private double preis;
	
	@ManyToOne(optional = false, cascade = { PERSIST, REMOVE })
	@JoinColumn(name = "a_artikelgruppe_fk", nullable = false, insertable = false, updatable = false)
	@NotNull(message = "{artikelverwaltung.artikel.artikelgruppe.notNull}", groups = PreExistingGroup.class)
	@XmlTransient
	private Artikelgruppe artikelgruppe;
	
	@Transient
	@XmlElement(name = "artikelgruppe")
	private URI artikelgruppeUri;

	public Artikel() {
		super();
	}
	
	public Artikel(String bezeichnung, double preis, Artikelgruppe ag) {
		super();
		this.bezeichnung = bezeichnung;
		this.preis = preis;
		this.artikelgruppe = ag;
	}
	
	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PostPersist
	private void postPersist() {
		LOGGER.log(FINER, "Neuer Artikel mit ID={0}", id);
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

	public String getBezeichnung() {
		return this.bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

	public boolean isErhaeltlich() {
		return this.erhaeltlich;
	}

	public void setErhaeltlich(boolean erhaeltlich) {
		this.erhaeltlich = erhaeltlich;
	}

	public Date getErzeugt() {
		return this.erzeugt == null ? null : (Date) this.erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public double getPreis() {
		return this.preis;
	}

	public void setPreis(double preis) {
		this.preis = preis;
	}
	
	public Artikelgruppe getArtikelgruppe() {
		return this.artikelgruppe;
	}

	public void setArtikelgruppe(Artikelgruppe artikelgruppe) {
		this.artikelgruppe = artikelgruppe;
	}
	
	public URI getArtikelgruppeUri() {
		return this.artikelgruppeUri;
	}
	
	public void setArtikelgruppeUri(URI artikelgruppeUri) {
		this.artikelgruppeUri = artikelgruppeUri;
	}
	
	public void setWerte(Artikel a) {
		bezeichnung = a.bezeichnung;
		erhaeltlich = a.erhaeltlich;
		preis = a.preis;
		artikelgruppe = a.artikelgruppe;
		artikelgruppeUri = a.artikelgruppeUri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (erhaeltlich ? 1231 : 1237);
		result = prime * result + ((bezeichnung == null) ? 0 : bezeichnung.hashCode());
		long temp;
		temp = Double.doubleToLongBits(preis);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		
		Artikel other = (Artikel) obj;
		if (erhaeltlich != other.erhaeltlich)
			return false;
		
		if (!bezeichnung.equals(other.bezeichnung))
			return false;
		
		if (Double.doubleToLongBits(preis) != Double.doubleToLongBits(other.preis))
			return false;
	
		return true;
	}

	@Override
	public String toString() {
		return id + ": " + bezeichnung + " " + preis;
	}

}