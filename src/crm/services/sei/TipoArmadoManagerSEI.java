package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.TipoArmado;

public interface TipoArmadoManagerSEI extends Remote {
	public TipoArmado getById(String codigo) throws RemoteException;
	public TipoArmado[] getAll() throws RemoteException;
	public TipoArmado[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(TipoArmado model) throws RemoteException;
	public Object[] getTipoArmadoReport() throws RemoteException;
}
