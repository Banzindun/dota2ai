package cz.cuni.mff.kocur.exceptions;

public class FrameworkException extends Exception{
	/**
	 * Generated serial version id. 
	 */
	private static final long serialVersionUID = 4866254078588122661L;

	public FrameworkException(String msg) {
		super(msg);
	}
	
	public FrameworkException(String msg, Throwable throwable) {
		super(msg, throwable);		
	}
}
