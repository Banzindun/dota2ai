package cz.cuni.mff.kocur.streaming;

/**
 * Base drop catcher, that registers itself in the Sink in register() method.
 * 
 * @author kocur
 *
 */
public abstract class BaseDropCatcher implements DropCatcher {
	/**
	 * Registers this catcher in the sink.
	 */
	public void register() {
		Sink.addCatcher(this);
	}

}
