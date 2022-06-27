package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.VistaPrecioServicioIdioma;

public interface VistaPrecioServicioIdiomaManagerSEI extends Remote{

	public VistaPrecioServicioIdioma getVistaPrecioServicioIdiomaById(
			String codigo) throws RemoteException;

	public double getVistaPrecioServicioIdiomaByServicioYLugar(
			String codServ, String codLugar) throws RemoteException;

	public VistaPrecioServicioIdioma[] getAllVistaPrecioServicioIdioma()
			throws RemoteException;

	public VistaPrecioServicioIdioma[] findByField(String field, String value)
			throws RemoteException;
	
	public int getCountVistaPrecioServicioByLugar(String codLugar) throws RemoteException;
	
}