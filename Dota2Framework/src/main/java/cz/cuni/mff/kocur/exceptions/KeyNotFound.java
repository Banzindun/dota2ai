package cz.cuni.mff.kocur.Exceptions;

public class KeyNotFound extends OverlayException {

	public KeyNotFound(String msg) {
		super("KEY_NOT_FOUND" + msg);
	}
	
	public KeyNotFound(String msg, Throwable throwable) {
		super("KEY_NOT_FOUND:" + msg, throwable);		
	}	
	
}
