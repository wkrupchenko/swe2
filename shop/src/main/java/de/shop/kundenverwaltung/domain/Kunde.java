package de.shop.kundenverwaltung.domain;

import static de.shop.util.Konstante.KEINE_ID;
import static de.shop.util.Konstante.MIN_ID;
import static de.shop.util.Konstante.ERSTE_VERSION;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;

import javax.persistence.UniqueConstraint;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jboss.logging.Logger;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.Email;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.IdGroup;
import de.shop.auth.service.jboss.AuthService.RolleType;

/**
 * The persistent class for the kunde database table.
 * 
 */
@Entity
@Table(name = "kunde")
@NamedQueries({
	@NamedQuery(name  = Kunde.FINDE_KUNDE,
            query = "SELECT k FROM   Kunde k"),
	@NamedQuery(name  = Kunde.FINDE_KUNDE_BESTELLUNGEN_ABRUFEN,
			query = "SELECT  DISTINCT k FROM Kunde k LEFT JOIN FETCH k.bestellungen"),
	@NamedQuery(name  = Kunde.FINDE_KUNDE_NACH_NACHNAME,
            query = "FROM Kunde k WHERE k.nachname = :" + Kunde.PARAM_NACHNAME),
    @NamedQuery(name  = Kunde.FINDE_NACHNAMEN_NACH_PREFIX,
	        query = "SELECT   DISTINCT k.nachname FROM  Kunde k WHERE UPPER(k.nachname) LIKE UPPER(:"
            		+   Kunde.PARAM_KUNDE_NACHNAME_PREFIX + ")"),
    @NamedQuery(name  = Kunde.FINDE_KUNDE_NACH_NACHNAME_BESTELLUNGEN_ABRUFEN,
            query = "SELECT DISTINCT k FROM  Kunde k LEFT JOIN FETCH k.bestellungen"
            		+ " WHERE UPPER(k.nachname) = UPPER(:" + Kunde.PARAM_NACHNAME + ")"),
    @NamedQuery(name  = Kunde.FINDE_KUNDE_NACH_ID_BESTELLUNGEN_ABRUFEN,
		    query = "SELECT DISTINCT k FROM  Kunde k LEFT JOIN FETCH k.bestellungen"
				    + " WHERE k.id = :" + Kunde.PARAM_ID),
	@NamedQuery(name  = Kunde.FINDE_IDS_NACH_PREFIX,
			query = "SELECT   k.id FROM  Kunde k WHERE CONCAT('', k.id) LIKE :" + Kunde.PARAM_KUNDE_ID_PREFIX
			        + " ORDER BY k.id"),
    @NamedQuery(name = Kunde.FINDE_KUNDE_NACH_PLZ,
            query = "FROM Kunde k WHERE k.adresse.plz = :" + Kunde.PARAM_PLZ),
    @NamedQuery(name  = Kunde.FINDE_KUNDE_SORTIERT_NACH_ID,
	        query = "SELECT k FROM  Kunde k ORDER BY k.id"),
	@NamedQuery(name = Kunde.FINDE_KUNDE_NACH_DATUM,
			query = "FROM Kunde k WHERE k.seit = :" + Kunde.PARAM_DATUM),
    @NamedQuery(name = Kunde.FINDE_KUNDE_NACH_EMAIL,
            query = "FROM Kunde k WHERE k.email = :" + Kunde.PARAM_EMAIL),
    @NamedQuery(name = Kunde.FINDE_KUNDE_NACH_MIN_UMSATZ,
            query = "FROM Kunde k WHERE k.umsatz >= :" + Kunde.PARAM_UMSATZ),
    @NamedQuery(name = Kunde.FINDE_KUNDE_NACH_MAX_UMSATZ,
            query = "FROM Kunde k WHERE k.umsatz <= :" + Kunde.PARAM_UMSATZ),
    @NamedQuery(name = Kunde.FINDE_KUNDE_NACH_ART,
            query = "FROM Kunde k WHERE k.art = :" + Kunde.PARAM_ART),
    @NamedQuery(name = Kunde.FINDE_KUNDE_NACH_NEWSLETTER,
            query = "FROM Kunde k WHERE k.newsletter = TRUE"),
    @NamedQuery(name = Kunde.FINDE_KUNDE_NACH_BESTELLUNG,
    		query = "SELECT k FROM Kunde k JOIN k.bestellungen b WHERE b.id = :" + Kunde.PARAM_BESTELLUNG_ID),    		
    @NamedQuery(name  = Kunde.FIND_KUNDE_BY_USERNAME,
            query = "SELECT k FROM  Kunde k WHERE CONCAT('', k.id) = :" + Kunde.PARAM_KUNDE_USERNAME),
    @NamedQuery(name  = Kunde.FIND_USERNAME_BY_USERNAME_PREFIX,
	            query = "SELECT   CONCAT('', k.id) FROM  Kunde k"
	            		+ " WHERE CONCAT('', k.id) LIKE :" + Kunde.PARAM_USERNAME_PREFIX)
            			        
})
public class Kunde implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final String PREFIX = "Kunde.";
	public static final String FIND_KUNDE_BY_USERNAME = PREFIX + "findKundeByUsername";
	public static final String FIND_USERNAME_BY_USERNAME_PREFIX = PREFIX + "findKundeByUsernamePrefix";
	public static final String FINDE_KUNDE = PREFIX + "findeKunde";
	public static final String FINDE_KUNDE_BESTELLUNGEN_ABRUFEN = 
	PREFIX + "findeKundeBestellungenAbrufen";
	public static final String FINDE_KUNDE_NACH_NACHNAME =
	PREFIX + "findeKundeNachNachname";
	public static final String FINDE_NACHNAMEN_NACH_PREFIX = 
	PREFIX + "findeNachnamenNachPrefix";
	public static final String FINDE_KUNDE_NACH_NACHNAME_BESTELLUNGEN_ABRUFEN =
    PREFIX + "findeKundeNachNachnameBestellungenAbrufen";
	public static final String FINDE_KUNDE_NACH_ID_BESTELLUNGEN_ABRUFEN =
    PREFIX + "findeKundeNachIdBestellungenAbrufen";
	public static final String FINDE_IDS_NACH_PREFIX =
	PREFIX + "findeIdsNachPrefix";
	public static final String FINDE_KUNDE_NACH_PLZ =
	PREFIX + "findeKundeNachPlz";
	public static final String FINDE_KUNDE_SORTIERT_NACH_ID =
	PREFIX + "findeKundeSortiertNachId";
	public static final String FINDE_KUNDE_NACH_DATUM =
    PREFIX + "findeKundeNachDatum";
	public static final String FINDE_KUNDE_NACH_EMAIL =
	PREFIX + "findeKundeNachEmail";
	public static final String FINDE_KUNDE_NACH_MIN_UMSATZ =
	PREFIX + "findeKundeNachMinUmsatz";
	public static final String FINDE_KUNDE_NACH_MAX_UMSATZ =
    PREFIX + "findeKundeNachMaxUmsatz";
	public static final String FINDE_KUNDE_NACH_ART =
    PREFIX + "findeKundeNachArt";
	public static final String FINDE_KUNDE_NACH_NEWSLETTER =
    PREFIX + "findeKundeNachNewsletter";
	public static final String LOESCHE_KUNDE_NACH_NACHNAME =
	PREFIX + "löscheKundeNachNachname";
	public static final String FINDE_KUNDE_NACH_BESTELLUNG =
	PREFIX + "findeKundeNachBestellung";
	
	public static final String PARAM_USERNAME_PREFIX = "usernamePrefix";
	public static final String PARAM_ID = "kundeId";
	public static final String PARAM_KUNDE_ID_PREFIX = "idPrefix";
	public static final String PARAM_NACHNAME = "nachname";
	public static final String PARAM_KUNDE_NACHNAME_PREFIX = "nachnamePrefix";
	public static final String PARAM_PLZ = "plz";
	public static final String PARAM_DATUM = "erzeugt";
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_UMSATZ = "umsatz";
	public static final String PARAM_ART = "art";
	public static final String PARAM_KUNDE_USERNAME = "username";
	public static final String PARAM_BESTELLUNG_ID = "bestellungId";
	
	private static final String NAME_PATTERN = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF]+";
	private static final String PREFIX_ADEL = "(o'|von|von der|von und zu|van)?";
	
	public static final String NACHNAME_PATTERN = PREFIX_ADEL + NAME_PATTERN + "(-" + NAME_PATTERN + ")?";
	public static final int NACHNAME_LAENGE_MIN = 2;
	public static final int NACHNAME_LAENGE_MAX = 32;
	public static final int VORNAME_LAENGE_MAX = 32;
	public static final int EMAIL_LAENGE_MAX = 128;
	public static final int DETAILS_LAENGE_MAX = 128 * 1024;
	public static final int PASSWORT_LAENGE_MAX = 256;
	public static final int ART_LAENGE_MIN_MAX = 1;
	
	public static final String PRIVATKUNDE = "P";
	public static final String FIRMENKUNDE = "F";
	
	
	@Id
	@GeneratedValue
	@Column(nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{kundenverwaltung.kunde.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;

	@Column(length = ART_LAENGE_MIN_MAX)
	@Size(min = ART_LAENGE_MIN_MAX, max = ART_LAENGE_MIN_MAX, message = "{kundenverwaltung.kunde.art.length}")
	private String art;

	@Column(length = EMAIL_LAENGE_MAX, unique = true)
	@Email(message = "{kundenverwaltung.kunde.email.pattern}")
	private String email;

	@Column(name = "familienstand_fk")
	private FamilienstandTyp familienstand;

	@Column(name = "geschlecht_fk")
	private GeschlechtTyp geschlecht;

	@Column(length = NACHNAME_LAENGE_MAX, nullable = false)
	@NotNull(message = "{kundenverwaltung.kunde.nachname.notNull}")
	@Size(min = NACHNAME_LAENGE_MIN, max = NACHNAME_LAENGE_MAX,
	      message = "{kundenverwaltung.kunde.nachname.length}")
	@Pattern(regexp = NACHNAME_PATTERN, message = "{kundenverwaltung.kunde.nachname.pattern}")
	private String nachname;
	
	@Column(length = VORNAME_LAENGE_MAX, nullable = false)
	@Size(max = VORNAME_LAENGE_MAX, message = "{kundenverwaltung.kunde.vorname.length}")
	private String vorname;

	@Column
	private boolean newsletter;

	@Column(length = PASSWORT_LAENGE_MAX, nullable = false)
	@Size(max = PASSWORT_LAENGE_MAX, message = "{kundenverwaltung.kunde.password.length}")
	private String passwort;
	
	@Transient
	@JsonIgnore
	private String passwortWdh;
	
	@AssertTrue(groups = PasswordGroup.class, message = "{kundenverwaltung.kunde.passwort.notEqual}")
	@JsonIgnore
	public boolean isPasswortEqual() {
		if (passwort == null) {
			return passwortWdh == null;
		}
		return passwort.equals(passwortWdh);
	}

	@Column(nullable = false, precision = 5, scale = 4)
	private BigDecimal rabatt;

	@Column(nullable = false, precision = 15, scale = 3)
	private BigDecimal umsatz;
	
	@Temporal(DATE)
	@Column
	@Past(message = "{kundenverwaltung.kunde.seit.past}")
	private Date seit;
	
	@OneToOne(mappedBy = "kunde", cascade = { PERSIST, REMOVE })
	@Valid
	@NotNull(message = "{kundenverwaltung.kunde.adresse.notNull}")
	private Adresse adresse;
	
	@OneToMany(fetch = EAGER)
	@JoinColumn(name = "kunde_fk", nullable = false)
	@OrderColumn(name = "idx", nullable = false)
	@JsonIgnore
	private List<Bestellung> bestellungen;
	
	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "kunde_rolle",
	                 joinColumns = @JoinColumn(name = "kunde_fk", nullable = false),
	                 uniqueConstraints =  @UniqueConstraint(columnNames = { "kunde_fk", "rolle_fk" }))
	@Column(table = "kunde_rolle", name = "rolle_fk", nullable = false)
	private Set<RolleType> rollen;
	
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
	
	@PrePersist
	protected void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PostPersist
	protected void postPersist() {
		LOGGER.debugf("Neuer Kunde mit ID=%d", id);
	}
	
	@PostUpdate
	protected void postUpdate() {
		LOGGER.debugf("Kunde mit ID=%d aktualisiert: version=%d", id, version);
	}
	
	@PreUpdate
	protected void preUpdate() {
		aktualisiert = new Date();
	}
	
	@PostLoad
	protected void postLoad() {
		passwortWdh = passwort;
	}
	
	
	public void setWerte(Kunde k) {
		version = k.version;
		art = k.art;
		familienstand = k.familienstand;
		geschlecht = k.geschlecht;
		newsletter = k.newsletter;
		nachname = k.nachname;
		vorname = k.vorname;
		umsatz = k.umsatz;
		rabatt = k.rabatt;
		seit = k.seit;
		email = k.email;
		passwort = k.passwort;
		passwortWdh = k.passwort;
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

	public String getArt() {
		return this.art;
	}

	public void setArt(String art) {
		this.art = art;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public FamilienstandTyp getFamilienstand() {
		return this.familienstand;
	}

	public void setFamilienstand(FamilienstandTyp familienstand) {
		this.familienstand = familienstand;
	}

	public GeschlechtTyp getGeschlecht() {
		return this.geschlecht;
	}

	public void setGeschlecht(GeschlechtTyp geschlecht) {
		this.geschlecht = geschlecht;
	}

	public String getNachname() {
		return this.nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public boolean getNewsletter() {
		return this.newsletter;
	}

	public void setNewsletter(boolean newsletter) {
		this.newsletter = newsletter;
	}

	public String getPasswort() {
		return this.passwort;
	}

	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}
	
	public String getPasswortWdh() {
		return this.passwortWdh;
	}

	public void setPasswortWdh(String passwortWdh) {
		this.passwortWdh = passwortWdh;
	}

	public BigDecimal getRabatt() {
		return this.rabatt;
	}

	public void setRabatt(BigDecimal rabatt) {
		this.rabatt = rabatt;
	}

	public BigDecimal getUmsatz() {
		return this.umsatz;
	}

	public void setUmsatz(BigDecimal umsatz) {
		this.umsatz = umsatz;
	}

	public String getVorname() {
		return this.vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}
	
	public Date getSeit() {
		return this.seit == null ? null : (Date) this.seit.clone();
	}

	public void setSeit(Date seit) {
		this.seit = seit == null ? null : (Date) seit.clone();
	}
	
	public Set<RolleType> getRollen() {
		return rollen;
	}

	public void setRollen(Set<RolleType> rollen) {
		this.rollen = rollen;
	}
	
	public Adresse getAdresse() {
		return this.adresse;
	}
	
	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
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
	
	public List<Bestellung> getBestellungen() {
		return Collections.unmodifiableList(bestellungen);
	}
	
	public void setBestellungen(List<Bestellung> bestellungen) {
		if (this.bestellungen == null) {
			this.bestellungen = bestellungen;
			return;
		}
		
		this.bestellungen.clear();
		if (bestellungen != null)
			this.bestellungen.addAll(bestellungen);
	}
	
	public Kunde addBestellung(Bestellung bestellung) {
		if (this.bestellungen == null)
			this.bestellungen = new ArrayList<>();
			
		this.bestellungen.add(bestellung);
		return this;
	}
	
	public URI getBestellungenUri() {
		return bestellungenUri;
	}
	
	public void setBestellungenUri(URI bestellungenUri) {
		this.bestellungenUri = bestellungenUri;
	}
	
	@Override
	public String toString() {
		return "Kunde [id=" + id + ", version=" + version + ", nachname=" + nachname + ", vorname=" + vorname + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
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
		
		final Kunde other = (Kunde) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		}
		else if (!email.equals(other.email)) {
			return false;
			}
		return true;
	}
	
}
