package de.shop.kundenverwaltung.domain;

import static de.shop.util.Konstante.KEINE_ID;
import static de.shop.util.Konstante.MIN_ID;
import static java.util.logging.Level.FINER;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.TemporalType.DATE;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;




import javax.persistence.Column;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
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

/**
 * The persistent class for the kunde database table.
 * 
 */
@Entity
@Table(name = "kunde")
@NamedQueries({
	@NamedQuery(name  = Kunde.FINDE_KUNDE,
            query = "SELECT k"
			        + " FROM   Kunde k"),
	@NamedQuery(name  = Kunde.FINDE_KUNDE_BESTELLUNGEN_ABRUFEN,
			query = "SELECT  DISTINCT k"
					+ " FROM Kunde k LEFT JOIN FETCH k.bestellungen"),
	@NamedQuery(name  = Kunde.FINDE_KUNDE_NACH_NACHNAME,
            query = "FROM Kunde k WHERE k.nachname = :" + Kunde.PARAM_NACHNAME),
    @NamedQuery(name  = Kunde.FINDE_NACHNAMEN_NACH_PREFIX,
	        query = "SELECT   DISTINCT k.nachname"
			        + " FROM  Kunde k "
            		+ " WHERE UPPER(k.nachname) LIKE UPPER(:"
            		+   Kunde.PARAM_KUNDE_NACHNAME_PREFIX + ")"),
    @NamedQuery(name  = Kunde.FINDE_KUNDE_NACH_NACHNAME_BESTELLUNGEN_ABRUFEN,
            query = "SELECT DISTINCT k"
		            + " FROM  Kunde k LEFT JOIN FETCH k.bestellungen"
		            + " WHERE UPPER(k.nachname) = UPPER(:" + Kunde.PARAM_NACHNAME + ")"),
    @NamedQuery(name  = Kunde.FINDE_KUNDE_NACH_ID_BESTELLUNGEN_ABRUFEN,
		    query = "SELECT DISTINCT k"
				    + " FROM  Kunde k LEFT JOIN FETCH k.bestellungen"
				    + " WHERE k.id = :" + Kunde.PARAM_ID),
	@NamedQuery(name  = Kunde.FINDE_IDS_NACH_PREFIX,
			query = "SELECT   k.id"
			        + " FROM  Kunde k"
			        + " WHERE CONCAT('', k.id) LIKE :" + Kunde.PARAM_KUNDE_ID_PREFIX
			        + " ORDER BY k.id"),
    @NamedQuery(name = Kunde.FINDE_KUNDE_NACH_PLZ,
            query = "FROM Kunde k WHERE k.adresse.plz = :" + Kunde.PARAM_PLZ),
    @NamedQuery(name  = Kunde.FINDE_KUNDE_SORTIERT_NACH_ID,
	        query = "SELECT   k"
			        + " FROM  Kunde k"
	                + " ORDER BY k.id"),
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
            query = "FROM Kunde k WHERE k.newsletter = TRUE")
            			        
})
public class Kunde implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final String PREFIX = "Kunde.";
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
	public static final String LÖSCHE_KUNDE_NACH_NACHNAME =
	PREFIX + "löscheKundeNachNachname";
	
	public static final String PARAM_ID = "kundeId";
	public static final String PARAM_KUNDE_ID_PREFIX = "idPrefix";
	public static final String PARAM_NACHNAME = "nachname";
	public static final String PARAM_KUNDE_NACHNAME_PREFIX = "nachnamePrefix";
	public static final String PARAM_PLZ = "plz";
	public static final String PARAM_DATUM = "erzeugt";
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_UMSATZ = "umsatz";
	public static final String PARAM_ART = "art";
	
	private static final String NAME_PATTERN = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF]+";
	private static final String PREFIX_ADEL = "(o'|von|von der|von und zu|van)?";
	
	public static final String NACHNAME_PATTERN = PREFIX_ADEL + NAME_PATTERN + "(-" + NAME_PATTERN + ")?";
	public static final int NACHNAME_LAENGE_MIN = 2;
	public static final int NACHNAME_LAENGE_MAX = 32;
	public static final int VORNAME_LAENGE_MAX = 32;
	public static final int EMAIL_LAENGE_MAX = 128;
	public static final int DETAILS_LAENGE_MAX = 128 * 1024;
	public static final int PASSWORT_LAENGE_MAX = 256;
	
	public static final String PRIVATKUNDE = "P";
	public static final String FIRMENKUNDE = "F";
	
	
	@Id
	@GeneratedValue
	@Column(name = "k_id", unique = true, nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{kundenverwaltung.kunde.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;

	@Temporal(DATE)
	@Column(name = "k_aktualisiert", nullable = false)
	@JsonIgnore
	private Date aktualisiert;

	@Column(name = "k_art")
	private String art;

	@Column(name = "k_email", length = EMAIL_LAENGE_MAX, nullable = false, unique = true)
	@Email(message = "{kundenverwaltung.kunde.email.pattern}")
	private String email;

	@Temporal(DATE)
	@Column(name = "k_erzeugt", nullable = false)
	@JsonIgnore
	private Date erzeugt;

	@Column(name = "k_familienstand")
	private FamilienstandTyp familienstand;

	@Column(name = "k_geschlecht")
	private GeschlechtTyp geschlecht;

	@Column(name = "k_nachname", length = NACHNAME_LAENGE_MAX, nullable = false)
	@NotNull(message = "{kundenverwaltung.kunde.nachname.notNull}")
	@Size(min = NACHNAME_LAENGE_MIN, max = NACHNAME_LAENGE_MAX,
	      message = "{kundenverwaltung.kunde.nachname.length}")
	@Pattern(regexp = NACHNAME_PATTERN, message = "{kundenverwaltung.kunde.nachname.pattern}")
	private String nachname;
	
	@Column(name = "k_vorname", length = VORNAME_LAENGE_MAX, nullable = false)
	@Size(max = VORNAME_LAENGE_MAX, message = "{kundenverwaltung.kunde.vorname.length}")
	private String vorname;

	@Column(name = "k_newsletter")
	private boolean newsletter;

	@Column(name = "k_passwort", nullable = false, length = PASSWORT_LAENGE_MAX)
	private String passwort;
	
	@Transient
	private String passwortWdh;
	
	@AssertTrue(groups = PasswordGroup.class, message = "{kundenverwaltung.kunde.passwort.notEqual}")
	public boolean isPasswortEqual() {
		if (passwort == null) {
			return passwortWdh == null;
		}
		return passwort.equals(passwortWdh);
	}

	@Column(name = "k_rabatt")
	private double rabatt;

	@Temporal(DATE)
	@Column(name = "k_seit")
	@Past(message = "{kundenverwaltung.kunde.seit.past}")
	private Date seit;

	@Column(name = "k_umsatz")
	private double umsatz;
	
	
	@OneToOne(cascade = {PERSIST, REMOVE })
	@JoinColumn(name = "k_adresse_fk")
	@Valid
	@NotNull(message = "{kundenverwaltung.kunde.adresse.notNull}")
	private Adresse adresse;
	
	@OneToMany
	@JoinColumn(name = "b_kunde_fk", nullable = false)
	@OrderColumn(name = "b_idx", nullable = false)
	@JsonIgnore
	private List<Bestellung> bestellungen;
	
	@Transient
	@JsonProperty("bestellungen")
	private URI bestellungenUri;
	
	@PrePersist
	protected void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PostPersist
	protected void postPersist() {
		LOGGER.log(FINER, "Neuer Kunde mit ID={0}", id);
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
		nachname = k.nachname;
		vorname = k.vorname;
		umsatz = k.umsatz;
		rabatt = k.rabatt;
		seit = k.seit;
		email = k.email;
		passwort = k.passwort;
		passwortWdh = k.passwort;
	}
	

	public Kunde() {
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

	public Date getErzeugt() {
		return this.erzeugt == null ? null : (Date) this.erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
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

	public double getRabatt() {
		return this.rabatt;
	}

	public void setRabatt(double rabatt) {
		this.rabatt = rabatt;
	}

	public Date getSeit() {
		return this.seit == null ? null : (Date) this.seit.clone();
	}

	public void setSeit(Date seit) {
		this.seit = seit == null ? null : (Date) seit.clone();
	}

	public double getUmsatz() {
		return this.umsatz;
	}

	public void setUmsatz(double umsatz) {
		this.umsatz = umsatz;
	}

	public String getVorname() {
		return this.vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}
	
	public Adresse getAdresse() {
		return this.adresse;
	}
	
	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
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
		return id + ": " + nachname + " " + vorname + " ";
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
		
		Kunde other = (Kunde) obj;
		if (email == null)
			if (other.email != null)
				return false;
		return true;
	}
}
