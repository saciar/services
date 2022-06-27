package crm.services.transaction.exception;

public class CanNotRemoveException extends Exception{

	public CanNotRemoveException() {
		super();
	}

	public CanNotRemoveException(String message, Throwable cause) {
		super(message, cause);
	}

	public CanNotRemoveException(String message) {
		super(message);
	}

	public CanNotRemoveException(Throwable cause) {
		super(cause);
	}

}
