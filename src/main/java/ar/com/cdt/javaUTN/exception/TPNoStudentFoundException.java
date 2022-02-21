package ar.com.cdt.javaUTN.exception;

public class TPNoStudentFoundException extends TPBaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4126269309919459102L;

	public TPNoStudentFoundException(String message, String serviceMessage) {
		super(message, serviceMessage);
	}

}
