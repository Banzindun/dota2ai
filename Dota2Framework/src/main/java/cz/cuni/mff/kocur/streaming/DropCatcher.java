package cz.cuni.mff.kocur.streaming;

/**
 * A place, that catches information drops. We should have a viewer, that can
 * display the information. And we should have a name.
 * 
 * @author kocur
 *
 */
public interface DropCatcher {

	/**
	 * Should catch the drop if this is the kind of drop this instance wants.
	 * 
	 * @param drop
	 *            Information drop.
	 */
	public void catchDrop(InformationDrop drop);

	/**
	 * 
	 * @return Returns name of this catcher.
	 */
	public String getCatcherName();

	/**
	 * 
	 * @return Returns a information drop viewer.
	 */
	public BaseDropViewer getViewer();
}
