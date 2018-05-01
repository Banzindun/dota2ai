package cz.cuni.mff.kocur.exceptions;

public class ComponentNotBuiltException extends FrameworkException{

	/**
	 * Generated serial version id. 
	 */
	 private static final long serialVersionUID = -2383304708193856789L;

	public ComponentNotBuiltException(String msg) {
		super("COMPONENTNOTBUILTEXCEPTION: " + msg);
	}
	
	public ComponentNotBuiltException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

}
