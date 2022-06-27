package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Idioma;

public interface IdiomaManagerSEI extends Remote {
	public Idioma getIdiomaById(String codigo) throws RemoteException;
	public Idioma getIdiomaByDescripcion(String descripcion) throws RemoteException;
	public Idioma[] getAllIdiomas() throws RemoteException;
	public Idioma[] findByField(String field,String value) throws RemoteException;
	//public Idioma[] getAllIdiomasTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(Idioma idioma) throws RemoteException;
}
