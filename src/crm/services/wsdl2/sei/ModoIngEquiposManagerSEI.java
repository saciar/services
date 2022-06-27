package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.ModoIngEquipos;

public interface ModoIngEquiposManagerSEI extends Remote {
	public ModoIngEquipos getById(String codigo) throws RemoteException;
	public ModoIngEquipos[] getAll() throws RemoteException;
	public ModoIngEquipos[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(ModoIngEquipos model) throws RemoteException;
}
