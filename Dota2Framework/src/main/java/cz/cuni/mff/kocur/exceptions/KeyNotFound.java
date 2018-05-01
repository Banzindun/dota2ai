package cz.cuni.mff.kocur.exceptions;

public class KeyNotFound extends FrameworkException {

	/**
	 * Generated serial version id. 
	 */
	private static final long serialVersionUID = -7496961106311495220L;

	public KeyNotFound(String msg) {
		super("KEY_NOT_FOUND" + msg);
	}
	
	public KeyNotFound(String msg, Throwable throwable) {
		super("KEY_NOT_FOUND:" + msg, throwable);		
	}	
	
}
