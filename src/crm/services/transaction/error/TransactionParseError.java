package crm.services.transaction.error;

public class TransactionParseError extends Error {
	
	private int errorCode;
	
	public TransactionParseError (int errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return this.errorCode;
	}
	
}
