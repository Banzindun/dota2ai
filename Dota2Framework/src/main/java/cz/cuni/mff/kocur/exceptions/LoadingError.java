package cz.cuni.mff.kocur.Exceptions;

public class LoadingError extends OverlayException {
	public LoadingError(String msg) {
		super("LOADING_ERROR:" + msg);
	}
	
	public LoadingError(String msg, Throwable throwable) {
		super("LOADING_ERROR:" + msg, throwable);		
	}
	
}
