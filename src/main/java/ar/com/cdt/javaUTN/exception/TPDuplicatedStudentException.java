package ar.com.cdt.javaUTN.exception;

public class TPDuplicatedStudentException extends TPBaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5254975806234731394L;

	public TPDuplicatedStudentException(String message, Throwable cause) {
		super(message, cause);
	}

	public TPDuplicatedStudentException(String message, String serviceMessage) {
		super(message, serviceMessage);
	}
}