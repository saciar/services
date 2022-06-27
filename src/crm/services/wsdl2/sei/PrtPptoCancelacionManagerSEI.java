package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.PrtPptoCancelacion;

public interface PrtPptoCancelacionManagerSEI extends Remote {
	public PrtPptoCancelacion getById(String codigo) throws RemoteException;
	public PrtPptoCancelacion[] getAll() throws RemoteException;
	public PrtPptoCancelacion[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(PrtPptoCancelacion model) throws RemoteException;
}
