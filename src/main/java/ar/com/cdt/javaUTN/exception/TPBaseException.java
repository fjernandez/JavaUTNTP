package ar.com.cdt.javaUTN.exception;

public class TPBaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String serviceMessage;
	
	public TPBaseException(String message) {
		super(message);
	}

	public TPBaseException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TPBaseException(Throwable cause, String serviceMessage) {
		super(cause);
		this.setServiceMessage(serviceMessage);
	}
	
	public TPBaseException(String message, String serviceMessage) {
		super(message);
		this.setServiceMessage(serviceMessage);
	}
	
	public TPBaseException(Throwable cause) {
		super(cause);
	}

	public String getServiceMessage() {
		return serviceMessage;
	}

	public void setServiceMessage(String serviceMessage) {
		this.serviceMessage = serviceMessage;
	}
	
}
