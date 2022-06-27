package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Egreso;

public interface EgresoManagerSEI extends Remote{
    public Egreso getById(String codigo) throws RemoteException;
	public Egreso[] getAll() throws RemoteException;
	public Egreso[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public String update(Egreso model) throws RemoteException;
	public Egreso[] findByFields(Object[] field,Object[] value) throws RemoteException;
	public Egreso[] findByFieldExactly(String field,String value) throws RemoteException;

}
