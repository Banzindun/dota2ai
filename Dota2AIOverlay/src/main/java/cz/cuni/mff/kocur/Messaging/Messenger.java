package cz.cuni.mff.kocur.Messaging;

public class Messenger {
	private static Messenger instance = null;
	
	protected Messenger() {}
	
	public Messenger getInstance() {
		if (instance == null) instance = new Messenger();
		return instance;
	}
	
	public boolean exists() {
		return (instance != null);
	}
	
	// SEND or POST ??? choose one approach
	
	/**
	 * Should send message to someone ... 
	 */
	public void send() {
		
	}
	
	/**
	 * Should post message to interface.
	 */
	public void post() {

	}
	
	/**
	 * Should get the message from interface
	 */
	public void get() {
		
		
	}
	
}
