package crm.services.transaction;

import java.rmi.RemoteException;

import crm.libraries.abm.helper.Dummy;
import crm.services.sei.DummyManagerSEI;

public class DummyManager implements DummyManagerSEI,ManagerService {

	public Dummy getDummy() throws RemoteException {
		return null;
	}

}
