package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.PrtPptoInstalacion;

public interface PrtPptoInstalacionManagerSEI extends Remote {
	public PrtPptoInstalacion getById(String codigo) throws RemoteException;
	public PrtPptoInstalacion[] getAll() throws RemoteException;
	public PrtPptoInstalacion[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(PrtPptoInstalacion model) throws RemoteException;
}
