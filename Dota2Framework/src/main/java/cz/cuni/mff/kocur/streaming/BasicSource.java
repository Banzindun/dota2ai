package cz.cuni.mff.kocur.streaming;

/**
 * This class represents a basic information source, that we will be using. This
 * class implements basic flow.
 * 
 * @author kocur
 *
 */
public abstract class BasicSource implements Source {
	/**
	 * Id of this source.
	 */
	protected int id = 0;

	/**
	 * True, if we are flowing.
	 */
	protected boolean flowing = false;

	/**
	 * Name of this source.
	 */
	protected String name = "";

	/**
	 * Simple constructor.
	 */
	public BasicSource() {
		// Add source to sink, this will set id
		Sink.addSource(this);
	}

	/**
	 * Simple constructor that sets the name of this Source.
	 * 
	 * @param name Name of the source.
	 */
	public BasicSource(String name) {
		Sink.addSource(this);
		this.name = name;
	}

	/**
	 * Spawns a new thread that calls Sink.drain to get the information from this
	 * source to sink (and to correct catcher).
	 */
	@Override
	public void flow(InformationDrop d) {
		if (flowing) {
			new Thread(new Runnable() {
				public void run() {
					Sink.drain(d);
				}
			}).start();
		}
	}

	@Override
	public void wasted() {
		Sink.removeSource(this);
	}

	@Override
	public int getSourceId() {
		return id;
	}

	@Override
	public String getSourceName() {
		return name;
	}

	@Override
	public void setSourceId(int id) {
		this.id = id;
	}

	@Override
	public void startFlowing() {
		flowing = true;
	}

	@Override
	public void stopFlowing() {
		flowing = false;
	}
}
