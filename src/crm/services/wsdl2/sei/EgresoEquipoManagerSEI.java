package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.EgresoEquipo;

public interface EgresoEquipoManagerSEI extends Remote{
    public EgresoEquipo getById(String codigo) throws RemoteException;
	public EgresoEquipo[] getAll() throws RemoteException;
	public EgresoEquipo[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(EgresoEquipo model) throws RemoteException;
	public EgresoEquipo[] findByFields(Object[] field,Object[] value) throws RemoteException;
	public EgresoEquipo[] findByFieldExactly(String field,String value) throws RemoteException;

}
