package net.rezzix.fhirservice.exceptions;

public class ValidationException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2569512966267636471L;

	public ValidationException (String message) {
		super(message);
	}
}
