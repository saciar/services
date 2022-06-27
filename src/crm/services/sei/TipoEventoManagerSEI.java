package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.SalaLugar;
import crm.libraries.abm.entities.TipoEvento;

public interface TipoEventoManagerSEI extends Remote {
	public TipoEvento getTipoEventoById(String codigo) throws RemoteException;
	public TipoEvento getTipoEventoByDescripcion(String descripcion) throws RemoteException;
	public TipoEvento[] getAllTipoEventos() throws RemoteException;
	public TipoEvento[] findByField(String field,String value) throws RemoteException;
	//public TipoEvento[] getAllTipoEventosTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(TipoEvento tipoEvento) throws RemoteException;
	public Object[] getTipoEventosReport() throws RemoteException;
}
