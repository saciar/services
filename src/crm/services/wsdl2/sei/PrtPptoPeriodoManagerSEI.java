package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.PrtPptoPeriodo;

public interface PrtPptoPeriodoManagerSEI extends Remote{

	public PrtPptoPeriodo getById(String codigo) throws RemoteException;

	public PrtPptoPeriodo[] getAll() throws RemoteException;

	public PrtPptoPeriodo[] findByField(String field, String value) throws RemoteException;

	public void remove(String codigo) throws RemoteException;

	public void update(PrtPptoPeriodo ppi) throws RemoteException;

}
