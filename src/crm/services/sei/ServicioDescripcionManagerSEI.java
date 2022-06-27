package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.ServicioDescripcion;

public interface ServicioDescripcionManagerSEI extends Remote {

	public ServicioDescripcion[] findByServicio(String codServicio,String codIdioma) throws RemoteException;
	public void saveDescripcion(String codServicio,String codIdioma,String descripcion)throws RemoteException;
	public void removeByServicio(String codServicio,String codIdioma)throws RemoteException;
}
