package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.exceptions.FrameworkException;

/**
 * Exception caused by combining different layers.
 * 
 * @author kocur
 *
 */
public class InfluenceCombinationException extends FrameworkException {

	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = 1429845752444314785L;

	public InfluenceCombinationException(String msg) {
		super("Influence configuration exception: " + msg);
	}

	public InfluenceCombinationException(String msg, Throwable throwable) {
		super("Influence configuration exception: " + msg, throwable);
	}

}
