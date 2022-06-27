package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.helper.Dummy;

public interface DummyManagerSEI extends Remote {
	public Dummy getDummy() throws RemoteException;
}
