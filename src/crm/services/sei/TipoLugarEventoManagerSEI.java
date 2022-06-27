package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.TipoLugarEvento;

public interface TipoLugarEventoManagerSEI extends Remote {
	public TipoLugarEvento getTipoLugarEventoById(String codigo) throws RemoteException;
	public TipoLugarEvento getTipoLugarEventoByDescripcion(String descripcion) throws RemoteException;
	public TipoLugarEvento[] getAllTipoLugarEventos() throws RemoteException;
	public TipoLugarEvento[] findByField(String field,String value) throws RemoteException;
	//public TipoLugarEvento[] getAllTipoLugarEventosTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(TipoLugarEvento tipoLugarEvento) throws RemoteException;
	public Object[] getTipoLugarEventosReport() throws RemoteException;
}
