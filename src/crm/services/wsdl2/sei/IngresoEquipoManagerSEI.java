package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.IngresoEquipo;

public interface IngresoEquipoManagerSEI extends Remote{
    public IngresoEquipo getById(String codigo) throws RemoteException;
	public IngresoEquipo[] getAll() throws RemoteException;
	public IngresoEquipo[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(IngresoEquipo model) throws RemoteException;
	public IngresoEquipo[] findByFields(Object[] field,Object[] value) throws RemoteException;
	public IngresoEquipo[] findByFieldExactly(String field,String value) throws RemoteException;
}
