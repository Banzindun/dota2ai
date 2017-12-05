package cz.cuni.mff.kocur.Exceptions;

public class ConversionError extends OverlayException{

	public ConversionError(String msg) {
		super("CONVERSION_ERROR:" + msg);
	}
	
	public ConversionError(String msg, Throwable throwable) {
		super("CONVERSION_ERROR:" + msg, throwable);		
	}
	
	
}
