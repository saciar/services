package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Acceso;
import crm.libraries.abm.entities.Titulo;

public interface TituloManagerSEI extends Remote {
	public Titulo getTituloById(String codigo) throws RemoteException;
	public Titulo getTituloByDescripcion(String descripcion) throws RemoteException;
	public Titulo[] getAllTitulos() throws RemoteException;
	public Titulo[] findByField(String field,String value) throws RemoteException;
	//public Titulo[] getAllTitulosTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(Titulo titulo) throws RemoteException;
}
