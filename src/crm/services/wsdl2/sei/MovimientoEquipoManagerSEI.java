package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.MovimientoEquipo;

public interface MovimientoEquipoManagerSEI extends Remote{
	public MovimientoEquipo getById(String codigo) throws RemoteException;
	public MovimientoEquipo[] getAll() throws RemoteException;
	public MovimientoEquipo[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(MovimientoEquipo model) throws RemoteException;
	public MovimientoEquipo[] findByFields(Object[] field,Object[] value) throws RemoteException;
	public MovimientoEquipo[] findByFieldExactly(String field,String value) throws RemoteException;

}
