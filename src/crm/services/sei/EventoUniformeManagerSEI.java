package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Acceso;
import crm.libraries.abm.entities.EventoUniforme;

public interface EventoUniformeManagerSEI extends Remote {
	public EventoUniforme getEventoUniformeById(String codigo) throws RemoteException;
	public EventoUniforme getEventoUniformeByDescripcion(String descripcion) throws RemoteException;
	public EventoUniforme[] getAllEventoUniformes() throws RemoteException;
	public EventoUniforme[] findByField(String field,String value) throws RemoteException;
	//public EventoUniforme[] getAllEventoUniformesTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(EventoUniforme EventoUniforme) throws RemoteException;
}
