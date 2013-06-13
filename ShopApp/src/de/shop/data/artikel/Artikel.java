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

public class Artikel implements JsonMappable, Serializable {
	private static final long serialVersionUID = 1L;

	public Long id;
	public int version;
	public String bezeichnung;
	public double preis;
	public boolean erhaeltlich;
	public String artikelgruppeUri;

	
	protected JsonObjectBuilder getJsonObjectBuilder() {
		return jsonBuilderFactory.createObjectBuilder()
				                 .add("id", id)
			                     .add("version", version)
			                     .add("bezeichnung", bezeichnung)
			                     .add("preis", preis)
			                     .add("erhaeltlich", erhaeltlich)
			                     .add("artikelgruppeUri", artikelgruppeUri);
	}
	
	@Override
	public JsonObject toJsonObject() {
		return getJsonObjectBuilder().build();
	}

	public void fromJsonObject(JsonObject jsonObject) {
		id = Long.valueOf(jsonObject.getJsonNumber("id").longValue());
	    version = jsonObject.getInt("version");
		bezeichnung = jsonObject.getString("bezeichnung");
		preis = jsonObject.getJsonNumber("preis").doubleValue();
		erhaeltlich= jsonObject.getBoolean("erhaeltlich");
		artikelgruppeUri = jsonObject.getString("artikelgruppeUri");
	}
	
	@Override
	public void updateVersion() {
		version++;
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