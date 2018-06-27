package org.frame.repository.exception;

public class IlleagalDataException extends Throwable {

	private static final long serialVersionUID = -2011318957535489497L;
	
	public IlleagalDataException() {
		super();
	}
	
	public IlleagalDataException(String message) {
		super(message);
	}
	
	public IlleagalDataException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public IlleagalDataException(Throwable cause) {
		super(cause);
	}
	
}
