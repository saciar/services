package crm.services.mail;

public class SendMailException extends Exception{

	public SendMailException() {
		super();
	}

	public SendMailException(String message, Throwable cause) {
		super(message, cause);
	}

	public SendMailException(String message) {
		super(message);
	}

	public SendMailException(Throwable cause) {
		super(cause);
	}

}
