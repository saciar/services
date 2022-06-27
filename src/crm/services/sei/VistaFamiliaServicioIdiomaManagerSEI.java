package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.VistaFamiliaServicioIdioma;

public interface VistaFamiliaServicioIdiomaManagerSEI extends Remote{

	public VistaFamiliaServicioIdioma getVistaFamiliaServicioIdiomaById(
			String codigo) throws RemoteException;

	public Object[] getDescripcionByFamiliaAndServicio(String codServ,
			String codFam) throws RemoteException;

	public VistaFamiliaServicioIdioma[] getAllVistaFamiliaServicioIdioma()
			throws RemoteException;

	public VistaFamiliaServicioIdioma[] findByField(String field, String value)
			throws RemoteException;
	
	public Object[] getDescripcionByServicio(String codServ) throws RemoteException ;
}