package de.shop.artikelverwaltung.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.logging.Logger;

import de.shop.util.IdGroup;

import java.lang.invoke.MethodHandles;
import java.net.URI;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import static de.shop.util.Konstante.ERSTE_VERSION;
import static de.shop.util.Konstante.KEINE_ID;
import static de.shop.util.Konstante.MIN_ID;

@Entity
@Table(name = "artikelgruppe")
@NamedQueries({
	@NamedQuery(name = Artikelgruppe.FINDE_ARTIKELGRUPPEN_SORTIERT_NACH_ID,
			    query = "Select ag From Artikelgruppe ag Order By ag.id"),
	@NamedQuery(name = Artikelgruppe.FINDE_ARTIKELGRUPPE_NACH_ARTIKEL_ID,
				query = "Select ag From Artikelgruppe ag JOIN ag.artikel a Where a.id = :" + Artikelgruppe.PARAM_ID),
	@NamedQuery(name = Artikelgruppe.FINDE_ARTIKELGRUPPE_NACH_BEZEICHNUNG,
				query = "From Artikelgruppe ag Where ag.bezeichnung = :" + Artikelgruppe.PARAM_BEZEICHNUNG)
})
public class Artikelgruppe implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final int BEZEICHNUNG_LENGTH_MAX = 32;
	
	private static final String PREFIX = "Artikelgruppe.";
	public static final String FINDE_ARTIKELGRUPPEN_SORTIERT_NACH_ID = PREFIX + "findeArtikelgruppenSortiertNachId";
	public static final String FINDE_ARTIKELGRUPPE_NACH_ARTIKEL_ID = PREFIX + "findeArtikelgruppeNachArtikelId";
	public static final String FINDE_ARTIKELGRUPPE_NACH_BEZEICHNUNG = PREFIX + "findeArtikelgruppeNachBEZEICHNUNG";
	
	public static final String PARAM_ID = "id";
	public static final String PARAM_BEZEICHNUNG = "bezeichnung";

	@Id
	@GeneratedValue
	@Column(nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{artikelverwaltung.artikelgruppe.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;

	@Column(length = BEZEICHNUNG_LENGTH_MAX, nullable = false)
	@NotNull(message = "{artikelverwaltung.artikelgruppe.bezeichnung.notNull}")
	@Size(max = BEZEICHNUNG_LENGTH_MAX, message = "{artikelverwaltung.artikelgruppe.bezeichnung.length}")
	private String bezeichnung;
	
	@OneToMany
	@JoinColumn(name = "artikelgruppe_fk", nullable = false)
	@JsonIgnore
	private List<Artikel> artikel;
	
	@Transient
	@JsonProperty("artikel")
	private URI artikelUri;
	
	@PostPersist
	private void postPersist() {
		LOGGER.debugf("Neue Artikelgruppe mit ID=%s", id);
	}
	
	@PostUpdate
	private void postUpdate() {
		LOGGER.debugf("Artikelgruppe mit ID=%s aktualisiert: version=%d", id, version);
	}

	public void setWerte(Artikelgruppe ag) {
		version = ag.version;
		bezeichnung = ag.bezeichnung;
		artikel = ag.artikel;
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
	
	public List<Artikel> getArtikel() {
		return this.artikel == null ? null : Collections.unmodifiableList(artikel);
	}
	
	public void setArtikel(List<Artikel> artikel) {
		if (this.artikel == null) {
			this.artikel = artikel;
			return;
		}
		
		this.artikel.clear();
		if (artikel != null)
			this.artikel.addAll(artikel);
	}
	
	public Artikelgruppe addArtikel(Artikel a) {
		if (this.artikel == null) {
			this.artikel = new ArrayList<>();
		}
		this.artikel.add(a);
		return this;
	}
	
	public URI getArtikelUri() {
		return this.artikelUri;
	}
	
	public void setArtikelUri(URI artikelUri) {
		this.artikelUri = artikelUri;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bezeichnung == null) ? 0 : bezeichnung.hashCode());
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
		
		final Artikelgruppe other = (Artikelgruppe) obj;
		if (!bezeichnung.equals(other.bezeichnung))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Artikel [id=" + id + ", version=" + version + ", bezeichnung=" + bezeichnung + "]";
	}

}
