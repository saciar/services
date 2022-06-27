package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.MarcaEquipo;

public interface MarcasEquiposManagerSEI extends Remote{
    public MarcaEquipo getById(String codigo) throws RemoteException;
	public MarcaEquipo[] getAll() throws RemoteException;
	public MarcaEquipo[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(MarcaEquipo model) throws RemoteException;

}
