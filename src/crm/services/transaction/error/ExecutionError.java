package crm.services.transaction.error;

public class ExecutionError extends Error {

	private int errorCode;
	
	public ExecutionError(int errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

}
