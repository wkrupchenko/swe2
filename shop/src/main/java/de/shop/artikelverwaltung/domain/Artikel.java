package de.shop.artikelverwaltung.domain;

import static de.shop.util.Konstante.ERSTE_VERSION;
import static de.shop.util.Konstante.KEINE_ID;
import static de.shop.util.Konstante.MIN_ID;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jboss.logging.Logger;

import de.shop.util.File;
import de.shop.util.IdGroup;
import de.shop.util.PreExistingGroup;

@Entity
@Table(name = "artikel")
@NamedQueries({
	@NamedQuery(name = Artikel.FINDE_ARTIKEL_NACH_BEZ,
				query = "FROM Artikel a WHERE a.bezeichnung = :" + Artikel.PARAM_BEZ),
    @NamedQuery(name  = Artikel.FINDE_BEZEICHNUNGEN_NACH_PREFIX,
		        query = "SELECT DISTINCT a.bezeichnung FROM  Artikel a WHERE UPPER(a.bezeichnung) LIKE UPPER(:"	
		                +   Artikel.PARAM_BEZEICHNUNG_PREFIX + ")"),
	@NamedQuery(name = Artikel.FINDE_VERFUEGBARE_ARTIKEL,
				query = "FROM Artikel a WHERE a.erhaeltlich = TRUE"),
	@NamedQuery(name = Artikel.FINDE_NICHT_VERFUEGBARE_ARTIKEL,
				query = "FROM Artikel a WHERE a.erhaeltlich = FALSE"),
	@NamedQuery(name = Artikel.FINDE_ARTIKEL_NACH_MAX_PREIS,
				query = "FROM Artikel a WHERE a.preis <= :" + Artikel.PARAM_PREIS),
	@NamedQuery(name = Artikel.FINDE_ARTIKEL_NACH_MIN_PREIS,
				query = "FROM Artikel a WHERE a.preis >= :" + Artikel.PARAM_PREIS),
	@NamedQuery(name = Artikel.FINDE_ARTIKEL_NACH_ARTIKELGRUPPE,
				query = "Select a FROM Artikel a JOIN a.artikelgruppe ag "
						+ "WHERE ag.bezeichnung = :" + Artikel.PARAM_NAME),
	@NamedQuery(name = Artikel.FINDE_ARTIKEL_SORTIERT_NACH_ID,
				query = "Select a From Artikel a Order By a.id"),
	@NamedQuery(name = Artikel.FINDE_ARTIKEL_NACH_ARTIKELGRUPPE_ID,
				query = "Select a From Artikel a JOIN a.artikelgruppe ag WHERE ag.id = :" + Artikel.PARAM_ID),
	@NamedQuery(name  = Artikel.FINDE_LADENHUETER,
   	            query = "SELECT a FROM Artikel a WHERE  a NOT IN (SELECT bp.artikel FROM Bestellposition bp)")
})
public class Artikel implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String PREFIX = "Artikel.";
	public static final String FINDE_ARTIKEL_NACH_BEZ = PREFIX + "findeArtikelNachBezeichnung";
	public static final String FINDE_BEZEICHNUNGEN_NACH_PREFIX = PREFIX + "findeBezeichnungenNachPrefix";
	public static final String FINDE_VERFUEGBARE_ARTIKEL = PREFIX + "findeVerfuegbareArtikel";
	public static final String FINDE_NICHT_VERFUEGBARE_ARTIKEL = PREFIX + "findeNichtVerfuegbareArtikel";
	public static final String FINDE_ARTIKEL_NACH_MAX_PREIS = PREFIX + "findeArtikelNachMaxPreis";
	public static final String FINDE_ARTIKEL_NACH_MIN_PREIS = PREFIX + "findeArtikelNachMinPreis";
	public static final String FINDE_ARTIKEL_NACH_ARTIKELGRUPPE = PREFIX + "findeArtikelNachArtikelgruppe";
	public static final String FINDE_ARTIKEL_SORTIERT_NACH_ID = PREFIX + "findeArtikelSortiertNachId";
	public static final String FINDE_ARTIKEL_NACH_ARTIKELGRUPPE_ID = PREFIX + "findeArtikelNachArtikelgruppeId";
	public static final String FINDE_LADENHUETER = PREFIX + "findeLadenhueter";
	
	public static final String PARAM_BEZ = "bezeichnung";
	public static final String PARAM_BEZEICHNUNG_PREFIX = "bezeichnungPrefix";
	public static final String PARAM_PREIS = "preis";
	public static final String PARAM_NAME = "name";
	public static final String PARAM_ID = "id";
	
	private static final int BEZEICHNUNG_LENGTH_MAX = 32;
	
	
	@Id
	@GeneratedValue
	@Column(nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{artikelverwaltung.artikel.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;

	@Column(length = BEZEICHNUNG_LENGTH_MAX, nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.bezeichnung.notNull}")
	@Size(max = BEZEICHNUNG_LENGTH_MAX, message = "{artikelverwaltung.artikel.bezeichnung.length}")
	private String bezeichnung;

	@Column
	private boolean erhaeltlich;

	@Column(nullable = false, precision = 10, scale = 2)
	private double preis;
	
	@ManyToOne(optional = false, cascade = { PERSIST, MERGE, REMOVE })
	@JoinColumn(name = "artikelgruppe_fk", nullable = false, insertable = false, updatable = false)
	@NotNull(message = "{artikelverwaltung.artikel.artikelgruppe.notNull}", groups = PreExistingGroup.class)
	@JsonIgnore
	private Artikelgruppe artikelgruppe;
	
	@Transient
	@JsonProperty("artikelgruppe")
	private URI artikelgruppeUri;
	
	@OneToOne(fetch = EAGER, cascade = { PERSIST, REMOVE })
	@JoinColumn(name = "file_fk")
	@JsonIgnore
	private File file;
	
	@Transient
	private URI fileUri;
	
	@Temporal(TIMESTAMP)
	@Column(nullable = false)
	@JsonIgnore
	private Date erzeugt;
	
	@Temporal(TIMESTAMP)
	@Column(nullable = false)
	@JsonIgnore
	private Date aktualisiert;

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
		LOGGER.debugf("Neuer Artikel mit ID=%s", id);
	}
	
	@PostUpdate
	private void postUpdate() {
		LOGGER.debugf("Artikel mit ID=%s aktualisiert: version=%d", id, version);
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
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public URI getFileUri() {
		return fileUri;
	}

	public void setFileUri(URI fileUri) {
		this.fileUri = fileUri;
	}
	
	public Date getErzeugt() {
		return this.erzeugt == null ? null : (Date) this.erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public Date getAktualisiert() {
		return this.aktualisiert == null ? null : (Date) this.aktualisiert.clone();
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert.clone();
	}
	
	public void setWerte(Artikel a) {
		version = a.version;
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
		
		final Artikel other = (Artikel) obj;
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
		return "Artikel [id=" + id + ", version=" + version + ", bezeichnung=" + bezeichnung + ", preis=" + preis + "]";
	}

}
