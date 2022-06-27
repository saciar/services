package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.TipoContacto;

public interface TipoContactoManagerSEI extends Remote {
	public TipoContacto getTipoContactoById(String codigo) throws RemoteException;
	public TipoContacto[] getAllTipoContactos() throws RemoteException;
	public TipoContacto[] findByField(String field,String value) throws RemoteException;
	//public TipoContacto[] getAllTipoContactosTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(TipoContacto tipoContacto) throws RemoteException;
}
