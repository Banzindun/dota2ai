package cz.cuni.mff.kocur.streaming;


public abstract class BaseDropCatcher implements DropCatcher{
	
	public BaseDropCatcher() {
		
	}
	
	public void register() {
		Sink.addCatcher(this);
	}
	
	
}
