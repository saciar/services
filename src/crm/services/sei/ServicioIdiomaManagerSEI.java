package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.ServicioIdioma;

public interface ServicioIdiomaManagerSEI extends Remote {
	public ServicioIdioma getServicioIdiomaById(String codigoServicio,String codigoIdioma) throws RemoteException;
	public ServicioIdioma getServicioIdiomaByIdNoIdioma(String codigoServicio) throws RemoteException;
	public ServicioIdioma[] getAllServicioIdiomas() throws RemoteException;
	public ServicioIdioma[] findByField(String field,String value) throws RemoteException;
	//public ServicioIdioma[] getAllServicioIdiomasTranslated(String lang) throws RemoteException;
	public void remove(String codigoServicio,String codigoIdioma) throws RemoteException;
	public void update(ServicioIdioma servicioIdioma) throws RemoteException;
	public String getDescripcionServicio(String codigoServicio,
			String codigoIdioma) throws RemoteException;
}
