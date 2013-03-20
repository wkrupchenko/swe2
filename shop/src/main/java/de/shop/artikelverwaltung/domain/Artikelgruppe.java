package de.shop.artikelverwaltung.domain;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import de.shop.util.IdGroup;
import java.net.URI;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import static de.shop.util.Konstante.KEINE_ID;
import static de.shop.util.Konstante.MIN_ID;

@Entity
@Table(name = "artikelgruppe")
@NamedQueries({
	@NamedQuery(name = Artikelgruppe.FINDE_ARTIKELGRUPPEN_SORTIERT_NACH_ID,
			    query = "Select ag From Artikelgruppe ag Order By ag.id"),
	@NamedQuery(name = Artikelgruppe.FINDE_ARTIKELGRUPPE_NACH_ARTIKEL_ID,
				query = "Select ag From Artikelgruppe ag JOIN ag.artikel a Where a.id = :" + Artikelgruppe.PARAM_ID),
	@NamedQuery(name = Artikelgruppe.FINDE_ARTIKELGRUPPE_NACH_NAME,
				query = "From Artikelgruppe ag Where ag.name = :" + Artikelgruppe.PARAM_NAME)
})
@XmlRootElement
public class Artikelgruppe implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final int BEZEICHNUNG_LENGTH_MAX = 32;
	
	private static final String PREFIX = "Artikelgruppe.";
	public static final String FINDE_ARTIKELGRUPPEN_SORTIERT_NACH_ID = PREFIX + "findeArtikelgruppenSortiertNachId";
	public static final String FINDE_ARTIKELGRUPPE_NACH_ARTIKEL_ID = PREFIX + "findeArtikelgruppeNachArtikelId";
	public static final String FINDE_ARTIKELGRUPPE_NACH_NAME = PREFIX + "findeArtikelgruppeNachName";
	
	public static final String PARAM_ID = "id";
	public static final String PARAM_NAME = "name";

	@Id
	@GeneratedValue
	@Column(name = "ag_id")
	@Min(value = MIN_ID, message = "{artikelverwaltung.artikelgruppe.id.min}", groups = IdGroup.class)
	@XmlAttribute
	private Long id = KEINE_ID;

	@Column(name = "ag_name")
	@NotNull(message = "{artikelverwaltung.artikelgruppe.bezeichnung.notNull}")
	@Size(max = BEZEICHNUNG_LENGTH_MAX, message = "{artikelverwaltung.artikelgruppe.bezeichnung.length}")
	@XmlElement(required = true)
	private String name;
	
	@OneToMany
	@JoinColumn(name = "a_artikelgruppe_fk", nullable = false)
	@OrderColumn(name = "a_idx")
	@XmlTransient
	private List<Artikel> artikel;
	
	@Transient
	@XmlElement
	private URI artikelUri;

	public Artikelgruppe() {
	}

	public void setWerte(Artikelgruppe ag) {
		name = ag.name;
		artikel = ag.artikel;
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		
		Artikelgruppe other = (Artikelgruppe) obj;
		if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return id + " " + name;
	}

}