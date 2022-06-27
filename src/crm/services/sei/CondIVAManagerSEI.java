package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.CondIVA;
;

public interface CondIVAManagerSEI extends Remote {
	public CondIVA getCondIVAById(String codigo) throws RemoteException;
	public CondIVA getCondIVAByDescripcion(String descripcion) throws RemoteException;
	public CondIVA[] getAllCondIVAs() throws RemoteException;
	public CondIVA[] findByField(String field,String value) throws RemoteException;
	//public CondIVA[] getAllCondIVAsTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(CondIVA cond) throws RemoteException;
}
