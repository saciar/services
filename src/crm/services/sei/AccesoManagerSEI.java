package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Acceso;

public interface AccesoManagerSEI extends Remote {
	public Acceso getAccesoById(String codigo) throws RemoteException;
	public Acceso getAccesoByDescripcion(String descripcion) throws RemoteException;
	public Acceso[] getAllAccesos() throws RemoteException;
	public Acceso[] findByField(String field,String value) throws RemoteException;
	//public Acceso[] getAllAccesosTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(Acceso acceso) throws RemoteException;
}
