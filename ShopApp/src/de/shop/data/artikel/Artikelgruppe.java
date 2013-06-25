package de.shop.data.artikel;

import static de.shop.ShopApp.jsonBuilderFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import de.shop.data.JsonMappable;
import de.shop.util.InternalShopError;

public class Artikelgruppe implements JsonMappable, Serializable {
	private static final long serialVersionUID = 1L;

	public Long id;
	public int version;
	public String bezeichnung;
	public String artikelUri;

	
	protected JsonObjectBuilder getJsonObjectBuilder() {
		return jsonBuilderFactory.createObjectBuilder()
				                 .add("id", id)
			                     .add("version", version)
			                     .add("artikel", artikelUri);
	}
	
	@Override
	public JsonObject toJsonObject() {
		return getJsonObjectBuilder().build();
	}

	public void fromJsonObject(JsonObject jsonObject) {
		id = Long.valueOf(jsonObject.getJsonNumber("id").longValue());
	    version = jsonObject.getInt("version");
		bezeichnung = jsonObject.getString("bezeichnung");
		artikelUri = jsonObject.getString("artikel");
	}
	
	@Override
	public void updateVersion() {
		version++;
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
		return "Artikelgruppe [id=" + id + ", version=" + version + ", bezeichnung=" + bezeichnung + "]";
	}
}