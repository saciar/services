package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.EstadoEquipos;;

public interface EstadoEquiposManagerSEI extends Remote{
    public EstadoEquipos getById(String codigo) throws RemoteException;
	public EstadoEquipos[] getAll() throws RemoteException;
	public EstadoEquipos[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(EstadoEquipos model) throws RemoteException;
	public EstadoEquipos[] findByFields(Object[] field,Object[] value) throws RemoteException;
}
