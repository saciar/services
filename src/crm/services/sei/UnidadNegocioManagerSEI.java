package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.UnidadNegocio;

public interface UnidadNegocioManagerSEI extends Remote {
	public UnidadNegocio getUnidadNegocioById(String codigo) throws RemoteException;
	public UnidadNegocio getUnidadNegocioByDescripcion(String descripcion) throws RemoteException;
	public UnidadNegocio[] getAllUnidadNegocios() throws RemoteException;
	public UnidadNegocio[] findByField(String field,String value) throws RemoteException;
	//public UnidadNegocio[] getAllUnidadNegociosTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(UnidadNegocio titulo) throws RemoteException;
}
