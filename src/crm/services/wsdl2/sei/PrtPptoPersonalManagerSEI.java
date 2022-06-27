package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.PrtPptoPersonal;

public interface PrtPptoPersonalManagerSEI extends Remote{
	public PrtPptoPersonal getById(String codigo) throws RemoteException;

	public PrtPptoPersonal[] getAll() throws RemoteException;

	public PrtPptoPersonal[] findByField(String field, String value) throws RemoteException;

	public void remove(String codigo) throws RemoteException;

	public void update(PrtPptoPersonal ppi) throws RemoteException;
}
