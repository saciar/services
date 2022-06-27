package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.MonedaExtranjera;

public interface MonedaExtranjeraManagerSEI extends Remote {
	public MonedaExtranjera getById(String codigo) throws RemoteException;
	public MonedaExtranjera[] getAll() throws RemoteException;
	public MonedaExtranjera[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(MonedaExtranjera model) throws RemoteException;
}
