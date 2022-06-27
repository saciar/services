package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.LugarEventoContacto;

public interface LugarEventoContactoManagerSEI extends Remote {
	
	public LugarEventoContacto getLugarEventoContactoById(String codigo) throws RemoteException;
	
	public LugarEventoContacto[] getAllLugarEventoContactos() throws RemoteException;
	public LugarEventoContacto[] findByField(String field,String value) throws RemoteException;
	public LugarEventoContacto[] findByLugarAndField(String codLugar,String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public String update(LugarEventoContacto lugarEventoContacto) throws RemoteException;
	public Object[] getLugarContactoByClienteCodeReport(String codLugar) throws RemoteException;
}
