package cz.cuni.mff.kocur.Exceptions;

public class OverlayUncheckedException extends RuntimeException{
	public OverlayUncheckedException(String msg) {
		super(msg);
	}
	
	public OverlayUncheckedException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
