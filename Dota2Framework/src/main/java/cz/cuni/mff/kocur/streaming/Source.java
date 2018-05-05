package cz.cuni.mff.kocur.streaming;

/**
 * Source emits information, that flows to Sink.
 * 
 * @author kocur
 *
 */
public interface Source {
	/**
	 * 
	 * Flows the information from this source to the sink.
	 * 
	 * @param d
	 *            The drop.
	 */
	public void flow(InformationDrop d);

	/**
	 * If the stream source should be wasted.
	 */
	public void wasted();

	/**
	 * Should return unique identification of this source. So that sink knows where
	 * the data come from.
	 * 
	 * @return Returns unique id that should be registered under the Sink.
	 */
	public int getSourceId();

	/**
	 * Sets source id. This should be unique number.
	 * @param id Id.
	 */
	public void setSourceId(int id);

	/**
	 * Source name might not be unique and should be used as string that can be read
	 * by user, so that he knows what is this source streaming etc.
	 * 
	 * @return Returns string representing this source's name.
	 */
	public String getSourceName();

	/**
	 * Starts the information flow.
	 */
	public void startFlowing();

	/**
	 * Stops the information flow.
	 * This is so that some flowing doesn't come to waste.
	 */
	public void stopFlowing();

}
