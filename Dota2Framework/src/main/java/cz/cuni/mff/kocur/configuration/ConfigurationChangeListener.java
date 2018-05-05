package cz.cuni.mff.kocur.configuration;

/**
 * Interface for listener, that listens for change in configuration.
 * @author kocur
 *
 */
public interface ConfigurationChangeListener {
	
	/**
	 * Should be called after the configuration changed.
	 * The class that implements this interface should change values that are affected by the change in this method if possible.
	 */
	public void configurationChanged();
	
	
}
