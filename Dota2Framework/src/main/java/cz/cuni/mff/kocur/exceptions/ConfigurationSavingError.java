package cz.cuni.mff.kocur.exceptions;

public class ConfigurationSavingError extends FrameworkException {

	/**
	 * Generated serial version id. 
	 */
	private static final long serialVersionUID = -1867133627610607047L;

	public ConfigurationSavingError(String msg) {
		super(msg);

	}
	
	public ConfigurationSavingError(String msg, Throwable throwable) {
		super("Configuration saving error: " + msg, throwable);

	}

}
