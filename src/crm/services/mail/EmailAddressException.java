package crm.services.mail;

public class EmailAddressException extends Exception{

	public EmailAddressException() {
		super();
	}

	public EmailAddressException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmailAddressException(String message) {
		super(message);
	}

	public EmailAddressException(Throwable cause) {
		super(cause);
	}

}
