package de.shop.data.kunde;

import static de.shop.ShopApp.jsonBuilderFactory;

import java.io.Serializable; 
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import de.shop.data.kunde.FamilienstandTyp;
import de.shop.data.JsonMappable;
import de.shop.util.InternalShopError;

public class Kunde implements JsonMappable, Serializable {
	private static final long serialVersionUID = 1293068472891525321L;
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	
	public Long id;
	public String art;
	public String email;	 
	public GeschlechtTyp geschlecht;
	public FamilienstandTyp familienstand;
	public String nachname;
	public String vorname;
	public Double rabatt;
	public Double umsatz;
	public Date seit;
	public int version;
	public String bestellungenUri;
	public Adresse adresse;	 
	 
		

	protected JsonObjectBuilder getJsonObjectBuilder() {
		return jsonBuilderFactory.createObjectBuilder()
				                 .add("id", id)
			                     .add("version", version)
			                     .add("art", art)
			                     .add("email", email)			                      			                      
		                         .add("nachname", nachname)
		                         .add("vorname", vorname)
		                         .add("rabatt", rabatt)
		                         .add("umsatz", umsatz)
		                         .add("adresse", adresse.getJsonBuilderFactory())
		                         .add("seit", new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(seit))
		                         .add("geschlecht", geschlecht.toString())
			                     .add("familienstand", familienstand.toString())
		                         .add("bestellungenUri", bestellungenUri);
	}
	
	@Override
	public JsonObject toJsonObject() {
		return getJsonObjectBuilder().build();
	}

	public void fromJsonObject(JsonObject jsonObject) {
		id = Long.valueOf(jsonObject.getJsonNumber("id").longValue());
	    version = jsonObject.getInt("version");
		art = jsonObject.getString("art");
		email = jsonObject.getString("email");
		geschlecht = GeschlechtTyp.valueOf(jsonObject.getString("geschlecht"));
		familienstand = FamilienstandTyp.valueOf(jsonObject.getString("familienstand"));		 
		nachname = jsonObject.getString("nachname");
		vorname = jsonObject.getString("vorname");		 
		rabatt = jsonObject.getJsonNumber("rabatt").doubleValue();
		umsatz = jsonObject.getJsonNumber("umsatz").doubleValue();
		adresse = new Adresse();
		adresse.fromJsonObject(jsonObject.getJsonObject("adresse")); 
		try {
			seit = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(jsonObject.getString("seit"));
		}
		catch (ParseException e) {
			throw new InternalShopError(e.getMessage(), e);
		};
		 
		bestellungenUri = jsonObject.getString("bestellungen");
	}
	
	@Override
	public void updateVersion() {
		version++;
	}
	
	 
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((art == null) ? 0 : art.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((familienstand == null) ? 0 : familienstand.hashCode());
		result = prime * result + ((geschlecht == null) ? 0 : geschlecht.hashCode());
		result = prime * result + ((nachname == null) ? 0 : nachname.hashCode());
		result = prime * result + ((vorname == null) ? 0 : vorname.hashCode());
		result = prime * result + ((seit == null) ? 0 : seit.hashCode());
		long Rabatt;
		Rabatt = Double.doubleToLongBits(rabatt);
		result = prime * result + (int) (Rabatt ^ (Rabatt >>> 32));
		long Umsatz;
		Umsatz = Double.doubleToLongBits(rabatt);
		result = prime * result + (int) (Umsatz ^ (Umsatz >>> 32));
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		
		if (art == null) {
			if (other.art != null)
				return false;
		} else if (!art.equals(other.art))
			return false;
		
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		
		if (familienstand == null) {
			if (other.familienstand != null)
				return false;
		} else if (!familienstand.equals(other.familienstand))
			return false;
		
		if (geschlecht == null) {
			if (other.geschlecht != null)
				return false;
		} else if (!geschlecht.equals(other.geschlecht))
			return false;
		
		if (nachname == null) {
			if (other.nachname != null)
				return false;
		} else if (!nachname.equals(other.nachname))
			return false;
		
		if (vorname == null) {
			if (other.vorname != null)
				return false;
		} else if (!vorname.equals(other.vorname))
			return false;
		
		if (rabatt == null) {
			if (other.rabatt != null)
				return false;
		} else if (!rabatt.equals(other.rabatt))
			return false;
		
		if (umsatz == null) {
			if (other.umsatz != null)
				return false;
		} else if (!umsatz.equals(other.umsatz))
			return false;
		
		if (seit == null) {
			if (other.seit != null)
				return false;
		} else if (!seit.equals(other.seit))
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		return "Kunde [id=" + id + ", art=" + art + ", email=" + email + ", familienstand=" + familienstand + ", geschlecht=" + geschlecht + ", familienstand=" + familienstand + ", vorname=" + vorname + ", nachname=" + nachname + ", rabatt=" + rabatt + ", umsatz=" + umsatz + ", seit=" + seit + " ]";
	}
}

