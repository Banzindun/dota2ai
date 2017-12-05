package cz.cuni.mff.kocur.Exceptions;

public class OverlayException extends Exception{
	public OverlayException(String msg) {
		super(msg);
	}
	
	public OverlayException(String msg, Throwable throwable) {
		super(msg, throwable);		
	}
}
