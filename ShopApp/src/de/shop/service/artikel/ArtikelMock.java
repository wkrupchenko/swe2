package de.shop.service.artikel;

import de.shop.data.artikel.Artikel;

final class ArtikelMock {
	
	static Artikel sucheArtikelNachId(Long id) {
		return new Artikel(id, "Bezeichnung" + id);
	}
	
	private ArtikelMock() {}
}
