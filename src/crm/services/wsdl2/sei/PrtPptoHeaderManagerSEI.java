package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.PrtPptoHeader;

public interface PrtPptoHeaderManagerSEI extends Remote {
	public PrtPptoHeader getById(String codigo) throws RemoteException;
	public PrtPptoHeader[] getAll() throws RemoteException;
	public PrtPptoHeader[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(PrtPptoHeader model) throws RemoteException;
}
