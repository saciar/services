package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.SeguridadEquipos;

public interface SeguridadEquiposManagerSEI extends Remote {
	public SeguridadEquipos getById(String codigo) throws RemoteException;
	public SeguridadEquipos[] getAll() throws RemoteException;
	public SeguridadEquipos[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(SeguridadEquipos model) throws RemoteException;
}
