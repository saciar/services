package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.TipoUniforme;

public interface TipoUniformeManagerSEI extends Remote {
	public TipoUniforme getTipoUniformeById(String codigo) throws RemoteException;
	public TipoUniforme[] getAllTipoUniformes() throws RemoteException;
	public TipoUniforme[] findByField(String field,String value) throws RemoteException;
	//public TipoUniforme[] getAllTipoUniformesTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(TipoUniforme tipoUniforme) throws RemoteException;
	public Object[] getTiposUniformeReport() throws RemoteException;
}
