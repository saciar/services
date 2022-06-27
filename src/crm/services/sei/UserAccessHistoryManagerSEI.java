package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface UserAccessHistoryManagerSEI extends Remote {
	public void saveHistory(String userId, String accessId, String type) throws RemoteException;
}
