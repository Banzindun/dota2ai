package cz.cuni.mff.kocur.exceptions;

public class LoadingError extends FrameworkException {
	/**
	 * Generated serial version id. 
	 */
	private static final long serialVersionUID = 4317680390243353494L;

	public LoadingError(String msg) {
		super("LOADING_ERROR:" + msg);
	}
	
	public LoadingError(String msg, Throwable throwable) {
		super("LOADING_ERROR:" + msg, throwable);		
	}
	
}
