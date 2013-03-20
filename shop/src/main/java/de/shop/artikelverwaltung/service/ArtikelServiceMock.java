package de.shop.artikelverwaltung.service;

import javax.enterprise.inject.Alternative;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.Log;

@Alternative
@Log
public class ArtikelServiceMock extends ArtikelService {
	private static final long serialVersionUID = -2919310633845009282L;

	@Override
	public Artikel findeArtikelNachId(Long id) {
		final Artikel artikel = new Artikel();
		artikel.setId((Long) id);
		artikel.setBezeichnung("Bezeichnung" + id);
		return artikel;
	}
}
