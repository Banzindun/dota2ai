package cz.cuni.mff.kocur.exceptions;

public class SetupException extends FrameworkException {

	/**
	 * Generated serial version id. 
	 */
	private static final long serialVersionUID = 7970233764875667631L;

	public SetupException(String msg) {
		super("SETUP ERROR: " + msg);
	}
	
	public SetupException(String msg, Throwable throwable) {
		super("SETUP ERROR: " + msg, throwable);
	}
}
