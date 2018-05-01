package cz.cuni.mff.kocur.world;

import cz.cuni.mff.kocur.streaming.BaseDropCatcher;
import cz.cuni.mff.kocur.streaming.BaseDropViewer;
import cz.cuni.mff.kocur.streaming.InformationDrop;

public class WorldDropCatcher extends BaseDropCatcher{
	private boolean changed = false; 
	
	//private World world = null; 
	
	private WorldDropViewer viewer = new WorldDropViewer();
	
	public WorldDropCatcher() {
		super(); // Will register this catcher.
	}
	
	public boolean wasChanged() {
		return changed;
	}
	
	public void setChanged(boolean b) {
		this.changed = b;
	}
	
	@Override
	public void catchDrop(InformationDrop drop) {
		// Take the drop only if it is WORLD information
		if (drop.getType() == InformationDrop.WORLD) {
			changed = true;
			
			viewer.receive(drop);
		}		
	}

	@Override
	public String getCatcherName() {
		return "WorldCatcher";
	}

	@Override
	public BaseDropViewer getViewer() {
		return viewer;
	}
	
	
}
