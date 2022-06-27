package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.PrtPptoFooter;

public interface PrtPptoFooterManagerSEI extends Remote {
	public PrtPptoFooter getById(String codigo) throws RemoteException;
	public PrtPptoFooter[] getAll() throws RemoteException;
	public PrtPptoFooter[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(PrtPptoFooter model) throws RemoteException;
}
