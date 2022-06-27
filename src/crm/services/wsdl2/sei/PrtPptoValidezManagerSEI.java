package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.PrtPptoValidez;

public interface PrtPptoValidezManagerSEI extends Remote {
	public PrtPptoValidez getById(String codigo) throws RemoteException;
	public PrtPptoValidez[] getAll() throws RemoteException;
	public PrtPptoValidez[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(PrtPptoValidez model) throws RemoteException;
}
