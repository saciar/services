package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Pais;

public interface PaisManagerSEI extends Remote {
	public Pais getPaisById(String codigo) throws RemoteException;
	public Pais getPaisByDescripcion(String descripcion) throws RemoteException;
	public Pais[] getAllPaises() throws RemoteException;
	public Pais[] findByField(String field,String value) throws RemoteException;
	//public Pais[] getAllPaisesTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(Pais pais) throws RemoteException;
	public String getNombrePaisById(String codigo) throws RemoteException;
}
