package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.PrtPptoSeguridad;

public interface PrtPptoSeguridadManagerSEI extends Remote{

	public PrtPptoSeguridad getById(String codigo) throws RemoteException;

	public PrtPptoSeguridad[] getAll() throws RemoteException;

	public PrtPptoSeguridad[] findByField(String field, String value) throws RemoteException;

	public void remove(String codigo) throws RemoteException;

	public void update(PrtPptoSeguridad ppi) throws RemoteException;

}