package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.CategEvento;

public interface CategEventoManagerSEI  extends Remote {

	public Object[] getCategEventosReport() throws RemoteException;
	
	public CategEvento getCategEventoById(String codigo) throws RemoteException;
	public CategEvento getCategEventoByDescripcion(String descripcion) throws RemoteException;
	public CategEvento[] getAllCategEventos() throws RemoteException;
	public CategEvento[] findByField(String field,String value) throws RemoteException;
	//public CategEvento[] getAllCategEventosTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(CategEvento categEvento) throws RemoteException;

}