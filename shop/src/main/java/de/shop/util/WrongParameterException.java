package de.shop.util;

public class WrongParameterException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public WrongParameterException(String msg) {
		super(msg);
	}
	
	public WrongParameterException(String msg, Throwable t) {
		super(msg, t);
	}
}
