package de.shop.data.kunde;

import static de.shop.ShopApp.jsonBuilderFactory;

import java.io.Serializable;
import java.util.Date;

import javax.json.JsonObject;

import de.shop.data.JsonMappable;


public class Bestellung implements JsonMappable, Serializable {
	private static final long serialVersionUID = -3227854872557641281L;
	
	public Long id;
	public int version;
	public boolean offenAbgeschlossen;
	 

	public Bestellung() {
		super();
	}

	public Bestellung(long id, Date datum) {
		super();
		this.id = id;
		 
	}

	@Override
	public JsonObject toJsonObject() {
		return jsonBuilderFactory.createObjectBuilder()
		                         .add("id", id)
		                         .add("version", version)		                          
		                         .build();
	}
	
	@Override
	public void fromJsonObject(JsonObject jsonObject) {
		id = Long.valueOf(jsonObject.getJsonNumber("id").longValue());
		offenAbgeschlossen = jsonObject.getBoolean("offenAbgeschlossen");	 
	}
	
	@Override
	public void updateVersion() {
		version++;
	}

	@Override
	public String toString() {
		return "Bestellung [id=" + id + " ]";
	}
}
