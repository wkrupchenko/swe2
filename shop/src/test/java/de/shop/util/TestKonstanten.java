package de.shop.util;

public final class TestKonstanten {
	public static final String WEB_PROJEKT = "shop2";
	
	// HTTP-Header
	public static final String ACCEPT = "Accept";
	public static final String LOCATION = "Location";
	
	// URLs und Pfade
	public static final String BASEURI;
	public static final int PORT;
	public static final String BASEPATH;
	
	static {
		BASEURI = System.getProperty("baseuri", "http://localhost");
		PORT = Integer.parseInt(System.getProperty("port", "8080"));
		BASEPATH = System.getProperty("basepath", "/shop/rest");
	}
	
	public static final String KUNDEN_PATH = "/kunden";
	public static final String KUNDEN_URI = BASEURI + ":" + PORT + BASEPATH + KUNDEN_PATH;
	public static final String KUNDEN_ID_PATH_PARAM = "kundeId";
	public static final String KUNDEN_ID_PATH = KUNDEN_PATH + "/{" + KUNDEN_ID_PATH_PARAM + "}";
	public static final String KUNDEN_NACHNAME_QUERY_PARAM = "nachname";
	public static final String KUNDEN_ID_PREFIX_PATH_PARAM = "prefixId";
	public static final String KUNDEN_ID_PREFIX_PATH = KUNDEN_PATH + "/prefix/id/{" + KUNDEN_ID_PREFIX_PATH_PARAM + "}";
	public static final String KUNDEN_NACHNAME_PREFIX_PATH_PARAM = "prefixNachname";
	public static final String KUNDEN_NACHNAME_PREFIX_PATH = KUNDEN_PATH + "/prefix/nachname/"
															+ "{" + KUNDEN_NACHNAME_PREFIX_PATH_PARAM + "}";
	
	public static final String BESTELLUNGEN_PATH = "/bestellungen";
	public static final String BESTELLUNGEN_URI = BASEURI + ":" + PORT + BASEPATH + BESTELLUNGEN_PATH;
	public static final String BESTELLUNGEN_ID_PATH_PARAM = "bestellungId";
	public static final String BESTELLUNGEN_ID_PATH = BESTELLUNGEN_PATH + "/{" + BESTELLUNGEN_ID_PATH_PARAM + "}";
	public static final String BESTELLUNGEN_ID_KUNDE_PATH = BESTELLUNGEN_ID_PATH + "/kunde";
	
	public static final String LIEFERUNGEN_PATH = BESTELLUNGEN_PATH + "/lieferungen";
	public static final String LIEFERUNGEN_URI = BASEURI + ":" + PORT + BASEPATH + LIEFERUNGEN_PATH;
	public static final String LIEFERUNGEN_ID_PATH_PARAM = "lieferungId";
	public static final String LIEFERUNGEN_ID_PATH = LIEFERUNGEN_PATH + "/{" + LIEFERUNGEN_ID_PATH_PARAM + "}";
	
	public static final String ARTIKEL_PATH = "/artikel";
	public static final String ARTIKEL_URI = BASEURI + ":" + PORT + BASEPATH + ARTIKEL_PATH;
	public static final String ARTIKEL_ID_PATH_PARAM = "artikelId";
	public static final String ARTIKEL_ID_PATH = ARTIKEL_PATH + "/{" + ARTIKEL_ID_PATH_PARAM + "}";
	public static final String ARTIKEL_BEZEICHNUNG_PATH_PARAM = "bezeichnung";
	public static final String ARTIKEL_ERHAELTLICH_PATH_PARAM = "erhaeltlich";
	public static final String ARTIKEL_ID_FILE_PATH = ARTIKEL_ID_PATH + "/file";
	
	public static final String ARTIKELGRUPPE_PATH = "/artikel/artikelgruppe";
	public static final String ARTIKELGRUPPE_URI = BASEURI + ":" + PORT + BASEPATH + ARTIKELGRUPPE_PATH;
	public static final String ARTIKELGRUPPE_ID_PATH_PARAM = "artikelgruppeId";
	public static final String ARTIKELGRUPPE_ID_PATH = ARTIKELGRUPPE_PATH + "/{" + ARTIKELGRUPPE_ID_PATH_PARAM + "}";
	public static final String ARTIKELGRUPPE_BEZEICHNUNG_PATH_PARAM = "bezeichnung";
	
	// Testklassen fuer Service- und Domain-Tests
	public static final Class<?>[] TEST_CLASSES = {};
	
	private TestKonstanten() {
	}
	
}
