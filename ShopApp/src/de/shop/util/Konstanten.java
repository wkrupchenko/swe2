package de.shop.util;

public final class Konstanten {
	public static final String ARTIKEL_KEY = "artikel";
	public static final String KUNDE_KEY = "kunde";	 
	public static final String KUNDEN_KEY = "kunden";
	
	public static final int WISCHEN_MIN_DISTANCE = 30;      
	public static final int WISCHEN_MAX_OFFSET_PATH = 30;  
	public static final int WISCHEN_THRESHOLD_VELOCITY = 30;
	
	public static final String PROTOCOL_DEFAULT = "http";
	public static final String LOCALHOST_EMULATOR = "10.0.2.2";
	public static final String HOST_DEFAULT = "10.0.2.2";
	public static final String PORT_DEFAULT = "8080";
	public static final String PATH_DEFAULT = "/shop/rest";
	public static final String TIMEOUT_DEFAULT = "10";
	public static final boolean MOCK_DEFAULT = false;
	
	public static final String LOCALHOST = "localhost";	

	public static final String KUNDEN_PATH = "/kunden";
	public static final String ARTIKEL_PATH = "/artikel";
	public static final String BESTELLUNGEN_PATH = "/bestellungen";
	public static final String NACHNAME_PATH = KUNDEN_PATH + "?nachname=";
	public static final String KUNDEN_PREFIX_PATH = KUNDEN_PATH + "/prefix";
	public static final String KUNDEN_ID_PREFIX_PATH = KUNDEN_PREFIX_PATH + "/id";
	public static final String NACHNAME_PREFIX_PATH = KUNDEN_PREFIX_PATH + "/nachname";

	public static final short MIN_KATEGORIE = 1;
	public static final short MAX_KATEGORIE = 1;
	
	private Konstanten() {}
}
