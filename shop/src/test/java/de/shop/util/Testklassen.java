package de.shop.util;

import java.util.Arrays;
import java.util.List;

import de.shop.kundenverwaltung.domain.KundeTest;
import de.shop.artikelverwaltung.domain.ArtikelTest;
import de.shop.bestellverwaltung.domain.BestellungTest;
import de.shop.kundenverwaltung.service.KundeServiceTest;
import de.shop.bestellverwaltung.service.BestellungServiceTest;
import de.shop.artikelverwaltung.service.ArtikelServiceTest;

public enum Testklassen {
	INSTANCE;
	
	// Testklassen aus *VERSCHIEDENEN* Packages auflisten (durch Komma getrennt):
	// so dass alle darin enthaltenen Klassen ins Web-Archiv mitverpackt werden
	private List<Class<? extends AbstractTest>> classes = Arrays.asList(KundeTest.class, 
			ArtikelTest.class, BestellungTest.class, KundeServiceTest.class, BestellungServiceTest.class, 
			ArtikelServiceTest.class);
	
	public static Testklassen getInstance() {
		return INSTANCE;
	}
	
	public List<Class<? extends AbstractTest>> getTestklassen() {
		return classes;
	}
}
