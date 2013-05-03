package de.shop.util;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ConcurrentDeletedException extends AbstractShopException {
	private static final long serialVersionUID = 1L;
	private final Object id;
	
	public ConcurrentDeletedException(Object id) {
		super("Das Objekt mit der ID " + id + " wurde konkurrierend geloescht");
		this.id = id;
	}
	
	public Object getId() {
		return id;
	}
}
