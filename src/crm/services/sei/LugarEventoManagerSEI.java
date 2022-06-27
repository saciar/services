package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.LugarEvento;

public interface LugarEventoManagerSEI extends Remote {
	public LugarEvento getLugarEventoById(String codigo) throws RemoteException;
	public LugarEvento getLugarEventoByNombre(String nombre) throws RemoteException;
	public LugarEvento[] getAllLugarEventos() throws RemoteException;
	public LugarEvento[] findByField(String field,String value) throws RemoteException;
	public LugarEvento[] getAllLugarEventosTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public String update(LugarEvento lugarEvento) throws RemoteException;
	public Object[] getLugarEventosReport() throws RemoteException;
	public String getCodigoLugarComisionById(String codLugar) throws RemoteException;
}
