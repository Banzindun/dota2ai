package cz.cuni.mff.kocur.streaming;

public interface DropCatcher {

	/**
	 * Should catch the drop if this is the kind of drop this instance wants.
	 * @param drop Information drop.
	 */
	public void catchDrop(InformationDrop drop);

	/**
	 * 
	 * @return Returns name of this catcher.
	 */
	public String getCatcherName();

	
	public BaseDropViewer getViewer();
}
