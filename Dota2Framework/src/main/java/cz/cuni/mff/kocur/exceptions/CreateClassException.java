package cz.cuni.mff.kocur.exceptions;

public class CreateClassException extends FrameworkException {
	/**
	 * Generated serial version id. 
	 */
	private static final long serialVersionUID = -2376645190581052073L;

	public CreateClassException(String msg) {
		super(msg);
	}

	public CreateClassException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

}
